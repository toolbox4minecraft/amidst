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
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import amidst.fragment.layer.LayerReloader;
import amidst.gui.widget.Widget;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.Player;
import amidst.minecraft.world.World;

public class MapViewer {
	private class Listeners implements MouseListener, MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			Point mousePosition = getMousePositionFromEvent(e);
			if (!mouseWheelMovedOnWidget(mousePosition, notches)) {
				zoom.adjustZoom(mousePosition, notches);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.isMetaDown()) {
				return;
			}
			Point mousePosition = getMousePositionFromEvent(e);
			if (!mouseClickedOnWidget(mousePosition)) {
				mouseClickedOnMap(mousePosition);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			Point mousePosition = getMousePositionFromEvent(e);
			if (e.isPopupTrigger()) {
				showPlayerMenu(e);
			} else if (e.isMetaDown()) {
				// noop
			} else if (mousePressedOnWidget(mousePosition)) {
				// noop
			} else {
				movement.setLastMouse(mousePosition);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPlayerMenu(e);
			} else if (mouseOwner != null) {
				mouseOwner.onMouseReleased();
				mouseOwner = null;
			} else {
				movement.setLastMouse(null);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		private void showPlayerMenu(MouseEvent e) {
			if (MinecraftUtil.getVersion().saveEnabled() && world.hasPlayers()) {
				createPlayerMenu(getMousePositionFromEvent(e)).show(
						e.getComponent(), e.getX(), e.getY());
			}
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
	private final List<Widget> widgets;

	private Widget mouseOwner;

	public MapViewer(Movement movement, Zoom zoom, World world, Map map,
			Drawer drawer, WorldIconSelection worldIconSelection,
			LayerReloader layerReloader, List<Widget> widgets) {
		this.movement = movement;
		this.zoom = zoom;
		this.world = world;
		this.map = map;
		this.drawer = drawer;
		this.worldIconSelection = worldIconSelection;
		this.layerReloader = layerReloader;
		this.widgets = widgets;
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

	private boolean mouseWheelMovedOnWidget(Point mousePosition, int notches) {
		for (Widget widget : widgets) {
			if (widget.isVisible()
					&& widget.isInBounds(mousePosition)
					&& widget
							.onMouseWheelMoved(
									widget.translateXToWidgetCoordinates(mousePosition),
									widget.translateYToWidgetCoordinates(mousePosition),
									notches)) {
				return true;
			}
		}
		return false;
	}

	private boolean mouseClickedOnWidget(Point mousePosition) {
		for (Widget widget : widgets) {
			if (widget.isVisible()
					&& widget.isInBounds(mousePosition)
					&& widget
							.onClick(
									widget.translateXToWidgetCoordinates(mousePosition),
									widget.translateYToWidgetCoordinates(mousePosition))) {
				return true;
			}
		}
		return false;
	}

	private boolean mousePressedOnWidget(Point mousePosition) {
		for (Widget widget : widgets) {
			if (widget.isVisible()
					&& widget.isInBounds(mousePosition)
					&& widget
							.onMousePressed(
									widget.translateXToWidgetCoordinates(mousePosition),
									widget.translateYToWidgetCoordinates(mousePosition))) {
				mouseOwner = widget;
				return true;
			}
		}
		return false;
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

	private void mouseClickedOnMap(Point mousePosition) {
		worldIconSelection.select(map.getClosestWorldIcon(mousePosition, 50.0));
	}
}
