package RSA_Karatsuba

import chisel3._

class ModularExponentiation(p: RSAParams) extends Module {
  val io = IO(new Bundle {
    val base = Input(UInt(p.keySize.W))
    val exponent = Input(UInt(p.keySize.W))
    val modulus = Input(UInt(p.keySize.W))
    val result = Output(UInt(p.keySize.W))
  })

    var result = RegInit(1.U)
    var baseExp = RegInit(io.base)
    var exp = RegInit(io.exponent)

    while ((exp > 0.U) == true.B) {
      when ((exp & 1.U) === 1.U) {
        result := (result * baseExp) % io.modulus
      }

      baseExp := (baseExp * baseExp) % io.modulus
      exp := (exp >> 1).asUInt
    }


  io.result :=result
}
