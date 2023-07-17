package instruction;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import exceptions.MyException;
import main.Simulator;
import registers.Register;

public class Instruction {

	private Register[] registers;
	private String[] arr;
	private HashMap<Long, Byte> map;

	public static String label = "";
	public static int labelNumber;
	public static int toDo = 0; // Until which line does the loop repeat
	public static int count = 0; // Counter of how many instructions we have executed
	public static boolean t;

	public Instruction(Register[] registers, String[] arr, HashMap<Long, Byte> map) {
		this.registers = registers;
		this.arr = arr;
		this.map = map;
		count++;
	}

	public void execute() throws FileNotFoundException, MyException {
		String registerOp = arr[0];

		String oprnd1 = "";
		String oprnd2 = "";

		Register desReg = null;
		Register sourReg = null;

		if (arr.length == 3) {
			registerExecute3(registerOp, oprnd1, oprnd2, desReg, sourReg, arr, map);
		} else if (arr.length == 2) {
			registerExecute2(registerOp, oprnd1, desReg, arr);
		} else if (arr.length == 5) {
			if (registerExecuteJump(oprnd1, oprnd2, desReg, sourReg, arr, map)) {
				t = true;
			} else {
				t = false;
			}
		}
	}

	// For instructions with 3 elements - ADD, SUB, OR, AND, MOV
	private void registerExecute3(String registerOp, String oprnd1, String oprnd2, Register desReg, Register sourReg,
			String[] arr, HashMap<Long, Byte> map) throws MyException {
		oprnd1 = arr[1];
		oprnd2 = arr[2];

		desReg = findFirstOperand(oprnd1);
		sourReg = findSecondOperand(oprnd2);

		if (isIndirect(oprnd1, oprnd2)) {
			indirectAdress(registerOp, oprnd1, oprnd2);
		} else {
			if (desReg != null && sourReg != null) {
				bothNotNull(registerOp, desReg, sourReg);
			} else if (desReg != null && sourReg == null) {
				firstNotNullSecondIs(registerOp, oprnd2, desReg);
			} else if (desReg == null && sourReg == null) {
				bothNull(registerOp, oprnd1, oprnd2);
			} else if (desReg == null && sourReg != null) {
				firstNullSecondNotNull(registerOp, oprnd1, sourReg);
			}
		}
	}

	// Determining which is the "first" register
	private Register findFirstOperand(String oprnd1) throws MyException {
		Register desReg = null;
		for (int i = 0; i < registers.length; i++) {
			if (registers[i].getName().equals(oprnd1) || registers[i].getName().equals("[" + oprnd1 + "]")) {
				desReg = registers[i];
			}
		}
		return desReg;
	}

	// Determining which is the "first" register
	private Register findSecondOperand(String oprnd2) throws MyException {
		Register sourReg = null;
		for (int i = 0; i < registers.length; i++) {
			if (registers[i].getName().equals(oprnd2)) {
				sourReg = registers[i];
			}
		}
		return sourReg;
	}

	// Checking whether we are performing indirect addressing or not
	private boolean isIndirect(String oprnd1, String oprnd2) {
		for (int i = 0; i < Simulator.numberOfRegisters; i++) {
			if (oprnd1.equals("[" + registers[i].getName() + "]")
					|| oprnd2.equals("[" + registers[i].getName() + "]")) {
				return true;
			}
		}
		return false;
	}

	// Perform indirect addressing
	private void indirectAdress(String registerOp, String oprnd1, String oprnd2) throws MyException {
		Register desReg = null, sourReg = null;
		if ((desReg = findFirstOperand(oprnd1.substring(1, oprnd1.length() - 1))) != null && oprnd2.matches("-?\\d+")) {
			long value = Long.parseLong(oprnd2);
			map.put(desReg.getValue(), (byte) value);
		} else if ((desReg = findFirstOperand(oprnd1.substring(1, oprnd1.length() - 1))) != null
				&& !oprnd2.matches("-?\\d+")) {
			long value = longOrString(oprnd2.substring(1, oprnd2.length() - 1));
			map.put(desReg.getValue(), (byte) value);
		} else {
			long value = longOrString(oprnd1.substring(1, oprnd1.length() - 1));
			sourReg = findSecondOperand(oprnd2.substring(1, oprnd2.length() - 1));
			map.put(value, sourReg.getValue().byteValue());
		}
	}

	 // Determining whether the entered address is in integer or hexadecimal format
	private Long longOrString(String address) throws MyException {
		long addr;
		if (address.startsWith("0x")) {
			address = address.substring(2, address.length());
			// addr = Integer.parseInt(address, 16);
			addr = Long.parseLong(address, 16);
		} else {
			addr = Long.parseLong(address);
		}
		return addr;
	}

	
	// The first element is a register, and the second element is a register
	private void bothNotNull(String registerOp, Register desReg, Register sourReg) throws MyException {
		desReg.setValue(executeOperation(registerOp, desReg.getValue(), sourReg.getValue()));
	}
	
	// The first element is a register, and the second element can be a value or a value from a memory address
	private void firstNotNullSecondIs(String registerOp, String oprnd2, Register desReg) throws MyException {
		if (oprnd2.matches("-?\\d+")) {
			desReg.setValue(executeOperation(registerOp, desReg.getValue(), Long.parseLong(oprnd2)));
		} else {
			String address = oprnd2.substring(1, oprnd2.length() - 1);
			Long addr = longOrString(address);
			if (map.get(addr) == null) {
				throw new MyException("Memory address does not exist.");
			} else {
				desReg.setValue(executeOperation(registerOp, desReg.getValue(), map.get(addr)));
			}
		}
	}
	
	// The first element is a memory address, and the second element can be a value or a memory address
	private void bothNull(String registerOp, String oprnd1, String oprnd2) throws MyException {
		String address = oprnd1.substring(1, oprnd1.length() - 1);
		Long addr = longOrString(address);
		byte addressValue = 0;
		if (oprnd2.matches("-?\\d+")) {
			long pom = Long.parseLong(oprnd2);
			addressValue = (byte) pom;
			map.put(addr, addressValue);
		} else {
			String str = oprnd2.substring(1, oprnd2.length() - 1);
			long l = longOrString(str);
			byte b = (byte) map.get(l);
			map.put(addr, b);
		}
	}
	
	// The first element is a memory address, and the second element is a register.
	private void firstNullSecondNotNull(String registerOp, String oprnd1, Register sourReg) throws MyException {
		String address = oprnd1.substring(1, oprnd1.length() - 1);
		long addr = longOrString(address);
		map.put(addr, sourReg.getValue().byteValue());
	}
	
	// For instructions with 2 parameters - IN, OUT, NOT
	private void registerExecute2(String registerOp, String oprnd1, Register desReg, String[] arr) throws MyException {
		oprnd1 = arr[1];
		desReg = findFirstOperand(oprnd1);
		if ("out".equals(registerOp)) {
			printReg(desReg);
		} else if ("in".equals(registerOp)) {
			String str = input();
			desReg.setValue(Long.parseLong(str));
		} else if ("not".equals(registerOp)) {
			desReg.setValue(~desReg.getValue());
			// print();
		}
	}
	
	private void printReg(Register desReg) {
		System.out.println(
				"===========================================================================================================");
		System.out.println(desReg);
	}
	
	// For conditional jumps - JNE, JN, JG, JE)
	private boolean registerExecuteJump(String oprnd1, String oprnd2, Register desReg, Register sourReg, String[] arr,
			HashMap<Long, Byte> map) throws MyException {
		oprnd1 = arr[1];
		oprnd2 = arr[2];

		desReg = findFirstOperand(oprnd1);
		sourReg = findSecondOperand(oprnd2);

		if (desReg != null && sourReg == null) {
			return operationRegisterJump(arr[3], desReg.getValue(), Long.parseLong(oprnd2));
		} else if (desReg != null && sourReg != null) {
			return operationRegisterJump(arr[3], desReg.getValue(), sourReg.getValue());
		}
		return false;
	}
	
	private boolean operationRegisterJump(String op, long a, long b) {
		if ("jl".equals(op)) {// jump if lower than
			return a < b;
		} else if ("je".equals(op)) {// jump if equal
			return a == b;
		} else if ("jne".equals(op)) {// jump if not equal
			return a != b;
		} else if ("jge".equals(op)) {// jump if greater than or equal
			return a >= b;
		}
		return false;
	}

	private long executeOperation(String op, long a, long b) {
		long value = 0L;
		if ("add".equals(op)) {
			value = a + b;
		} else if ("sub".equals(op)) {
			value = b - a;
		} else if ("and".equals(op)) {
			value = a & b;
		} else if ("or".equals(op)) {
			value = a | b;
		} else if ("mov".equals(op)) {
			value = b;
		} else if ("not".equals(op)) {
			value = ~a;
		}
		return value;
	}

	private String input() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Input value: ");
		String str = "";
		while (!str.matches("-?\\d+")) {
			str = scan.nextLine();
		}
		scan.close();
		return str;
	}

	public void print() {
		System.out.println(
				"===========================================================================================================");
		for (int i = 0; i < 4; i++) {
			System.out.println(registers[i]);
		}
	}
	
	public void printMap() {
		System.out.println(map);
	}

	public String[] getArr() {
		return arr;
	}
	
	public void setArr(String[] arr) {
		this.arr = arr;
	}

}
