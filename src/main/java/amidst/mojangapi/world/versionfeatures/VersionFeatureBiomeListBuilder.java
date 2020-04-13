package amidst.mojangapi.world.versionfeatures;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.world.biome.Biome;

public class VersionFeatureBiomeListBuilder<B extends VersionFeatureBiomeListBuilder<B>> extends VersionFeatureBuilder<BiomeList, B> {

	public VersionFeatureBiomeListBuilder() {
		super();
	}

	@SafeVarargs
	public final B init(Biome... defaultValues) {
		return init(new BiomeList(defaultValues));
	}

	@SafeVarargs
	public final B since(RecognisedVersion since, Biome... values) {
		return since(since, new BiomeList(values));
	}

	@SafeVarargs
	public final B sinceExtend(RecognisedVersion since, Biome... additionalValues) {
		return sinceUpdate(since, (features, oldValue) -> oldValue.addAllToNew(oldValue, additionalValues));
	}

	@Override
	public VersionFeature<BiomeList> construct() {
		return super.construct().andThen(BiomeList::construct);
	}
}