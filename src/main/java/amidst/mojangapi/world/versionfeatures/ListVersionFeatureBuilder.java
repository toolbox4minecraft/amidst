package amidst.mojangapi.world.versionfeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@NotThreadSafe
public class ListVersionFeatureBuilder<V> {
	private static <V> List<V> concat(List<V> values, List<V> additionalValues) {
		List<V> result = new ArrayList<>(values.size() + additionalValues.size());
		result.addAll(values);
		result.addAll(additionalValues);
		return result;
	}

	private List<V> defaultValue = null;
	private RecognisedVersion previous = null;
	private final List<VersionFeature.Entry<List<V>>> entriesNewestFirst = new LinkedList<>();

	@SafeVarargs
	public final ListVersionFeatureBuilder<V> init(V... defaultValues) {
		return init(Arrays.asList(defaultValues));
	}

	public ListVersionFeatureBuilder<V> init(List<V> defaultValue) {
		if (this.defaultValue == null) {
			this.defaultValue = defaultValue;
			return this;
		} else {
			throw new IllegalStateException("only one default value is allowed");
		}
	}

	@SafeVarargs
	public final ListVersionFeatureBuilder<V> since(RecognisedVersion since, V... values) {
		return since(since, Arrays.asList(values));
	}

	@SafeVarargs
	public final ListVersionFeatureBuilder<V> sinceExtend(RecognisedVersion since, V... additionalValues) {
		return since(since, concat(getLatest(), Arrays.asList(additionalValues)));
	}

	public ListVersionFeatureBuilder<V> since(RecognisedVersion version, List<V> value) {
		Objects.requireNonNull(version, "version cannot be null");
		if (this.defaultValue == null) {
			throw new IllegalStateException("you need to specify a default value first");
		} else if (previous != null && version == previous) {
			throw new IllegalStateException("you can only specify one value per version");
		} else if (previous != null && RecognisedVersion.isOlder(version, previous)) {
			throw new IllegalStateException("you have to specify versions in ascending order");
		} else {
			previous = version;
			entriesNewestFirst.add(0, new VersionFeature.Entry<>(version, Collections.unmodifiableList(value)));
			return this;
		}
	}

	private List<V> getLatest() {
		if (this.defaultValue == null) {
			throw new IllegalStateException("you need to specify a default value first");
		} else if (entriesNewestFirst.isEmpty()) {
			return defaultValue;
		} else {
			return entriesNewestFirst.get(0).getValue();
		}
	}

	public VersionFeature<List<V>> construct() {
		return new VersionFeature<>(
				defaultValue,
				Collections.unmodifiableList(entriesNewestFirst));
	}
}
