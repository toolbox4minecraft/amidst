package amidst.map.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import amidst.map.Map;
import amidst.map.MapViewer;
import amidst.map.layer.BiomeLayer;
import amidst.minecraft.Biome;
import amidst.minecraft.world.World;

public class BiomeWidget extends Widget {
	private static Color innerBoxBgColor = new Color(0.3f, 0.3f, 0.3f, 0.3f);
	private static Color biomeBgColor1 = new Color(0.8f, 0.8f, 0.8f, 0.2f);
	private static Color biomeBgColor2 = new Color(0.6f, 0.6f, 0.6f, 0.2f);
	private static Color biomeLitBgColor1 = new Color(0.8f, 0.8f, 1.0f, 0.7f);
	private static Color biomeLitBgColor2 = new Color(0.6f, 0.6f, 0.8f, 0.7f);
	private static Color innerBoxBorderColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	private static Color scrollbarColor = new Color(0.6f, 0.6f, 0.6f, 0.8f);
	private static Color scrollbarLitColor = new Color(0.6f, 0.6f, 0.8f, 0.8f);
	private static Color selectButtonColor = new Color(0.6f, 0.6f, 0.8f, 1.0f);

	private ArrayList<Biome> biomes = new ArrayList<Biome>();
	private int maxNameWidth = 0;
	private Rectangle innerBox = new Rectangle(0, 0, 1, 1);

	private int biomeListHeight;
	private int biomeListYOffset = 0;
	private boolean scrollbarVisible = false;
	private boolean scrollbarGrabbed = false;
	private int scrollbarHeight = 0, scrollbarWidth = 10, scrollbarY = 0,
			mouseYOnGrab = 0, scrollbarYOnGrab;

	public BiomeWidget(MapViewer mapViewer, Map map, World world,
			CornerAnchorPoint anchor) {
		super(mapViewer, map, world, anchor);

		FontMetrics fontMetrics = mapViewer.getFontMetrics(TEXT_FONT);
		for (int i = 0; i < Biome.biomes.length; i++) {
			if (Biome.biomes[i] != null) {
				biomes.add(Biome.biomes[i]);
				maxNameWidth = Math.max(
						fontMetrics.stringWidth(Biome.biomes[i].name),
						maxNameWidth);
			}
		}
		biomeListHeight = biomes.size() * 16;
		setWidth(250);
		setHeight(400);
		setY(100);
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics) {
		setX(mapViewer.getWidth() - getWidth());
		drawBorderAndBackground(g2d, time);
		g2d.drawString("Highlight Biomes", getX() + 10, getY() + 20);

		innerBox.x = getX() + 8;
		innerBox.y = getY() + 30;
		innerBox.width = getWidth() - 16;
		innerBox.height = getHeight() - 58;

		biomeListYOffset = Math.min(0,
				Math.max(-biomeListHeight + innerBox.height, biomeListYOffset));

		if (biomeListHeight > innerBox.height) {
			innerBox.width -= scrollbarWidth;
			scrollbarVisible = true;
		} else {
			scrollbarVisible = false;
		}

		g2d.setColor(innerBoxBgColor);
		g2d.fillRect(innerBox.x, innerBox.y, innerBox.width, innerBox.height);
		g2d.setColor(innerBoxBorderColor);
		g2d.drawRect(innerBox.x - 1, innerBox.y - 1, innerBox.width + 1
				+ (scrollbarVisible ? scrollbarWidth : 0), innerBox.height + 1);
		g2d.setClip(innerBox);

		for (int i = 0; i < biomes.size(); i++) {
			Biome biome = biomes.get(i);
			if (BiomeLayer.getInstance().isBiomeSelected(biome.index))
				g2d.setColor(((i % 2) == 1) ? biomeLitBgColor1
						: biomeLitBgColor2);
			else
				g2d.setColor(((i % 2) == 1) ? biomeBgColor1 : biomeBgColor2);
			g2d.fillRect(innerBox.x, innerBox.y + i * 16 + biomeListYOffset,
					innerBox.width, 16);
			g2d.setColor(new Color(biome.color));
			g2d.fillRect(innerBox.x, innerBox.y + i * 16 + biomeListYOffset,
					20, 16);
			g2d.setColor(Color.white);
			g2d.drawString(biome.name, innerBox.x + 25, innerBox.y + 13 + i
					* 16 + biomeListYOffset);
		}

		g2d.setClip(null);

		if (scrollbarVisible) {
			float boxHeight = innerBox.height;
			float listHeight = biomeListHeight;

			if (scrollbarGrabbed) {
				Point mouse = mapViewer.getMousePosition();
				if (mouse != null) {
					int tempScrollbarY = -scrollbarYOnGrab
							- (mouse.y - mouseYOnGrab);
					biomeListYOffset = (int) ((listHeight / boxHeight) * tempScrollbarY);
					biomeListYOffset = Math.min(0, Math.max(-biomeListHeight
							+ innerBox.height, biomeListYOffset));
				} else {
					scrollbarGrabbed = false;
				}
			}

			float yOffset = -biomeListYOffset;

			scrollbarY = (int) ((yOffset / listHeight) * boxHeight);
			scrollbarHeight = (int) (Math.ceil(boxHeight
					* (boxHeight / listHeight)));
			g2d.setColor(scrollbarGrabbed ? scrollbarLitColor : scrollbarColor);
			g2d.fillRect(innerBox.x + innerBox.width, innerBox.y + scrollbarY,
					scrollbarWidth, scrollbarHeight);
		}

		g2d.setColor(Color.white);
		g2d.drawString("Select:", getX() + 8, getY() + getHeight() - 10);
		g2d.setColor(selectButtonColor);
		g2d.drawString("All  Special  None", getX() + 120, getY() + getHeight()
				- 10);

	}

	@Override
	public boolean onMouseWheelMoved(int mouseX, int mouseY, int notches) {
		if ((mouseX > innerBox.x - getX())
				&& (mouseX < innerBox.x - getX() + innerBox.width)
				&& (mouseY > innerBox.y - getY())
				&& (mouseY < innerBox.y - getY() + innerBox.height)) {
			biomeListYOffset = Math.min(0, Math.max(-biomeListHeight
					+ innerBox.height, biomeListYOffset - notches * 35));
		}
		return true;
	}

	@Override
	public void onMouseReleased() {
		scrollbarGrabbed = false;
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY) {
		if (scrollbarVisible) {
			if ((mouseX > innerBox.x - getX() + innerBox.width)
					&& (mouseX < innerBox.x - getX() + innerBox.width
							+ scrollbarWidth)
					&& (mouseY > innerBox.y - getY() + scrollbarY)
					&& (mouseY < innerBox.y - getY() + scrollbarY
							+ scrollbarHeight)) {

				mouseYOnGrab = mouseY + getY();
				scrollbarYOnGrab = scrollbarY;
				scrollbarGrabbed = true;
			}
		}

		boolean needsRedraw = false;
		if ((mouseX > innerBox.x - getX())
				&& (mouseX < innerBox.x - getX() + innerBox.width)
				&& (mouseY > innerBox.y - getY())
				&& (mouseY < innerBox.y - getY() + innerBox.height)) {
			int id = (mouseY - (innerBox.y - getY()) - biomeListYOffset) / 16;
			if (id < biomes.size()) {
				BiomeLayer.getInstance()
						.toggleBiomeSelect(biomes.get(id).index);
				needsRedraw = true;
			}
		}

		// TODO: These values are temporarly hard coded for the sake of a fast
		// release
		if ((mouseY > getHeight() - 25) && (mouseY < getHeight() - 9)) {
			if ((mouseX > 117) && (mouseX < 139)) {
				BiomeLayer.getInstance().selectAllBiomes();
				needsRedraw = true;
			} else if ((mouseX > 143) && (mouseX < 197)) {
				for (int i = 128; i < Biome.biomes.length; i++)
					if (Biome.biomes[i] != null)
						BiomeLayer.getInstance().selectBiome(i);
				needsRedraw = true;
			} else if ((mouseX > 203) && (mouseX < 242)) {
				BiomeLayer.getInstance().deselectAllBiomes();
				needsRedraw = true;
			}
		}
		if (needsRedraw) {
			(new Thread(new Runnable() {
				@Override
				public void run() {
					map.repaintImageLayer(BiomeLayer.getInstance().getLayerId());
				}
			})).start();
		}
		return true;
	}

	@Override
	public boolean onVisibilityCheck() {
		setHeight(Math.max(200, mapViewer.getHeight() - 200));
		return BiomeToggleWidget.isBiomeWidgetVisible & (getHeight() > 200);
	}
}
