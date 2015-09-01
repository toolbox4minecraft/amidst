package amidst.clazz.symbolic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SymbolicConstructor {
	private SymbolicClass parent;
	private String symbolicName;
	private Constructor<?> constructor;

	public SymbolicConstructor(SymbolicClass parent, String symbolicName,
			Constructor<?> constructor) {
		this.parent = parent;
		this.symbolicName = symbolicName;
		this.constructor = constructor;
	}

	public SymbolicObject call(Object... parameters) {
		return new SymbolicObject(parent, newInstance(parameters));
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
		return "[Constructor " + symbolicName + " of class "
				+ parent.getSymbolicName() + "]";
	}
}
