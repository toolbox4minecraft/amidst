package amidst.gui.main.worldsurroundings.widget;

import java.awt.Point;
import java.util.List;

public class WidgetManager {
	private final List<Widget> widgets;

	private Widget mouseOwner;

	public WidgetManager(List<Widget> widgets) {
		this.widgets = widgets;
	}

	public boolean mouseWheelMoved(Point mousePosition, int notches) {
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

	public boolean mouseClicked(Point mousePosition) {
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

	public boolean mousePressed(Point mousePosition) {
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
