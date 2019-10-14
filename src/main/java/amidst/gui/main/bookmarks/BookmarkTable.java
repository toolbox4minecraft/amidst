package amidst.gui.main.bookmarks;

import ca.odell.glazedlists.GlazedLists;

import ca.odell.glazedlists.gui.TableFormat;

import org.dishevelled.eventlist.view.ElementsTable;

/**
 * Bookmark table.
 */
public final class BookmarkTable extends ElementsTable<Bookmark> {

    /** Table property names. */
    private static final String[] PROPERTY_NAMES = { "x", "z", "label" };

    /** Table column labels. */
    private static final String[] COLUMN_LABELS = { "X", "Z", "Label" };

    /** Table format. */
    private static final TableFormat<Bookmark> TABLE_FORMAT = GlazedLists.tableFormat(Bookmark.class, PROPERTY_NAMES, COLUMN_LABELS);


    public BookmarkTable(final Bookmarks bookmarks) {
        super("Bookmarks", bookmarks, TABLE_FORMAT);
    }


    @Override
    public void add() {
        Bookmark bookmark = BookmarkChooser.showDialog(this, "Create a new bookmark", 0, 0);
        if (bookmark != null) {
            getModel().add(bookmark);
        }
    }
}
