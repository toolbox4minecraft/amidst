package amidst.gui.main.bookmarks;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javax.swing.border.EmptyBorder;

import org.dishevelled.layout.LabelFieldPanel;

final class BookmarkChooser extends LabelFieldPanel {
    private final JTextField x;
    private final JTextField z;
    private final JTextField label;

    BookmarkChooser(final int x, final int z) {
        super();
        this.x = new JTextField(String.valueOf(x));
        this.z = new JTextField(String.valueOf(z));
        label = new JTextField();
        label.requestFocus();

        layoutComponents();
    }

    private void layoutComponents() {
        setBorder(new EmptyBorder(12, 12, 0, 12));
        addField("X", x);
        addField("Z", z);
        addField("Label", label);
        addFinalSpacing(12);
    }

    JTextField x() {
        return x;
    }

    JTextField z() {
        return z;
    }

    JTextField label() {
        return label;
    }

    boolean ready() {
        // todo validate
        return true;
    }

    Bookmark getBookmark() {
        if (ready()) {
            return Bookmark.valueOf(Integer.parseInt(x.getText()), Integer.parseInt(z.getText()), label.getText());
        }
        return null;
    }

    public static Bookmark showDialog(final Component component, final String title, final int x, final int z) {
        if (component == null) {
            throw new IllegalArgumentException("component must not be null");
        }
        if (title == null) {
            throw new IllegalArgumentException("title must not be null");
        }
        BookmarkChooser chooserPane = new BookmarkChooser(x, z);
        BookmarkChooserDialog dialog = createDialog(component, title, true, chooserPane);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(component);
        dialog.show();
        return dialog.getBookmark();
    }

    static BookmarkChooserDialog createDialog(final Component component, final String title, final boolean modal, final BookmarkChooser chooserPane) {
        if (component == null) {
            throw new IllegalArgumentException("component must not be null");
        }
        if (title == null) {
            throw new IllegalArgumentException("title must not be null");
        }
        if (chooserPane == null) {
            throw new IllegalArgumentException("chooserPane must not be null");
        }
        Window window = SwingUtilities.windowForComponent(component);
        BookmarkChooserDialog dialog;
        if (window instanceof Frame) {
            dialog = new BookmarkChooserDialog((Frame) window, title, modal, chooserPane);
        }
        else { //if (window instanceof Dialog)
            dialog = new BookmarkChooserDialog((Dialog) window, title, modal, chooserPane);
        }
        dialog.getAccessibleContext().setAccessibleDescription(title);
        return dialog;
    }
}
