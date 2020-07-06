package amidst.gui.main.bookmarks;

import java.io.Serializable;

import java.util.Objects;

/**
 * Bookmark.
 */
public final class Bookmark implements Serializable {
    private final long x;
    private final long z;
    private final String label;
    private final int hashCode;

    private Bookmark(final long x, final long z, final String label) {
        if (label == null) {
            throw new NullPointerException("label must not be null");
        }
        this.x = x;
        this.z = z;
        this.label = label;
        this.hashCode = Objects.hash(this.x, this.z, this.label);
    }

    public long getX() {
        return x;
    }

    public long getZ() {
        return z;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public static Bookmark valueOf(final long x, final long z, final String label) {
        return new Bookmark(x, z, label);
    }
}
