package amidst.json;

import amidst.Util;

public class RuleOs {
	public String name;
	public RuleOs() {
		
	}
	public RuleOs(String name) {
		this.name = name;
	}
	public boolean check() {
		if (name.equals("any"))
			return true;
		if (name.equals(Util.getOs()))
			return true;
		return false;
	}
}
