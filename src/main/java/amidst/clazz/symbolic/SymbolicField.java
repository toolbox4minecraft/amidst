package amidst.clazz.symbolic;

import java.lang.reflect.Field;

import amidst.documentation.Immutable;

@Immutable
public class SymbolicField {
	private final SymbolicClass parent;
	private final String symbolicName;
	private final String realName;
	private final Field field;
	private final SymbolicClass type;

	public SymbolicField(SymbolicClass parent, String symbolicName, String realName, Field field, SymbolicClass type) {
		this.parent = parent;
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.field = field;
		this.type = type;
	}

	public Field getRawField() {
		return field;
	}

	public Object getValue(SymbolicObject symbolicObject) throws IllegalArgumentException, IllegalAccessException {
		return getValueFromObject(symbolicObject.getObject());
	}

	public Object getStaticValue() throws IllegalArgumentException, IllegalAccessException {
		return getValueFromObject(null);
	}

	private Object getValueFromObject(Object object) throws IllegalArgumentException, IllegalAccessException {
		Object value = get(object);
		if (isTypeSymbolicClass()) {
			return new SymbolicObject(type, value);
		} else {
			return value;
		}
	}

	private Object get(Object object) throws IllegalArgumentException, IllegalAccessException {
		return field.get(object);
	}

	private boolean isTypeSymbolicClass() {
		return type != null;
	}

	@Override
	public String toString() {
		return "[Field " + symbolicName + " (" + realName + ") of class " + parent.getSymbolicName() + "]";
	}
}
