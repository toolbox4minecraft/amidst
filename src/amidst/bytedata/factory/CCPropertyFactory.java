package amidst.bytedata.factory;

import java.util.ArrayList;
import java.util.List;

import amidst.bytedata.CCProperty;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.factory.ClassCheckerBuilder.ClassCheckerFactory;

public class CCPropertyFactory extends ClassCheckerFactory {
	private String name;
	private List<String> properties = new ArrayList<String>();

	public CCPropertyFactory(String name) {
		this.name = name;
	}

	public CCPropertyFactory property(String val1, String val2) {
		properties.add(val1);
		properties.add(val2);
		return this;
	}

	@Override
	protected ClassChecker get() {
		ensurePropertyPresent();
		return new CCProperty(name,
				properties.toArray(new String[properties.size()]));
	}

	private void ensurePropertyPresent() {
		if (properties.isEmpty()) {
			throw new IllegalStateException(
					"property matcher must have at least one property");
		}
	}
}
