package amidst.gui.main.viewer.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.layer.LayerReloader;
import amidst.gui.main.viewer.BiomeSelection;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.BiomeColor;
import amidst.mojangapi.world.biome.BiomeList;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@NotThreadSafe
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
	private final BiomeProfileSelection biomeProfileSelection;

	private List<Biome> biomes = new ArrayList<>();
	private BiomeList biomeList;
	private int maxNameWidth = 0;
	private int biomeListHeight;
	private boolean isInitialized = false;
	private boolean isVisible = false;

	private Rectangle innerBox = new Rectangle(0, 0, 1, 1);

	private int biomeListYOffset = 0;
	private boolean scrollbarVisible = false;
	private boolean scrollbarGrabbed = false;
	private int scrollbarHeight = 0;
	private int scrollbarWidth = 10;
	private int scrollbarY = 0;
	private int mouseYOnGrab = 0;
	private int scrollbarYOnGrab;

	@CalledOnlyBy(AmidstThread.EDT)
	public BiomeWidget(
			CornerAnchorPoint anchor,
			BiomeSelection biomeSelection,
			LayerReloader layerReloader,
			BiomeProfileSelection biomeProfileSelection,
			BiomeList biomeList) {
		super(anchor);
		this.biomeSelection = biomeSelection;
		this.layerReloader = layerReloader;
		this.biomeProfileSelection = biomeProfileSelection;
		this.biomeList = biomeList;
		this.isVisible = biomeSelection.isWidgetVisible();
		setWidth(250);
		setHeight(400);
		setY(100);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public void toggleVisibility() {
		isVisible = biomeSelection.toggleWidgetVisibility();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doUpdate(FontMetrics fontMetrics, float time) {
		initializeIfNecessary(fontMetrics);
		updateX();
		updateHeight();
		updateInnerBoxPositionAndSize();
		updateBiomeListYOffset();
		updateScrollbarVisibility();
		if (scrollbarVisible) {
			updateInnerBoxWidth();
			updateScrollbarParameter(getMousePosition());
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void initializeIfNecessary(FontMetrics fontMetrics) {
		if (!isInitialized) {
			isInitialized = true;
			for (Biome biome : biomeList.iterable()) {
				biomes.add(biome);
				int width = fontMetrics.stringWidth(biome.getName());
				maxNameWidth = Math.max(width, maxNameWidth);
			}
			biomeListHeight = biomes.size() * 16;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateX() {
		setX(getViewerWidth() - getWidth());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateHeight() {
		setHeight(Math.max(200, getViewerHeight() - 200));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateInnerBoxPositionAndSize() {
		innerBox.x = getX() + 8;
		innerBox.y = getY() + 30;
		innerBox.width = getWidth() - 16;
		innerBox.height = getHeight() - 58;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateBiomeListYOffset() {
		biomeListYOffset = Math.min(0, Math.max(-biomeListHeight + innerBox.height, biomeListYOffset));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateScrollbarVisibility() {
		if (biomeListHeight > innerBox.height) {
			scrollbarVisible = true;
		} else {
			scrollbarVisible = false;
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateInnerBoxWidth() {
		innerBox.width -= scrollbarWidth;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateScrollbarParameter(Point mousePosition) {
		float boxHeight = innerBox.height;
		float listHeight = biomeListHeight;
		if (scrollbarGrabbed) {
			if (mousePosition != null) {
				biomeListYOffset = (int) ((listHeight / boxHeight)
						* (-scrollbarYOnGrab - (mousePosition.y - mouseYOnGrab)));
				updateBiomeListYOffset();
			} else {
				scrollbarGrabbed = false;
			}
		}
		scrollbarY = (int) ((-biomeListYOffset / listHeight) * boxHeight);
		scrollbarHeight = (int) (Math.ceil(boxHeight * (boxHeight / listHeight)));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	protected void doDraw(Graphics2D g2d) {
		drawTextHighlightBiomes(g2d);
		drawInnerBoxBackground(g2d);
		drawInnerBoxBorder(g2d);
		setClipToInnerBox(g2d);
		for (int i = 0; i < biomes.size(); i++) {
			Biome biome = biomes.get(i);
			drawBiomeBackgroundColor(g2d, i, getBiomeBackgroudColor(i, biome));
			drawBiomeColor(g2d, i, getBiomeColorOrUnknown(biome));
			drawBiomeName(g2d, i, biome);
		}
		clearClip(g2d);
		if (scrollbarVisible) {
			drawScrollbar(g2d);
		}
		drawTextSelect(g2d);
		drawSpecialButtons(g2d);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawTextHighlightBiomes(Graphics2D g2d) {
		g2d.drawString("Highlight Biomes", getX() + 10, getY() + 20);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawInnerBoxBackground(Graphics2D g2d) {
		g2d.setColor(INNER_BOX_BG_COLOR);
		g2d.fillRect(innerBox.x, innerBox.y, innerBox.width, innerBox.height);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawInnerBoxBorder(Graphics2D g2d) {
		g2d.setColor(INNER_BOX_BORDER_COLOR);
		g2d.drawRect(
				innerBox.x - 1,
				innerBox.y - 1,
				innerBox.width + 1 + (scrollbarVisible ? scrollbarWidth : 0),
				innerBox.height + 1);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void setClipToInnerBox(Graphics2D g2d) {
		g2d.setClip(innerBox);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private Color getBiomeBackgroudColor(int i, Biome biome) {
		if (biomeSelection.isSelected(biome.getId())) {
			if (i % 2 != 0) {
				return BIOME_LIT_BG_COLOR_1;
			} else {
				return BIOME_LIT_BG_COLOR_2;
			}
		} else {
			if (i % 2 != 0) {
				return BIOME_BG_COLOR_1;
			} else {
				return BIOME_BG_COLOR_2;
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawBiomeBackgroundColor(Graphics2D g2d, int i, Color color) {
		g2d.setColor(color);
		g2d.fillRect(innerBox.x, innerBox.y + i * 16 + biomeListYOffset, innerBox.width, 16);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private BiomeColor getBiomeColorOrUnknown(Biome biome) {
		return biomeProfileSelection.getBiomeColorOrUnknown(biome.getId());
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawBiomeColor(Graphics2D g2d, int i, BiomeColor biomeColor) {
		g2d.setColor(biomeColor.getColor());
		g2d.fillRect(innerBox.x, innerBox.y + i * 16 + biomeListYOffset, 20, 16);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawBiomeName(Graphics2D g2d, int i, Biome biome) {
		g2d.setColor(Color.white);
		g2d.drawString(biomeList.getByIdOrNull(biome.getId()).getName(), innerBox.x + 25, innerBox.y + 13 + i * 16 + biomeListYOffset);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void clearClip(Graphics2D g2d) {
		g2d.setClip(null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawScrollbar(Graphics2D g2d) {
		g2d.setColor(scrollbarGrabbed ? SCROLLBAR_LIT_COLOR : SCROLLBAR_COLOR);
		g2d.fillRect(innerBox.x + innerBox.width, innerBox.y + scrollbarY, scrollbarWidth, scrollbarHeight);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawTextSelect(Graphics2D g2d) {
		g2d.setColor(Color.white);
		g2d.drawString("Select:", getX() + 8, getY() + getHeight() - 10);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void drawSpecialButtons(Graphics2D g2d) {
		g2d.setColor(SELECT_BUTTON_COLOR);
		String activeText = biomeSelection.isHighlightMode() ? "Active" : "Inactive";
		g2d.drawString(activeText, getX() + getWidth() - 65, getY() + 20);
		g2d.drawString("All  Special  None", getX() + 120, getY() + getHeight() - 10);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onMouseWheelMoved(int mouseX, int mouseY, int notches) {
		if (!isInitialized) {
			return false;
		}
		if (isInBoundsOfInnerBox(mouseX, mouseY)) {
			biomeListYOffset = Math
					.min(0, Math.max(-biomeListHeight + innerBox.height, biomeListYOffset - notches * 35));
		}
		return true;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void onMouseReleased() {
		scrollbarGrabbed = false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onMousePressed(int mouseX, int mouseY) {
		if (!isInitialized) {
			return false;
		}
		updateScrollbarParameters(mouseX, mouseY);
		if (processClick(mouseX, mouseY)) {
			layerReloader.reloadBackgroundLayer();
		}
		return true;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateScrollbarParameters(int mouseX, int mouseY) {
		if (scrollbarVisible) {
			if (isInBoundsOfScrollbar(mouseX, mouseY)) {
				mouseYOnGrab = mouseY + getY();
				scrollbarYOnGrab = scrollbarY;
				scrollbarGrabbed = true;
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean processClick(int mouseX, int mouseY) {
		if (isInBoundsOfInnerBox(mouseX, mouseY)) {
			int id = (mouseY - (innerBox.y - getY()) - biomeListYOffset) / 16;
			if (id < biomes.size()) {
				int index = biomes.get(id).getId();
				biomeSelection.toggle(index);
				return biomeSelection.isHighlightMode();
			}
		} else if (isActiveButton(mouseX, mouseY)) {
			biomeSelection.toggleHighlightMode();
			return true;
		} else if (isBottomButton(mouseY)) {
			if (isSelectAllButton(mouseX)) {
				biomeSelection.selectAll();
				return biomeSelection.isHighlightMode();
			} else if (isSelectSpecialBiomesButton(mouseX)) {
				selectOnlySpecialBiomes();
				return biomeSelection.isHighlightMode();
			} else if (isDeselectAllButton(mouseX)) {
				biomeSelection.deselectAll();
				return biomeSelection.isHighlightMode();
			}
		}
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void selectOnlySpecialBiomes() {
		biomeSelection.deselectAll();
		for (Biome biome: biomeList.iterable()) {
			if (biome.isSpecialBiome()) {
				biomeSelection.toggle(biome.getId());
			}
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isInBoundsOfInnerBox(int mouseX, int mouseY) {
		int offsetX = translateXToWidgetCoordinates(innerBox.x);
		int offsetY = translateYToWidgetCoordinates(innerBox.y);
		int width = innerBox.width;
		int height = innerBox.height;
		return Widget.isInBounds(mouseX, mouseY, offsetX, offsetY, width, height);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isInBoundsOfScrollbar(int mouseX, int mouseY) {
		int offsetX = translateXToWidgetCoordinates(innerBox.x + innerBox.width);
		int offsetY = translateYToWidgetCoordinates(innerBox.y + scrollbarY);
		int width = scrollbarWidth;
		int height = scrollbarHeight;
		return Widget.isInBounds(mouseX, mouseY, offsetX, offsetY, width, height);
	}

	// TODO: These values are temporarily hard coded for the sake of a fast
	// release
	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isBottomButton(int mouseY) {
		return mouseY > getHeight() - 25 && mouseY < getHeight() - 9;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isSelectAllButton(int mouseX) {
		return mouseX > 117 && mouseX < 139;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isSelectSpecialBiomesButton(int mouseX) {
		return mouseX > 143 && mouseX < 197;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isDeselectAllButton(int mouseX) {
		return mouseX > 203 && mouseX < 242;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isActiveButton(int mouseX, int mouseY) {
		return mouseX >= getWidth() - 65 && mouseX < getWidth() - 5 && mouseY >= 5 && mouseY < 21;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onVisibilityCheck() {
		return isVisible && getHeight() > 200;
	}
}
