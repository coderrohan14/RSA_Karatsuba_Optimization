/* Online Scala Compiler */
object HelloWorld {
  def main(args: Array[String]) {
    val model = new RSAModel(RSAParams(2048))
    //   val a = BigInt("153221898620232986177317217641280438070990731942460873927340020810859069474364542091355206345367017658483765786233461571736778814049286760330285729806310443271349452754281762965653936283024416193223140638209521282376576864415216543602107367744458252222914318889762102714618483121316325773692446676374178636309")
    //   val b = BigInt("149731925192973174013810553662533023170473061441534375095059210345995205654808267787706908463489051459220072210261003946131164675386433378218307257245936367153364667719847140452239046424177001864830905178868833297140666252380656912752348829182909001104371761699908991538673034766214996772083406625398771458889")
    val base = BigInt("138290492412987591233917717260904136494269796031292907217597107118830232777617141717130736506561423248817297520225304834085144362421963700707324819523593564802001094536766379206474466832612243878325611390414225595691304333067735840781233056242325165587018408399431266358020645016653639359034056249704476731651")
    val exp = BigInt("145752403387280946034796014844826135670335831523591076009242930028664926802244420092731873782398469495857150849917849072404849023375608092222433715177556457929397439981225185884754649436568233772361228114359244172284057723845170298839959519403284383141108534060621956356700377916677422107654326122370578492869")
    val mod = BigInt("149640102534676830219290743493803996312479106175015598787184555575246063310871253685714450770284193194997345492931276689510069161878913757305452969990858824739239445312245342452985345751321501623429774685942305600740958844296753244233723760842537868742326794717952528369817276946509791360959169618146431583151")
    //  println(model.getModPow(base, exp, mod))
    // println(model.getRandomPrimeNumber())
    val (g,x,y) = model.extendedGCD(10, 15)
    println(g, x, y)
  }
}


import scala.sys.process._


case class RSAParams(keySize: Int)

class RSAModel(params: RSAParams) {
  private var publicKey: (BigInt, BigInt) = _ // (n,e)
  private var privateKey: (BigInt, BigInt) = _ // (n,d)
  private var n: BigInt = _
  private var p: BigInt = _
  private var q: BigInt = _
  private var e: BigInt = BigInt(2)
  private var d: BigInt = _
  private var phiN: BigInt = _


  def getModPow(base: BigInt, exp: BigInt, mod: BigInt) = {
    base.modPow(exp, mod)
  }

  // Custom constructor that calls generatePublicKey and generatePrivateKey
  def this(params: RSAParams, generateKeys: Boolean) = {
    this(params)
    if (generateKeys) {
      generatePublicKey()
      generatePrivateKey()
    }
  }

  // Extended Euclidean Algorithm to find modular inverse
  def extendedGCD(a: Int, b: Int): (Int, Int, Int) = {
    if (b == 0) {
      (a, 1, 0)
    } else {
      val (d, x, y) = extendedGCD(b, a % b)
      (d, y, x - (a / b) * y)
    }
  }

  // Modular inverse function
  def modInverse(a: Int, m: Int): Int = {
    val (g, x, _) = extendedGCD(a, m)
    if (g == 1) {
      x + m // Ensure the result is positive
    }
    0
  }

  // Generate public and private keys
  private def generatePublicKey(): Unit = {
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

  private def generatePrivateKey(): Unit = {
    d = e.modInverse(phiN)
    privateKey = (n,d)
  }

  private def gcd(a: BigInt, b: BigInt): BigInt = {
    if (b == 0) a else gcd(b, a % b)
  }


  def getRandomPrimeNumber(): BigInt = {
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