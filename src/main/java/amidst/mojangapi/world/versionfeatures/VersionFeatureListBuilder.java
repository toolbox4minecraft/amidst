package amidst.mojangapi.world.versionfeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;

public class VersionFeatureListBuilder<V, B extends VersionFeatureListBuilder<V, B>> extends VersionFeatureBuilder<List<V>, B> {

	public VersionFeatureListBuilder() {
		super();
	}

	@SafeVarargs
	public final B init(V... defaultValues) {
		return init(new ArrayList<>(Arrays.asList(defaultValues)));
	}

	@SafeVarargs
	public final B since(RecognisedVersion since, V... values) {
		return since(since, new ArrayList<>(Arrays.asList(values)));
	}

	@SafeVarargs
	public final B sinceExtend(RecognisedVersion since, V... additionalValues) {
		return sinceUpdate(since, (features, oldValue) -> {
			for (V additionalValue: additionalValues) {
				oldValue.add(additionalValue);
			}
			return oldValue;
		});
	}

	@Override
	public VersionFeature<List<V>> construct() {
		return super.construct().andThen(Collections::unmodifiableList);
	}
}