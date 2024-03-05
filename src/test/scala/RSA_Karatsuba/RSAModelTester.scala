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

    val message = BigInt("1234567890868758454563436656546546776776675765757657657657657657576546745456")
    val ciphertext = rsaModel.encrypt(message)
    val decryptedMessage = rsaModel.decrypt(ciphertext)

    // Add assertions to validate encryption and decryption
    assert(ciphertext !== message)
    assert(decryptedMessage === message)
  }

  it should "correctly perform Karatsuba multiplication" in {
    val rsaModel = new RSAModel(RSAParams(1024), generateKeys = false)

    // Example numbers for multiplication
    val a = BigInt("1234567890123456789068768768757646546535424553564765876869869698696786767565656445443323232")
    val b = BigInt("9876543210987654321009986756645634434332321212113234345356456756567576786788979879798789786")

    val result = rsaModel.multiplyWithKaratsuba(a, b)

    // Add assertions to validate Karatsuba multiplication
    assert(result == (a*b))
  }

  // Add more test cases as needed for different scenarios
}
