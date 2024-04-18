j <reset_vector>
li ra,0
li sp,0
li gp,0
li tp,0
li t0,0
li t1,0
li t2,0
li s0,0
li s1,0
li a0,0
li a1,0
li a2,0
li a3,0
li a4,0
li a5,0
li a6,0
li a7,0
li s2,0
li s3,0
li s4,0
li s5,0
li s6,0
li s7,0
li s8,0
li s9,0
li s10,0
li s11,0
li t3,0
li t4,0
li t5,0
li t6,0
li gp,0
li a0,1
slli a0,a0,0x1f
bltz a0,<test_2>
nop 
li gp,1
li a7,93
li a0,0
li gp,2
lui ra,0x0
li t2,0
bne ra,t2,<fail>
li gp,3
lui ra,0xfffff
srai ra,ra,0x1
li t2,-2048
bne ra,t2,<fail>
li gp,4
lui ra,0x7ffff
srai ra,ra,0x14
li t2,2047
bne ra,t2,<fail>
li gp,5
lui ra,0x80000
srai ra,ra,0x14
li t2,-2048
bne ra,t2,<fail>
li gp,6
lui zero,0x80000
li t2,0
bne zero,t2,<fail>
bne zero,gp,<pass>
nop 
beqz gp,<fail+0x4>
mv a0,gp
slli gp,gp,0x1
ori gp,gp,1
li a7,93
j <pass_fail_end>
nop 
li gp,1
li a7,93
li a0,0
nop 
nop 
nop 
nop 
nop 
hcf
