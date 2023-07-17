package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import exceptions.MyException;
import instruction.Instruction;
import registers.Register;

public class Simulator {
	public static final int numberOfRegisters = 4;

	private static final String test = "tests\\test1.txt";

	private static void checkValidation(String[] arr, String s, Register[] registers, String[] operands)
			throws MyException {
		int br = 0;
		if (s.isEmpty()) {
			throw new MyException("The field must not be empty.");
		} else if (arr.length > 3) {
			throw new MyException("The number of elements in the given instruction is not appropriate.");
		}
		if (arr.length == 3) {
			br = pom3(operands, arr, registers);
			if (br != 3) {
				throw new MyException("Invalid instruction.");
			}
		} else if (arr.length == 2) {
			br = pom2(operands, arr, registers);
			if (br != 2) {
				throw new MyException("Invalid instruction.");
			}
		}
	}

	private static int pom3(String[] operands, String[] arr, Register[] registers) {
		int br = 0;
		for (int i = 0; i < operands.length; i++) {
			if (arr[0].equals(operands[i])) {
				br++;
			}
		}
		for (int i = 1; i < 3; i++) {
			if (arr[i].matches("\\W0x?[abcdef]*\\d*\\W") || arr[i].matches("-?\\d+") || arr[i].matches("\\W\\d+\\W")) {
				br++;
			} else {
				for (int j = 0; j < registers.length; j++) {
					if (arr[i].equals(registers[j].getName()) || arr[i].equals("[" + registers[j].getName() + "]")) {
						br++;
					}
				}
			}
		}
		return br;
	}

	private static int pom2(String[] operands, String[] arr, Register[] registers) {
		int br = 0;
		for (int i = 0; i < operands.length; i++) {
			if (arr[0].equals(operands[i])) {
				br++;
			}
		}
		if ((arr[1].equals(Instruction.label) || "jmp".equals(arr[0])) || arr[1].matches("\\W0x?[abcdef]*\\d+\\W")) {
			br++;
		} else {
			for (int i = 0; i < registers.length; i++) {
				if (arr[1].equals(registers[i].getName()) || arr[1].equals("[" + registers[i].getName() + "]")) {
					br++;
				}
			}
		}
		return br;
	}

	private static void createRegisters(Register[] registers) {
		for (int i = 0; i < numberOfRegisters; i++) {
			registers[i] = new Register("reg" + Integer.toString(i), 0L);
		}
	}

	private static void readInstructions(Register[] registers, ArrayList<Instruction> instructions,
			ArrayList<Instruction> pomInstructions) {
		String[] arr = new String[3];
		HashMap<Long, Byte> map = new HashMap<>();

		String[] operands3 = { "add", "sub", "or", "and", "mov", "cmp" };
		String[] operands2 = { "not", "in", "out", "jl", "je", "jge", "jne", "jmp" };
		try (BufferedReader bf = new BufferedReader(new FileReader(test))) {
			String s;
			while ((s = bf.readLine()) != null) {
				arr = s.split(" ");
				if (arr.length == 3) {
					checkValidation(arr, s, registers, operands3);
				} else {
					checkValidation(arr, s, registers, operands2);
				}
				if (arr.length != 1) {
					if (arr[0].equals("cmp")) {
						String[] arr1 = new String[5];
						String[] pomArr = new String[3];

						s = bf.readLine();
						pomArr = s.split(" ");

						checkValidation(pomArr, s, registers, operands2);

						System.arraycopy(arr, 0, arr1, 0, arr.length);
						System.arraycopy(pomArr, 0, arr1, arr.length, pomArr.length);

						instructions.add(new Instruction(registers, arr1, map));
						Instruction.toDo = Instruction.count;

						if (Instruction.count != 0 && Instruction.labelNumber != 0) {
							pomInstructions.add(new Instruction(registers, arr1, map));
						}
					} else {
						instructions.add(new Instruction(registers, arr, map));
						if (Instruction.labelNumber != 0 && Instruction.toDo == 0) {
							pomInstructions.add(new Instruction(registers, arr, map));
						}
					}
				} else {
					if ("DEBUGING".equals(arr[0].toUpperCase())) {
						instructions.add(new Instruction(registers, arr, map));
					} else {
						Instruction.label = arr[0];
						Instruction.labelNumber = Instruction.count;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void executeInstructions(Register[] registers, ArrayList<Instruction> instructions,
			ArrayList<Instruction> pomInstructions) throws FileNotFoundException, MyException {
		for (int j = 0; j < instructions.size(); j++) {
			if (instructions.get(j).getArr().length == 2 || instructions.get(j).getArr().length == 3) {
				String[] array = instructions.get(j).getArr();
				if (!array[0].equals("jmp")) {
					instructions.get(j).execute();
				} else {
					j = Instruction.labelNumber - 1;
				}
			} else if (instructions.get(j).getArr().length == 5) {
				instructions.get(j).execute();
				if (Instruction.t == true) {
					while (Instruction.t == true) {
						for (Instruction list2 : pomInstructions) {
							list2.execute();
						}
					}
				}
			} else if (instructions.get(j).getArr().length == 1) {
				instructions.get(j).print();
				instructions.get(j).printMap();
				Scanner scan = new Scanner(System.in);
				String str = "";
				while (j < Instruction.count - 1) {
					str = scan.nextLine();
					if (!str.toUpperCase().equals("NEXT") && !str.toUpperCase().equals("CONTINUE")) {
						while (!str.toUpperCase().equals("NEXT") && !str.toUpperCase().equals("CONTINUE")) {
							str = scan.nextLine();
						}
					}
					j++;
					if (str.toUpperCase().equals("NEXT") && !instructions.get(j).getArr()[0].toUpperCase().equals("DEBUGING")) {
						instructions.get(j).execute();
						instructions.get(j).print();
						instructions.get(j).printMap();
					} else if (str.toUpperCase().equals("CONTINUE")) {
						for (int i = j; i < Instruction.count - 1; i++) {
							if (instructions.get(i).getArr()[0].toUpperCase().equals("DEBUGING")) {
								j = i;
							}
						}
					}
				}
				scan.close();
			}
		}
	}

	public static void main(String[] args) {
		Register[] registers = new Register[numberOfRegisters];
		ArrayList<Instruction> instructions = new ArrayList<>();
		ArrayList<Instruction> pomInstructions = new ArrayList<>();

		createRegisters(registers);
		readInstructions(registers, instructions, pomInstructions);
		try {
			executeInstructions(registers, instructions, pomInstructions);
		} catch (FileNotFoundException | MyException e) {
			e.printStackTrace();
		}
	}
}
