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
			Map<String, ByteClass> byteClassesByMinecraftClassName)
			throws ClassNotFoundException {
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
			Log.crash(e, "SecurityException on (" + parent.getMinecraftName()
					+ " / " + parent.getByteName() + ") contructor ("
					+ minecraftName + ")");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.crash(
					e,
					"Unable to find class constructor ("
							+ parent.getMinecraftName() + " / "
							+ parent.getByteName() + ") (" + minecraftName
							+ ")");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.crash(e, "Unable to load class (" + parent.getMinecraftName()
					+ " / " + parent.getByteName() + ") (" + minecraftName
					+ ")");
			e.printStackTrace();
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
			Log.w(e, "SecurityException on (" + parent.getMinecraftName()
					+ " / " + parent.getByteName() + ") method ("
					+ minecraftName + " / " + byteName + ")");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.w(e,
					"Unable to find class method (" + parent.getMinecraftName()
							+ " / " + parent.getByteName() + ") ("
							+ minecraftName + " / " + byteName + ")");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.crash(e, "Unable to load class (" + parent.getMinecraftName()
					+ " / " + parent.getByteName() + ") (" + minecraftName
					+ ")");
			e.printStackTrace();
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
			Log.crash(e, "SecurityException on (" + parent.getMinecraftName()
					+ " / " + parent.getByteName() + ") property ("
					+ minecraftName + " / " + byteName + ")");
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			Log.crash(
					e,
					"Unable to find class property ("
							+ parent.getMinecraftName() + " / "
							+ parent.getByteName() + ") (" + minecraftName
							+ " / " + byteName + ")");
			e.printStackTrace();
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
}
