package amidst.mojangapi.world.versionfeatures;

import java.util.Map.Entry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;

public class VersionFeatureBiMapBuilder<K, V, B extends VersionFeatureBiMapBuilder<K, V, B>> extends VersionFeatureBuilder<BiMap<K, V>, B> {

	public VersionFeatureBiMapBuilder() {
		super();
	}

	@SafeVarargs
	public final B init(Entry<K, V>... defaultEntries) {
		BiMap<K, V> map = HashBiMap.<K, V>create(defaultEntries.length);
		for(Entry<K, V> entry : defaultEntries) {
			map.put(entry.getKey(), entry.getValue());
		}
		return init(map);
	}

	@SafeVarargs
	public final B since(RecognisedVersion since, Entry<K, V>... entries) {
		BiMap<K, V> map = HashBiMap.<K, V>create(entries.length);
		for(Entry<K, V> entry : entries) {
			map.put(entry.getKey(), entry.getValue());
		}
		return since(since, map);
	}

	@SafeVarargs
	public final B sinceExtend(RecognisedVersion since, Entry<K, V>... additionalEntries) {
		return sinceUpdate(since, (features, oldValue) -> {
			for (Entry<K, V> additionalEntry : additionalEntries) {
				oldValue.put(additionalEntry.getKey(), additionalEntry.getValue());
			}
			return oldValue;
		});
	}

	@Override
	public VersionFeature<BiMap<K, V>> construct() {
		return super.construct().andThen(Maps::unmodifiableBiMap);
	}
}