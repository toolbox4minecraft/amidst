package amidst.minecraft.local;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MinecraftMethod {
	private String symbolicName;
	private String realName;
	private Method method;
	private MinecraftClass returnType;

	public MinecraftMethod(String symbolicName, String realName, Method method,
			MinecraftClass returnType) {
		this.symbolicName = symbolicName;
		this.realName = realName;
		this.method = method;
		this.returnType = returnType;
	}

	public Object call(MinecraftObject minecraftObject, Object... parameters) {
		return callFromObject(minecraftObject.getObject(), parameters);
	}

	public Object callStatic(Object... parameters) {
		return callFromObject(null, parameters);
	}

	private Object callFromObject(Object object, Object... parameters) {
		Object value = invoke(object, parameters);
		if (isReturnTypeMinecraftClass()) {
			return new MinecraftObject(returnType, value);
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

	private boolean isReturnTypeMinecraftClass() {
		return returnType != null;
	}
}
