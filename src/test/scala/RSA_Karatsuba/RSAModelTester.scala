package RSA_Karatsuba


import chiseltest.ChiselScalatestTester
import org.scalatest.flatspec.AnyFlatSpec

class RSAModelTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "RSAModel"

  it should "correctly generate keys" in {
    val rsaModel = new RSAModel(RSAParams(2048), generateKeys = true)

    val publicKey = rsaModel.getPublicKey

    // Add assertions to validate key generation
    assert(publicKey._1.bitLength == 2048)
  }

  it should "correctly encrypt and decrypt a message" in {
    val rsaModel = new RSAModel(RSAParams(2048), generateKeys = true)

    val message = BigInt("1234567890")
    val ciphertext = rsaModel.encrypt(message)
    val decryptedMessage = rsaModel.decrypt(ciphertext)

    // Add assertions to validate encryption and decryption
    assert(ciphertext !== message)
    assert(decryptedMessage === message)
  }

  it should "correctly perform Karatsuba multiplication" in {
    val rsaModel = new RSAModel(RSAParams(1024), generateKeys = false)

    // Example numbers for multiplication
    val a = BigInt("12345678901234567890")
    val b = BigInt("98765432109876543210")

    val result = rsaModel.multiplyWithKaratsuba(a, b)

    // Add assertions to validate Karatsuba multiplication
    assert(result == (a*b))
  }

  // Add more test cases as needed for different scenarios
}
