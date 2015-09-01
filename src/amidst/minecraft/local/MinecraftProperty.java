package amidst.minecraft.local;

import java.lang.reflect.Field;

import amidst.logging.Log;

public class MinecraftProperty {
	private MinecraftClass parent;
	private String minecraftName;
	private String byteName;
	private Field field;
	private MinecraftClass type;

	public MinecraftProperty(MinecraftClass parent, String minecraftName,
			String byteName, Field field, MinecraftClass type) {
		this.parent = parent;
		this.minecraftName = minecraftName;
		this.byteName = byteName;
		this.field = field;
		this.type = type;
	}

	public String getMinecraftName() {
		return minecraftName;
	}

	public String getByteName() {
		return byteName;
	}

	public Object getValue(MinecraftObject minecraftObject) {
		return getValueFromObject(minecraftObject.getObject());
	}

	public Object getStaticValue() {
		return getValueFromObject(null);
	}

	private Object getValueFromObject(Object object) {
		Object value = get(object);
		if (isTypeMinecraftClass()) {
			return new MinecraftObject(type, value);
		} else {
			return value;
		}
	}

	private Object get(Object obj) {
		try {
			return field.get(obj);
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

	private boolean isTypeMinecraftClass() {
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
		return "[Method " + minecraftName + " (" + byteName + ") of class "
				+ parent.getMinecraftName() + "]";
	}
}
