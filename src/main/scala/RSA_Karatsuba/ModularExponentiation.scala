package RSA_Karatsuba

import chisel3._
import chisel3.util._

class ModularExponentiationIO(p: RSAParams) extends Bundle {
  val base = Input(UInt(p.keySize.W))
  val exp = Input(UInt(p.keySize.W))
  val modulus = Input(UInt(p.keySize.W))
  val result = Output(UInt(p.keySize.W))
  val start = Input(Bool())
  val done = Output(Bool())
}

class ModularExponentiation(p: RSAParams) extends Module {
  val io = IO(new ModularExponentiationIO(p))

  val res = Reg(UInt(p.keySize.W))
  val b = Reg(UInt(p.keySize.W))
  val e = Reg(UInt(p.keySize.W))
  val done = Reg(Bool())

  val sIdle :: sCompute :: sFinished :: Nil = Enum(3)
  val state = RegInit(sIdle)

  switch(state) {
    is(sIdle) {
      when(io.start) {
        res := 1.U
        b := io.base % io.modulus
        e := io.exp
        state := sCompute
      }
    }
    is(sCompute) {
      when(e > 0.U) {
        when(e(0) === 1.U) {
          res := (res * b) % io.modulus
        }
        b := (b * b) % io.modulus
        e := e >> 1
      }.otherwise {
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

  io.result := res
  io.done := done
}