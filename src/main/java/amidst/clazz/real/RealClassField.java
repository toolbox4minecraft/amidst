package amidst.clazz.real;

import amidst.documentation.Immutable;

@Immutable
public class RealClassField {
	private final int accessFlags;

	public RealClassField(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	public boolean hasFlags(int flags) {
		return AccessFlags.hasFlags(accessFlags, flags);
	}
}
