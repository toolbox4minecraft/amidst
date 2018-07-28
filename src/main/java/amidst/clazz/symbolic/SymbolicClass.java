package amidst.clazz.symbolic;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import amidst.documentation.Immutable;

/**
 * The contents of the maps will be altered by the {@link SymbolicClassBuilder}
 * after this instance is constructed. However, these maps will not be altered
 * after the method {@link SymbolicClassGraphBuilder#construct()} finished
 * execution.
 */
@Immutable
public class SymbolicClass {
	private final String symbolicClassName;
	private final String realClassName;
	private final Class<?> clazz;
	private final Map<String, SymbolicConstructor> constructorsBySymbolicName;
	private final Map<String, SymbolicMethod> methodsBySymbolicName;
	private final Map<String, SymbolicField> fieldsBySymbolicName;

	public SymbolicClass(
			String symbolicClassName,
			String realClassName,
			Class<?> clazz,
			Map<String, SymbolicConstructor> constructorsBySymbolicName,
			Map<String, SymbolicMethod> methodsBySymbolicName,
			Map<String, SymbolicField> fieldsBySymbolicName) {
		this.symbolicClassName = symbolicClassName;
		this.realClassName = realClassName;
		this.clazz = clazz;
		this.constructorsBySymbolicName = constructorsBySymbolicName;
		this.methodsBySymbolicName = methodsBySymbolicName;
		this.fieldsBySymbolicName = fieldsBySymbolicName;
	}

	public String getSymbolicName() {
		return symbolicClassName;
	}

	public String getRealName() {
		return realClassName;
	}

	public Class<?> getClazz() {
		return clazz;
	}
	
	public SymbolicConstructor getConstructor(String symbolicName) {
		return constructorsBySymbolicName.get(symbolicName);
	}
	
	public SymbolicMethod getMethod(String symbolicName) {
		return methodsBySymbolicName.get(symbolicName);
	}
	
	public SymbolicField getField(String symbolicName) {
		return fieldsBySymbolicName.get(symbolicName);
	}

	public boolean hasConstructor(String symbolicName) {
		return getConstructor(symbolicName) != null;
	}

	public boolean hasMethod(String symbolicName) {
		return getMethod(symbolicName) != null;
	}

	public boolean hasField(String symbolicName) {
		return getField(symbolicName) != null;
	}

	public SymbolicObject callConstructor(String symbolicName, Object... parameters)
			throws InstantiationException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		return constructorsBySymbolicName.get(symbolicName).call(parameters);
	}

	public Object callMethod(String symbolicName, SymbolicObject symbolicObject, Object... parameters)
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		return methodsBySymbolicName.get(symbolicName).call(symbolicObject, parameters);
	}

	public Object callStaticMethod(String symbolicName, Object... parameters)
			throws IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		return methodsBySymbolicName.get(symbolicName).callStatic(parameters);
	}

	public Object getFieldValue(String symbolicName, SymbolicObject symbolicObject)
			throws IllegalArgumentException,
			IllegalAccessException {
		return fieldsBySymbolicName.get(symbolicName).getValue(symbolicObject);
	}

	public Object getStaticFieldValue(String symbolicName) throws IllegalArgumentException, IllegalAccessException {
		return fieldsBySymbolicName.get(symbolicName).getStaticValue();
	}

	@Override
	public String toString() {
		return realClassName;
	}
}
