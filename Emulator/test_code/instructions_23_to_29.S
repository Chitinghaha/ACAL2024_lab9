# Instruction rori, sh1add, sh2add, sh3add, rev8, zext_h, orc.b

lui x20, 0x1234
addi x20, x20, 0x567
li x21, 5
li x22, 3
li x23, 0xFF
li x24, 0xFFFF8000   
li x25, 0x00100100  
li x26, 0x10000001


rori x1, x20, 4
rori x2, x20, 8
rori x3, x20, 16
sh1add x4, x21, x22
sh2add x5, x21, x22
sh3add x6, x21, x22 
rev8  x7, x20 
zext.h x8, x23 
zext.h x9, x24 
orc.b x10, x25
orc.b x11, x26

nop
nop
nop
nop
nop
hcf