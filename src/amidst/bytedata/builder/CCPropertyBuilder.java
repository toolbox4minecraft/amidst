package amidst.bytedata.builder;

import java.util.ArrayList;
import java.util.List;

import amidst.bytedata.CCProperty;
import amidst.bytedata.ClassChecker;
import amidst.bytedata.builder.ClassCheckerBuilder.SimpleClassCheckerBuilder;

public class CCPropertyBuilder extends SimpleClassCheckerBuilder {
	private String name;
	private List<String> properties = new ArrayList<String>();

	public CCPropertyBuilder(String name) {
		this.name = name;
	}

	public CCPropertyBuilder property(String val1, String val2) {
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
