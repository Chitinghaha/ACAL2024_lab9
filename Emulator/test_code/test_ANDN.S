## RV32 Emulator Testing Assembly Code for ANDN function

main:
addi x28,x0 ,3
addi x29,x0 ,-4
or   x30,x28,x29
andn x31,x28,x29
nop
nop
nop
nop
nop
hcf
