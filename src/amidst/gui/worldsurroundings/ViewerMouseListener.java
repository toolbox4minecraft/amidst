package amidst.gui.worldsurroundings;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import amidst.fragment.FragmentGraph;
import amidst.gui.menu.MenuActions;
import amidst.gui.widget.WidgetManager;

public class ViewerMouseListener implements MouseListener, MouseWheelListener {
	private final WidgetManager widgetManager;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final Movement movement;
	private final MenuActions actions;

	public ViewerMouseListener(WidgetManager widgetManager,
			FragmentGraph graph, FragmentGraphToScreenTranslator translator,
			Zoom zoom, Movement movement, MenuActions actions) {
		this.widgetManager = widgetManager;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.movement = movement;
		this.actions = actions;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point mousePosition = e.getPoint();
		int notches = e.getWheelRotation();
		if (!widgetManager.mouseWheelMoved(mousePosition, notches)) {
			doMouseWheelMoved(mousePosition, notches);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point mousePosition = e.getPoint();
		if (isRightClick(e)) {
			// noop
		} else if (!widgetManager.mouseClicked(mousePosition)) {
			doMouseClicked(mousePosition);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point mousePosition = e.getPoint();
		if (isPopup(e)) {
			showPopupMenu(mousePosition, e.getComponent(), e.getX(), e.getY());
		} else if (isRightClick(e)) {
			// noop
		} else if (!widgetManager.mousePressed(mousePosition)) {
			doMousePressed(mousePosition);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point mousePosition = e.getPoint();
		if (isPopup(e)) {
			showPopupMenu(mousePosition, e.getComponent(), e.getX(), e.getY());
		} else if (!widgetManager.mouseReleased()) {
			doMouseReleased();
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

	private void doMouseWheelMoved(Point mousePosition, int notches) {
		actions.adjustZoom(mousePosition, notches);
	}

	private void doMouseClicked(Point mousePosition) {
		actions.selectWorldIcon(graph.getClosestWorldIcon(
				translator.screenToWorld(mousePosition), zoom.screenToWorld(50)));
	}

	private void doMousePressed(Point mousePosition) {
		movement.setLastMouse(mousePosition);
	}

	private void doMouseReleased() {
		movement.setLastMouse(null);
	}

	private void showPopupMenu(Point mousePosition, Component component, int x,
			int y) {
		actions.showPlayerPopupMenu(translator.screenToWorld(mousePosition),
				component, x, y);
	}
}
