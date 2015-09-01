package amidst.minecraft.local;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import amidst.byteclass.ByteClass;
import amidst.byteclass.ConstructorDeclaration;
import amidst.byteclass.MethodDeclaration;
import amidst.byteclass.ParameterDeclarationList.Entry;
import amidst.byteclass.PropertyDeclaration;
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
			Map<String, ByteClass> byteClassesByMinecraftClassName,
			ConstructorDeclaration declaration) {
		String externalName = declaration.getExternalName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(classLoader,
					byteClassesByMinecraftClassName, declaration
							.getParameters().getEntries());
			Constructor<?> constructor = getConstructor(parent.getClazz(),
					parameterClasses);
			return new MinecraftConstructor(parent, externalName, constructor);
		} catch (SecurityException e) {
			throwRuntimeException(parent, externalName, e, "constructor");
		} catch (NoSuchMethodException e) {
			throwRuntimeException(parent, externalName, e, "constructor");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(parent, externalName, e, "constructor");
		}
		return null;
	}

	public static MinecraftMethod createMethod(ClassLoader classLoader,
			Map<String, MinecraftClass> minecraftClassesByByteClassName,
			MinecraftClass parent,
			Map<String, ByteClass> byteClassesByMinecraftClassName,
			MethodDeclaration declaration) {
		String externalName = declaration.getExternalName();
		String internalName = declaration.getInternalName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(classLoader,
					byteClassesByMinecraftClassName, declaration
							.getParameters().getEntries());
			Method method = getMethod(parent.getClazz(), internalName,
					parameterClasses);
			MinecraftClass returnType = getType(
					minecraftClassesByByteClassName, method.getReturnType());
			return new MinecraftMethod(externalName, internalName, method,
					returnType);
		} catch (SecurityException e) {
			warn(parent, externalName, e, "method");
		} catch (NoSuchMethodException e) {
			warn(parent, externalName, e, "method");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(parent, externalName, e, "method");
		}
		return null;
	}

	public static MinecraftProperty createProperty(
			Map<String, MinecraftClass> minecraftClassesByByteClassName,
			MinecraftClass parent, PropertyDeclaration declaration) {
		String externalName = declaration.getExternalName();
		String internalName = declaration.getInternalName();
		try {
			Field field = getField(parent.getClazz(), internalName);
			MinecraftClass type = getType(minecraftClassesByByteClassName,
					field.getType());
			return new MinecraftProperty(parent, externalName, internalName,
					field, type);
		} catch (SecurityException e) {
			throwRuntimeException(parent, externalName, e, "property");
		} catch (NoSuchFieldException e) {
			throwRuntimeException(parent, externalName, e, "property");
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
			Map<String, ByteClass> byteClassesByMinecraftClassName,
			List<Entry> entries) throws ClassNotFoundException {
		Class<?>[] result = new Class<?>[entries.size()];
		for (int i = 0; i < entries.size(); i++) {
			result[i] = getParameterClass(classLoader,
					byteClassesByMinecraftClassName, entries.get(0));
		}
		return result;
	}

	private static Class<?> getParameterClass(ClassLoader classLoader,
			Map<String, ByteClass> byteClassesByMinecraftClassName, Entry entry)
			throws ClassNotFoundException {
		Class<?> result = StatelessResources.INSTANCE.getPrimitivesMap().get(
				entry.getType());
		if (result != null) {
			return result;
		} else if (entry.isExternal()) {
			ByteClass byteClass = byteClassesByMinecraftClassName.get(entry
					.getType());
			if (byteClass != null) {
				return classLoader.loadClass(byteClass.getByteClassName());
			} else {
				throw new ClassNotFoundException(
						"cannot resolce external class name: "
								+ entry.getType());
			}
		} else {
			return classLoader.loadClass(entry.getType());
		}
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
