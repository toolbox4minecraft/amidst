package amidst.clazz.real;

import amidst.documentation.Immutable;

@Immutable
public class ReferenceIndex {
	private final int val1;
	private final int val2;

	public ReferenceIndex(int val1, int val2) {
		this.val1 = val1;
		this.val2 = val2;
	}

	public int getVal1() {
		return val1;
	}

	public int getVal2() {
		return val2;
	}
}
