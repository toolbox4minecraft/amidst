package amidst.clazz.symbolic;

import java.lang.reflect.Field;

import amidst.documentation.Immutable;
import amidst.logging.Log;

@Immutable
public class SymbolicField {
	private final SymbolicClass parent;
	private final String symbolicName;
	private final String realName;
	private final Field field;
	private final SymbolicClass type;

	public SymbolicField(SymbolicClass parent, String symbolicName,
			String realName, Field field, SymbolicClass type) {
		this.parent = parent;
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.field = field;
		this.type = type;
	}

	public Object getValue(SymbolicObject symbolicObject) {
		return getValueFromObject(symbolicObject.getObject());
	}

	public Object getStaticValue() {
		return getValueFromObject(null);
	}

	private Object getValueFromObject(Object object) {
		Object value = get(object);
		if (isTypeSymbolicClass()) {
			return new SymbolicObject(type, value);
		} else {
			return value;
		}
	}

	private Object get(Object object) {
		try {
			return field.get(object);
		} catch (IllegalArgumentException e) {
			Log.crash(e,
					"Error [IllegalArgumentException] getting field value ("
							+ toString() + ")");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.crash(e, "Error [IllegalAccessException] getting field value ("
					+ toString() + ")");
			e.printStackTrace();
		}
		return null;
	}

	private boolean isTypeSymbolicClass() {
		return type != null;
	}

	@Override
	public String toString() {
		return "[Field " + symbolicName + " (" + realName + ") of class "
				+ parent.getSymbolicName() + "]";
	}
}
