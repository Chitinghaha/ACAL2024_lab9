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
li t0,0
auipc t1,0x0
addi t1,t1,16
jalr t0,t1
j <fail>
auipc t1,0x0
addi t1,t1,-4
bne t0,t1,<fail>
li gp,3
auipc t0,0x0
addi t0,t0,16
jalr t0,t0
j <fail>
auipc t1,0x0
addi t1,t1,-4
bne t0,t1,<fail>
li gp,4
li tp,0
auipc t1,0x0
addi t1,t1,16
jalr a3,t1
bne zero,gp,<fail>
addi tp,tp,1
li t0,2
bne tp,t0,<test_4+0x8>
li gp,5
li tp,0
auipc t1,0x0
addi t1,t1,20
nop 
jalr a3,t1
bne zero,gp,<fail>
addi tp,tp,1
li t0,2
bne tp,t0,<test_5+0x8>
li gp,6
li tp,0
auipc t1,0x0
addi t1,t1,24
nop 
nop 
jalr a3,t1
bne zero,gp,<fail>
addi tp,tp,1
li t0,2
bne tp,t0,<test_6+0x8>
li gp,7
li t0,1
auipc t1,0x0
addi t1,t1,28
jr -4(t1)
addi t0,t0,1
addi t0,t0,1
addi t0,t0,1
addi t0,t0,1
addi t0,t0,1
addi t0,t0,1
li t2,4
bne t0,t2,<fail>
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
