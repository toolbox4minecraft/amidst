package amidst.minecraft.local;

import java.lang.reflect.Field;

import amidst.logging.Log;

public class MinecraftProperty {
	private MinecraftClass parent;
	private String symbolicName;
	private String realName;
	private Field field;
	private MinecraftClass type;

	public MinecraftProperty(MinecraftClass parent, String symbolicName,
			String realName, Field field, MinecraftClass type) {
		this.parent = parent;
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.field = field;
		this.type = type;
	}

	public Object getValue(MinecraftObject object) {
		return getValueFromObject(object.getObject());
	}

	public Object getStaticValue() {
		return getValueFromObject(null);
	}

	private Object getValueFromObject(Object object) {
		Object value = get(object);
		if (isTypeSymbolicClass()) {
			return new MinecraftObject(type, value);
		} else {
			return value;
		}
	}

	private Object get(Object object) {
		try {
			return field.get(object);
		} catch (IllegalArgumentException e) {
			Log.crash(e,
					"Error [IllegalArgumentException] getting property value ("
							+ toString() + ")");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.crash(e,
					"Error [IllegalAccessException] getting property value ("
							+ toString() + ")");
			e.printStackTrace();
		}
		return null;
	}

	private boolean isTypeSymbolicClass() {
		return type != null;
	}

	public void setValue(Object object, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalArgumentException e) {
			Log.crash(e,
					"Error [IllegalArgumentException] setting property value ("
							+ toString() + ")");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.crash(e,
					"Error [IllegalAccessException] setting property value ("
							+ toString() + ")");
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "[Method " + symbolicName + " (" + realName + ") of class "
				+ parent.getSymbolicName() + "]";
	}
}
