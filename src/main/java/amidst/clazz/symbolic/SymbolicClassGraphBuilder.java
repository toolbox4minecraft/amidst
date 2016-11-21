package amidst.clazz.symbolic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import amidst.clazz.symbolic.declaration.SymbolicClassDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicConstructorDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicFieldDeclaration;
import amidst.clazz.symbolic.declaration.SymbolicMethodDeclaration;
import amidst.documentation.Immutable;

@Immutable
public class SymbolicClassGraphBuilder {
	private final ClassLoader classLoader;
	private final Map<SymbolicClassDeclaration, String> realClassNamesBySymbolicClassDeclaration;

	public SymbolicClassGraphBuilder(
			ClassLoader classLoader,
			Map<SymbolicClassDeclaration, String> realClassNamesBySymbolicClassDeclaration) {
		this.classLoader = classLoader;
		this.realClassNamesBySymbolicClassDeclaration = realClassNamesBySymbolicClassDeclaration;
	}

	public Map<String, SymbolicClass> construct() throws SymbolicClassGraphCreationException {
		Map<String, String> realClassNamesBySymbolicClassName = new HashMap<>();
		Map<String, SymbolicClass> symbolicClassesByRealClassName = new HashMap<>();
		Map<SymbolicClassDeclaration, SymbolicClassBuilder> symbolicClassBuildersBySymbolicClassDeclaration = new HashMap<>();
		createSymbolicClasses(
				realClassNamesBySymbolicClassName,
				symbolicClassesByRealClassName,
				symbolicClassBuildersBySymbolicClassDeclaration);
		addConstructorsMethodsAndFields(symbolicClassBuildersBySymbolicClassDeclaration);
		return createProduct(symbolicClassBuildersBySymbolicClassDeclaration);
	}

	private void createSymbolicClasses(
			Map<String, String> realClassNamesBySymbolicClassName,
			Map<String, SymbolicClass> symbolicClassesByRealClassName,
			Map<SymbolicClassDeclaration, SymbolicClassBuilder> symbolicClassBuildersBySymbolicClassDeclaration)
			throws SymbolicClassGraphCreationException {
		for (Entry<SymbolicClassDeclaration, String> entry : realClassNamesBySymbolicClassDeclaration.entrySet()) {
			SymbolicClassDeclaration declaration = entry.getKey();
			String symbolicClassName = declaration.getSymbolicClassName();
			String realClassName = entry.getValue();
			try {
				SymbolicClassBuilder builder = new SymbolicClassBuilder(
						classLoader,
						realClassNamesBySymbolicClassName,
						symbolicClassesByRealClassName,
						declaration.getSymbolicClassName(),
						realClassName);
				SymbolicClass symbolicClass = builder.getProduct();
				realClassNamesBySymbolicClassName.put(symbolicClassName, realClassName);
				symbolicClassesByRealClassName.put(realClassName, symbolicClass);
				symbolicClassBuildersBySymbolicClassDeclaration.put(declaration, builder);
			} catch (ClassNotFoundException e) {
				declaration.handleMissing(e, realClassName);
			}
		}
	}

	private void addConstructorsMethodsAndFields(
			Map<SymbolicClassDeclaration, SymbolicClassBuilder> symbolicClassBuildersBySymbolicClassDeclaration)
			throws SymbolicClassGraphCreationException {
		for (Entry<SymbolicClassDeclaration, SymbolicClassBuilder> entry : symbolicClassBuildersBySymbolicClassDeclaration
				.entrySet()) {
			SymbolicClassDeclaration declaration = entry.getKey();
			SymbolicClassBuilder builder = entry.getValue();
			addConstructors(builder, declaration.getConstructors());
			addMethods(builder, declaration.getMethods());
			addFields(builder, declaration.getFields());
		}
	}

	private void addConstructors(SymbolicClassBuilder builder, List<SymbolicConstructorDeclaration> constructors)
			throws SymbolicClassGraphCreationException {
		for (SymbolicConstructorDeclaration constructor : constructors) {
			builder.addConstructor(constructor);
		}
	}

	private void addMethods(SymbolicClassBuilder builder, List<SymbolicMethodDeclaration> methods)
			throws SymbolicClassGraphCreationException {
		for (SymbolicMethodDeclaration method : methods) {
			builder.addMethod(method);
		}
	}

	private void addFields(SymbolicClassBuilder builder, List<SymbolicFieldDeclaration> fields)
			throws SymbolicClassGraphCreationException {
		for (SymbolicFieldDeclaration field : fields) {
			builder.addField(field);
		}
	}

	private Map<String, SymbolicClass> createProduct(
			Map<SymbolicClassDeclaration, SymbolicClassBuilder> symbolicClassBuildersBySymbolicClassDeclaration) {
		Map<String, SymbolicClass> result = new HashMap<>();
		for (Entry<SymbolicClassDeclaration, SymbolicClassBuilder> entry : symbolicClassBuildersBySymbolicClassDeclaration
				.entrySet()) {
			result.put(entry.getKey().getSymbolicClassName(), entry.getValue().getProduct());
		}
		return result;
	}
}
