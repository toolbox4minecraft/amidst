package amidst.gui.main.bookmarks;

import org.dishevelled.eventlist.view.ElementsList;

/**
 * Bookmark list.
 */
public final class BookmarkList extends ElementsList<Bookmark> {

    public BookmarkList(final Bookmarks bookmarks) {
        super("Bookmarks", bookmarks);
        getList().setCellRenderer(new BookmarkListCellRenderer());
    }

    @Override
    public void add() {
        Bookmark bookmark = BookmarkChooser.showDialog(this, "Create a new bookmark", 0, 0);
        if (bookmark != null) {
            getModel().add(bookmark);
        }
    }
}
