import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Disassembler {
    static final int HEADER_SIZE = 20;
    static final byte[] MAGIC = new byte[]{'M','H','C','1'};

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java Disassembler <input.mhc-bin|input.mhc-ascii> [output.asm]");
            System.exit(1);
        }
        Path input = Path.of(args[0]);
        String name = input.getFileName().toString();
        String stem = name.replaceFirst("\\\\.mhc-(bin|ascii)$", "");
        Path output = args.length == 2 ? Path.of(args[1]) : Path.of(stem + "_reconstructed.asm");
        try {
            byte[] container = readContainer(input);
            String asm = disassemble(container);
            Files.writeString(output, asm);
            System.out.println("Disassembly successful.");
            System.out.println("Input: " + input);
            System.out.println("Output: " + output);
        } catch (Exception e) {
            System.err.println("Disassembler error: " + e.getMessage());
            System.exit(1);
        }
    }

    static byte[] readContainer(Path path) throws Exception {
        byte[] raw = Files.readAllBytes(path);
        String n = path.getFileName().toString();
        if (n.endsWith(".mhc-ascii")) {
            String bits = new String(raw, java.nio.charset.StandardCharsets.US_ASCII).trim();
            if ((bits.length() % 8) != 0) throw new Exception("ASCII bitstream length is not divisible by 8");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int i=0; i<bits.length(); i+=8) {
                int v=0;
                for (int j=0; j<8; j++) {
                    char c=bits.charAt(i+j);
                    if (c!='0' && c!='1') throw new Exception("invalid character in .mhc-ascii at position " + (i+j));
                    v=(v<<1)|(c-'0');
                }
                out.write(v);
            }
            raw=out.toByteArray();
        }
        if (raw.length < HEADER_SIZE) throw new Exception("file is shorter than the MHC header");
        for (int i=0;i<4;i++) if(raw[i]!=MAGIC[i]) throw new Exception("invalid MHC magic header (expected MHC1)");
        int textBase=readInt(raw,4), dataBase=readInt(raw,8);
        int textLen=readInt(raw,12), dataLen=readInt(raw,16);
        if (textLen<0 || dataLen<0 || (textLen%4)!=0) throw new Exception("invalid text/data lengths in header");
        if ((long)HEADER_SIZE+textLen+dataLen != raw.length) throw new Exception("header lengths do not match file size");
        if (textBase != 0x00400000 || dataBase != 0x10010000)
            throw new Exception(String.format("unsupported segment addresses: text=0x%08X data=0x%08X", textBase, dataBase));
        return raw;
    }
    static int readInt(byte[] b, int i) {
        return ((b[i]&255)<<24)|((b[i+1]&255)<<16)|((b[i+2]&255)<<8)|(b[i+3]&255);
    }
    static String reg(int n) {
        String[] names={"$zero","$at","$v0","$v1","$a0","$a1","$a2","$a3",
            "$t0","$t1","$t2","$t3","$t4","$t5","$t6","$t7",
            "$s0","$s1","$s2","$s3","$s4","$s5","$s6","$s7",
            "$t8","$t9","$k0","$k1","$gp","$sp","$fp","$ra"};
        return names[n & 31];
    }
    static String label(int addr) { return String.format("L_%08X", addr); }
    static String disassemble(byte[] raw) throws Exception {
        int textBase=readInt(raw,4), dataBase=readInt(raw,8);
        int textLen=readInt(raw,12), dataLen=readInt(raw,16);
        int textStart=HEADER_SIZE, dataStart=HEADER_SIZE+textLen;
        int count=textLen/4;
        int[] words=new int[count];
        Set<Integer> targets=new TreeSet<>();
        for(int i=0;i<count;i++) {
            words[i]=readInt(raw,textStart+i*4);
            int pc=textBase+i*4, w=words[i], op=w>>>26;
            if(op==2 || op==3) {
                int target=((pc+4)&0xF0000000)|((w&0x03FFFFFF)<<2);
                if(target>=textBase && target<=textBase+textLen) targets.add(target);
            } else if(op==4 || op==5) {
                int target=pc+4+(((short)(w&0xffff))<<2);
                if(target>=textBase && target<=textBase+textLen) targets.add(target);
            }
        }
        StringBuilder out=new StringBuilder();
        out.append("# Reconstructed from MHC machine code by Disassembler.java\n");
        out.append("# Addresses: text=0x").append(String.format("%08X",textBase))
           .append(", data=0x").append(String.format("%08X",dataBase)).append("\n\n");
        out.append(".data\n");
        if(dataLen>0) {
            out.append("data_bytes: .byte ");
            for(int i=0;i<dataLen;i++) {
                if(i>0) out.append(", ");
                out.append(raw[dataStart+i]&255);
            }
            out.append("\n");
        }
        out.append("\n.text\n");
        for(int i=0;i<count;i++) {
            int pc=textBase+i*4;
            if(targets.contains(pc)) out.append(label(pc)).append(":\n");
            out.append("    ").append(decode(words[i],pc)).append("\n");
        }
        int end=textBase+textLen;
        if(targets.contains(end)) out.append(label(end)).append(":\n");
        return out.toString();
    }
    static String decode(int w, int pc) throws Exception {
        int op=w>>>26, rs=(w>>>21)&31, rt=(w>>>16)&31, rd=(w>>>11)&31;
        int sh=(w>>>6)&31, fn=w&63, imm=(short)(w&0xffff), uimm=w&0xffff;
        switch(op) {
            case 0:
                switch(fn) {
                    case 0: return "sll " + reg(rd) + ", " + reg(rt) + ", " + sh;
                    case 8: return "jr " + reg(rs);
                    case 12: return "syscall";
                    case 16: return "mfhi " + reg(rd);
                    case 26: return "div " + reg(rs) + ", " + reg(rt);
                    case 32: return "add " + reg(rd) + ", " + reg(rs) + ", " + reg(rt);
                    case 33: return "addu " + reg(rd) + ", " + reg(rs) + ", " + reg(rt);
                    case 34: return "sub " + reg(rd) + ", " + reg(rs) + ", " + reg(rt);
                    default: throw new Exception(String.format("unsupported R-type funct %d at 0x%08X",fn,pc));
                }
            case 2: return "j " + label(((pc+4)&0xF0000000)|((w&0x03FFFFFF)<<2));
            case 3: return "jal " + label(((pc+4)&0xF0000000)|((w&0x03FFFFFF)<<2));
            case 4: return "beq " + reg(rs) + ", " + reg(rt) + ", " + label(pc+4+(imm<<2));
            case 5: return "bne " + reg(rs) + ", " + reg(rt) + ", " + label(pc+4+(imm<<2));
            case 8: return "addi " + reg(rt) + ", " + reg(rs) + ", " + imm;
            case 9: return "addiu " + reg(rt) + ", " + reg(rs) + ", " + imm;
            case 13: return "ori " + reg(rt) + ", " + reg(rs) + ", " + uimm;
            case 15: return "lui " + reg(rt) + ", " + uimm;
            case 35: return "lw " + reg(rt) + ", " + imm + "(" + reg(rs) + ")";
            case 43: return "sw " + reg(rt) + ", " + imm + "(" + reg(rs) + ")";
            default: throw new Exception(String.format("unsupported opcode %d at 0x%08X",op,pc));
        }
    }
}
