package amidst.clazz.symbolic.declaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolicParameterDeclarationList {
	public static interface ExecuteOnEnd {
		public void run(SymbolicParameterDeclarationList parameters);
	}

	public static <T> Builder<T> builder(T nextBuilder,
			ExecuteOnEnd executeOnEnd) {
		return new Builder<T>(nextBuilder, executeOnEnd);
	}

	public static class Builder<T> {
		private T nextBuilder;
		private SymbolicParameterDeclarationList product = new SymbolicParameterDeclarationList();
		private ExecuteOnEnd executeOnEnd;

		private Builder(T nextBuilder, ExecuteOnEnd executeOnEnd) {
			this.nextBuilder = nextBuilder;
			this.executeOnEnd = executeOnEnd;
		}

		public Builder<T> real(String realType) {
			product.add(realType, false);
			return this;
		}

		public Builder<T> symbolic(String symbolicType) {
			product.add(symbolicType, true);
			return this;
		}

		public T end() {
			executeOnEnd.run(product);
			return nextBuilder;
		}
	}

	public class ParameterDeclaration {
		private String type;
		private boolean isSymbolic;

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

	private List<ParameterDeclaration> declarations = new ArrayList<ParameterDeclaration>();
	private List<ParameterDeclaration> declarationsView;

	public SymbolicParameterDeclarationList() {
		this.declarationsView = Collections.unmodifiableList(declarations);
	}

	private void add(String type, boolean isSymbolic) {
		declarations.add(new ParameterDeclaration(type, isSymbolic));
	}

	public List<ParameterDeclaration> getDeclarations() {
		return declarationsView;
	}
}
