package amidst.gui.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import amidst.fragment.layer.LayerReloader;
import amidst.gui.worldsurroundings.BiomeSelection;
import amidst.minecraft.Biome;
import amidst.preferences.BiomeColorProfileSelection;
import amidst.utilities.CoordinateUtils;

public class BiomeWidget extends Widget {
	// @formatter:off
	private static final Color INNER_BOX_BG_COLOR = 	new Color(0.3f, 0.3f, 0.3f, 0.3f);
	private static final Color BIOME_BG_COLOR_1 = 		new Color(0.8f, 0.8f, 0.8f, 0.2f);
	private static final Color BIOME_BG_COLOR_2 = 		new Color(0.6f, 0.6f, 0.6f, 0.2f);
	private static final Color BIOME_LIT_BG_COLOR_1 = 	new Color(0.8f, 0.8f, 1.0f, 0.7f);
	private static final Color BIOME_LIT_BG_COLOR_2 = 	new Color(0.6f, 0.6f, 0.8f, 0.7f);
	private static final Color INNER_BOX_BORDER_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	private static final Color SCROLLBAR_COLOR = 		new Color(0.6f, 0.6f, 0.6f, 0.8f);
	private static final Color SCROLLBAR_LIT_COLOR = 	new Color(0.6f, 0.6f, 0.8f, 0.8f);
	private static final Color SELECT_BUTTON_COLOR = 	new Color(0.6f, 0.6f, 0.8f, 1.0f);
	// @formatter:on

	private final BiomeSelection biomeSelection;
	private final LayerReloader layerReloader;
	private final BiomeColorProfileSelection biomeColorProfileSelection;

	private List<Biome> biomes = new ArrayList<Biome>();
	private int maxNameWidth = 0;
	private int biomeListHeight;
	private boolean isInitialized = false;

	private Rectangle innerBox = new Rectangle(0, 0, 1, 1);

	private int biomeListYOffset = 0;
	private boolean scrollbarVisible = false;
	private boolean scrollbarGrabbed = false;
	private int scrollbarHeight = 0;
	private int scrollbarWidth = 10;
	private int scrollbarY = 0;
	private int mouseYOnGrab = 0;
	private int scrollbarYOnGrab;

	public BiomeWidget(CornerAnchorPoint anchor, BiomeSelection biomeSelection,
			LayerReloader layerReloader,
			BiomeColorProfileSelection biomeColorProfileSelection) {
		super(anchor);
		this.biomeSelection = biomeSelection;
		this.layerReloader = layerReloader;
		this.biomeColorProfileSelection = biomeColorProfileSelection;
		setWidth(250);
		setHeight(400);
		setY(100);
		forceVisibility(false);
	}

	private void initializeIfNecessary(FontMetrics fontMetrics) {
		if (!isInitialized) {
			isInitialized = true;
			for (Biome biome : Biome.allBiomes()) {
				biomes.add(biome);
				int width = fontMetrics.stringWidth(biome.getName());
				maxNameWidth = Math.max(width, maxNameWidth);
			}
			biomeListHeight = biomes.size() * 16;
		}
	}

	@Override
	public void draw(Graphics2D g2d, float time, FontMetrics fontMetrics,
			int viewerWidth, int viewerHeight, Point mousePosition) {
		initializeIfNecessary(fontMetrics);
		updateX(viewerWidth);
		updateHeight(viewerHeight);
		updateInnerBoxPositionAndSize();
		updateBiomeListYOffset();
		updateScrollbarVisibility();
		updateInnerBoxWidth();
		drawBorderAndBackground(g2d, time, viewerWidth, viewerHeight);
		drawTextHighlightBiomes(g2d);
		drawInnerBoxBackground(g2d);
		drawInnerBoxBorder(g2d);
		setClipToInnerBox(g2d);
		for (int i = 0; i < biomes.size(); i++) {
			Biome biome = biomes.get(i);
			drawBiomeBackgroundColor(g2d, i, getBiomeBackgroudColor(i, biome));
			drawBiomeColor(g2d, i, biome);
			drawBiomeName(g2d, i, biome);
		}
		clearClip(g2d);
		if (scrollbarVisible) {
			updateScrollbarParameter(mousePosition);
			drawScrollbar(g2d);
		}

		drawTextSelect(g2d);
		drawSpecialButtons(g2d);
	}

	private void updateX(int viewerWidth) {
		setX(viewerWidth - getWidth());
	}

	private void updateHeight(int viewerHeight) {
		setHeight(Math.max(200, viewerHeight - 200));
	}

	private void updateInnerBoxPositionAndSize() {
		innerBox.x = getX() + 8;
		innerBox.y = getY() + 30;
		innerBox.width = getWidth() - 16;
		innerBox.height = getHeight() - 58;
	}

	private void updateBiomeListYOffset() {
		biomeListYOffset = Math.min(0,
				Math.max(-biomeListHeight + innerBox.height, biomeListYOffset));
	}

	private void updateScrollbarVisibility() {
		if (biomeListHeight > innerBox.height) {
			scrollbarVisible = true;
		} else {
			scrollbarVisible = false;
		}
	}

	private void updateInnerBoxWidth() {
		if (scrollbarVisible) {
			innerBox.width -= scrollbarWidth;
		}
	}

	private void drawTextHighlightBiomes(Graphics2D g2d) {
		g2d.drawString("Highlight Biomes", getX() + 10, getY() + 20);
	}

	private void drawInnerBoxBackground(Graphics2D g2d) {
		g2d.setColor(INNER_BOX_BG_COLOR);
		g2d.fillRect(innerBox.x, innerBox.y, innerBox.width, innerBox.height);
	}

	private void drawInnerBoxBorder(Graphics2D g2d) {
		g2d.setColor(INNER_BOX_BORDER_COLOR);
		g2d.drawRect(innerBox.x - 1, innerBox.y - 1, innerBox.width + 1
				+ (scrollbarVisible ? scrollbarWidth : 0), innerBox.height + 1);
	}

	private void setClipToInnerBox(Graphics2D g2d) {
		g2d.setClip(innerBox);
	}

	private void drawBiomeBackgroundColor(Graphics2D g2d, int i, Color color) {
		g2d.setColor(color);
		g2d.fillRect(innerBox.x, innerBox.y + i * 16 + biomeListYOffset,
				innerBox.width, 16);
	}

	private Color getBiomeBackgroudColor(int i, Biome biome) {
		if (biomeSelection.isSelected(biome.getIndex())) {
			if (i % 2 == 1) {
				return BIOME_LIT_BG_COLOR_1;
			} else {
				return BIOME_LIT_BG_COLOR_2;
			}
		} else {
			if (i % 2 == 1) {
				return BIOME_BG_COLOR_1;
			} else {
				return BIOME_BG_COLOR_2;
			}
		}
	}

	private void drawBiomeColor(Graphics2D g2d, int i, Biome biome) {
		g2d.setColor(biomeColorProfileSelection.getColorByBiome(biome));
		g2d.fillRect(innerBox.x, innerBox.y + i * 16 + biomeListYOffset, 20, 16);
	}

	private void drawBiomeName(Graphics2D g2d, int i, Biome biome) {
		g2d.setColor(Color.white);
		g2d.drawString(biome.getName(), innerBox.x + 25, innerBox.y + 13 + i
				* 16 + biomeListYOffset);
	}

	private void clearClip(Graphics2D g2d) {
		g2d.setClip(null);
	}

	private void updateScrollbarParameter(Point mousePosition) {
		float boxHeight = innerBox.height;
		float listHeight = biomeListHeight;
		if (scrollbarGrabbed) {
			if (mousePosition != null) {
				biomeListYOffset = (int) ((listHeight / boxHeight) * (-scrollbarYOnGrab - (mousePosition.y - mouseYOnGrab)));
				updateBiomeListYOffset();
			} else {
				scrollbarGrabbed = false;
			}
		}
		float yOffset = -biomeListYOffset;
		scrollbarY = (int) ((yOffset / listHeight) * boxHeight);
		scrollbarHeight = (int) (Math
				.ceil(boxHeight * (boxHeight / listHeight)));
	}

	private void drawScrollbar(Graphics2D g2d) {
		g2d.setColor(scrollbarGrabbed ? SCROLLBAR_LIT_COLOR : SCROLLBAR_COLOR);
		g2d.fillRect(innerBox.x + innerBox.width, innerBox.y + scrollbarY,
				scrollbarWidth, scrollbarHeight);
	}

	private void drawTextSelect(Graphics2D g2d) {
		g2d.setColor(Color.white);
		g2d.drawString("Select:", getX() + 8, getY() + getHeight() - 10);
	}

	private void drawSpecialButtons(Graphics2D g2d) {
		g2d.setColor(SELECT_BUTTON_COLOR);
		g2d.drawString("All  Special  None", getX() + 120, getY() + getHeight()
				- 10);
	}

	@Override
	public boolean onMouseWheelMoved(int mouseX, int mouseY, int notches) {
		if (!isInitialized) {
			return false;
		}
		if (isInBoundsOfInnerBox(mouseX, mouseY)) {
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
		if (!isInitialized) {
			return false;
		}
		updateScrollbarParameters(mouseX, mouseY);
		if (processClick(mouseX, mouseY)) {
			layerReloader.reloadBiomeLayer();
		}
		return true;
	}

	private void updateScrollbarParameters(int mouseX, int mouseY) {
		if (scrollbarVisible) {
			if (isInBoundsOfScrollbar(mouseX, mouseY)) {
				mouseYOnGrab = mouseY + getY();
				scrollbarYOnGrab = scrollbarY;
				scrollbarGrabbed = true;
			}
		}
	}

	private boolean processClick(int mouseX, int mouseY) {
		if (isInBoundsOfInnerBox(mouseX, mouseY)) {
			int id = (mouseY - (innerBox.y - getY()) - biomeListYOffset) / 16;
			if (id < biomes.size()) {
				int index = biomes.get(id).getIndex();
				biomeSelection.toggleSelect(index);
				return true;
			}
		} else if (isButton(mouseY)) {
			if (isSelectAllButton(mouseX)) {
				biomeSelection.selectAll();
				return true;
			} else if (isSelectSpecialBiomesButton(mouseX)) {
				biomeSelection.selectOnlySpecial();
				return true;
			} else if (isDeselectAllButton(mouseX)) {
				biomeSelection.deselectAll();
				return true;
			}
		}
		return false;
	}

	private boolean isInBoundsOfInnerBox(int mouseX, int mouseY) {
		int offsetX = translateXToWidgetCoordinates(innerBox.x);
		int offsetY = translateYToWidgetCoordinates(innerBox.y);
		int width = innerBox.width;
		int height = innerBox.height;
		return CoordinateUtils.isInBounds(mouseX, mouseY, offsetX, offsetY,
				width, height);
	}

	private boolean isInBoundsOfScrollbar(int mouseX, int mouseY) {
		int offsetX = translateXToWidgetCoordinates(innerBox.x + innerBox.width);
		int offsetY = translateYToWidgetCoordinates(innerBox.y + scrollbarY);
		int width = scrollbarWidth;
		int height = scrollbarHeight;
		return CoordinateUtils.isInBounds(mouseX, mouseY, offsetX, offsetY,
				width, height);
	}

	// TODO: These values are temporarily hard coded for the sake of a fast
	// release
	private boolean isButton(int mouseY) {
		return mouseY > getHeight() - 25 && mouseY < getHeight() - 9;
	}

	private boolean isSelectAllButton(int mouseX) {
		return mouseX > 117 && mouseX < 139;
	}

	private boolean isSelectSpecialBiomesButton(int mouseX) {
		return mouseX > 143 && mouseX < 197;
	}

	private boolean isDeselectAllButton(int mouseX) {
		return mouseX > 203 && mouseX < 242;
	}

	@Override
	public boolean onVisibilityCheck() {
		return biomeSelection.isHighlightMode() && getHeight() > 200;
	}
}
