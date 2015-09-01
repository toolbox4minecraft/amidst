package amidst.symbolicclass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SymbolicMethod {
	private String symbolicName;
	private String realName;
	private Method method;
	private SymbolicClass returnType;

	public SymbolicMethod(String symbolicName, String realName, Method method,
			SymbolicClass returnType) {
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.method = method;
		this.returnType = returnType;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getRealName() {
		return realName;
	}

	public Object call(SymbolicObject symbolicObject, Object... parameters) {
		return callFromObject(symbolicObject.getObject(), parameters);
	}

	public Object callStatic(Object... parameters) {
		return callFromObject(null, parameters);
	}

	private Object callFromObject(Object object, Object... parameters) {
		Object value = invoke(object, parameters);
		if (isReturnTypeSymbolicClass()) {
			return new SymbolicObject(returnType, value);
		}
		return value;
	}

	private Object invoke(Object object, Object... parameters) {
		try {
			return method.invoke(object, parameters);
		} catch (IllegalArgumentException e) { // TODO : Add error text
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isReturnTypeSymbolicClass() {
		return returnType != null;
	}
}
