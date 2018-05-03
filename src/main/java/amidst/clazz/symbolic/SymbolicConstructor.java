package amidst.clazz.symbolic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import amidst.documentation.Immutable;

@Immutable
public class SymbolicConstructor {
	private final SymbolicClass parent;
	private final String symbolicName;
	private final Constructor<?> constructor;

	public SymbolicConstructor(SymbolicClass parent, String symbolicName, Constructor<?> constructor) {
		this.parent = parent;
		this.symbolicName = symbolicName;
		this.constructor = constructor;
	}

	public SymbolicObject call(Object... parameters)
			throws InstantiationException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		return new SymbolicObject(parent, newInstance(parameters));
	}
	
	public Constructor<?> getRawConstructor() {
		return constructor;
	}

	private Object newInstance(Object... parameters)
			throws InstantiationException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		return constructor.newInstance(parameters);
	}

	@Override
	public String toString() {
		return "[Constructor " + symbolicName + " of class " + parent.getSymbolicName() + "]";
	}
}
