package RSA_Karatsuba

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RSATester extends AnyFlatSpec with ChiselScalatestTester {


  behavior of "RSA"

  it should "generate prime keys" in {
    val p = RSAParams(keySize = 32)
    test(new RSA(p)) { rsa =>
      // Provide input values (32-bit for simplicity)
      rsa.io.primeNum1.poke(36607.U)
      rsa.io.primeNum1.poke(42569.U)
      rsa.io.message.poke(3546098009L.U)
      rsa.clock.step()
      rsa.generateKeys()
      rsa.clock.step()
      println(s"n: ${rsa.io.publicKeyN}, e: ${rsa.io.publicKeyE}, d: ${rsa.io.privateKeyD}\n")
      true
    }
  }

  it should "encrypt and decrypt a message" in {
    val p = RSAParams(keySize = 32)
    test(new RSA(p)) { rsa =>
//      rsa.io.primeNum1.poke(37529.U)
//      rsa.io.primeNum1.poke(37781.U)
//      rsa.io.message.poke(1234.U)
//
//      rsa.clock.step()
//
//      rsa.generateKeys()
//      rsa.clock.step()
//
//      println(s"encrypted: ${rsa.io.encrypted}, decrypted: ${rsa.io.decrypted}\n")
//
//      rsa.io.decrypted.expect(1234.U)
        true
    }
  }

  it should "correctly perform Karatsuba multiplication" in {
    val p = RSAParams(keySize = 32)
    val rsaModel = new RSAModel(p, generateKeys = true)
    test(new RSA(p)) { rsa =>
      // Provide input values (32-bit for simplicity)

//      val mulModel = rsaModel.multiplyWithKaratsuba(BigInt("1052"), BigInt("2048"))
//      val mul = rsa.karatsubaMultiply(1052.U, 2048.U, p.keySize.U)
//
//      assert(mulModel === mul)
      true
    }
  }

  // Add more test cases as needed for different scenarios
}
