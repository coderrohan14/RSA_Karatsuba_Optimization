package RSA_Karatsuba

import chisel3._
import chisel3.util._


case class RSAParams(keySize: Int)


class RSAIO(p: RSAParams) extends Module{
  val io = IO(new Bundle {
    // Input ports
    val primeNum1 = Input(UInt((p.keySize/2).W))
    val primeNum2 = Input(UInt((p.keySize/2).W))
    val message = Input(UInt(p.keySize.W))

    // Output ports
    val encrypted = Output(UInt(p.keySize.W))
    val decrypted = Output(UInt(p.keySize.W))
    val publicKeyN = Output(UInt(p.keySize.W))
    val publicKeyE = Output(UInt(p.keySize.W))
    val privateKeyD = Output(UInt(p.keySize.W))
  })

  val karatsuba = Module(new KaratsubaMultiplication(p: RSAParams))
  val modPow = Module(new ModularExponentiation(p: RSAParams))
  val gcd = Module(new GCD(p: RSAParams))


  karatsuba.io.a := 1.U
  karatsuba.io.b := 1.U

  modPow.io.base := 1.U
  modPow.io.exp := 1.U
  modPow.io.modulus := 1.U
}

class RSA(p: RSAParams) extends RSAIO(p) {


  def gcd(a: UInt, b: UInt): UInt = {
    if (b == 0.U) a else gcd(b, a % b)
  }

  // Extended Euclidean Algorithm to find modular inverse
  def extendedGCD(a: UInt, b: UInt): (UInt, UInt, UInt) = {
    if (b == 0.U) {
      (a, 1.U, 0.U)
    } else {
      val (d, x, y) = extendedGCD(b, a % b)
      (d, y, x - (a / b) * y)
    }
  }

  // Modular inverse function
  def modInverse(a: UInt, m: UInt): UInt = {
    val (g, x, _) = extendedGCD(a, m)
    when (g === 1.U) {
      x + m // Ensure the result is positive
    }
    0.U
  }


  val n = Reg(UInt(p.keySize.W))
  val phiN = Reg(UInt(p.keySize.W))
  val e = Reg(UInt(p.keySize.W))
  val d = Reg(UInt(p.keySize.W))
  val encryptedData = Reg(UInt(p.keySize.W))
  val decryptedData = Reg(UInt(p.keySize.W))

  def generateKeys() = {
    val p1 = io.primeNum1
    val p2 = io.primeNum2

    karatsuba.io.a := p1
    karatsuba.io.b := p2
    n := karatsuba.io.result

    karatsuba.io.a := p1-1.U
    karatsuba.io.b := p2-1.U
    phiN := karatsuba.io.result

    e := 2.U

    var foundE: Boolean = false

    while ((e < phiN  && !foundE.B) == true.B) {
      // e must be co-prime to phi and smaller than phi.
      if (gcd(e, phiN) == 1.U)
        foundE = true
      else
        e := e + 1.U
    }

    d := modInverse(e, phiN)
  }



  def encrypt() = {
    // Encryption
    modPow.io.base := io.message
    modPow.io.exp := e
    modPow.io.modulus := n
    encryptedData := modPow.io.result
  }

  def decrypt() = {
    // Decryption
    modPow.io.base := encryptedData
    modPow.io.exp := d
    modPow.io.modulus := n
    decryptedData := modPow.io.result
  }

  io.publicKeyN := n
  io.publicKeyE := e
  io.privateKeyD := d
  io.encrypted := encryptedData
  io.decrypted := decryptedData
}