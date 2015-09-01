package amidst.minecraft.local;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import amidst.logging.Log;

public class MinecraftMethod {
	private Map<String, Class<?>> primitivesMap;
	private String minecraftName;
	private String byteName;
	private String[] parameterNames;
	private Class<?>[] parameterClasses;
	private Method method;
	private MinecraftClass returnType;
	private boolean loadFailed = false;

	public MinecraftMethod(Map<String, Class<?>> primitivesMap,
			String minecraftName, String byteName, String... parameterNames) {
		this.primitivesMap = primitivesMap;
		this.minecraftName = minecraftName;
		this.byteName = byteName;
		this.parameterNames = parameterNames;
	}

	public String getMinecraftName() {
		return minecraftName;
	}

	public String getByteName() {
		return byteName;
	}

	public boolean exists() {
		return !loadFailed;
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

	private Object invoke(Object obj, Object... param) {
		try {
			return method.invoke(obj, param);
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

	public void initialize(Minecraft minecraft, MinecraftClass minecraftClass) {
		Class<?> clazz = minecraftClass.getClazz();
		try {
			parameterClasses = MinecraftFeatureUtils.getParameterClasses(
					minecraft, parameterNames, primitivesMap);
			method = MinecraftFeatureUtils.getMethod(clazz, byteName,
					parameterClasses);
			returnType = MinecraftFeatureUtils.getType(minecraft,
					method.getReturnType());
		} catch (SecurityException e) {
			loadFailed = true;
			Log.w(e,
					"SecurityException on ("
							+ minecraftClass.getMinecraftName() + " / "
							+ minecraftClass.getByteName() + ") method ("
							+ minecraftName + " / " + byteName + ")");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			loadFailed = true;
			Log.w(e,
					"Unable to find class method ("
							+ minecraftClass.getMinecraftName() + " / "
							+ minecraftClass.getByteName() + ") ("
							+ minecraftName + " / " + byteName + ")");
			e.printStackTrace();
		}
	}
}
