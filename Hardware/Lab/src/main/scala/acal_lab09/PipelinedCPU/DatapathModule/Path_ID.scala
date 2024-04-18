package acal_lab09.PiplinedCPU.DatapathModule

import chisel3._
import chisel3.util._
import acal_lab09.PiplinedCPU.DatapathModule.DatapathComponent._
import acal_lab09.PiplinedCPU.forwarding_sel_map._

class Path_ID(addrWidth:Int) extends Module {
    val io = IO(new Bundle{

        val ID_inst_in = Input(UInt(32.W))
        val WB_index = Input(UInt(5.W))
        val WB_wdata = Input(UInt(32.W))
        val WB_RegWEn = Input(Bool())
        val ImmSel = Input(UInt(3.W))
        
        val ID_rs1_rdata = Output(UInt(32.W))
        val ID_rs2_rdata = Output(UInt(32.W))
        val imm = Output(UInt(32.W))

        // forwarding signals
        val EXE_alu_out = Input(UInt(32.W))
        val EXE_pc_plus_4 = Input(UInt(addrWidth.W))
        val MEM_alu_out = Input(UInt(32.W))
        val MEM_ld_data = Input(UInt(32.W))
        val MEM_pc_plus_4 = Input(UInt(addrWidth.W))
        val For_ASel = Input(UInt(3.W))
        val For_BSel = Input(UInt(3.W))

        val regs = Output(Vec(32,UInt(32.W)))  // for monitor
    })
    // Inst Decode
    val rs1_index = io.ID_inst_in(19,15)
    val rs2_index = io.ID_inst_in(24,20)

    // Reg File Module
    val rf = Module(new RegFile(2))
    rf.io.wen := io.WB_RegWEn
    rf.io.waddr := io.WB_index
    rf.io.wdata := io.WB_wdata
    rf.io.raddr(0) := rs1_index
    rf.io.raddr(1) := rs2_index
    io.ID_rs1_rdata := MuxLookup(io.For_ASel, rf.io.rdata(0), Seq(
        ORG -> rf.io.rdata(0),
        EXE_ALU -> io.EXE_alu_out,
        EXE_PC -> io.EXE_pc_plus_4,
        MEM_ALU -> io.MEM_alu_out,
        MEM_PC -> io.MEM_pc_plus_4,
        MEM_LD -> io.MEM_ld_data,
        WB -> io.WB_wdata,
    ))
    io.ID_rs2_rdata := MuxLookup(io.For_BSel, rf.io.rdata(1), Seq(
        ORG -> rf.io.rdata(1),
        EXE_ALU -> io.EXE_alu_out,
        EXE_PC -> io.EXE_pc_plus_4,
        MEM_ALU -> io.MEM_alu_out,
        MEM_PC -> io.MEM_pc_plus_4,
        MEM_LD -> io.MEM_ld_data,
        WB -> io.WB_wdata,
    ))
    io.regs := rf.io.regs // for monitor

    // Imm Gen Module
    val ig = Module(new ImmGen)
    ig.io.ImmSel := io.ImmSel
    ig.io.inst_31_7 := io.ID_inst_in(31,7)
    io.imm := ig.io.imm
}
