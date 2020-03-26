package amidst.mojangapi.world.export;

import java.nio.file.Path;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class WorldExporterConfiguration {
	private final Path imageFile;
	private final boolean useQuarterResolution;
	private final CoordinatesInWorld topLeftCoord;
	private final CoordinatesInWorld bottomRightCoord;
	private final BiomeProfileSelection biomeProfileSelection;
	
	public WorldExporterConfiguration(Path imageFile, boolean useQuarterResolution, CoordinatesInWorld topLeftCoord, CoordinatesInWorld bottomRightCoord, BiomeProfileSelection biomeProfileSelection) {
		this.imageFile = imageFile;
		this.useQuarterResolution = useQuarterResolution;
		this.topLeftCoord = topLeftCoord;
		this.bottomRightCoord = bottomRightCoord;
		this.biomeProfileSelection = biomeProfileSelection;
	}

	public Path getImageFile() {
		return imageFile;
	}

	public boolean isQuarterResolution() {
		return useQuarterResolution;
	}

	public CoordinatesInWorld getTopLeftCoord() {
		return topLeftCoord;
	}

	public CoordinatesInWorld getBottomRightCoord() {
		return bottomRightCoord;
	}
	
	public BiomeProfileSelection getBiomeProfileSelection() {
		return biomeProfileSelection;
	}
	
}
