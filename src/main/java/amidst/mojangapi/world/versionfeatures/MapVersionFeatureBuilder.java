package amidst.mojangapi.world.versionfeatures;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@NotThreadSafe
public class MapVersionFeatureBuilder<K, V> {
	private static <K, V> Map<K, V> concat(Map<K, V> keyValues, Map<K, V> additionalKeyValues) {
		Map<K, V> result = new LinkedHashMap<>(keyValues.size() + additionalKeyValues.size());
		for(Map.Entry<K, V> keyValue: keyValues.entrySet()) result.put(keyValue.getKey(), keyValue.getValue());
		for(Map.Entry<K, V> keyValue: additionalKeyValues.entrySet()) result.put(keyValue.getKey(), keyValue.getValue());
		return result;
	}

	private Map<K, V> defaultValue = null;
	private RecognisedVersion previous = null;
	private RecognisedVersion previousExact = null;
	private final List<VersionFeatureEntry<Map<K, V>>> exactMatches = new LinkedList<>();
	private final List<VersionFeatureEntry<Map<K, V>>> entriesNewestFirst = new LinkedList<>();

	@SafeVarargs
	public final MapVersionFeatureBuilder<K, V> init(Entry<K, V>... defaultKeyValues) {
		Map<K, V> map = new LinkedHashMap<>(defaultKeyValues.length);
		for(Map.Entry<K, V> keyValue: defaultKeyValues) map.put(keyValue.getKey(), keyValue.getValue());		
		return init(map);
	}

	public MapVersionFeatureBuilder<K, V> init(Map<K, V> defaultMap) {
		if (this.defaultValue == null) {
			this.defaultValue = defaultMap;
			return this;
		} else {
			throw new IllegalStateException("only one default map is allowed");
		}
	}

	@SafeVarargs
	public final MapVersionFeatureBuilder<K, V> exact(RecognisedVersion version, Entry<K, V>... keyValues) {
		Map<K, V> map = new LinkedHashMap<>(keyValues.length);
		for(Map.Entry<K, V> keyValue: keyValues) map.put(keyValue.getKey(), keyValue.getValue());		
		return exact(version, map);
	}

	public MapVersionFeatureBuilder<K, V> exact(RecognisedVersion version, Map<K, V> map) {
		Objects.requireNonNull(version, "version cannot be null");
		if (this.defaultValue == null) {
			throw new IllegalStateException("you need to specify a default value first");
		} else if (previousExact != null && version == previousExact) {
			throw new IllegalStateException("you can only specify one value per version");
		} else if (previousExact != null && RecognisedVersion.isOlder(version, previousExact)) {
			throw new IllegalStateException("you have to specify versions in ascending order");
		} else if (!entriesNewestFirst.isEmpty()) {
			throw new IllegalStateException("you have to specify all exact matches before the first since");
		} else {
			previousExact = version;
			exactMatches.add(0, new VersionFeatureEntry<>(version, Collections.unmodifiableMap(map)));
			return this;
		}
	}

	@SafeVarargs
	public final MapVersionFeatureBuilder<K, V> since(RecognisedVersion since, Entry<K, V>... keyValues) {
		Map<K, V> map = new LinkedHashMap<>(keyValues.length);
		for(Map.Entry<K, V> keyValue: keyValues) map.put(keyValue.getKey(), keyValue.getValue());		
		return since(since, map);
	}

	@SafeVarargs
	public final MapVersionFeatureBuilder<K, V> sinceExtend(RecognisedVersion since, Entry<K, V>... additionalKeyValues) {
		Map<K, V> map = new LinkedHashMap<>(additionalKeyValues.length);
		for(Map.Entry<K, V> keyValue: additionalKeyValues) map.put(keyValue.getKey(), keyValue.getValue());		
		return since(since, concat(getLatest(), map));
	}

	public MapVersionFeatureBuilder<K, V> since(RecognisedVersion version, Map<K, V> value) {
		Objects.requireNonNull(version, "version cannot be null");
		if (this.defaultValue == null) {
			throw new IllegalStateException("you need to specify a default value first");
		} else if (previous != null && version == previous) {
			throw new IllegalStateException("you can only specify one value per version");
		} else if (previous != null && RecognisedVersion.isOlder(version, previous)) {
			throw new IllegalStateException("you have to specify versions in ascending order");
		} else {
			previous = version;
			entriesNewestFirst.add(0, new VersionFeatureEntry<>(version, Collections.unmodifiableMap(value)));
			return this;
		}
	}

	private Map<K, V> getLatest() {
		if (this.defaultValue == null) {
			throw new IllegalStateException("you need to specify a default value first");
		} else if (entriesNewestFirst.isEmpty()) {
			return defaultValue;
		} else {
			return entriesNewestFirst.get(0).getValue();
		}
	}

	public VersionFeature<Map<K, V>> construct() {
		return new VersionFeature<>(
				defaultValue,
				Collections.unmodifiableList(exactMatches),
				Collections.unmodifiableList(entriesNewestFirst));
	}
}
