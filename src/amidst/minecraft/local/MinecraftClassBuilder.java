package amidst.minecraft.local;

import java.util.HashMap;
import java.util.Map;

public class MinecraftClassBuilder {
	private Map<String, MinecraftConstructor> constructorsByMinecraftName = new HashMap<String, MinecraftConstructor>();
	private Map<String, MinecraftMethod> methodsByMinecraftName = new HashMap<String, MinecraftMethod>();
	private Map<String, MinecraftProperty> propertiesByMinecraftName = new HashMap<String, MinecraftProperty>();

	private ClassLoader classLoader;
	private Map<String, MinecraftClass> minecraftClassesByByteClassName;
	private MinecraftClass product;

	public MinecraftClassBuilder(ClassLoader classLoader,
			Map<String, MinecraftClass> minecraftClassesByByteClassName,
			String minecraftClassName, String byteClassName) {
		this.classLoader = classLoader;
		this.minecraftClassesByByteClassName = minecraftClassesByByteClassName;
		this.product = new MinecraftClass(minecraftClassName, byteClassName,
				loadClass(byteClassName), constructorsByMinecraftName,
				methodsByMinecraftName, propertiesByMinecraftName);
	}

	public Class<?> loadClass(String byteClassName) {
		try {
			return classLoader.loadClass(byteClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Error loading a class ("
					+ byteClassName + ")", e);
		}
	}

	public void addConstructor(String minecraftName,
			String... parameterByteNames) {
		MinecraftConstructor constructor = MinecraftClasses.createConstructor(
				classLoader, product, minecraftName, parameterByteNames);
		constructorsByMinecraftName.put(minecraftName, constructor);
	}

	public void addMethod(String minecraftName, String byteName,
			String... parameterByteNames) {
		MinecraftMethod method = MinecraftClasses.createMethod(classLoader,
				minecraftClassesByByteClassName, product, minecraftName,
				byteName, parameterByteNames);
		methodsByMinecraftName.put(minecraftName, method);
	}

	public void addProperty(String minecraftName, String byteName) {
		MinecraftProperty property = MinecraftClasses.createProperty(
				minecraftClassesByByteClassName, product, minecraftName,
				byteName);
		propertiesByMinecraftName.put(minecraftName, property);
	}

	public MinecraftClass create() {
		return product;
	}
}
