package amidst.minecraft.local;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class MinecraftFeatureUtils {
	private MinecraftFeatureUtils() {
	}

	public static Constructor<?> getConstructor(Class<?> clazz,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Constructor<?> result = clazz.getConstructor(parameterClasses);
		result.setAccessible(true);
		return result;
	}

	public static Method getMethod(Class<?> clazz, String byteName,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Method result = clazz.getDeclaredMethod(byteName, parameterClasses);
		result.setAccessible(true);
		return result;
	}

	public static Field getField(Class<?> clazz, String byteName)
			throws NoSuchFieldException {
		Field result = clazz.getDeclaredField(byteName);
		result.setAccessible(true);
		return result;
	}

	public static Class<?>[] getParameterClasses(Minecraft minecraft,
			String[] parameterNames, Map<String, Class<?>> primitivesMap) {
		Class<?>[] result = new Class<?>[parameterNames.length];
		for (int i = 0; i < parameterNames.length; i++) {
			result[i] = getParameterClass(minecraft, parameterNames[i],
					primitivesMap);
		}
		return result;
	}

	public static Class<?> getParameterClass(Minecraft minecraft,
			String parameterName, Map<String, Class<?>> primitivesMap) {
		Class<?> result = primitivesMap.get(parameterName);
		if (result == null && parameterName.charAt(0) != '@') {
			// TODO: Does this cause duplicate loads?
			result = minecraft.loadClass(parameterName);
		}
		return result;
	}

	public static MinecraftClass getType(Minecraft minecraft, Class<?> type) {
		String result = type.getName();
		if (result.contains(".")) {
			String[] typeSplit = result.split("\\.");
			result = typeSplit[typeSplit.length - 1];
		}
		return minecraft.getMinecraftClassByByteClassName(result);
	}
}
