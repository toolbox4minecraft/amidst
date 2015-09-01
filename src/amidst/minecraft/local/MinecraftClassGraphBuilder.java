package amidst.minecraft.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.byteclass.ByteClass;

public class MinecraftClassGraphBuilder {
	private ClassLoader classLoader;
	private Map<String, ByteClass> byteClassesByMinecraftClassName;
	private Map<String, MinecraftClass> minecraftClassesByByteClassName = new HashMap<String, MinecraftClass>();
	private Map<String, MinecraftClassBuilder> minecraftClassBuildersByMinecraftClassName = new HashMap<String, MinecraftClassBuilder>();

	public MinecraftClassGraphBuilder(ClassLoader classLoader,
			Map<String, ByteClass> byteClassesByMinecraftClassName) {
		this.classLoader = classLoader;
		this.byteClassesByMinecraftClassName = byteClassesByMinecraftClassName;
	}

	public Map<String, MinecraftClass> create() {
		populateMinecraftClassMaps();
		addPropertiesMethodsAndConstructors();
		return createProduct();
	}

	private void populateMinecraftClassMaps() {
		for (Entry<String, ByteClass> entry : byteClassesByMinecraftClassName
				.entrySet()) {
			String minecraftClassName = entry.getKey();
			String byteClassName = entry.getValue().getByteClassName();
			MinecraftClassBuilder builder = new MinecraftClassBuilder(
					classLoader, minecraftClassesByByteClassName,
					minecraftClassName, byteClassName);
			minecraftClassesByByteClassName
					.put(byteClassName, builder.create());
			minecraftClassBuildersByMinecraftClassName.put(minecraftClassName,
					builder);
		}
	}

	private void addPropertiesMethodsAndConstructors() {
		for (Entry<String, ByteClass> entry : byteClassesByMinecraftClassName
				.entrySet()) {
			ByteClass byteClass = entry.getValue();
			MinecraftClassBuilder builder = minecraftClassBuildersByMinecraftClassName
					.get(entry.getKey());
			addConstructors(builder, byteClass.getConstructors());
			addMethods(builder, byteClass.getMethods());
			addProperties(builder, byteClass.getProperties());
		}
	}

	private void addConstructors(MinecraftClassBuilder builder,
			List<String[]> constructors) {
		for (String[] constructor : constructors) {
			ParameterStringParser parser = new ParameterStringParser(
					byteClassesByMinecraftClassName, constructor[0],
					constructor[1]);
			builder.addConstructor(parser.getMinecraftName(),
					parser.getParameterByteNames());
		}
	}

	private void addMethods(MinecraftClassBuilder builder,
			List<String[]> methods) {
		for (String[] method : methods) {
			ParameterStringParser parser = new ParameterStringParser(
					byteClassesByMinecraftClassName, method[0], method[1]);
			builder.addMethod(parser.getMinecraftName(), parser.getByteName(),
					parser.getParameterByteNames());
		}
	}

	private void addProperties(MinecraftClassBuilder builder,
			List<String[]> properties) {
		for (String[] property : properties) {
			builder.addProperty(property[1], property[0]);
		}
	}

	private Map<String, MinecraftClass> createProduct() {
		Map<String, MinecraftClass> result = new HashMap<String, MinecraftClass>();
		for (Entry<String, MinecraftClassBuilder> entry : minecraftClassBuildersByMinecraftClassName
				.entrySet()) {
			result.put(entry.getKey(), entry.getValue().create());
		}
		return result;
	}
}
