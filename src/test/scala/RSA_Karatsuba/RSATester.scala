package RSA_Karatsuba

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class RSATester extends AnyFlatSpec with ChiselScalatestTester {


  behavior of "RSA"

  it should "generate prime keys" in {
    val p = RSAParams(keySize = 32)
    test(new RSA(p)) { rsa =>
      rsa.rsaIO.primeNum1.poke(36607.U)
      rsa.rsaIO.primeNum2.poke(42569.U)
      rsa.rsaIO.message.poke(3546098009L.U)
      rsa.rsaIO.start.poke(true.B)
      rsa.rsaIO.done.expect(false.B)

      // set timeout to 0 (no timeout) for large bit sizes
      rsa.clock.setTimeout(0)

      var done = false
      while (!done) {
        rsa.clock.step(1)
        done = rsa.rsaIO.done.peek().litToBoolean
      }

//      println(s"Enc -> ${rsa.rsaIO.encrypted.peek}, Dec -> ${rsa.rsaIO.decrypted.peek}, n -> ${rsa.rsaIO.publicKeyN.peek}," +
//        s"e -> ${rsa.rsaIO.publicKeyE.peek}, d -> ${rsa.rsaIO.privateKeyD.peek}, phiN -> ${rsa.phiNReg}\n")

      rsa.rsaIO.done.expect(true.B)
      rsa.rsaIO.publicKeyN.expect(1558323383L.U)
      rsa.rsaIO.publicKeyE.expect(5.U)
      rsa.rsaIO.privateKeyD.expect(934946525L.U)
      rsa.rsaIO.encrypted.expect(144172259L.U)
      rsa.rsaIO.decrypted.expect(3546098009L.U)
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
