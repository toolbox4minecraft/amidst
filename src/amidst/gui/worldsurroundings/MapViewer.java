package amidst.gui.worldsurroundings;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import amidst.fragment.layer.LayerReloader;
import amidst.gui.widget.Widget;
import amidst.gui.widget.WidgetManager;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.Player;
import amidst.minecraft.world.World;

public class MapViewer {
	private class Listeners implements MouseListener, MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			Point mousePosition = getMousePositionFromEvent(e);
			if (!widgetManager.mouseWheelMoved(mousePosition, notches)) {
				mouseWheelMovedOnMap(mousePosition, notches);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			Point mousePosition = getMousePositionFromEvent(e);
			if (isRightClick(e)) {
				// noop
			} else if (!widgetManager.mouseClicked(mousePosition)) {
				mouseClickedOnMap(mousePosition);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			Point mousePosition = getMousePositionFromEvent(e);
			if (isPopup(e)) {
				showPlayerMenu(mousePosition, e.getComponent(), e.getX(),
						e.getY());
			} else if (isRightClick(e)) {
				// noop
			} else if (!widgetManager.mousePressed(mousePosition)) {
				mousePressedOnMap(mousePosition);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Point mousePosition = getMousePositionFromEvent(e);
			if (isPopup(e)) {
				showPlayerMenu(mousePosition, e.getComponent(), e.getX(),
						e.getY());
			} else if (!widgetManager.mouseReleased()) {
				mouseReleasedOnMap();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// noop
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// noop
		}

		private boolean isPopup(MouseEvent e) {
			return e.isPopupTrigger();
		}

		private boolean isRightClick(MouseEvent e) {
			return e.isMetaDown();
		}

		/**
		 * Don't use getMousePosition() of the JComponent in mouse events,
		 * because when computer is swapping/grinding, mouse may have moved out
		 * of window before execution reaches here.
		 */
		private Point getMousePositionFromEvent(MouseEvent e) {
			return e.getPoint();
		}
	}

	@SuppressWarnings("serial")
	private class ViewerComponent extends JComponent {
		private final FontMetrics widgetFontMetrics = getFontMetrics(Widget.TEXT_FONT);

		@Override
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			drawer.draw(g2d, getWidth(), getHeight(), getMousePosition(),
					widgetFontMetrics);
		}

		public void drawScreenshot(Graphics2D g2d) {
			drawer.drawScreenshot(g2d, getWidth(), getHeight(),
					getMousePosition(), widgetFontMetrics);
		}
	}

	private final Listeners listeners = new Listeners();
	private final ViewerComponent component = new ViewerComponent();

	private final Movement movement;
	private final Zoom zoom;
	private final World world;
	private final Map map;
	private final Drawer drawer;
	private final WorldIconSelection worldIconSelection;
	private final LayerReloader layerReloader;
	private final WidgetManager widgetManager;

	public MapViewer(Movement movement, Zoom zoom, World world, Map map,
			Drawer drawer, WorldIconSelection worldIconSelection,
			LayerReloader layerReloader, WidgetManager widgetManager) {
		this.movement = movement;
		this.zoom = zoom;
		this.world = world;
		this.map = map;
		this.drawer = drawer;
		this.worldIconSelection = worldIconSelection;
		this.layerReloader = layerReloader;
		this.widgetManager = widgetManager;
		initComponent();
	}

	private void initComponent() {
		component.addMouseListener(listeners);
		component.addMouseWheelListener(listeners);
		component.setFocusable(true);
	}

	public BufferedImage createCaptureImage() {
		BufferedImage image = new BufferedImage(component.getWidth(),
				component.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		component.drawScreenshot(g2d);
		g2d.dispose();
		return image;
	}

	public Point getMousePositionOrCenter() {
		Point result = component.getMousePosition();
		if (result == null) {
			result = new Point(component.getWidth() >> 1,
					component.getHeight() >> 1);
		}
		return result;
	}

	public void repaint() {
		component.repaint();
	}

	public Component getComponent() {
		return component;
	}

	private void showPlayerMenu(Point mousePosition, Component component,
			int x, int y) {
		if (MinecraftUtil.getVersion().saveEnabled() && world.hasPlayers()) {
			createPlayerMenu(mousePosition).show(component, x, y);
		}
	}

	private JPopupMenu createPlayerMenu(Point mousePosition) {
		JPopupMenu result = new JPopupMenu();
		for (Player player : world.getMovablePlayers()) {
			result.add(createPlayerMenuItem(player, mousePosition));
		}
		return result;
	}

	private JMenuItem createPlayerMenuItem(final Player player,
			final Point mousePosition) {
		JMenuItem result = new JMenuItem(player.getPlayerName());
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.moveTo(map.screenToWorld(mousePosition));
				world.reloadPlayerWorldIcons();
				layerReloader.reloadPlayerLayer();
			}
		});
		return result;
	}

	private void mouseWheelMovedOnMap(Point mousePosition, int notches) {
		zoom.adjustZoom(mousePosition, notches);
	}

	private void mouseClickedOnMap(Point mousePosition) {
		worldIconSelection.select(map.getClosestWorldIcon(mousePosition, 50.0));
	}

	private void mousePressedOnMap(Point mousePosition) {
		movement.setLastMouse(mousePosition);
	}

	private void mouseReleasedOnMap() {
		movement.setLastMouse(null);
	}
}
