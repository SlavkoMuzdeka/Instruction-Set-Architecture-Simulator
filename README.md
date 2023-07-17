# Instruction-Set-Architecture-Simulator

## Project Motivation:

The motivation behind this project is to develop a simulator for an instruction set architecture that allows for the interpretation and execution of assembly code. The goal is to simulate a custom machine architecture with specific instructions, memory management, branching, debugging support, and the ability to translate assembly instructions into machine code. The simulator aims to provide a flexible and customizable environment for executing and debugging assembly programs.

## Getting Started

### Key Dependencies & Platforms

- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html): Make sure you have Java 17 installed on your machine. You can download and install it from the official Oracle website or use a Java Development Kit (JDK) distribution suitable for your operating system. Download Java 17

- [Eclipse IDE](https://www.eclipse.org/ide/): I recommend using Eclipse IDE for Java development. Make sure you have Eclipse IDE installed on your machine. You can download it from the Eclipse website and follow the installation instructions. Download Eclipse IDE

Please make sure to install Java 17 and set up Eclipse IDE before proceeding with the project setup.

## Key Features

- `Interpreter-Based Execution`: The simulator functions as an interpreter for the custom instruction set architecture, allowing the execution of assembly instructions.

- `Assembly Code Loading`: The simulator loads the source assembly code from a file, enabling the user to provide their own assembly programs for execution.

- `Syntax and Semantic Analysis`: The simulator performs syntactic and semantic analysis of the assembly code to detect and handle invalid or erroneous code segments.

- `Instruction Set`: The simulated machine architecture includes essential arithmetic operations (ADD, SUB), bitwise logical operations (AND, OR, NOT), data movement between registers (MOV), input from standard input, and output to standard output.

- `Memory Management`: The simulated machine has a 64-bit address space, supporting direct and indirect addressing. The simulator allows for reading and writing to memory addresses using appropriate instructions (MOV or LOAD/STORE).

- `Branching Instructions`: The simulator implements instructions for unconditional and conditional branching, such as JMP, CMP, JE, JNE, JGE, and JL.

- `Single-Step Debugging Support`: The simulator provides a simple single-step debugging feature. It allows for executing and inspecting register values and specified memory addresses at breakpoints during program execution. The user can navigate to the next instruction (NEXT or STEP commands) or the next breakpoint (CONTINUE command).
