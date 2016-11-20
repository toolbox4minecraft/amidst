package amidst.clazz.symbolic;

import java.lang.reflect.InvocationTargetException;

import amidst.documentation.Immutable;

/**
 * While the state that is directly contained in instances of this class cannot
 * be altered, it is still possible to alter the object that is contained in it.
 */
@Immutable
public class SymbolicObject {
	private final SymbolicClass type;
	private final Object object;

	public SymbolicObject(SymbolicClass type, Object object) {
		this.type = type;
		this.object = object;
	}

	public SymbolicClass getType() {
		return type;
	}

	public Object getObject() {
		return object;
	}

	public boolean hasMethod(String symbolicName) {
		return type.hasMethod(symbolicName);
	}

	public boolean hasField(String symbolicName) {
		return type.hasField(symbolicName);
	}

	public Object callMethod(String symbolicName, Object... parameters)
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		return type.callMethod(symbolicName, this, parameters);
	}

	public Object getFieldValue(String symbolicName) throws IllegalArgumentException, IllegalAccessException {
		return type.getFieldValue(symbolicName, this);
	}
}
