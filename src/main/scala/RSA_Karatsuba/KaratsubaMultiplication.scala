package RSA_Karatsuba

import chisel3._

class KaratsubaMultiplication(p: RSAParams) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt((p.keySize/2).W))
    val b = Input(UInt((p.keySize/2).W))
    val result = Output(UInt(p.keySize.W))
  })

  val m = (p.keySize + 1).U / 2.U
  val a0: UInt = (io.a >> m).asUInt
  val a1: UInt = io.a % (1.U << m).asUInt
  val b0: UInt = (io.b >> m).asUInt
  val b1: UInt = io.b % (1.U << m).asUInt

  val z0: UInt = a0 * b0
  val z2: UInt = a1 * b1
  val z1: UInt = (a0 + a1) * (b0 + b1.asUInt) - z0 - z2
  val c: UInt = (z1 << m).asUInt

  io.result := (z0 << (2.U * m)).asUInt + c + z2
}

