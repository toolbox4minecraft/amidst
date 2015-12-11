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
		private final List<ParameterDeclaration> declarations = new ArrayList<ParameterDeclaration>();
		private final ExecuteOnEnd executeOnEnd;

		public SymbolicParameterDeclarationListBuilder(T nextBuilder,
				ExecuteOnEnd executeOnEnd) {
			this.nextBuilder = nextBuilder;
			this.executeOnEnd = executeOnEnd;
		}

		public SymbolicParameterDeclarationListBuilder<T> real(String realType) {
			declarations.add(new ParameterDeclaration(realType, false));
			return this;
		}

		public SymbolicParameterDeclarationListBuilder<T> symbolic(
				String symbolicType) {
			declarations.add(new ParameterDeclaration(symbolicType, true));
			return this;
		}

		public T end() {
			executeOnEnd
					.run(new SymbolicParameterDeclarationList(declarations));
			return nextBuilder;
		}
	}

	@Immutable
	public static class ParameterDeclaration {
		private final String type;
		private final boolean isSymbolic;

		private ParameterDeclaration(String type, boolean isSymbolic) {
			this.type = type;
			this.isSymbolic = isSymbolic;
		}

		public String getType() {
			return type;
		}

		public boolean isSymbolic() {
			return isSymbolic;
		}
	}

	private final List<ParameterDeclaration> declarations;

	public SymbolicParameterDeclarationList(
			List<ParameterDeclaration> declarations) {
		this.declarations = Collections.unmodifiableList(declarations);
	}

	public List<ParameterDeclaration> getDeclarations() {
		return declarations;
	}
}
