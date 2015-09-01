package amidst.byteclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterDeclarationList {
	public static interface ExecuteOnEnd {
		public void run(ParameterDeclarationList parameters);
	}

	public static <T> Builder<T> builder(T nextBuilder,
			ExecuteOnEnd executeOnEnd) {
		return new Builder<T>(nextBuilder, executeOnEnd);
	}

	public static class Builder<T> {
		private T nextBuilder;
		private ParameterDeclarationList product = new ParameterDeclarationList();
		private ExecuteOnEnd executeOnEnd;

		private Builder(T nextBuilder, ExecuteOnEnd executeOnEnd) {
			this.nextBuilder = nextBuilder;
			this.executeOnEnd = executeOnEnd;
		}

		public Builder<T> real(String realType) {
			product.addEntry(realType, false);
			return this;
		}

		public Builder<T> symbolic(String symbolicType) {
			product.addEntry(symbolicType, true);
			return this;
		}

		public T end() {
			executeOnEnd.run(product);
			return nextBuilder;
		}
	}

	public class Entry {
		private String type;
		private boolean isSymbolic;

		private Entry(String type, boolean isSymbolic) {
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

	private List<Entry> entries = new ArrayList<Entry>();
	private List<Entry> entriesView;

	public ParameterDeclarationList() {
		this.entriesView = Collections.unmodifiableList(entries);
	}

	private void addEntry(String type, boolean isSymbolic) {
		entries.add(new Entry(type, isSymbolic));
	}

	public List<Entry> getEntries() {
		return entriesView;
	}
}
