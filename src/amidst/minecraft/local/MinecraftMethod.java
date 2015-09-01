package amidst.minecraft.local;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MinecraftMethod {
	private String minecraftName;
	private String byteName;
	private Method method;
	private MinecraftClass returnType;

	public MinecraftMethod(String minecraftName, String byteName,
			Method method, MinecraftClass returnType) {
		this.minecraftName = minecraftName;
		this.byteName = byteName;
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
