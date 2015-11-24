package amidst.map;

import java.awt.image.BufferedImage;
import java.util.EnumMap;

import amidst.map.layer.BiomeLayer;
import amidst.map.layer.LayerType;
import amidst.map.layer.SlimeLayer;
import amidst.minecraft.world.BiomeDataProvider;
import amidst.minecraft.world.Resolution;

public class FragmentFactory {
	public Fragment create() {
		return new Fragment(createBiomeData(), createImagesMap());
	}

	private short[][] createBiomeData() {
		return BiomeDataProvider.createEmptyBiomeDataArray();
	}

	private EnumMap<LayerType, BufferedImage> createImagesMap() {
		EnumMap<LayerType, BufferedImage> result = new EnumMap<LayerType, BufferedImage>(
				LayerType.class);
		result.put(BiomeLayer.LAYER_TYPE,
				createBufferedImage(BiomeLayer.RESOLUTION));
		result.put(SlimeLayer.LAYER_TYPE,
				createBufferedImage(SlimeLayer.RESOLUTION));
		return result;
	}

	private BufferedImage createBufferedImage(Resolution resolution) {
		int size = Fragment.SIZE / resolution.getStep();
		return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	}
}
