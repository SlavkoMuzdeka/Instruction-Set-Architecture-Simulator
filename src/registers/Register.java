package registers;

public class Register {

	private String name;
	private Long value;
	
	public Register() {
		super();
	}

	public Register(String name, Long value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		if(value >= 0) {
			String str1 = String.format("%064d", Long.valueOf(Long.toBinaryString(value)));
			String reg = "Register name: " + name + "; " + "Register value: " + str1;
			return reg;
		}else {
			String str2 = Long.toBinaryString(value);
			String reg1 = str2.substring(str2.length() - 64 , str2.length());
			return "Register name: " + name + "; " + "Register value: " + reg1;
		}
	}

}
