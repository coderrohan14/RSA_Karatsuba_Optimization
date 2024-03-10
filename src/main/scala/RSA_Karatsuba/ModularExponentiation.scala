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
  val kar_done = RegInit(0.U) // Flag to indicate Karatsuba completion
  val karatsuba_A = RegInit(1.U(p.keySize.W))
  val karatsuba_B = RegInit(1.U(p.keySize.W))

  val sIdle :: sCompute :: sKaratsuba :: sFinished :: Nil = Enum(4)
  val state = RegInit(sIdle)

  val karatsuba = Module(new KaratsubaMultiplication(p))


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
        when((e % 2.U) =/= 0.U) {
          // Start Karatsuba multiplication
          karatsuba_A := res
          karatsuba_B := b
          state := sKaratsuba
          kar_done := 1.U
        }.otherwise {
          karatsuba_A := b
          karatsuba_B := b
          state := sKaratsuba
          kar_done := 2.U
        }
      }.otherwise {
        done := true.B
        state := sFinished
      }
    }
    is(sKaratsuba) {
      when(kar_done === 1.U) { // Wait for Karatsuba completion
        println(s"Odd\n")
        res := karatsuba.io.result % io.modulus
        e := e - 1.U
      }.elsewhen(kar_done === 2.U){
        println(s"Even\n")
        b := karatsuba.io.result % io.modulus
        e := e >> 1
      }
      kar_done := 0.U
      state := sCompute
    }
    is(sFinished) {
      when(!io.start) {
        done := false.B
        state := sIdle
      }
    }
  }
  karatsuba.io.a := karatsuba_A
  karatsuba.io.b := karatsuba_B
  io.result := res
  io.done := done
}
