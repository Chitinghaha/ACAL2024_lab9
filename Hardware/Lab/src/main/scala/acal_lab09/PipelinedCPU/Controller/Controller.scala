package acal_lab09.PiplinedCPU.Controller

import chisel3._
import chisel3.util._

import acal_lab09.PiplinedCPU.opcode_map._
import acal_lab09.PiplinedCPU.condition._
import acal_lab09.PiplinedCPU.inst_type._
import acal_lab09.PiplinedCPU.alu_op_map._
import acal_lab09.PiplinedCPU.pc_sel_map._
import acal_lab09.PiplinedCPU.wb_sel_map._
import acal_lab09.PiplinedCPU.forwarding_sel_map._

class Controller(memAddrWidth: Int) extends Module {
  val io = IO(new Bundle {
    // Memory control signal interface
    val IM_Mem_R = Output(Bool())
    val IM_Mem_W = Output(Bool())
    val IM_Length = Output(UInt(4.W))
    val IM_Valid = Input(Bool())

    val DM_Mem_R = Output(Bool())
    val DM_Mem_W = Output(Bool())
    val DM_Length = Output(UInt(4.W))
    val DM_Valid = Input(Bool())

    // branch Comp.
    val E_BrEq = Input(Bool())
    val E_BrLT = Input(Bool())

    // Branch Prediction
    val E_Branch_taken = Output(Bool())
    val E_En = Output(Bool())

    val ID_pc = Input(UInt(memAddrWidth.W))
    val EXE_target_pc = Input(UInt(memAddrWidth.W))

    // Flush
    val Flush_DH = Output(Bool()) //TBD
    val Flush_BH = Output(Bool()) //TBD

    // Stall
    // To Be Modified
    val Stall_DH = Output(Bool()) //TBD
    val Stall_MA = Output(Bool()) //TBD

    // inst
    val IF_Inst = Input(UInt(32.W))
    val ID_Inst = Input(UInt(32.W))
    val EXE_Inst = Input(UInt(32.W))
    val MEM_Inst = Input(UInt(32.W))
    val WB_Inst = Input(UInt(32.W))

    // sel
    val PCSel = Output(UInt(2.W))
    val D_ImmSel = Output(UInt(3.W))
    val W_RegWEn = Output(Bool())
    val E_BrUn = Output(Bool())
    val E_ASel = Output(UInt(2.W))
    val E_BSel = Output(UInt(1.W))
    val E_ALUSel = Output(UInt(15.W))
    val W_WBSel = Output(UInt(2.W))

    // forwarding sel
    val For_ASel = Output(UInt(3.W))
    val For_BSel = Output(UInt(3.W))

    val Hcf = Output(Bool())
  })
  // Inst Decode for each stage
  val IF_opcode = io.IF_Inst(6, 0)

  val ID_opcode = io.ID_Inst(6, 0)
  val ID_rs1 = io.ID_Inst(19, 15)
  val ID_rs2 = io.ID_Inst(24, 20)

  val EXE_opcode = io.EXE_Inst(6, 0)
  val EXE_funct3 = io.EXE_Inst(14, 12)
  val EXE_funct7 = io.EXE_Inst(31, 25)
  val EXE_rd = io.EXE_Inst(11, 7)
  val EXE_rs1 = io.EXE_Inst(19, 15)
  val EXE_rs2 = io.EXE_Inst(24, 20)

  val MEM_opcode = io.MEM_Inst(6, 0)
  val MEM_funct3 = io.MEM_Inst(14, 12)
  val MEM_rd = io.MEM_Inst(11, 7)

  val WB_opcode = io.WB_Inst(6, 0)
  val WB_rd = io.WB_Inst(11, 7)

  // Control signal - Branch/Jump
  val E_En = Wire(Bool())
  E_En := (EXE_opcode === BRANCH || EXE_opcode === JAL || EXE_opcode === JALR)         // To Be Modified

  val E_Branch_taken = Wire(Bool())
  E_Branch_taken := MuxLookup(EXE_opcode, false.B, Seq(
          BRANCH -> MuxLookup(EXE_funct3, false.B, Seq(
            "b000".U(3.W) -> io.E_BrEq.asUInt,    // BEQ
            "b001".U(3.W) -> ~(io.E_BrEq.asUInt), // BNE
            "b100".U(3.W) -> io.E_BrLT.asUInt,    // BLT
            "b101".U(3.W) -> ~(io.E_BrLT.asUInt), // BGE
            "b110".U(3.W) -> io.E_BrLT.asUInt,    // BLTU
            "b111".U(3.W) -> ~(io.E_BrLT.asUInt), // BGEU
          )),
          JAL -> true.B,
          JALR -> true.B,
        ))    // To Be Modified

  io.E_En := E_En
  io.E_Branch_taken := E_Branch_taken

  // pc predict miss signal
  val Predict_Miss = Wire(Bool())
  Predict_Miss := (E_En && E_Branch_taken && io.ID_pc =/= io.EXE_target_pc)

  // Control signal - PC
  when(Predict_Miss){
    io.PCSel := EXE_T_PC
  }.otherwise{
    io.PCSel := IF_PC_PLUS_4
  }

  // Control signal - Branch comparator
  io.E_BrUn := (io.EXE_Inst(13) === 1.U)

  // Control signal - Immediate generator
  io.D_ImmSel := MuxLookup(ID_opcode, 0.U, Seq(
    OP_IMM -> I_type,
    LOAD -> I_type,
    BRANCH -> B_type,
    LUI -> U_type,
    STORE -> S_type,
    JALR -> I_type,
    JAL -> J_type,
    OP -> R_type,
    AUIPC -> U_type,
  ))

  // Control signal - Scalar ALU
  val E_ASel_w = MuxLookup(EXE_opcode, 0.U, Seq(
    BRANCH -> 1.U,
    LUI -> 2.U,
    JAL -> 1.U,
    AUIPC -> 1.U,
  ))
  val E_BSel_w = MuxLookup(EXE_opcode, 0.U, Seq(
    LUI -> 1.U,
    LOAD -> 1.U,
    STORE -> 1.U,
    BRANCH -> 1.U,
    JALR -> 1.U,
    JAL -> 1.U,
    OP_IMM -> 1.U,
    AUIPC -> 1.U,
  ))
  io.E_ASel := E_ASel_w    // To Be Modified
  io.E_BSel := E_BSel_w  // To Be Modified

  io.E_ALUSel := MuxLookup(EXE_opcode, (Cat(0.U(7.W), "b11111".U, 0.U(3.W))), Seq(
    OP -> Cat(EXE_funct7, "b11111".U, EXE_funct3),
    OP_IMM -> MuxLookup(EXE_funct3, Cat(0.U(7.W), "b11111".U, EXE_funct3), Seq(
      "b101".U -> Cat(EXE_funct7, "b11111".U, EXE_funct3),
      "b001".U -> Mux(EXE_funct7 === "b0110000".U, Cat(EXE_funct7, io.EXE_Inst(24,20), EXE_funct3), Cat(EXE_funct7, "b11111".U, EXE_funct3)),
    )),
  )) // To Be Modified

  // Control signal - Data Memory
  io.DM_Mem_R := (MEM_opcode === LOAD)
  io.DM_Mem_W := (MEM_opcode === STORE)
  io.DM_Length := Cat(0.U(1.W),MEM_funct3) // length

  // Control signal - Inst Memory
  io.IM_Mem_R := true.B // always true
  io.IM_Mem_W := false.B // always false
  io.IM_Length := "b0010".U // always load a word(inst)

  // Control signal - Scalar Write Back
  io.W_RegWEn := MuxLookup(WB_opcode, false.B, Seq(
    OP_IMM -> true.B,
    LOAD -> true.B,
    LUI -> true.B,
    OP -> true.B,
    JAL -> true.B,
    JALR -> true.B,
    AUIPC -> true.B,
  ))  // To Be Modified


  io.W_WBSel := MuxLookup(WB_opcode, ALUOUT, Seq(
    LOAD -> LD_DATA,
    JAL -> PC_PLUS_4,
    JALR -> PC_PLUS_4,
  )) // To Be Modified

  // Control signal - Others
  io.Hcf := (IF_opcode === HCF)

  /****************** Data Hazard ******************/

  // Control signal - Data Forwarding (Bonus)
  val ID_use_rs1 = MuxLookup(ID_opcode, false.B, Seq(
    LOAD -> true.B,
    STORE -> true.B,
    BRANCH -> true.B,
    JALR -> true.B,
    OP_IMM -> true.B,
    OP -> true.B,
  ))
  val ID_use_rs2 = MuxLookup(ID_opcode, false.B, Seq(
    STORE -> true.B,
    BRANCH -> true.B,
    OP -> true.B,
  ))
  val ld_HD_rs1 = ID_use_rs1 && ID_rs1 === EXE_rd
  val ld_HD_rs2 = ID_use_rs2 && ID_rs2 === EXE_rd

  io.Stall_DH := (ld_HD_rs1 || ld_HD_rs2) && EXE_opcode === LOAD && EXE_rd =/= 0.U // Stall for Data Hazard
  io.Stall_MA := false.B // Stall for Waiting Memory Access
  // Control signal - Flush
  io.Flush_DH := (ld_HD_rs1 || ld_HD_rs2) && EXE_opcode === LOAD && EXE_rd =/= 0.U
  io.Flush_BH := Predict_Miss

  // sel
  val EXE_use_rd = MuxLookup(EXE_opcode, false.B, Seq(
    // LOAD -> true.B,  need to stall
    JALR -> true.B,
    JAL -> true.B,
    OP_IMM -> true.B,
    OP -> true.B,
    AUIPC -> true.B,
    LUI -> true.B,
  ))
  val MEM_use_rd = MuxLookup(MEM_opcode, false.B, Seq(
    LOAD -> true.B,
    JALR -> true.B,
    JAL -> true.B,
    OP_IMM -> true.B,
    OP -> true.B,
    AUIPC -> true.B,
    LUI -> true.B,
  ))
  val WB_use_rd = MuxLookup(WB_opcode, false.B, Seq(
    LOAD -> true.B,
    JALR -> true.B,
    JAL -> true.B,
    OP_IMM -> true.B,
    OP -> true.B,
    AUIPC -> true.B,
    LUI -> true.B,
  ))

  val en_AFor_EXE = Wire(Bool())
  val en_AFor_MEM = Wire(Bool())
  val en_AFor_WB = Wire(Bool())
  val en_BFor_EXE = Wire(Bool())
  val en_BFor_MEM = Wire(Bool())
  val en_BFor_WB = Wire(Bool())

  // check data dependency between ID & EXE & MEM & WB
  when(EXE_use_rd){
    en_AFor_EXE := ID_rs1 === EXE_rd && EXE_rd =/= 0.U
    en_BFor_EXE := ID_rs2 === EXE_rd && EXE_rd =/= 0.U
  }.otherwise{
    en_AFor_EXE := false.B
    en_BFor_EXE := false.B
  }
  when(MEM_use_rd){
    en_AFor_MEM := ID_rs1 === MEM_rd && MEM_rd =/= 0.U
    en_BFor_MEM := ID_rs2 === MEM_rd && MEM_rd =/= 0.U
  }.otherwise{
    en_AFor_MEM := false.B
    en_BFor_MEM := false.B
  }
  when(WB_use_rd){
    en_AFor_WB := ID_rs1 === WB_rd && WB_rd =/= 0.U
    en_BFor_WB := ID_rs2 === WB_rd && WB_rd =/= 0.U
  }.otherwise{
    en_AFor_WB := false.B
    en_BFor_WB := false.B
  }

  // select alu operants
  when(en_AFor_EXE){
    io.For_ASel := Mux(EXE_opcode === JAL || EXE_opcode === JALR, EXE_PC, EXE_ALU)
  }.elsewhen(en_AFor_MEM){
    io.For_ASel := MuxLookup(MEM_opcode, MEM_ALU, Seq(
      JAL -> MEM_PC,
      JALR -> MEM_PC,
      LOAD -> MEM_LD,
    ))
  }.elsewhen(en_AFor_WB){
    io.For_ASel := WB
  }.otherwise{
    io.For_ASel := ORG
  }
  
  when(en_BFor_EXE){
    io.For_BSel := Mux(EXE_opcode === JAL || EXE_opcode === JALR, EXE_PC, EXE_ALU)
  }.elsewhen(en_BFor_MEM){
    io.For_BSel := MuxLookup(MEM_opcode, MEM_ALU, Seq(
      JAL -> MEM_PC,
      JALR -> MEM_PC,
      LOAD -> MEM_LD,
    ))
  }.elsewhen(en_BFor_WB){
    io.For_BSel := WB
  }.otherwise{
    io.For_BSel := ORG
  }
  
  /****************** Data Hazard End******************/


}
