RSA Karatsuba Project

Overview:

This project implements an RSA encryption and decryption module using Chisel, a hardware construction language for Scala. The implementation focuses on optimizing the RSA algorithm by incorporating the Karatsuba multiplication technique for large integer multiplication, which is crucial for RSA's performance with large key sizes.

Features:

RSA Implementation: Core RSA algorithm for encryption and decryption.
Karatsuba Multiplication: An efficient algorithm for large integer multiplication, integrated into the RSA module to optimize performance.
Modular Exponentiation: A critical component of RSA for performing the encryption and decryption operations.
GCD Calculation: Utilized in the RSA algorithm for key generation, specifically for finding the modular inverse.

Project Structure:

src/main/scala/RSA_Karatsuba/: Contains the Scala and Chisel source files for the RSA implementation, including the Karatsuba multiplication (KaratsubaMultiplication.scala), modular exponentiation (ModularExponentiation.scala), and the main RSA module (RSA.scala).
src/test/scala/RSA_Karatsuba/: Includes test suites for the RSA module (RSATester.scala), Karatsuba multiplication (KaratsubaTester.scala), and modular exponentiation (ModularExponentiationTester.scala).

Getting Started:

To run this project, you need to have SBT (Scala Build Tool) installed on your system. The project uses SBT for compiling and running the tests.

1. Clone the Repository: Clone this repository to your local machine.
2. Navigate to the Project Directory: Change into the project directory.
3. Run the Tests: Execute the tests using SBT with the command sbt test.
