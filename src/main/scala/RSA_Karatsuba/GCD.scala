package RSA_Karatsuba

import chisel3._
import chisel3.util._

class GcdIO(p: RSAParams) extends Bundle {
  val a = Input(UInt(p.keySize.W))
  val b = Input(UInt(p.keySize.W))
  val result = Output(UInt(p.keySize.W))
  val start = Input(Bool())
  val done = Output(Bool())
}

class GCD(p: RSAParams) extends Module {
  val io = IO(new GcdIO(p))

  val a = Reg(UInt(p.keySize.W))
  val b = Reg(UInt(p.keySize.W))
  val result = Reg(UInt(p.keySize.W))
  val done = Reg(Bool())

  val sIdle :: sCompute :: sFinished :: Nil = Enum(3)
  val state = RegInit(sIdle)

  switch(state) {
    is(sIdle) {
      when(io.start) {
        a := io.a
        b := io.b
        state := sCompute
      }
    }
    is(sCompute) {
      val temp = a % b
      a := b
      b := temp
      when(b === 0.U) {
        result := a
        done := true.B
        state := sFinished
      }
    }
    is(sFinished) {
      when(!io.start) {
        done := false.B
        state := sIdle
      }
    }
  }

  io.result := result
  io.done := done
}