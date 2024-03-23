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

object GCDState extends ChiselEnum {
  val sIdle, sCompute, sFinished = Value
}

class GCD(p: RSAParams) extends Module {
  val io = IO(new GcdIO(p))

  val a = Reg(UInt(p.keySize.W))
  val b = Reg(UInt(p.keySize.W))
  val result = Reg(UInt(p.keySize.W))
  val done = Reg(Bool())

  val state = RegInit(GCDState.sIdle)

  switch(state) {
    is(GCDState.sIdle) {
      when(io.start) {
        a := io.a
        b := io.b
        state := GCDState.sCompute
      }
    }
    is(GCDState.sCompute) {
      val temp = a % b
      a := b
      b := temp
      when(b === 0.U) {
        result := a
        done := true.B
        state := GCDState.sFinished
      }
    }
    is(GCDState.sFinished) {
      when(!io.start) {
        done := false.B
        state := GCDState.sIdle
      }
    }
  }

  io.result := result
  io.done := done
}