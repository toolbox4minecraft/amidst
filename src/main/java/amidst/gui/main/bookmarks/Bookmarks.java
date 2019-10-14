package amidst.gui.main.bookmarks;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;

import ca.odell.glazedlists.GlazedLists;

/**
 * Bookmarks.
 */
public final class Bookmarks extends ForwardingEventList<Bookmark> {

    private Bookmarks() {
        super(GlazedLists.eventList(new ArrayList<Bookmark>()));
    }

    public void readCsvFrom(final BufferedReader reader) throws IOException {
        while (reader.ready()) {
            String line = reader.readLine()
                .replace("\"", "")
                .replace(",", "\t");
            String[] tokens = line.split("\t");
            if (tokens.length == 3) {
                add(Bookmark.valueOf(Long.parseLong(tokens[0]), Long.parseLong(tokens[1]), tokens[2]));
            }
        }
    }

    public void readTsvFrom(final BufferedReader reader) throws IOException {
        while (reader.ready()) {
            String line = reader.readLine();
            String[] tokens = line.split("\t");
            if (tokens.length == 3) {
                add(Bookmark.valueOf(Long.parseLong(tokens[0]), Long.parseLong(tokens[1]), tokens[2]));
            }
        }
    }

    public void readJsonFrom(final BufferedReader reader) throws IOException {
        while (reader.ready()) {
            String line = reader.readLine()
                .replace("{ \"x\":", "")
                .replace(", \"z\":", "\t")
                .replace(", \"label\":\"", "\t")
                .replace("\" },", "")
                .replace("\" }", "");
            String[] tokens = line.split("\t");
            if (tokens.length == 3) {
                add(Bookmark.valueOf(Long.parseLong(tokens[0]), Long.parseLong(tokens[1]), tokens[2]));
            }
        }
    }

    public void writeCsvTo(final Appendable a) throws IOException {
        for (Bookmark b : this) {
            a.append(String.valueOf(b.getX()));
            a.append(",");
            a.append(String.valueOf(b.getZ()));
            a.append(",\"");
            a.append(b.getLabel());
            a.append("\"\n");
        }
    }

    public void writeTsvTo(final Appendable a) throws IOException {
        for (Bookmark b : this) {
            a.append(String.valueOf(b.getX()));
            a.append("\t");
            a.append(String.valueOf(b.getZ()));
            a.append("\t");
            a.append(b.getLabel());
            a.append("\n");
        }
    }

    public void writeJsonTo(final Appendable a) throws IOException {
        for (Iterator<Bookmark> i = iterator(); i.hasNext(); ) {
            Bookmark b = i.next();
            a.append("{ \"x\":");
            a.append(String.valueOf(b.getX()));
            a.append(", \"z\":");
            a.append(String.valueOf(b.getZ()));
            a.append(", \"label\":\"");
            a.append(b.getLabel());
            a.append("\" }");
            if (i.hasNext()) {
                a.append(",");
            }
            a.append("\n");
        }
    }


    // todo: is there a better place to instantiate this?
    private static final Bookmarks INSTANCE = new Bookmarks();
    public static final Bookmarks getInstance() {
        return INSTANCE;
    }
}
