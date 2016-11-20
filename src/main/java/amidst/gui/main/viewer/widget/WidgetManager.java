package amidst.gui.main.viewer.widget;

import java.awt.Point;
import java.util.List;

import amidst.documentation.AmidstThread;
import amidst.documentation.CalledOnlyBy;
import amidst.documentation.NotThreadSafe;

@NotThreadSafe
public class WidgetManager {
	private final List<Widget> widgets;

	private Widget mouseOwner;

	@CalledOnlyBy(AmidstThread.EDT)
	public WidgetManager(List<Widget> widgets) {
		this.widgets = widgets;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean mouseWheelMoved(Point mousePosition, int notches) {
		for (Widget widget : widgets) {
			if (widget.isVisible() && widget.isInBounds(mousePosition)
					&& widget.onMouseWheelMoved(
							widget.translateXToWidgetCoordinates(mousePosition),
							widget.translateYToWidgetCoordinates(mousePosition),
							notches)) {
				return true;
			}
		}
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean mouseClicked(Point mousePosition) {
		for (Widget widget : widgets) {
			if (widget.isVisible() && widget.isInBounds(mousePosition)
					&& widget.onClick(
							widget.translateXToWidgetCoordinates(mousePosition),
							widget.translateYToWidgetCoordinates(mousePosition))) {
				return true;
			}
		}
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean mousePressed(Point mousePosition) {
		for (Widget widget : widgets) {
			if (widget.isVisible() && widget.isInBounds(mousePosition)
					&& widget.onMousePressed(
							widget.translateXToWidgetCoordinates(mousePosition),
							widget.translateYToWidgetCoordinates(mousePosition))) {
				mouseOwner = widget;
				return true;
			}
		}
		return false;
	}

	@CalledOnlyBy(AmidstThread.EDT)
	public boolean mouseReleased() {
		if (mouseOwner != null) {
			mouseOwner.onMouseReleased();
			mouseOwner = null;
			return true;
		} else {
			return false;
		}
	}
}
