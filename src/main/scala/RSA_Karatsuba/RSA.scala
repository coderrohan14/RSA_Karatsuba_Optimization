package RSA_Karatsuba

import chisel3._
import chisel3.util._


case class RSAParams(keySize: Int, karatsubaThreshold: UInt = 32.U)

object RSAState extends ChiselEnum {
  val sIdle, sGenPublicKey, sGenPrivateKey, sEncrypt, sDecrypt, sModPow,  sGCD, sModInverse, sKaratsuba, sFinished = Value
}

class RSAIO(p: RSAParams) extends Bundle{

  val primeNum1 = Input(UInt((p.keySize/2).W))
  val primeNum2 = Input(UInt((p.keySize/2).W))
  val message = Input(UInt(p.keySize.W))
  val start = Input(Bool())

  // Output ports
  val encrypted = Output(UInt(p.keySize.W))
  val decrypted = Output(UInt(p.keySize.W))
  val publicKeyN = Output(UInt(p.keySize.W))
  val publicKeyE = Output(UInt(p.keySize.W))
  val privateKeyD = Output(UInt(p.keySize.W))
  val done = Output(Bool())

  val karatsuba = Module(new KaratsubaMultiplication(p: RSAParams))
  val modPow = Module(new ModularExponentiation(p: RSAParams))
  val gcd = Module(new GCD(p: RSAParams))
  val modInverse = Module(new ModularInverse(p: RSAParams))
}

class RSA(p: RSAParams) extends Module {

  val rsaIO = IO(new RSAIO(p))

  val state = RegInit(RSAState.sIdle)

  // Registers to hold the outputs

  val primeNum1Reg = Reg(UInt((p.keySize/2).W))
  val primeNum2Reg = Reg(UInt((p.keySize/2).W))
  val messageReg = Reg(UInt(p.keySize.W))
  val encryptedReg = Reg(UInt(p.keySize.W))
  val decryptedReg = Reg(UInt(p.keySize.W))
  val phiNReg = Reg(UInt(p.keySize.W))
  val pubKeyNReg = Reg(UInt(p.keySize.W))
  val pubKeyEReg = Reg(UInt(p.keySize.W))
  val privKeyDReg = Reg(UInt(p.keySize.W))
  val doneReg = RegInit(false.B)
  val foundE = Reg(Bool())

  // Registers to specify the source of the caller of module
  val karatsubaMode = Reg(UInt(4.W))
  val modPowMode = Reg(UInt(4.W))

  // Initialize module ports...

  val karatsubaA = Reg(UInt(p.keySize.W))
  val karatsubaB = Reg(UInt(p.keySize.W))

  val modPowBase = Reg(UInt(p.keySize.W))
  val modPowExp = Reg(UInt(p.keySize.W))
  val modPowModulus = Reg(UInt(p.keySize.W))
  val modPowStart = RegInit(false.B)

  val gcdA = Reg(UInt(p.keySize.W))
  val gcdB = Reg(UInt(p.keySize.W))
  val gcdStart = RegInit(false.B)

  val modInverseA = Reg(UInt(p.keySize.W))
  val modInverseM = Reg(UInt(p.keySize.W))
  val modInverseStart = RegInit(false.B)

  switch(state){
    is(RSAState.sIdle){
      when(rsaIO.start){
        primeNum1Reg := rsaIO.primeNum1
        primeNum2Reg := rsaIO.primeNum2
        messageReg := rsaIO.message
        doneReg := false.B
        karatsubaMode := 0.U
        modPowMode := 0.U
        karatsubaA := 1.U
        karatsubaB := 1.U
        state := RSAState.sGenPublicKey
      }
    }

    is(RSAState.sGenPublicKey){
      when(karatsubaMode === 0.U) {
        karatsubaA := primeNum1Reg
        karatsubaB := primeNum2Reg
        karatsubaMode := 1.U
        state := RSAState.sKaratsuba
      }.otherwise{
       when((pubKeyEReg < phiNReg) && foundE === false.B){
         gcdA := pubKeyEReg
         gcdB := phiNReg
         gcdStart := true.B
         state := RSAState.sGCD
       }.otherwise{
         state := RSAState.sGenPrivateKey
       }
      }
    }

    is(RSAState.sGenPrivateKey){
      // d -> modInverse(e, phiN)
      modInverseA := pubKeyEReg
      modInverseM := phiNReg
      modInverseStart := true.B
      state := RSAState.sModInverse
    }

    is(RSAState.sEncrypt){
      // Cipher value -> (M^e) % n
      modPowBase := messageReg
      modPowExp := pubKeyEReg
      modPowModulus := pubKeyNReg
      modPowMode := 1.U
      modPowStart := true.B
      state := RSAState.sModPow
    }

    is(RSAState.sDecrypt){
      // Decrypted value -> (C^d) % n
      modPowBase := encryptedReg
      modPowExp := privKeyDReg
      modPowModulus := pubKeyNReg
      modPowMode := 2.U
      modPowStart := true.B
      state := RSAState.sModPow
    }

    is(RSAState.sKaratsuba){
      switch(karatsubaMode){
        is(1.U){
          // for calculating the value of N -> P * Q
          pubKeyNReg := rsaIO.karatsuba.io.result
          karatsubaA := primeNum1Reg - 1.U
          karatsubaB := primeNum2Reg - 1.U
          karatsubaMode := 2.U
        }
        is(2.U){
          // for calculating the value of phiN -> (P-1) * (Q-1)
          phiNReg := rsaIO.karatsuba.io.result
          pubKeyEReg := 2.U
          foundE := false.B
          state := RSAState.sGenPublicKey
        }
      }
    }

    is(RSAState.sGCD){
      when(rsaIO.gcd.io.done){
        when(rsaIO.gcd.io.result === 1.U){
          foundE := true.B
        }.otherwise{
          pubKeyEReg := pubKeyEReg + 1.U
        }
        gcdStart := false.B
        state := RSAState.sGenPublicKey
      }
    }

    is(RSAState.sModInverse){
      when(rsaIO.modInverse.io.done){
        privKeyDReg := rsaIO.modInverse.io.result
        modInverseStart := false.B
        state := RSAState.sEncrypt
      }
    }

    is(RSAState.sModPow){
      when(rsaIO.modPow.io.done){
        switch(modPowMode){
          is(1.U){
            // called from encryption state
            encryptedReg := rsaIO.modPow.io.result
            modPowStart := false.B
            state := RSAState.sDecrypt
          }
          is(2.U){
            // called from decryption state
            decryptedReg := rsaIO.modPow.io.result
            modPowStart := false.B
            state := RSAState.sFinished
          }
        }
      }
    }

    is(RSAState.sFinished){
      doneReg := true.B
      state := RSAState.sIdle
    }
  }


  // Setting the IO ports back to the register values...

  rsaIO.karatsuba.io.a := karatsubaA
  rsaIO.karatsuba.io.b := karatsubaB

  rsaIO.modPow.io.base := modPowBase
  rsaIO.modPow.io.exp := modPowExp
  rsaIO.modPow.io.modulus := modPowModulus
  rsaIO.modPow.io.start := modPowStart

  rsaIO.modInverse.io.a := modInverseA
  rsaIO.modInverse.io.m := modInverseM
  rsaIO.modInverse.io.start := modInverseStart

  rsaIO.gcd.io.a := gcdA
  rsaIO.gcd.io.b := gcdB
  rsaIO.gcd.io.start := gcdStart

  rsaIO.encrypted := encryptedReg
  rsaIO.decrypted := decryptedReg
  rsaIO.publicKeyN := pubKeyNReg
  rsaIO.publicKeyE := pubKeyEReg
  rsaIO.privateKeyD := privKeyDReg
  rsaIO.done := doneReg
}