.data

fizz: .asciiz "Fizz"
buzz: .asciiz "Buzz"

.text

li $t1, 1
li $t2, 101

loop1:

    li $t3, 3
    div $t1, $t3
    mfhi $t4

    beq $t4, $zero, Check5

    li $t3, 5
    div $t1, $t3
    mfhi $t4

    beq $t4, $zero, PrintBuzz

    j PrintInt
PrintIntEnd:

    j PrintNewLine
PrintNewLineEnd:

    j NextNumber


Check5:

    li $t3, 5
    div $t1, $t3
    mfhi $t4

    beq $t4, $zero, PrintFizzBuzz

    j PrintFizz
PrintFizzEnd:

    j PrintNewLine
PrintNewLineEnd2:

    j NextNumber


PrintFizzBuzz:

    li $v0, 4
    la $a0, fizz
    syscall

    li $v0, 4
    la $a0, buzz
    syscall

    j PrintNewLine2
PrintNewLineEnd4:

    j NextNumber


PrintFizz:

    li $v0, 4
    la $a0, fizz
    syscall

    j PrintFizzEnd


PrintBuzz:

    li $v0, 4
    la $a0, buzz
    syscall

    j PrintNewLine3
PrintNewLineEnd3:

    j NextNumber


NextNumber:

    addi $t1, $t1, 1
    bne $t1, $t2, loop1

j Exit

PrintInt:

    li $v0, 1
    move $a0, $t1
    syscall

    j PrintIntEnd

PrintNewLine:

    li $a0, '\n'
    li $v0, 11
    syscall

    j PrintNewLineEnd

PrintNewLine2:

    li $a0, '\n'
    li $v0, 11
    syscall

    j PrintNewLineEnd4

PrintNewLine3:

    li $a0, '\n'
    li $v0, 11
    syscall

    j PrintNewLineEnd3

Exit:

    li $v0, 10
    syscall