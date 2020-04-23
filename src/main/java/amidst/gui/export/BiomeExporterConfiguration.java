package amidst.gui.export;

import java.nio.file.Path;

import amidst.documentation.Immutable;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@Immutable
public class BiomeExporterConfiguration {
	private final Path imageFile;
	private final boolean useQuarterResolution;
	private final CoordinatesInWorld topLeftCoord;
	private final CoordinatesInWorld bottomRightCoord;
	private final BiomeProfileSelection biomeProfileSelection;
	
	public BiomeExporterConfiguration(Path imageFile, boolean useQuarterResolution, CoordinatesInWorld topLeftCoord, CoordinatesInWorld bottomRightCoord, BiomeProfileSelection biomeProfileSelection) {
		this.imageFile = imageFile;
		this.useQuarterResolution = useQuarterResolution;
		this.topLeftCoord = topLeftCoord;
		this.bottomRightCoord = bottomRightCoord;
		this.biomeProfileSelection = biomeProfileSelection;
	}

	public Path getImagePath() {
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
