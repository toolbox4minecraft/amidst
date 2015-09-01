package amidst.minecraft.local;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import amidst.byteclass.ByteClass;
import amidst.logging.Log;

public class MinecraftClasses {
	private MinecraftClasses() {
	}

	public static Map<String, MinecraftClass> createClasses(
			ClassLoader classLoader,
			Map<String, ByteClass> byteClassesByMinecraftClassName) {
		return new MinecraftClassGraphBuilder(classLoader,
				byteClassesByMinecraftClassName).create();
	}

	public static MinecraftConstructor createConstructor(
			ClassLoader classLoader, MinecraftClass parent,
			String minecraftName, String[] parameterByteNames) {
		try {
			Class<?>[] parameterClasses = MinecraftClasses.getParameterClasses(
					classLoader, parameterByteNames);
			Constructor<?> constructor = MinecraftClasses.getConstructor(
					parent.getClazz(), parameterClasses);
			return new MinecraftConstructor(parent, minecraftName, constructor);
		} catch (SecurityException e) {
			throwRuntimeException(parent, minecraftName, e, "constructor");
		} catch (NoSuchMethodException e) {
			throwRuntimeException(parent, minecraftName, e, "constructor");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(parent, minecraftName, e, "constructor");
		}
		return null;
	}

	public static MinecraftMethod createMethod(ClassLoader classLoader,
			Map<String, MinecraftClass> minecraftClassesByByteClassName,
			MinecraftClass parent, String minecraftName, String byteName,
			String[] parameterByteNames) {
		try {
			Class<?>[] parameterClasses = MinecraftClasses.getParameterClasses(
					classLoader, parameterByteNames);
			Method method = MinecraftClasses.getMethod(parent.getClazz(),
					byteName, parameterClasses);
			MinecraftClass returnType = MinecraftClasses.getType(
					minecraftClassesByByteClassName, method.getReturnType());
			return new MinecraftMethod(minecraftName, byteName, method,
					returnType);
		} catch (SecurityException e) {
			warn(parent, minecraftName, e, "method");
		} catch (NoSuchMethodException e) {
			warn(parent, minecraftName, e, "method");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(parent, minecraftName, e, "method");
		}
		return null;
	}

	public static MinecraftProperty createProperty(
			Map<String, MinecraftClass> minecraftClassesByByteClassName,
			MinecraftClass parent, String minecraftName, String byteName) {
		try {
			Field field = MinecraftClasses
					.getField(parent.getClazz(), byteName);
			MinecraftClass type = MinecraftClasses.getType(
					minecraftClassesByByteClassName, field.getType());
			return new MinecraftProperty(parent, minecraftName, byteName,
					field, type);
		} catch (SecurityException e) {
			throwRuntimeException(parent, minecraftName, e, "property");
		} catch (NoSuchFieldException e) {
			throwRuntimeException(parent, minecraftName, e, "property");
		}
		return null;
	}

	private static Constructor<?> getConstructor(Class<?> clazz,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Constructor<?> result = clazz.getConstructor(parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private static Method getMethod(Class<?> clazz, String byteName,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Method result = clazz.getDeclaredMethod(byteName, parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private static Field getField(Class<?> clazz, String byteName)
			throws NoSuchFieldException {
		Field result = clazz.getDeclaredField(byteName);
		result.setAccessible(true);
		return result;
	}

	private static Class<?>[] getParameterClasses(ClassLoader classLoader,
			String[] parameterByteNames) throws ClassNotFoundException {
		Class<?>[] result = new Class<?>[parameterByteNames.length];
		for (int i = 0; i < parameterByteNames.length; i++) {
			result[i] = getParameterClass(classLoader, parameterByteNames[i]);
		}
		return result;
	}

	private static Class<?> getParameterClass(ClassLoader classLoader,
			String parameterByteName) throws ClassNotFoundException {
		Class<?> result = StatelessResources.INSTANCE.getPrimitivesMap().get(
				parameterByteName);
		if (result == null && parameterByteName.charAt(0) != '@') {
			result = classLoader.loadClass(parameterByteName);
		}
		return result;
	}

	private static MinecraftClass getType(
			Map<String, MinecraftClass> minecraftClassesByByteClassName,
			Class<?> type) {
		String result = type.getName();
		if (result.contains(".")) {
			String[] typeSplit = result.split("\\.");
			result = typeSplit[typeSplit.length - 1];
		}
		return minecraftClassesByByteClassName.get(result);
	}

	private static void throwRuntimeException(MinecraftClass parent,
			String minecraftName, Exception e, String featureType) {
		throw new RuntimeException(errorMessage(parent, minecraftName, e,
				featureType), e);
	}

	private static void warn(MinecraftClass parent, String minecraftName,
			Exception e, String featureType) {
		Log.w(errorMessage(parent, minecraftName, e, featureType));
		e.printStackTrace();
	}

	private static String errorMessage(MinecraftClass parent,
			String minecraftName, Exception e, String featureType) {
		return e.getClass().getSimpleName() + " on ("
				+ parent.getMinecraftName() + " / " + parent.getByteName()
				+ ") " + featureType + " (" + minecraftName + ")";
	}
}
