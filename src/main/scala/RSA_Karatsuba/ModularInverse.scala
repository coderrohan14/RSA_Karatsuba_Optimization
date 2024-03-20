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

  val karatsuba = Module(new KaratsubaMultiplication(p: RSAParams))
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

  val karatsubaA = Reg(UInt(p.keySize.W))
  val karatsubaB = Reg(UInt(p.keySize.W))

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
        karatsubaA := 1.U
        karatsubaB := 1.U
        doneReg := false.B
      }
    }

    is(ModInverseState.sCompute) {
      when(m0Reg === 1.U) {
        resultReg := 0.U
        doneReg := true.B
        state := ModInverseState.sFinished
      }.otherwise {
        when(AReg > 1.U){
          val q: UInt = AReg / MReg
          karatsubaA := q
          karatsubaB := yReg.asUInt
          state := ModInverseState.sKaratsuba
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

    is(ModInverseState.sKaratsuba){
      var t: SInt = MReg.asSInt
      val newM = AReg % MReg
      val newA = t
      t = yReg
      val res = io.karatsuba.io.result.asSInt
      yReg := xReg - res
      xReg := t
      MReg := newM
      AReg := newA.asUInt
      state := ModInverseState.sCompute
    }

    is(ModInverseState.sFinished) {
      doneReg := false.B
      state := ModInverseState.sIdle
    }
  }

  io.karatsuba.io.a := karatsubaA
  io.karatsuba.io.b := karatsubaB

  io.result := resultReg
  io.done := doneReg
}