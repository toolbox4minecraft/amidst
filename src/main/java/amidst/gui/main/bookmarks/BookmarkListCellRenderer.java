package amidst.gui.main.bookmarks;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;

import org.dishevelled.identify.StripeListCellRenderer;

final class BookmarkListCellRenderer extends StripeListCellRenderer {

    @Override
    public final Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean hasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

        if (value instanceof Bookmark) {
            Bookmark bookmark = (Bookmark) value;
            label.setText(bookmark.getLabel() + " [" + bookmark.getX() + ", " + bookmark.getZ() + "]");
        }
        return label;
    }
}
