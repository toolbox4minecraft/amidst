package amidst.gui.main.viewer.widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import amidst.ResourceLoader;
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
	private static final Color SEARCH_HIGHLIGHT_COLOR = new Color(0.4f, 0.8f, 0.4f, 0.5f);
	// @formatter:on
	private static final BufferedImage SEARCH_IMAGE = ResourceLoader.getImage("/amidst/gui/main/search.png");

	private final BiomeSelection biomeSelection;
	private final LayerReloader layerReloader;
	private final BiomeProfileSelection biomeProfileSelection;
	private final JTextField searchField;
	private final Supplier<JComponent> parentComponentSupplier;

	private List<Biome> biomes = new ArrayList<>();
	private List<Biome> displayedBiomes = new ArrayList<>();
	private List<Rectangle> highlightRects = new ArrayList<>();
	private BiomeList biomeList;
	private int maxNameWidth = 0;
	private boolean isInitialized = false;
	private boolean isVisible = false;
	private FontMetrics fontMetrics;

	private Rectangle innerBox = new Rectangle(0, 0, 1, 1);

	private int biomeListYOffset = 0;
	private boolean scrollbarVisible = false;
	private boolean scrollbarGrabbed = false;
	private int scrollbarHeight = 0;
	private int scrollbarWidth = 10;
	private int scrollbarY = 0;
	private int mouseYOnGrab = 0;
	private int scrollbarYOnGrab;
	
	private boolean updateHighlightRects = false;
	
	private String lastUpdateSearchText = "\0"; // can be anything the user can't type

	@CalledOnlyBy(AmidstThread.EDT)
	public BiomeWidget(
			CornerAnchorPoint anchor,
			BiomeSelection biomeSelection,
			LayerReloader layerReloader,
			BiomeProfileSelection biomeProfileSelection,
			BiomeList biomeList,
			Supplier<JComponent> parentComponentSupplier) {
		super(anchor);
		this.biomeSelection = biomeSelection;
		this.layerReloader = layerReloader;
		this.biomeProfileSelection = biomeProfileSelection;
		this.biomeList = biomeList;
		this.isVisible = biomeSelection.isWidgetVisible();
		
		this.searchField = new JTextField() {
			private static final long serialVersionUID = 7635606378222847774L;
			public void paintComponent(Graphics g) {
				setForeground(multiplyTransparency(Color.white, getAlpha()));
				setBounds(BiomeWidget.this.getX() + 31, BiomeWidget.this.getY() + 30, BiomeWidget.this.getWidth() - 39, 18);
				super.paintComponent(g);
			}
		};
		this.parentComponentSupplier = parentComponentSupplier;
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
		if(tryUpdateSearch()) {
			updateBiomeListYOffset();
			updateScrollbarVisibility();
		}
		if (scrollbarVisible) {
			updateInnerBoxWidth();
			updateScrollbarParameter(getMousePosition());
		}
		tryUpdateHighlightRects();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void initializeIfNecessary(FontMetrics fontMetrics) {
		if (!isInitialized) {
			this.fontMetrics = fontMetrics;
			setupSearchField(fontMetrics);
			for (Biome biome : biomeList.iterable()) {
				biomes.add(biome);
				int width = fontMetrics.stringWidth(biome.getName());
				maxNameWidth = Math.max(width, maxNameWidth);
			}
			isInitialized = true;
		}
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private void setupSearchField(FontMetrics fontMetrics) {
		parentComponentSupplier.get().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Point p = e.getPoint();
				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				if(!searchField.getBounds().contains(p)
				   && focusOwner != null
				   && focusOwner.equals(searchField)) {
					KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
				}
			}
		});
		searchField.setBorder(null);
		searchField.setOpaque(false);
		searchField.setFont(fontMetrics.getFont().deriveFont(13f));
		searchField.setBackground(new Color(0, 0, 0, 0));
		searchField.setCaretColor(Color.white);
		changeCaretSize((DefaultCaret) searchField.getCaret(), 2);
		// this makes sure it actually shows up and completes the first paint
		// so it can be resized correctly in paintComponent()
		searchField.setSize(1,1);
		parentComponentSupplier.get().add(searchField);
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private void changeCaretSize(DefaultCaret c, int size) {
		try {
			Field f = DefaultCaret.class.getDeclaredField("caretWidth");
			f.setAccessible(true);
			f.set(c, size);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private boolean tryUpdateSearch() {
		searchField.setVisible(isVisible());
		String currentText = searchField.getText().toLowerCase();
		if(!currentText.equals(lastUpdateSearchText)) {
			// optimization for if the text gets added to
			if(currentText.length() > lastUpdateSearchText.length() && currentText.contains(lastUpdateSearchText)) {
				Set<Biome> biomesToRemove = new HashSet<>();
				for (Biome biome : displayedBiomes) {
					if(!biome.getName().toLowerCase().contains(currentText)) {
						biomesToRemove.add(biome);
					}
				}
				displayedBiomes.removeAll(biomesToRemove);
				sortBiomeList(displayedBiomes, currentText);
			} else {
				displayedBiomes.clear();
				if(currentText.equals("")) {
					displayedBiomes.addAll(biomes);
				} else {
					Set<Biome> biomesToAdd = new HashSet<>();
					for (Biome biome : biomes) {
						if(biome.getName().toLowerCase().contains(currentText)) {
							biomesToAdd.add(biome);
						}
					}
					displayedBiomes.addAll(biomesToAdd);
				}
				sortBiomeList(displayedBiomes, currentText);
			}
			lastUpdateSearchText = currentText;
			updateHighlightRects = true;
			return true;
		}
		return false;
	}
	
	/*
	 * first sort by index of string, then by id
	 */
	@CalledOnlyBy(AmidstThread.EDT)
	private void sortBiomeList(List<Biome> list, String string) {
		list.sort((b1,b2) -> {
			int firstCompare = Integer.compare(b1.getName().toLowerCase().indexOf(string), b2.getName().toLowerCase().indexOf(string));
			if (firstCompare == 0) {
				return Integer.compare(b1.getId(), b2.getId());
			} else {
				return firstCompare;
			}
		});
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private void tryUpdateHighlightRects() {
		if (updateHighlightRects) {
			// pad size so we can use set()
			while (highlightRects.size() < displayedBiomes.size()) highlightRects.add(null);
			
			for (int i = 0; i < displayedBiomes.size(); i++) {
				Biome biome = displayedBiomes.get(i);
				String biomeName = biome.getName();
				
				int stringX = innerBox.x + 25;
				int startY = innerBox.y + i * 16 + biomeListYOffset;
				
				int startIndex = biomeName.toLowerCase().indexOf(lastUpdateSearchText);
				int startX = stringX + fontMetrics.stringWidth(biomeName.substring(0, startIndex));
				
				int endIndex = startIndex + lastUpdateSearchText.length();
				int width = fontMetrics.stringWidth(biomeName.substring(startIndex, endIndex));
				
				highlightRects.set(i, new Rectangle(startX, startY, width, 16));
			}
			
			updateHighlightRects = false;
		}
	}
	
	private static Color multiplyTransparency(Color c, float alpha) {
		if(alpha == 1.0) {
			return c;
		} else {
			return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Math.floor(alpha * c.getAlpha()));
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
		innerBox.y = getY() + 49;
		innerBox.width = getWidth() - 16;
		innerBox.height = getHeight() - 77;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateBiomeListYOffset() {
		biomeListYOffset = Math.min(0, Math.max(-getBiomeListHeight() + innerBox.height, biomeListYOffset));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void updateScrollbarVisibility() {
		if (getBiomeListHeight() > innerBox.height) {
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
		float listHeight = getBiomeListHeight();
		if (scrollbarGrabbed) {
			if (mousePosition != null) {
				biomeListYOffset = (int) ((listHeight / boxHeight)
						* (-scrollbarYOnGrab - (mousePosition.y - mouseYOnGrab)));
				updateBiomeListYOffset();
				updateHighlightRects = true;
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
		for (int i = 0; i < displayedBiomes.size(); i++) {
			Biome biome = displayedBiomes.get(i);
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
		drawSearchBackground(g2d);
		drawSearchIcon(g2d);
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
		g2d.drawRect(
				innerBox.x - 1,
				innerBox.y - 20,
				innerBox.width + 1 + (scrollbarVisible ? scrollbarWidth : 0),
				19);
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
		g2d.drawString(biome.getName(), innerBox.x + 25, innerBox.y + 13 + i * 16 + biomeListYOffset);
		
		g2d.setColor(SEARCH_HIGHLIGHT_COLOR);
		g2d.fill(highlightRects.get(i));
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
	private void drawSearchBackground(Graphics2D g2d) {
		g2d.setColor(BIOME_BG_COLOR_2);
		g2d.fillRect(innerBox.x, innerBox.y - 19, innerBox.width + (scrollbarVisible ? scrollbarWidth : 0), 18);
	}
	
	@CalledOnlyBy(AmidstThread.EDT)
	private void drawSearchIcon(Graphics2D g2d) {
		g2d.drawImage(SEARCH_IMAGE, innerBox.x, innerBox.y - 19, null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onMouseWheelMoved(int mouseX, int mouseY, int notches) {
		if (!isInitialized) {
			return false;
		}
		if (isInBoundsOfInnerBox(mouseX, mouseY)) {
			biomeListYOffset = Math
					.min(0, Math.max(-getBiomeListHeight() + innerBox.height, biomeListYOffset - notches * 35));
			updateHighlightRects = true;
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
			int index = (mouseY - (innerBox.y - getY()) - biomeListYOffset) / 16;
			if (index < displayedBiomes.size()) {
				int id = displayedBiomes.get(index).getId();
				biomeSelection.toggle(id);
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
	private int getBiomeListHeight() {
		return displayedBiomes.size() * 16;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public boolean onVisibilityCheck() {
		return isVisible && getHeight() > 200;
	}
}
