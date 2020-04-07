package amidst.clazz.symbolic.declaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import amidst.documentation.Immutable;

@Immutable
public class SymbolicParameterDeclarationList {
	public static interface ExecuteOnEnd {
		public void run(SymbolicParameterDeclarationList parameters);
	}

	@Immutable
	public static class SymbolicParameterDeclarationListBuilder<T> {
		private final T nextBuilder;
		private final List<SymbolicParameterDeclaration> declarations = new ArrayList<>();
		private final ExecuteOnEnd executeOnEnd;

		public SymbolicParameterDeclarationListBuilder(T nextBuilder, ExecuteOnEnd executeOnEnd) {
			this.nextBuilder = nextBuilder;
			this.executeOnEnd = executeOnEnd;
		}
		
		public SymbolicParameterDeclarationListBuilder<T> real(String realType) {
			declarations.add(new SymbolicParameterDeclaration(realType, false));
			return this;
		}
		
		public SymbolicParameterDeclarationListBuilder<T> realArray(String realType, int dimensions) {
			declarations.add(new SymbolicParameterDeclaration(realType, false, dimensions));
			return this;
		}

		public SymbolicParameterDeclarationListBuilder<T> symbolic(String symbolicType) {
			declarations.add(new SymbolicParameterDeclaration(symbolicType, true));
			return this;
		}
		
		public SymbolicParameterDeclarationListBuilder<T> symbolicArray(String symbolicType, int dimensions) {
			declarations.add(new SymbolicParameterDeclaration(symbolicType, true, dimensions));
			return this;
		}

		public T end() {
			executeOnEnd.run(new SymbolicParameterDeclarationList(declarations));
			return nextBuilder;
		}
	}

	private final List<SymbolicParameterDeclaration> declarations;

	public SymbolicParameterDeclarationList(List<SymbolicParameterDeclaration> declarations) {
		this.declarations = Collections.unmodifiableList(declarations);
	}

	public List<SymbolicParameterDeclaration> getDeclarations() {
		return declarations;
	}

	public String getParameterString() {
		String separator = "";
		StringBuilder stringBuilder = new StringBuilder("(");
		for (SymbolicParameterDeclaration declaration : declarations) {
			stringBuilder.append(separator).append(declaration.getParameterString());
			separator = ", ";
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
