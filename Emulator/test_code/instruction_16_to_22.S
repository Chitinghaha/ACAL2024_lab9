# Instruction BEXT, BSETI, BCLRI, BINVI, BEXTI, ROR, ROL

addi x16, x0, 10
addi x17, x0, 1
addi x18, x0, 2
addi x19, x0, 26
addi x20, x0, 8

bext x1, x16, x17
bext x2, x16, x18
bseti x3, x16, 1
bseti x4, x16, 2
bclri x5, x16, 1
bclri x6, x16, 2
binvi x7, x16, 1
binvi x8, x16, 2
bexti x9, x16, 1
bexti x10, x16, 2
ror x11, x19, x20
rol x12, x19, x20