package amidst.bytedata.factory;

import java.util.ArrayList;
import java.util.List;

import amidst.bytedata.CCMethodPreset;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.ClassCheckerFactory;

public class CCMethodPresetFactory extends ClassCheckerFactory {
	private String name;
	private List<String> methods = new ArrayList<String>();

	public CCMethodPresetFactory(String name) {
		this.name = name;
	}

	public CCMethodPresetFactory method(String val1, String val2) {
		methods.add(val1);
		methods.add(val2);
		return this;
	}

	@Override
	protected ClassChecker get() {
		ensureMethodPresent();
		return new CCMethodPreset(name, methods.toArray(new String[methods
				.size()]));
	}

	private void ensureMethodPresent() {
		if (methods.isEmpty()) {
			throw new IllegalStateException(
					"method matcher must have at least one method");
		}
	}
}
