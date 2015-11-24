package amidst.map.layer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

import amidst.map.Fragment;
import amidst.map.Map;
import amidst.minecraft.world.CoordinatesInWorld;
import amidst.minecraft.world.World;
import amidst.minecraft.world.finder.WorldObject;
import amidst.minecraft.world.finder.WorldObjectConsumer;
import amidst.minecraft.world.finder.WorldObjectProducer;
import amidst.preferences.BooleanPrefModel;

public abstract class IconLayer extends Layer {
	private final AffineTransform iconLayerMatrix = new AffineTransform();

	public IconLayer(World world, Map map, LayerType layerType) {
		super(world, map, layerType);
	}

	@Override
	public boolean isVisible() {
		return getIsVisiblePreference().get();
	}

	@Override
	public void load(Fragment fragment, int[] imageCache) {
		doLoad(fragment);
	}

	@Override
	public void reload(Fragment fragment, int[] imageCache) {
		fragment.removeMapObjects(layerType);
		doLoad(fragment);
	}

	protected void doLoad(Fragment fragment) {
		getProducer().produce(fragment.getCorner(),
				createWorldObjectConsumer(fragment));
	}

	private WorldObjectConsumer createWorldObjectConsumer(
			final Fragment fragment) {
		return new WorldObjectConsumer() {
			@Override
			public void consume(WorldObject worldObject) {
				fragment.addMapObject(layerType, new MapObject(worldObject,
						IconLayer.this));
			}
		};
	}

	@Override
	public void draw(Fragment fragment, Graphics2D g2d,
			AffineTransform layerMatrix) {
		List<MapObject> mapObjects = fragment.getMapObjects(layerType);
		double invZoom = 1.0 / map.getZoom();
		for (MapObject mapObject : mapObjects) {
			drawObject(mapObject, invZoom, g2d, layerMatrix);
		}
	}

	private void drawObject(MapObject mapObject, double invZoom,
			Graphics2D g2d, AffineTransform layerMatrix) {
		WorldObject worldObject = mapObject.getWorldObject();
		BufferedImage image = worldObject.getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		if (map.getSelectedMapObject() == mapObject) {
			width *= 1.5;
			height *= 1.5;
		}
		initIconLayerMatrix(invZoom, worldObject.getCoordinates(), layerMatrix);
		g2d.setTransform(iconLayerMatrix);
		g2d.drawImage(image, -(width >> 1), -(height >> 1), width, height, null);
	}

	private void initIconLayerMatrix(double invZoom,
			CoordinatesInWorld coordinates, AffineTransform layerMatrix) {
		iconLayerMatrix.setTransform(layerMatrix);
		iconLayerMatrix.translate(coordinates.getXRelativeToFragment(),
				coordinates.getYRelativeToFragment());
		iconLayerMatrix.scale(invZoom, invZoom);
	}

	protected abstract BooleanPrefModel getIsVisiblePreference();

	protected abstract WorldObjectProducer getProducer();
}
