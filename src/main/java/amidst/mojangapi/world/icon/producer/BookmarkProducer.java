package amidst.mojangapi.world.icon.producer;

import java.util.LinkedList;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.gui.main.bookmarks.Bookmark;
import amidst.gui.main.bookmarks.Bookmarks;
import amidst.logging.AmidstLogger;
import amidst.mojangapi.world.Dimension;
import amidst.mojangapi.world.coordinates.CoordinatesInWorld;
import amidst.mojangapi.world.icon.WorldIcon;
import amidst.mojangapi.world.icon.type.DefaultWorldIconTypes;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

@ThreadSafe
public class BookmarkProducer extends CachedWorldIconProducer {
    private final Bookmarks bookmarks;

    public BookmarkProducer(final Bookmarks bookmarks) {
        this.bookmarks = bookmarks;
        this.bookmarks.addListEventListener(new ListEventListener<Bookmark>() {
                @Override
                public void listChanged(final ListEvent<Bookmark> e) {
                    AmidstLogger.info("Resetting cache on list event " + e + "...");
                    resetCache();
                }
            });
    }

    @Override
    protected List<WorldIcon> doCreateCache() {
        AmidstLogger.info("Creating bookmark world icon cache...");
        List<WorldIcon> result = new LinkedList<>();
        for (Bookmark b : bookmarks) {
            AmidstLogger.info("Creating bookmark world icon at [" + b.getX() + ", " + b.getZ() + "] in world coordinates " + CoordinatesInWorld.from((long) b.getX(), (long) b.getZ()) + "...");
            result.add(new WorldIcon(CoordinatesInWorld.from((long) b.getX(), (long) b.getZ()), b.getLabel(), DefaultWorldIconTypes.BOOKMARK.getImage(), Dimension.OVERWORLD, true));
        }
        return result;
    }
}
