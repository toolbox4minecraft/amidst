package amidst.map;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import amidst.map.widget.BiomeToggleWidget;
import amidst.map.widget.BiomeWidget;
import amidst.map.widget.CursorInformationWidget;
import amidst.map.widget.DebugWidget;
import amidst.map.widget.FpsWidget;
import amidst.map.widget.ScaleWidget;
import amidst.map.widget.SeedWidget;
import amidst.map.widget.SelectedObjectWidget;
import amidst.map.widget.Widget;
import amidst.map.widget.Widget.CornerAnchorPoint;
import amidst.minecraft.MinecraftUtil;
import amidst.minecraft.world.FileWorld.Player;
import amidst.minecraft.world.World;

public class MapViewer {
	private class Listeners implements MouseListener, MouseWheelListener {
		private Widget mouseOwner;

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			Point mouse = getMousePositionFromEvent(e);
			if (!mouseWheelMovedOnWidget(notches, mouse)) {
				zoom.adjustZoom(mouse, notches);
			}
		}

		private boolean mouseWheelMovedOnWidget(int notches, Point mouse) {
			for (Widget widget : widgets) {
				if (widget.isVisible() && widget.isInBounds(mouse)) {
					if (widget != null
							&& widget
									.onMouseWheelMoved(
											widget.translateXToWidgetCoordinates(mouse),
											widget.translateYToWidgetCoordinates(mouse),
											notches)) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.isMetaDown()) {
				return;
			}
			Point mouse = getMousePositionFromEvent(e);
			if (!mouseClickedOnWidget(mouse)) {
				mouseClickedOnMap(mouse);
			}
		}

		private boolean mouseClickedOnWidget(Point mouse) {
			for (Widget widget : widgets) {
				if (widget.isVisible() && widget.isInBounds(mouse)) {
					if (widget != null
							&& widget
									.onClick(
											widget.translateXToWidgetCoordinates(mouse),
											widget.translateYToWidgetCoordinates(mouse))) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			Point mouse = getMousePositionFromEvent(e);
			if (e.isPopupTrigger()) {
				showPlayerMenu(e);
			} else if (e.isMetaDown()) {
				// noop
			} else if (mousePressedOnWidget(e, mouse)) {
				// noop
			} else {
				movement.setLastMouse(mouse);
			}
		}

		private boolean mousePressedOnWidget(MouseEvent e, Point mouse) {
			for (Widget widget : widgets) {
				if (widget.isVisible() && widget.isInBounds(mouse)) {
					if (widget != null
							&& widget
									.onMousePressed(
											widget.translateXToWidgetCoordinates(mouse),
											widget.translateYToWidgetCoordinates(mouse))) {
						mouseOwner = widget;
						return true;
					}
				}
			}
			return false;
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
			if (MinecraftUtil.getVersion().saveEnabled() && world.isFileWorld()) {
				createPlayerMenu(getMousePositionFromEvent(e)).show(
						e.getComponent(), e.getX(), e.getY());
			}
		}

		private JPopupMenu createPlayerMenu(Point lastRightClicked) {
			JPopupMenu result = new JPopupMenu();
			if (world.isFileWorld()) {
				for (Player player : world.getAsFileWorld().getMovablePlayers()) {
					result.add(createPlayerMenuItem(player, lastRightClicked));
				}
			}
			return result;
		}

		private JMenuItem createPlayerMenuItem(final Player player,
				final Point lastRightClick) {
			JMenuItem result = new JMenuItem(player.getPlayerName());
			result.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					player.moveTo(map.screenToWorld(lastRightClick));
					world.reloadPlayers();
					map.reloadPlayerLayer();
				}
			});
			return result;
		}

		private void mouseClickedOnMap(Point mouse) {
			map.setSelectedWorldObject(map.getWorldObjectAt(mouse, 50.0));
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
	private class Component extends JComponent {
		private long lastTime = System.currentTimeMillis();

		@Override
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			float time = calculateTimeSpanSinceLastDrawInSeconds();
			drawer.draw(g2d, time, getWidth(), getHeight(), getMousePosition());
		}

		private float calculateTimeSpanSinceLastDrawInSeconds() {
			long currentTime = System.currentTimeMillis();
			float result = Math.min(Math.max(0, currentTime - lastTime), 100) / 1000.0f;
			lastTime = currentTime;
			return result;
		}

		public void drawScreenshot(Graphics2D g2d) {
			drawer.drawScreenshot(g2d, 0, getWidth(), getHeight(),
					getMousePosition());
		}
	}

	private Listeners listeners = new Listeners();
	private List<Widget> widgets = new ArrayList<Widget>();
	private Component component = new Component();
	private JPanel panel = new JPanel();
	private MapDrawer drawer;

	private MapMovement movement;
	private MapZoom zoom;
	private World world;
	private Map map;

	public MapViewer(MapMovement movement, MapZoom zoom, World world, Map map) {
		this.movement = movement;
		this.zoom = zoom;
		this.world = world;
		this.map = map;
		initWidgets();
		initComponent();
		initPanel();
		initDrawer();
	}

	private void initWidgets() {
		widgets.add(new FpsWidget(this, map, world,
				CornerAnchorPoint.BOTTOM_LEFT));
		widgets.add(new ScaleWidget(this, map, world,
				CornerAnchorPoint.BOTTOM_CENTER));
		widgets.add(new SeedWidget(this, map, world, CornerAnchorPoint.TOP_LEFT));
		widgets.add(new DebugWidget(this, map, world,
				CornerAnchorPoint.BOTTOM_RIGHT));
		widgets.add(new SelectedObjectWidget(this, map, world,
				CornerAnchorPoint.TOP_LEFT));
		widgets.add(new CursorInformationWidget(this, map, world,
				CornerAnchorPoint.TOP_RIGHT));
		widgets.add(new BiomeToggleWidget(this, map, world,
				CornerAnchorPoint.BOTTOM_RIGHT));
		widgets.add(new BiomeWidget(this, map, world, CornerAnchorPoint.NONE));
	}

	private void initComponent() {
		component.addMouseListener(listeners);
		component.addMouseWheelListener(listeners);
		component.setFocusable(true);
	}

	private void initPanel() {
		panel.setBackground(Color.BLUE);
		panel.setLayout(new BorderLayout());
		panel.add(component, BorderLayout.CENTER);
	}

	private void initDrawer() {
		drawer = new MapDrawer(map, movement, zoom, widgets,
				component.getFontMetrics(Widget.TEXT_FONT));
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

	public Point getMousePosition() {
		return component.getMousePosition();
	}

	public int getWidth() {
		return component.getWidth();
	}

	public int getHeight() {
		return component.getHeight();
	}

	public void repaint() {
		component.repaint();
	}

	public JPanel getPanel() {
		return panel;
	}
}
