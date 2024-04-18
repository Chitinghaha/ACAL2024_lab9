package acal_lab09.PiplinedCPU.DatapathModule.DatapathComponent

import chisel3._
import chisel3.util._

import acal_lab09.PiplinedCPU.opcode_map._
import acal_lab09.PiplinedCPU.alu_op_map._

class ALUIO extends Bundle{
  val src1    = Input(UInt(32.W))
  val src2    = Input(UInt(32.W))
  val ALUSel  = Input(UInt(15.W))
  val out  = Output(UInt(32.W))
}

class ALU extends Module{
  val io = IO(new ALUIO)

  val sext_b_helper = WireDefault(0.S(32.W))
  val sext_h_helper = WireDefault(0.S(32.W))

  // CLZ
  val clz_buf = Wire(Vec(32, UInt(6.W)))
  val clz_stop = Wire(Vec(32, UInt(1.W))) // detect if there is a '1' in MSB
  clz_stop(31) := io.src1(31)
  for(i <- 30 to 0 by -1)
    clz_stop(i) := clz_stop(i+1) | io.src1(i)

  clz_buf(31) := ~io.src1(31)
  for(i <- 30 to 0 by -1)
    clz_buf(i) := clz_buf(i+1) + ~(io.src1(i) | clz_stop(i))


  // CTZ
  val ctz_buf = Wire(Vec(32, UInt(6.W)))
  val ctz_stop = Wire(Vec(32, UInt(1.W))) // detect if there is a '1' in MSB
  ctz_stop(0) := io.src1(0)
  for(i <- 1 until 32)
    ctz_stop(i) := ctz_stop(i-1) | io.src1(i)

  ctz_buf(0) := ~io.src1(0)
  for(i <- 1 until 32)
    ctz_buf(i) := ctz_buf(i-1) + ~(io.src1(i) | ctz_stop(i))


  // CPOP
  val cpop_buf = Wire(Vec(32, UInt(6.W)))
  cpop_buf(0) := io.src1(0)
  for(i <- 1 until 32)
    cpop_buf(i) := cpop_buf(i-1) + io.src1(i)


  io.out := 0.U
  switch(io.ALUSel){
    is(ADD ){io.out := io.src1 + io.src2}
    is(SLL ){io.out := io.src1 << io.src2(4,0)}
    is(SLT ){io.out := Mux(io.src1.asSInt < io.src2.asSInt, 1.U, 0.U)}
    is(SLTU){io.out := Mux(io.src1 < io.src2              , 1.U, 0.U)}
    is(XOR ){io.out := io.src1 ^ io.src2}
    is(SRL ){io.out := io.src1 >> io.src2(4,0)}
    is(OR  ){io.out := io.src1 | io.src2}
    is(AND ){io.out := io.src1 & io.src2}
    is(SUB ){io.out := io.src1 - io.src2}
    is(SRA ){io.out := (io.src1.asSInt >> io.src2(4,0)).asUInt}

    // additional instructions
    is(CLZ){io.out := clz_buf(0)}
    is(CTZ){io.out := ctz_buf(31)}
    is(CPOP){io.out := cpop_buf(31)}
    is(ANDN){io.out := io.src1 & ~(io.src2)}
    is(ORN){io.out := io.src1 | ~(io.src2)}
    is(XNOR){io.out := ~(io.src1 ^ io.src2)}
    is(MIN){io.out := Mux(io.src1.asSInt < io.src2.asSInt, io.src1, io.src2)}
    is(MAX){io.out := Mux(io.src1.asSInt > io.src2.asSInt, io.src1, io.src2)}
    is(MINU){io.out := Mux(io.src1 < io.src2, io.src1, io.src2)}
    is(MAXU){io.out := Mux(io.src1 > io.src2, io.src1, io.src2)}
    is(SEXT_B){
      sext_b_helper := io.src1(7,0).asSInt
      io.out := sext_b_helper.asUInt
    }
    is(SEXT_H){
      sext_h_helper := io.src1(15,0).asSInt
      io.out := sext_h_helper.asUInt
    }
    is(BSET){io.out := io.src1 | (1.U << io.src2(4,0))}
    is(BCLR){io.out := io.src1 & ~(1.U << io.src2(4,0))}
    is(BINV){io.out := io.src1 ^ (1.U << io.src2(4,0))}
    is(BEXT){io.out := io.src1(io.src2(4,0))}
    is(ROR){io.out := (io.src1 >> io.src2(4,0)) | (io.src1 << (32.U - io.src2(4,0)))}
    is(ROL){io.out := (io.src1 << io.src2(4,0)) | (io.src1 >> (32.U - io.src2(4,0)))}
    is(SH1ADD){io.out := (io.src1 << 1.U) + io.src2}
    is(SH2ADD){io.out := (io.src1 << 2.U) + io.src2}
    is(SH3ADD){io.out := (io.src1 << 3.U) + io.src2}
    is(REV8){io.out := Cat(io.src1(7,0), io.src1(15,8), io.src1(23,16), io.src1(31,24))}
    is(ZEXT_H){io.out := io.src1(15,0)}
    is(ORC_B){io.out := Cat(Mux(io.src1(31,24) === 0.U, 0.U(8.W), "b1111_1111".U),
                            Mux(io.src1(23,16) === 0.U, 0.U(8.W), "b1111_1111".U),
                            Mux(io.src1(15,8) === 0.U, 0.U(8.W), "b1111_1111".U),
                            Mux(io.src1(7,0) === 0.U, 0.U(8.W), "b1111_1111".U))}
  }
}

