package RSA_Karatsuba

import scala.sys.process._

case class RSAParams(keySize: Int)

class RSAModel(params: RSAParams) {
  // Placeholder for public and private keys
  private var publicKey: (BigInt, BigInt) = _ // (n,e)
  private var privateKey: (BigInt, BigInt) = _ // (n,d)
  private var n: BigInt = _
  private var p: BigInt = _
  private var q: BigInt = _
  private var e: BigInt = BigInt(2)
  private var d: BigInt = _
  private var phiN: BigInt = _

  // Generate public and private keys
  def generatePublicKey(): Unit = {
    p = getRandomPrimeNumber()
    q = getRandomPrimeNumber()

    n = karatsubaMultiply(p, q)
    phiN = karatsubaMultiply(p-1, q-1)

    var foundE = false

    while (e < phiN  && !foundE) {
      // e must be co-prime to phi and smaller than phi.
      if (gcd(e, phiN) == 1)
        foundE = true
      else
        e += 1
    }
    publicKey = (n, e)
  }

  def generatePrivateKey(): Unit = {
    d = e.modInverse(phiN)
    privateKey = (n,d)
  }

  private def gcd(a: BigInt, b: BigInt): BigInt = {
    if (b == 0) a else gcd(b, a % b)
  }

  private def getRandomPrimeNumber(): BigInt = {
    val bitLength = params.keySize / 2 // Adjust the desired bit length

    // Generate a large random prime using OpenSSL
    val primeCommand = s"openssl prime -generate -bits $bitLength"
    val result = primeCommand.!!

    // Parse the result to extract the generated prime
    BigInt(result.trim)
  }



  def getPublicKey: (BigInt, BigInt) = publicKey

  def getPrivateKey: (BigInt, BigInt) = privateKey

  // Encrypt a message using RSA
  def encrypt(message: BigInt): BigInt = {
    message.modPow(publicKey._2, publicKey._1)
  }

  // Decrypt a message using RSA
  def decrypt(ciphertext: BigInt): BigInt = {
    ciphertext.modPow(privateKey._2, privateKey._1)
  }

  // Perform multiplication using Karatsuba algorithm
  private def karatsubaMultiply(a: BigInt, b: BigInt): BigInt = {
    val n = a.bitLength.max(b.bitLength)
    if (n <= 64) {
      // Use regular multiplication for small numbers
      a * b
    } else {
      val m = (n + 1) / 2
      val (a0, a1) = (a >> m, a % (BigInt(1) << m))
      val (b0, b1) = (b >> m, b % (BigInt(1) << m))
      val z0 = karatsubaMultiply(a0, b0)
      val z2 = karatsubaMultiply(a1, b1)
      val z1 = karatsubaMultiply((a0 + a1), (b0 + b1)) - z0 - z2
      (z0 << (2 * m)) + (z1 << m) + z2
    }
  }

  // Public method to perform Karatsuba multiplication
  def multiplyWithKaratsuba(a: BigInt, b: BigInt): BigInt = {
    karatsubaMultiply(a, b)
  }
}