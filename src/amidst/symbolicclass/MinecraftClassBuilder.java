package amidst.symbolicclass;

import java.util.HashMap;
import java.util.Map;

import amidst.byteclass.ByteClass;
import amidst.byteclass.ConstructorDeclaration;
import amidst.byteclass.MethodDeclaration;
import amidst.byteclass.PropertyDeclaration;

public class MinecraftClassBuilder {
	private Map<String, MinecraftConstructor> constructorsBySymbolicName = new HashMap<String, MinecraftConstructor>();
	private Map<String, MinecraftMethod> methodsBySymbolicName = new HashMap<String, MinecraftMethod>();
	private Map<String, MinecraftProperty> propertiesBySymbolicName = new HashMap<String, MinecraftProperty>();

	private ClassLoader classLoader;
	private Map<String, MinecraftClass> symbolicClassesByRealClassName;
	private MinecraftClass product;

	public MinecraftClassBuilder(ClassLoader classLoader,
			Map<String, MinecraftClass> symbolicClassesByRealClassName,
			String symbolicClassName, String realClassName) {
		this.classLoader = classLoader;
		this.symbolicClassesByRealClassName = symbolicClassesByRealClassName;
		this.product = new MinecraftClass(symbolicClassName, realClassName,
				loadClass(realClassName), constructorsBySymbolicName,
				methodsBySymbolicName, propertiesBySymbolicName);
	}

	public Class<?> loadClass(String realClassName) {
		try {
			return classLoader.loadClass(realClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Error loading a class ("
					+ realClassName + ")", e);
		}
	}

	public void addConstructor(
			Map<String, ByteClass> realClassesBySymbolicClassName,
			ConstructorDeclaration declaration) {
		MinecraftConstructor constructor = MinecraftClasses.createConstructor(
				classLoader, product, realClassesBySymbolicClassName,
				declaration);
		constructorsBySymbolicName.put(declaration.getSymbolicName(),
				constructor);
	}

	public void addMethod(
			Map<String, ByteClass> realClassesBySymbolicClassName,
			MethodDeclaration declaration) {
		MinecraftMethod method = MinecraftClasses.createMethod(classLoader,
				symbolicClassesByRealClassName, product,
				realClassesBySymbolicClassName, declaration);
		methodsBySymbolicName.put(declaration.getSymbolicName(), method);
	}

	public void addProperty(PropertyDeclaration declaration) {
		MinecraftProperty property = MinecraftClasses.createProperty(
				symbolicClassesByRealClassName, product, declaration);
		propertiesBySymbolicName.put(declaration.getSymbolicName(), property);
	}

	public MinecraftClass create() {
		return product;
	}
}
