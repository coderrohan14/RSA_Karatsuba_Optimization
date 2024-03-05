package RSA_Karatsuba

import chisel3._
import chisel3.util._

import scala.util.Random

case class RSAParams(keySize: Int)

class RSA(val p: RSAParams) extends Module {
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

  // Function to perform Karatsuba multiplication
  def karatsubaMultiply(a: UInt, b: UInt, n: UInt): UInt = {
    val m = (n + 1.U) / 2.U
    val (a0: UInt, a1: UInt) = (a >> m, a % (1.U << m).asUInt)
    val (b0: UInt, b1: UInt) = (b >> m, b % (1.U << m).asUInt)
    val z0 = a0 * b0
    val z2 = a1 * b1
    val z1: UInt = (a0 + a1) * (b0 + b1) - z0 - z2
    val c: UInt = (z1.asUInt << m.asUInt).asUInt
    (z0 << (2.U * m)).asUInt + c + z2
  }

//  def generateRandomPrime(): UInt = {
//    val bits = p.keySize / 2
//    val prime = UInt(bits.W)
//
//    // Generate a random odd number with the specified number of bits
//    println(s"bits = $bits\n")
//    val randomNum = Random.nextInt((1 << bits) - 1)
//    prime := randomNum.U
//
//    // Ensure the generated number is prime
//    while (!isPrime(prime)) {
//      prime := prime + 2.U // Increment by 2 to keep it odd
//    }
//
//    prime
//  }

//  def isPrime(num: UInt): Boolean = {
//    // Basic primality testing (you may want to use a more sophisticated method)
//    val isPrime = Wire(Bool())
//    isPrime := true.B
//
//    // Check divisibility by odd numbers up to the square root of num
//    for (i <- 3 until math.sqrt(1 << p.keySize).toInt by 2) {
//      when(num % i.U === 0.U) {
//        isPrime := false.B
//      }
//    }
//
//    isPrime==true.B
//  }

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

  def modPow(base: UInt, exponent: UInt, modulus: UInt): UInt = {
    var result = 1.U
    var baseExp = base
    var exp = exponent

    while ((exp > 0.U) == true.B) {
      when ((exp & 1.U) === 1.U) {
        result = (result * baseExp) % modulus
      }

      baseExp = (baseExp * baseExp) % modulus
      exp = (exp >> 1).asUInt
    }

    result
  }

  val n = Reg(UInt(p.keySize.W))
  val phiN = Reg(UInt(p.keySize.W))
  val e = Reg(UInt(p.keySize.W))
  val d = Reg(UInt(p.keySize.W))
  val encryptedData = Reg(UInt(32.W))
  val decryptedData = Reg(UInt(32.W))

  def generateKeys() = {
    val p1 = io.primeNum1
    val p2 = io.primeNum2

    n := karatsubaMultiply(p1, p2, p.keySize.U)

    phiN := karatsubaMultiply((p1-1.U), (p2-1.U), p.keySize.U)

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
    encryptedData := modPow(io.message, e, n)
  }

  def decrypt() = {
    // Decryption
    decryptedData := modPow(encryptedData, d, n)
  }

  io.publicKeyN := n
  io.publicKeyE := e
  io.privateKeyD := d
  io.encrypted := encryptedData
  io.decrypted := decryptedData
}