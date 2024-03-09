package RSA_Karatsuba

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class GcdTester extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "GCD Calculator"

  it should "compute GCD of small numbers correctly" in {
    val p = RSAParams(keySize = 8)
    test(new GcdCalculator(p)) { dut =>
      dut.io.a.poke(12.U)
      dut.io.b.poke(16.U)
      dut.io.start.poke(true.B)

      var done = false
      while (!done) {
        dut.clock.step(1)
        done = dut.io.done.peek().litToBoolean
      }

      dut.io.result.expect(4.U)
    }
  }

  it should "compute GCD of large numbers correctly" in {
    val p = RSAParams(keySize = 1024)
    test(new GcdCalculator(p)) { dut =>
      dut.io.a.poke(BigInt("149640102534676830219290743493803996312479106175015598787184555575246063310871253685714450770284193194997345492931276689510069161878913757305452969990858824739239445312245342452985345751321501623429774685942305600740958844296753244233723760842537868742326794717952528369817276946509791360959169618146431583151").U)
      dut.io.b.poke(BigInt("138290492412987591233917717260904136494269796031292907217597107118830232777617141717130736506561423248817297520225304834085144362421963700707324819523593564802001094536766379206474466832612243878325611390414225595691304333067735840781233056242325165587018408399431266358020645016653639359034056249704476731651").U)
      dut.io.start.poke(true.B)

      var done = false
      while (!done) {
        dut.clock.step(1)
        done = dut.io.done.peek().litToBoolean
      }

      dut.io.result.expect(BigInt("1").U)
    }
  }
}