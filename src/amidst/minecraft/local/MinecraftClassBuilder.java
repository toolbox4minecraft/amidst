package amidst.minecraft.local;

import java.util.HashMap;
import java.util.Map;

import amidst.byteclass.ByteClass;
import amidst.byteclass.ConstructorDeclaration;
import amidst.byteclass.MethodDeclaration;
import amidst.byteclass.PropertyDeclaration;

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

	public void addConstructor(
			Map<String, ByteClass> byteClassesByMinecraftClassName,
			ConstructorDeclaration declaration) {
		MinecraftConstructor constructor = MinecraftClasses.createConstructor(
				classLoader, product, byteClassesByMinecraftClassName,
				declaration);
		constructorsByMinecraftName.put(declaration.getExternalName(),
				constructor);
	}

	public void addMethod(
			Map<String, ByteClass> byteClassesByMinecraftClassName,
			MethodDeclaration declaration) {
		MinecraftMethod method = MinecraftClasses.createMethod(classLoader,
				minecraftClassesByByteClassName, product,
				byteClassesByMinecraftClassName, declaration);
		methodsByMinecraftName.put(declaration.getExternalName(), method);
	}

	public void addProperty(PropertyDeclaration declaration) {
		MinecraftProperty property = MinecraftClasses.createProperty(
				minecraftClassesByByteClassName, product, declaration);
		propertiesByMinecraftName.put(declaration.getExternalName(), property);
	}

	public MinecraftClass create() {
		return product;
	}
}
