package amidst.clazz.real;

import amidst.documentation.Immutable;

/*-
 * 	ACC_PUBLIC	 	0x0001	 Declared public; may be accessed from outside its package.
 *	ACC_PRIVATE	 	0x0002	 Declared private; usable only within the defining class.
 *	ACC_PROTECTED	0x0004	 Declared protected; may be accessed within subclasses.
 *	ACC_STATIC	 	0x0008	 Declared static.
 *	ACC_FINAL	 	0x0010	 Declared final; no further assignment after initialization.
 *	ACC_VOLATILE	0x0040	 Declared volatile; cannot be cached.
 *	ACC_TRANSIENT	0x0080	 Declared transient; not written or read by a persistent object manager.
 *	ACC_INTERFACE	0x0200	 Is an interface, not a class.
 **/
@Immutable
public enum AccessFlags {
	;

	public static final int PUBLIC = 0x01;
	public static final int PRIVATE = 0x02;
	public static final int PROTECTED = 0x04;
	public static final int STATIC = 0x08;
	public static final int FINAL = 0x10;
	public static final int VOLATILE = 0x40;
	public static final int TRANSIENT = 0x80;
	public static final int INTERFACE = 0x0200;

	public static boolean hasFlags(int accessFlags, int flags) {
		return (accessFlags & flags) == flags;
	}
}
