package RSA_Karatsuba

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class RSAModelTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "RSAWithKaratsubaModel"

  it should "correctly generate keys" in {
    test(new RSAModel(RSAParams(1024))) { c =>
      c.generateKeys()

      val publicKey = c.getPublicKey
      val privateKey = c.getPrivateKey

      // Add assertions to validate key generation
       assert(publicKey.bitLength == 1024)
       assert(privateKey.bitLength == 1024)
    }
  }

  it should "correctly encrypt and decrypt a message" in {
    test(new RSAModel(RSAParams(1024))) { c =>
      c.generateKeys()

      val message = BigInt("1234567890")
      val ciphertext = c.encrypt(message)
      val decryptedMessage = c.decrypt(ciphertext)

      // Add assertions to validate encryption and decryption
      // For example:
       assert(ciphertext != message)
       assert(decryptedMessage == message)
    }
  }

  it should "correctly perform Karatsuba multiplication" in {
    test(new RSAModel(RSAParams(1024))) { c =>
      // Example numbers for multiplication
      val a = BigInt("12345678901234567890")
      val b = BigInt("98765432109876543210")

      val result = c.multiplyWithKaratsuba(a, b)

      // Add assertions to validate Karatsuba multiplication
      // For example:
       assert(result == (a * b))
    }
  }

  // Add more test cases as needed for different scenarios
}
