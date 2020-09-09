package amidst.mojangapi.minecraftinterface;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import amidst.clazz.symbolic.SymbolicClass;

public enum ReflectionUtils {
	;
	
	public static MethodHandle getMethodHandle(SymbolicClass symbolicClass, String method) throws IllegalAccessException {
	    Method rawMethod = symbolicClass.getMethod(method).getRawMethod();
	    MethodHandle mh = MethodHandles.lookup().unreflect(rawMethod);
	    return mh.asType(eraseTypesKeepArrays(mh.type()));
	}

	public static MethodType eraseTypesKeepArrays(MethodType type) {
		Class<?>[] newParameters = type.parameterArray();
		for (int i= 0; i < newParameters.length; i++){
			newParameters[i] = eraseTypeKeepArrays(newParameters[i]);
		}
		
		return MethodType.methodType(eraseTypeKeepArrays(type.returnType()), newParameters);
	}

	public static Class<?> eraseTypeKeepArrays(Class<?> originalType) {
		Class<?> newType = originalType;
		
		boolean isArray = newType.isArray();
		boolean isPrimitive = newType.isPrimitive() || (isArray ? newType.getComponentType().isPrimitive() : false);
		
		if (!isPrimitive) {
			newType = Object.class;
			
			if (isArray) {
				newType = Array.newInstance(newType, 0).getClass();
			}
		}
		
		return newType;
	}

	public static Object callParameterlessMethodReturning(Object obj, Class<?> retClass)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, MinecraftInterfaceException {
		Method candidate = null;
		for (Method meth: obj.getClass().getMethods()) {
			if (((meth.getModifiers() & Modifier.STATIC) == 0)
			&& meth.getParameterCount() == 0
			&& retClass.isAssignableFrom(meth.getReturnType())) {
				if (candidate == null) {
					candidate = meth;
				} else {
					throw new MinecraftInterfaceException("found multiple methods returning " + retClass.getCanonicalName()
						+ " on class " + obj.getClass().getCanonicalName());
				}
			}
		}
		if (candidate == null) {
			throw new MinecraftInterfaceException("couldn't find method returning " + retClass.getCanonicalName()
				+ " on class " + obj.getClass().getCanonicalName());
		}
		return candidate.invoke(obj);
	}

}
