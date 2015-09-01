package amidst.minecraft.local;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import amidst.logging.Log;

public class MinecraftMethod {
	private Map<String, Class<?>> primitivesMap;
	private MinecraftClass parent;
	private String minecraftName;
	private String byteName;
	private String[] parameterNames;
	private Class<?>[] parameterClasses;
	private Method method;
	private MinecraftClass returnType;
	private boolean loadFailed = false;

	public MinecraftMethod(Map<String, Class<?>> primitivesMap,
			MinecraftClass parent, String minecraftName, String byteName,
			String... parameterNames) {
		this.primitivesMap = primitivesMap;
		this.parent = parent;
		this.minecraftName = minecraftName;
		this.byteName = byteName;
		this.parameterNames = parameterNames;
		this.parameterClasses = new Class<?>[parameterNames.length];
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
			for (int i = 0; i < parameterNames.length; i++) {
				parameterClasses[i] = getParameterClass(minecraft,
						parameterNames[i]);
			}

			method = getMethod(clazz);
			returnType = getReturnType(minecraft);
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

	private Class<?> getParameterClass(Minecraft minecraft, String parameterName) {
		Class<?> result = primitivesMap.get(parameterName);
		if (result == null && parameterName.charAt(0) != '@') {
			// TODO: Does this cause duplicate loads?
			result = minecraft.loadClass(parameterName);
		}
		return result;
	}

	private Method getMethod(Class<?> clazz) throws NoSuchMethodException {
		Method result = clazz.getDeclaredMethod(byteName, parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private MinecraftClass getReturnType(Minecraft minecraft) {
		String result = method.getReturnType().getName();
		if (result.contains(".")) {
			String[] typeSplit = result.split("\\.");
			result = typeSplit[typeSplit.length - 1];
		}
		MinecraftClass e = minecraft.getMinecraftClassByByteClassName(result);
		return e;
	}
}
