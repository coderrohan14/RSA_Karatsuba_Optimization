package RSA_Karatsuba

import chisel3._
import chisel3.util._

object ModInverseState extends ChiselEnum {
  val sIdle, sCompute, sKaratsuba, sFinished = Value
}

class ModularInverseIO(p: RSAParams) extends Bundle {
  val a = Input(UInt(p.keySize.W))
  val m = Input(UInt(p.keySize.W))
  val start = Input(Bool())
  val result = Output(UInt())
  val done = Output(Bool())
}

class ModularInverse(p: RSAParams) extends Module {
  val io = IO(new ModularInverseIO(p))

  val xReg = RegInit(1.S(p.keySize.W))
  val yReg = RegInit(0.S(p.keySize.W))
  val AReg = RegInit(0.U(p.keySize.W))
  val MReg = RegInit(0.U(p.keySize.W))
  val m0Reg = RegInit(0.U(p.keySize.W))
  val resultReg = RegInit(0.U(p.keySize.W))
  val doneReg = RegInit(false.B)

  val state = RegInit(ModInverseState.sIdle)



  switch(state) {
    is(ModInverseState.sIdle){
      when(io.start) {
        state := ModInverseState.sCompute
        AReg := io.a
        MReg := io.m
        xReg := 1.S
        yReg := 0.S
        m0Reg := io.m
        doneReg := false.B
      }
    }
    is(ModInverseState.sCompute) {
      when(m0Reg === 1.U) {
        resultReg := 0.U
        doneReg := true.B
        state := ModInverseState.sFinished
      }.otherwise {
//        printf("A-> %x, M -> %x, x -> %x, y -> %x, res -> %x\n", AReg, MReg, xReg, yReg, resultReg)
        when(AReg > 1.U){
          val q: UInt = AReg / MReg
          var t: SInt = MReg.asSInt
          val newM = AReg % MReg
          val newA = t
          t = yReg
          yReg := xReg - (q*yReg)
          xReg := t
          MReg := newM
          AReg := newA.asUInt
        }.otherwise {
          when(xReg < 0.S){
            resultReg := (xReg + m0Reg.asSInt).asUInt
          }.otherwise{
            resultReg := xReg.asUInt
          }
          doneReg := true.B
          state := ModInverseState.sFinished
        }
      }
    }
    is(ModInverseState.sFinished) {
      doneReg := false.B
      state := ModInverseState.sIdle
    }
  }

  io.result := resultReg
  io.done := doneReg
}