# Instruction MINU, MAXU, sext_b, sext_h, bset, bclr, binv

li x20, -3
li x21, 1
li x22, 3
li x23, 0x7f
li x24, 0xff
li x25, 0x7fff
li x26, 0xffff

minu x1, x20, x21
minu x2, x21, x22
maxu x3, x20, x21
maxu x4, x21, x22
sext.b x5, x23
sext.b x6, x24
sext.h x7, x25
sext.h x8, x26
bset x9, x20, x21
bclr x10, x20, x22
binv x11, x20, x22

nop
nop
nop
nop
nop
hcf
