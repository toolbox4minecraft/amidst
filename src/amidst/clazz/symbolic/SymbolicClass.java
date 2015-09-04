package amidst.clazz.symbolic;

import java.util.Map;

public class SymbolicClass {
	private String symbolicClassName;
	private String realClassName;
	private Class<?> clazz;
	private Map<String, SymbolicConstructor> constructorsBySymbolicName;
	private Map<String, SymbolicMethod> methodsBySymbolicName;
	private Map<String, SymbolicField> fieldsBySymbolicName;

	public SymbolicClass(String symbolicClassName, String realClassName,
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

	public boolean hasConstructor(String symbolicName) {
		return constructorsBySymbolicName.get(symbolicName) != null;
	}

	public boolean hasMethod(String symbolicName) {
		return methodsBySymbolicName.get(symbolicName) != null;
	}

	public boolean hasField(String symbolicName) {
		return fieldsBySymbolicName.get(symbolicName) != null;
	}

	public SymbolicObject callConstructor(String symbolicName,
			Object... parameters) {
		return constructorsBySymbolicName.get(symbolicName).call(parameters);
	}

	public Object callMethod(String symbolicName,
			SymbolicObject symbolicObject, Object... parameters) {
		return methodsBySymbolicName.get(symbolicName).call(symbolicObject,
				parameters);
	}

	public Object callStaticMethod(String symbolicName, Object... parameters) {
		return methodsBySymbolicName.get(symbolicName).callStatic(parameters);
	}

	public Object getFieldValue(String symbolicName,
			SymbolicObject symbolicObject) {
		return fieldsBySymbolicName.get(symbolicName).getValue(symbolicObject);
	}

	public Object getStaticFieldValue(String symbolicName) {
		return fieldsBySymbolicName.get(symbolicName).getStaticValue();
	}

	@Override
	public String toString() {
		return realClassName;
	}
}
