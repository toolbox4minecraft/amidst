package amidst.symbolicclass;

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
import amidst.minecraft.local.StatelessResources;

public class SymbolicClasses {
	private SymbolicClasses() {
	}

	public static Map<String, SymbolicClass> createClasses(
			ClassLoader classLoader,
			Map<String, ByteClass> realClassesBySymbolicClassName) {
		return new SymbolicClassGraphBuilder(classLoader,
				realClassesBySymbolicClassName).create();
	}

	public static SymbolicConstructor createConstructor(
			ClassLoader classLoader, SymbolicClass parent,
			Map<String, ByteClass> realClassesBySymbolicClassName,
			ConstructorDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(classLoader,
					realClassesBySymbolicClassName, declaration.getParameters()
							.getEntries());
			Constructor<?> constructor = getConstructor(parent.getClazz(),
					parameterClasses);
			return new SymbolicConstructor(parent, symbolicName, constructor);
		} catch (SecurityException e) {
			throwRuntimeException(parent, symbolicName, e, "constructor");
		} catch (NoSuchMethodException e) {
			throwRuntimeException(parent, symbolicName, e, "constructor");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(parent, symbolicName, e, "constructor");
		}
		return null;
	}

	public static SymbolicMethod createMethod(ClassLoader classLoader,
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			SymbolicClass parent,
			Map<String, ByteClass> realClassesBySymbolicClassName,
			MethodDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		try {
			Class<?>[] parameterClasses = getParameterClasses(classLoader,
					realClassesBySymbolicClassName, declaration.getParameters()
							.getEntries());
			Method method = getMethod(parent.getClazz(), realName,
					parameterClasses);
			SymbolicClass returnType = getType(symbolicClassesByRealClassName,
					method.getReturnType());
			return new SymbolicMethod(symbolicName, realName, method,
					returnType);
		} catch (SecurityException e) {
			warn(parent, symbolicName, e, "method");
		} catch (NoSuchMethodException e) {
			warn(parent, symbolicName, e, "method");
		} catch (ClassNotFoundException e) {
			throwRuntimeException(parent, symbolicName, e, "method");
		}
		return null;
	}

	public static SymbolicProperty createProperty(
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			SymbolicClass parent, PropertyDeclaration declaration) {
		String symbolicName = declaration.getSymbolicName();
		String realName = declaration.getRealName();
		try {
			Field field = getField(parent.getClazz(), realName);
			SymbolicClass type = getType(symbolicClassesByRealClassName,
					field.getType());
			return new SymbolicProperty(parent, symbolicName, realName, field,
					type);
		} catch (SecurityException e) {
			throwRuntimeException(parent, symbolicName, e, "property");
		} catch (NoSuchFieldException e) {
			throwRuntimeException(parent, symbolicName, e, "property");
		}
		return null;
	}

	private static Constructor<?> getConstructor(Class<?> clazz,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Constructor<?> result = clazz.getConstructor(parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private static Method getMethod(Class<?> clazz, String realName,
			Class<?>[] parameterClasses) throws NoSuchMethodException {
		Method result = clazz.getDeclaredMethod(realName, parameterClasses);
		result.setAccessible(true);
		return result;
	}

	private static Field getField(Class<?> clazz, String realName)
			throws NoSuchFieldException {
		Field result = clazz.getDeclaredField(realName);
		result.setAccessible(true);
		return result;
	}

	private static Class<?>[] getParameterClasses(ClassLoader classLoader,
			Map<String, ByteClass> realClassesBySymbolicClassName,
			List<Entry> entries) throws ClassNotFoundException {
		Class<?>[] result = new Class<?>[entries.size()];
		for (int i = 0; i < entries.size(); i++) {
			result[i] = getParameterClass(classLoader,
					realClassesBySymbolicClassName, entries.get(i));
		}
		return result;
	}

	private static Class<?> getParameterClass(ClassLoader classLoader,
			Map<String, ByteClass> realClassesBySymbolicClassName, Entry entry)
			throws ClassNotFoundException {
		Class<?> result = StatelessResources.INSTANCE.getPrimitivesMap().get(
				entry.getType());
		if (result != null) {
			return result;
		} else if (entry.isSymbolic()) {
			ByteClass realClass = realClassesBySymbolicClassName.get(entry
					.getType());
			if (realClass != null) {
				return classLoader.loadClass(realClass.getRealClassName());
			} else {
				throw new ClassNotFoundException(
						"cannot resolve symbolic class name: "
								+ entry.getType());
			}
		} else {
			return classLoader.loadClass(entry.getType());
		}
	}

	private static SymbolicClass getType(
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			Class<?> type) {
		String result = type.getName();
		if (result.contains(".")) {
			String[] typeSplit = result.split("\\.");
			result = typeSplit[typeSplit.length - 1];
		}
		return symbolicClassesByRealClassName.get(result);
	}

	private static void throwRuntimeException(SymbolicClass parent,
			String symbolicName, Exception e, String featureType) {
		throw new RuntimeException(errorMessage(parent, symbolicName, e,
				featureType), e);
	}

	private static void warn(SymbolicClass parent, String symbolicName,
			Exception e, String featureType) {
		Log.w(errorMessage(parent, symbolicName, e, featureType));
		e.printStackTrace();
	}

	private static String errorMessage(SymbolicClass parent,
			String symbolicName, Exception e, String featureType) {
		return e.getClass().getSimpleName() + " on ("
				+ parent.getSymbolicName() + " / " + parent.getRealName()
				+ ") " + featureType + " (" + symbolicName + ")";
	}
}
