package amidst.gui.main.viewer;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;
import amidst.fragment.FragmentGraph;
import amidst.gui.main.Actions;
import amidst.gui.main.viewer.widget.WidgetManager;

@NotThreadSafe
public class ViewerMouseListener implements MouseListener, MouseWheelListener {
	private final WidgetManager widgetManager;
	private final FragmentGraph graph;
	private final FragmentGraphToScreenTranslator translator;
	private final Zoom zoom;
	private final Movement movement;
	private final Actions actions;

	@CalledOnlyBy(AmidstThread.EDT)
	public ViewerMouseListener(
			WidgetManager widgetManager,
			FragmentGraph graph,
			FragmentGraphToScreenTranslator translator,
			Zoom zoom,
			Movement movement,
			Actions actions) {
		this.widgetManager = widgetManager;
		this.graph = graph;
		this.translator = translator;
		this.zoom = zoom;
		this.movement = movement;
		this.actions = actions;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Point mousePosition = e.getPoint();
		int notches = e.getWheelRotation();
		if (!widgetManager.mouseWheelMoved(mousePosition, notches)) {
			doMouseWheelMoved(mousePosition, notches);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void mouseClicked(MouseEvent e) {
		Point mousePosition = e.getPoint();
		if (isRightClick(e)) {
			// noop
		} else if (!widgetManager.mouseClicked(mousePosition)) {
			doMouseClicked(mousePosition);
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
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

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void mouseReleased(MouseEvent e) {
		Point mousePosition = e.getPoint();
		if (isPopup(e)) {
			showPopupMenu(mousePosition, e.getComponent(), e.getX(), e.getY());
		} else if (!widgetManager.mouseReleased()) {
			doMouseReleased();
		}
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void mouseEntered(MouseEvent e) {
		// noop
	}

	@CalledOnlyBy(AmidstThread.EDT)
	@Override
	public void mouseExited(MouseEvent e) {
		// noop
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isPopup(MouseEvent e) {
		return e.isPopupTrigger();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private boolean isRightClick(MouseEvent e) {
		return e.isMetaDown();
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void doMouseWheelMoved(Point mousePosition, int notches) {
		actions.adjustZoom(mousePosition, notches);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void doMouseClicked(Point mousePosition) {
		actions.selectWorldIcon(
				graph.getClosestWorldIcon(translator.screenToWorld(mousePosition), zoom.screenToWorld(50)));
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void doMousePressed(Point mousePosition) {
		movement.setLastMouse(mousePosition);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void doMouseReleased() {
		movement.setLastMouse(null);
	}

	@CalledOnlyBy(AmidstThread.EDT)
	private void showPopupMenu(Point mousePosition, Component component, int x, int y) {
		actions.showPlayerPopupMenu(translator.screenToWorld(mousePosition), component, x, y);
	}
}
