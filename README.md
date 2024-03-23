# RSA Karatsuba Optimization Project

## Overview

The RSA Karatsuba Optimization project implements the RSA encryption and decryption algorithm, optimized with Karatsuba multiplication for efficient handling of large integers. Developed in Chisel, a hardware design language based on Scala, it aims to create efficient, synthesizable hardware circuits.

## Key Components

- **RSA Algorithm**: Implements RSA encryption and decryption.
- **Karatsuba Multiplication**: Efficiently multiplies large numbers, enhancing RSA operations.
- **Modular Exponentiation**: Core operation in RSA encryption and decryption.
- **Greatest Common Divisor (GCD)**: Used in RSA key generation for finding the modular inverse.
- **Modular Inverse**: Calculates the modular inverse of a number, essential for generating the private key in RSA.

## Project Structure

Organized into `src/main/scala/RSA_Karatsuba/` for source code and `src/test/scala/RSA_Karatsuba/` for tests.

### Main Directory

- `RSA.scala`: Main RSA module integrating all RSA algorithm components.
- `KaratsubaMultiplication.scala`: Implements Karatsuba algorithm for large integer multiplication.
- `ModularExponentiation.scala`: Handles modular exponentiation, crucial for RSA.
- `GCD.scala`: Calculates the greatest common divisor, aiding in RSA key generation.
- `ModularInverse.scala`: Computes the modular inverse, vital for RSA private key generation.

### Test Directory

- `RSATester.scala`: Tests RSA module functionality, including encryption and decryption.
- `KaratsubaTester.scala`: Verifies Karatsuba multiplication correctness.
- `ModularExponentiationTester.scala`: Ensures modular exponentiation component's functionality.
- `GCDTester.scala`: Checks GCD calculation functionality.
- `RSAModelTester.scala`: Scala-based test suite for RSA operations modeling and testing.
- `ModularInverseTester.scala`: Tests the Modular Inverse calculation, ensuring its accuracy for use in RSA key generation.
- `RSAModel.scala`: A Scala class that models the RSA algorithm, providing methods for encryption, decryption, and key generation. It serves as a reference implementation to validate the correctness of the hardware design.

## Getting Started

Requires Scala and SBT (Scala Build Tool).

1. **Clone the Repository**: Get a copy of the project.
2. **Project Compilation**: In the project directory, compile with `sbt compile`.
3. **Running Tests**: Use `sbt test` to run tests and verify functionality.

## Dependencies

- **Chisel3**: For Scala-based hardware design.
- **ScalaTest**: For writing and executing tests.

## Contributing

Contributions welcome. Submit pull requests or open issues for changes or bug reports.

## Acknowledgments

The help and guidance of the instructor (Scott Beamer) for the course CSE 228A (Agile Hardware Design) and the detailed course material is gratefully acknowledged.
