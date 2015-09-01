package amidst.symbolicclass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MinecraftConstructor {
	private MinecraftClass parent;
	private String minecraftName;
	private Constructor<?> constructor;

	public MinecraftConstructor(MinecraftClass parent, String minecraftName,
			Constructor<?> constructor) {
		this.parent = parent;
		this.minecraftName = minecraftName;
		this.constructor = constructor;
	}

	public MinecraftObject call(Object... parameters) {
		return new MinecraftObject(parent, newInstance(parameters));
	}

	private Object newInstance(Object... parameters) {
		try {
			return constructor.newInstance(parameters);
		} catch (IllegalArgumentException e) { // TODO : Add error text
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "[Constructor " + minecraftName + " of class "
				+ parent.getSymbolicName() + "]";
	}
}
