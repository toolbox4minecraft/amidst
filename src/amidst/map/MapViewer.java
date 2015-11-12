package amidst.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import MoF.Project;
import amidst.Options;
import amidst.gui.menu.PlayerMenuItem;
import amidst.logging.Log;
import amidst.map.layers.BiomeLayer;
import amidst.map.layers.GridLayer;
import amidst.map.layers.NetherFortressLayer;
import amidst.map.layers.OceanMonumentLayer;
import amidst.map.layers.PlayerLayer;
import amidst.map.layers.SlimeLayer;
import amidst.map.layers.SpawnLayer;
import amidst.map.layers.StrongholdLayer;
import amidst.map.layers.TempleLayer;
import amidst.map.layers.VillageLayer;
import amidst.map.widget.BiomeToggleWidget;
import amidst.map.widget.BiomeWidget;
import amidst.map.widget.CursorInformationWidget;
import amidst.map.widget.DebugWidget;
import amidst.map.widget.FpsWidget;
import amidst.map.widget.PanelWidget.CornerAnchorPoint;
import amidst.map.widget.ScaleWidget;
import amidst.map.widget.SeedWidget;
import amidst.map.widget.SelectedObjectWidget;
import amidst.map.widget.Widget;
import amidst.minecraft.MinecraftUtil;
import amidst.resources.ResourceLoader;

public class MapViewer {
	private class Listeners implements MouseListener, MouseWheelListener,
			KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			Point mouse = getMousePositionOrCenterFromComponent();
			if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
				adjustZoom(mouse, -1);
			} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
				adjustZoom(mouse, 1);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			Point mouse = getMousePositionFromEvent(e);
			Widget widget = findWidget(mouse);
			if (widget != null
					&& widget.onMouseWheelMoved(
							translateMouseXCoordinateToWidget(mouse, widget),
							translateMouseYCoordinateToWidget(mouse, widget),
							notches)) {
				// noop
			} else {
				adjustZoom(mouse, notches);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.isMetaDown()) {
				return;
			}
			Point mouse = getMousePositionFromEvent(e);
			Widget widget = findWidget(mouse);
			if (widget != null
					&& widget.onClick(
							translateMouseXCoordinateToWidget(mouse, widget),
							translateMouseYCoordinateToWidget(mouse, widget))) {
				// noop
			} else {
				mouseClickedOnMap(mouse);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isMetaDown()) {
				return;
			}
			Point mouse = getMousePositionFromEvent(e);
			Widget widget = findWidget(mouse);
			if (widget != null
					&& widget.onMousePressed(
							translateMouseXCoordinateToWidget(mouse, widget),
							translateMouseYCoordinateToWidget(mouse, widget))) {
				mouseOwner = widget;
			} else {
				lastMouse = mouse;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && MinecraftUtil.getVersion().saveEnabled()) {
				lastRightClick = getMousePositionFromEvent(e);
				if (proj.saveLoaded) {
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			} else {
				if (mouseOwner != null) {
					mouseOwner.onMouseReleased();
					mouseOwner = null;
				} else {
					lastMouse = null;
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		private void mouseClickedOnMap(Point mouse) {
			MapObject object = map.getObjectAt(mouse, 50.0);

			if (selectedObject != null) {
				selectedObject.localScale = 1.0;
			}

			if (object != null) {
				object.localScale = 1.5;
			}
			selectedObject = object;
		}

		/**
		 * Don't use getMousePosition() of the JComponent in mouse events,
		 * because when computer is swapping/grinding, mouse may have moved out
		 * of window before execution reaches here.
		 */
		private Point getMousePositionFromEvent(MouseEvent e) {
			return e.getPoint();
		}

		private Widget findWidget(Point mouse) {
			for (Widget widget : widgets) {
				if (widget.isVisible() && isMouseInWidgetBounds(mouse, widget)) {
					return widget;
				}
			}
			return null;
		}

		private int translateMouseXCoordinateToWidget(Point mouse, Widget widget) {
			return mouse.x - widget.getX();
		}

		private int translateMouseYCoordinateToWidget(Point mouse, Widget widget) {
			return mouse.y - widget.getY();
		}

		private boolean isMouseInWidgetBounds(Point mouse, Widget widget) {
			return mouse.x > widget.getX() && mouse.y > widget.getY()
					&& mouse.x < widget.getX() + widget.getWidth()
					&& mouse.y < widget.getY() + widget.getHeight();
		}
	}

	private class Component extends JComponent {
		private long lastTime = System.currentTimeMillis();

		@Override
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();

			float time = calculateTimeSpanSinceLastDrawInSeconds();

			clear(g2d);

			if (zoomTicksRemaining-- > 0) {
				double lastZoom = curZoom;
				curZoom = (targetZoom + curZoom) * 0.5;

				Point2D.Double targetZoom = map.getScaled(lastZoom, curZoom,
						zoomMouse);
				map.moveBy(targetZoom);
				map.setZoom(curZoom);
			}

			Point curMouse = getMousePosition();
			if (lastMouse != null) {
				if (curMouse != null) {
					double difX = curMouse.x - lastMouse.x;
					double difY = curMouse.y - lastMouse.y;
					// TODO : Scale with time
					panSpeed.setLocation(difX * 0.2, difY * 0.2);
				}

				lastMouse.translate((int) panSpeed.x, (int) panSpeed.y);
			}

			map.moveBy((int) panSpeed.x, (int) panSpeed.y);
			if (Options.instance.mapFlicking.get()) {
				panSpeed.x *= 0.95f;
				panSpeed.y *= 0.95f;
			} else {
				panSpeed.x *= 0.f;
				panSpeed.y *= 0.f;
			}

			map.setViewerWidth(getWidth());
			map.setViewerHeight(getHeight());

			drawMap(g2d, time);
			drawBorder(g2d);
			drawWidgets(g2d, time);
		}

		private void clear(Graphics2D g2d) {
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		}

		private void drawMap(Graphics2D g2d, float time) {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			map.draw((Graphics2D) g2d.create(), time);
		}

		private void drawBorder(Graphics2D g2d) {
			int width10 = getWidth() - 10;
			int height10 = getHeight() - 10;
			int width20 = getWidth() - 20;
			int height20 = getHeight() - 20;
			g2d.drawImage(DROP_SHADOW_TOP_LEFT, 0, 0, null);
			g2d.drawImage(DROP_SHADOW_TOP_RIGHT, width10, 0, null);
			g2d.drawImage(DROP_SHADOW_BOTTOM_LEFT, 0, height10, null);
			g2d.drawImage(DROP_SHADOW_BOTTOM_RIGHT, width10, height10, null);
			g2d.drawImage(DROP_SHADOW_TOP, 10, 0, width20, 10, null);
			g2d.drawImage(DROP_SHADOW_BOTTOM, 10, height10, width20, 10, null);
			g2d.drawImage(DROP_SHADOW_LEFT, 0, 10, 10, height20, null);
			g2d.drawImage(DROP_SHADOW_RIGHT, width10, 10, 10, height20, null);
		}

		private void drawWidgets(Graphics2D g2d, float time) {
			g2d.setFont(textFont);
			for (Widget widget : widgets) {
				if (widget.isVisible()) {
					g2d.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, widget.getAlpha()));
					widget.draw(g2d, time);
				}
			}
		}

		private float calculateTimeSpanSinceLastDrawInSeconds() {
			long currentTime = System.currentTimeMillis();
			float time = Math.min(Math.max(0, currentTime - lastTime), 100) / 1000.0f;
			lastTime = currentTime;
			return time;
		}
	}

	private static final BufferedImage DROP_SHADOW_BOTTOM_LEFT = ResourceLoader
			.getImage("dropshadow/inner_bottom_left.png");
	private static final BufferedImage DROP_SHADOW_BOTTOM_RIGHT = ResourceLoader
			.getImage("dropshadow/inner_bottom_right.png");
	private static final BufferedImage DROP_SHADOW_TOP_LEFT = ResourceLoader
			.getImage("dropshadow/inner_top_left.png");
	private static final BufferedImage DROP_SHADOW_TOP_RIGHT = ResourceLoader
			.getImage("dropshadow/inner_top_right.png");
	private static final BufferedImage DROP_SHADOW_BOTTOM = ResourceLoader
			.getImage("dropshadow/inner_bottom.png");
	private static final BufferedImage DROP_SHADOW_TOP = ResourceLoader
			.getImage("dropshadow/inner_top.png");
	private static final BufferedImage DROP_SHADOW_LEFT = ResourceLoader
			.getImage("dropshadow/inner_left.png");
	private static final BufferedImage DROP_SHADOW_RIGHT = ResourceLoader
			.getImage("dropshadow/inner_right.png");

	// TODO: This should likely be moved somewhere else.
	private static FragmentManager fragmentManager;
	private static PlayerLayer playerLayer;

	static {
		playerLayer = new PlayerLayer();
		ImageLayer[] imageLayers = { new BiomeLayer(), new SlimeLayer() };
		LiveLayer[] liveLayers = { new GridLayer() };
		IconLayer[] iconLayers = { new VillageLayer(),
				new OceanMonumentLayer(), new StrongholdLayer(),
				new TempleLayer(), new SpawnLayer(), new NetherFortressLayer(),
				playerLayer };
		LayerContainer layerContainer = new LayerContainer(imageLayers,
				liveLayers, iconLayers);
		fragmentManager = new FragmentManager(layerContainer);
	}

	private Listeners listeners = new Listeners();
	private Component component = new Component();

	private Widget mouseOwner;

	private Project proj;

	private JPopupMenu menu = new JPopupMenu();
	public int strongholdCount, villageCount;

	private Map map;
	private MapObject selectedObject = null;
	private Point lastMouse;
	public Point lastRightClick = null;
	private Point2D.Double panSpeed;

	private static int zoomLevel = 0, zoomTicksRemaining = 0;
	private static double targetZoom = 0.25f, curZoom = 0.25f;
	private Point zoomMouse = new Point();

	private Font textFont = new Font("arial", Font.BOLD, 15);

	private FontMetrics textMetrics;

	private ArrayList<Widget> widgets = new ArrayList<Widget>();

	public MapViewer(Project proj) {
		panSpeed = new Point2D.Double();
		this.proj = proj;
		if (playerLayer.isEnabled = proj.saveLoaded) {
			playerLayer.setPlayers(proj.save);
			for (MapObjectPlayer player : proj.save.getPlayers()) {
				menu.add(new PlayerMenuItem(this, player, playerLayer));
			}
		}

		map = new Map(fragmentManager);
		map.setZoom(curZoom);

		widgets.add(new FpsWidget(this)
				.setAnchorPoint(CornerAnchorPoint.BOTTOM_LEFT));
		widgets.add(new ScaleWidget(this)
				.setAnchorPoint(CornerAnchorPoint.BOTTOM_CENTER));
		widgets.add(new SeedWidget(this)
				.setAnchorPoint(CornerAnchorPoint.TOP_LEFT));
		widgets.add(new DebugWidget(this)
				.setAnchorPoint(CornerAnchorPoint.BOTTOM_RIGHT));
		widgets.add(new SelectedObjectWidget(this)
				.setAnchorPoint(CornerAnchorPoint.TOP_LEFT));
		widgets.add(new CursorInformationWidget(this)
				.setAnchorPoint(CornerAnchorPoint.TOP_RIGHT));
		widgets.add(new BiomeToggleWidget(this)
				.setAnchorPoint(CornerAnchorPoint.BOTTOM_RIGHT));
		widgets.add(BiomeWidget.get(this)
				.setAnchorPoint(CornerAnchorPoint.NONE));
		component.addMouseListener(listeners);
		component.addMouseWheelListener(listeners);

		component.setFocusable(true);

		textMetrics = component.getFontMetrics(textFont);
	}

	public void adjustZoom(Point position, int notches) {
		zoomMouse = position;
		if (notches > 0) {
			if (zoomLevel < (Options.instance.maxZoom.get() ? 10 : 10000)) {
				targetZoom /= 1.1;
				zoomLevel++;
				zoomTicksRemaining = 100;
			}
		} else {
			if (zoomLevel > -20) {
				targetZoom *= 1.1;
				zoomLevel--;
				zoomTicksRemaining = 100;
			}
		}
	}

	public void saveToFile(File f) {
		BufferedImage image = new BufferedImage(map.getViewerWidth(),
				map.getViewerHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		map.draw(g2d, 0);

		for (Widget widget : widgets)
			if (widget.isVisible())
				widget.draw(g2d, 0);

		try {
			ImageIO.write(image, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		g2d.dispose();
		image.flush();
	}

	public void movePlayer(String name, ActionEvent e) {
		// PixelInfo p = getCursorInformation(new Point(tempX, tempY));
		// proj.movePlayer(name, p);
	}

	private Point getMousePositionOrCenterFromComponent() {
		Point mouse = component.getMousePosition();
		if (mouse == null) {
			mouse = new Point(component.getWidth() >> 1,
					component.getHeight() >> 1);
		}
		return mouse;
	}

	public void dispose() {
		Log.debug("Disposing of map viewer.");
		map.dispose();
		menu.removeAll();
		proj = null;
	}

	public void centerAt(long x, long y) {
		map.centerOn(x, y);
	}

	public void repaint() {
		component.repaint();
	}

	public MapObject getSelectedObject() {
		return selectedObject;
	}

	public FragmentManager getFragmentManager() {
		return fragmentManager;
	}

	public Map getMap() {
		return map;
	}

	@Deprecated
	public FontMetrics getFontMetrics() {
		return textMetrics;
	}

	@Deprecated
	public FontMetrics getFontMetrics(Font font) {
		return component.getFontMetrics(font);
	}

	@Deprecated
	public KeyListener getKeyListener() {
		return listeners;
	}

	@Deprecated
	public JComponent getComponent() {
		return component;
	}

	@Deprecated
	public Point getMousePosition() {
		return component.getMousePosition();
	}

	@Deprecated
	public int getWidth() {
		return component.getWidth();
	}

	@Deprecated
	public int getHeight() {
		return component.getHeight();
	}
}
