lui x20, 0x1234
addi x20, x20, 0x567
lui x21, 0x00000000
addi x21, x21, 0x00000005
lui x22, 0x00000000
addi x22, x22, 0x00000003
lui x23, 0x00000000
addi x23, x23, 0x000000ff
lui x24, 0x000ffff8
addi x24, x24, 0x00000000
lui x25, 0x00000100
addi x25, x25, 0x00000100
lui x26, 0x00010000
addi x26, x26, 0x00000001
rori x1, x20, 4
rori x2, x20, 8
rori x3, x20, 16
sh1add x4, x21, x22
sh2add x5, x21, x22
sh3add x6, x21, x22
rev8 x7, x20
zext.h x8, x23
zext.h x9, x24
orc.b x10, x25
orc.b x11, x26
nop zero, zero, 0
nop zero, zero, 0
nop zero, zero, 0
nop zero, zero, 0
nop zero, zero, 0
hcf
nop
nop
nop
nop
nop
hcf
