# Reconstructed from MHC machine code by Disassembler.java
# Addresses: text=0x00400000, data=0x10010000

.data
data_bytes: .byte 70, 105, 122, 122, 0, 66, 117, 122, 122, 0

.text
    addi $t1, $zero, 1
    addi $t2, $zero, 101
L_00400008:
    addi $t3, $zero, 3
    div $t1, $t3
    mfhi $t4
    beq $t4, $zero, L_00400034
    addi $t3, $zero, 5
    div $t1, $t3
    mfhi $t4
    beq $t4, $zero, L_0040008C
    j L_004000B0
L_0040002C:
    j L_004000C0
L_00400030:
    j L_004000A4
L_00400034:
    addi $t3, $zero, 5
    div $t1, $t3
    mfhi $t4
    beq $t4, $zero, L_00400050
    j L_00400078
L_00400048:
    j L_004000C0
    j L_004000A4
L_00400050:
    addi $v0, $zero, 4
    lui $a0, 4097
    ori $a0, $a0, 0
    syscall
    addi $v0, $zero, 4
    lui $a0, 4097
    ori $a0, $a0, 5
    syscall
    j L_004000D0
L_00400074:
    j L_004000A4
L_00400078:
    addi $v0, $zero, 4
    lui $a0, 4097
    ori $a0, $a0, 0
    syscall
    j L_00400048
L_0040008C:
    addi $v0, $zero, 4
    lui $a0, 4097
    ori $a0, $a0, 5
    syscall
    j L_004000E0
L_004000A0:
    j L_004000A4
L_004000A4:
    addi $t1, $t1, 1
    bne $t1, $t2, L_00400008
    j L_004000F0
L_004000B0:
    addi $v0, $zero, 1
    addu $a0, $t1, $zero
    syscall
    j L_0040002C
L_004000C0:
    addi $a0, $zero, 10
    addi $v0, $zero, 11
    syscall
    j L_00400030
L_004000D0:
    addi $a0, $zero, 10
    addi $v0, $zero, 11
    syscall
    j L_00400074
L_004000E0:
    addi $a0, $zero, 10
    addi $v0, $zero, 11
    syscall
    j L_004000A0
L_004000F0:
    addi $v0, $zero, 10
    syscall
