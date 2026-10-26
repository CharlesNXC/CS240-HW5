# CS240-HW5

## Description

This project implements a MIPS assembler and disassembler in Java.

- **Part 3:** The assembler converts `fizzbuzz.asm` into `.mhc-ascii` and `.mhc-bin` files.
- **Part 4:** The disassembler reconstructs MIPS assembly from either machine-code format.

The custom MHC container contains a header, a text segment, and a data segment. Instructions are stored in big-endian byte order.

## Requirements

- Java installed
- Terminal or command prompt

## How to Run

Open a terminal in the project folder.

### 1. Compile

```bash
javac Assembler.java Disassembler.java
```

### 2. Assemble the original program

```bash
java Assembler fizzbuzz.asm
```

This generates `fizzbuzz.mhc-ascii` and `fizzbuzz.mhc-bin`.

### 3. Disassemble the binary file

```bash
java Disassembler fizzbuzz.mhc-bin fizzbuzz_reconstructed.asm
```

Alternatively, use the ASCII file:

```bash
java Disassembler fizzbuzz.mhc-ascii reconstructed_from_ascii.asm
```

### 4. Reassemble the reconstructed program

```bash
java Assembler fizzbuzz_reconstructed.asm
```

## Files

- `Assembler.java` — assembler implementation.
- `Disassembler.java` — disassembler implementation.
- `fizzbuzz.asm` — original assembly source.
- `.mhc-ascii` and `.mhc-bin` — machine-code output formats.
- `fizzbuzz_reconstructed.asm` — reconstructed source, if included.
- `Part4_Test_Results.txt` — round-trip verification results, if included.
