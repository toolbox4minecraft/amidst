package amidst.util;

import java.util.Objects;

public enum TriState {
	TRUE,
	FALSE,
	UNKNOWN;
	
	public TriState not() {
		if(this == TRUE)
			return FALSE;
		if(this == FALSE)
			return TRUE;
		return UNKNOWN;
	}
	
	public TriState and(TriState other) {
		Objects.requireNonNull(other);
		if(this == TRUE)
			return other;
		return this;
	}
	
	public TriState or(TriState other) {
		Objects.requireNonNull(other);
		if(this == TRUE)
			return TRUE;
		return other;
	}
}
