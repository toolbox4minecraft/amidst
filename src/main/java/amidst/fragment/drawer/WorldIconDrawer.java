package amidst.fragment.drawer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;
import amidst.gui.main.viewer.WorldIconSelection;
import amidst.gui.main.viewer.Zoom;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.settings.Setting;

@NotThreadSafe
public class WorldIconDrawer extends FragmentDrawer {
	private final Zoom zoom;
	private final WorldIconSelection worldIconSelection;
	private final Setting<Boolean> useHybridScaling;

	public WorldIconDrawer(
			LayerDeclaration declaration,
			Zoom zoom,
			WorldIconSelection worldIconSelection,
			Setting<Boolean> useHybridScaling) {
		super(declaration);
		this.zoom = zoom;
		this.worldIconSelection = worldIconSelection;
		this.useHybridScaling = useHybridScaling;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void draw(Fragment fragment, Graphics2D g2d, float time) {
		double invZoom = 1.0 / zoom.getCurrentValue();
		
		Object oldHint = g2d.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		AffineTransform originalTransform = g2d.getTransform();
		
		for (WorldIcon icon : fragment.getWorldIcons(declaration.getLayerId())) {
			drawIcon(icon, invZoom, g2d);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldHint);
			g2d.setTransform(originalTransform);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawIcon(WorldIcon icon, double invZoom, Graphics2D g2d) {
		BufferedImage image = icon.getImage().getImage();
		int width = image.getWidth();
		int height = image.getHeight();
		if (worldIconSelection.isSelected(icon)) {
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, getInterpolationHint());
			width *= 1.5;
			height *= 1.5;
		}
		CoordinatesInWorld coordinates = icon.getCoordinates();
		g2d.translate(coordinates.getXRelativeToFragment(), coordinates.getYRelativeToFragment());
		g2d.scale(invZoom, invZoom);
		g2d.drawImage(image, -(width >> 1), -(height >> 1), width, height, null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Object getInterpolationHint() {
		if (useHybridScaling.get()) {
			return RenderingHints.VALUE_INTERPOLATION_BICUBIC;
		} else {
			return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		}
	}
}
