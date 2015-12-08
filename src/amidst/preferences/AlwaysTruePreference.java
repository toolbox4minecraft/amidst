package amidst.preferences;

import java.io.IOException;

public class AlwaysTruePreference implements PrefModel<Boolean> {
	@Override
	public String getKey() {
		throw new UnsupportedOperationException(
				"AlwaysTruePreference has no key!");
	}

	@Override
	public Boolean get() {
		return true;
	}

	@Override
	public void set(Boolean value) throws IOException {
		throw new UnsupportedOperationException(
				"AlwaysTruePreference cannot be set!");
	}
}
