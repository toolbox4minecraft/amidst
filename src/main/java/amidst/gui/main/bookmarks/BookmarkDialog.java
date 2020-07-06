package amidst.gui.main.bookmarks;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import javax.swing.border.EmptyBorder;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

import com.google.common.io.Files;

import org.dishevelled.iconbundle.IconSize;

import org.dishevelled.iconbundle.tango.TangoProject;

import org.dishevelled.identify.ContextMenuListener;
import org.dishevelled.identify.IdentifiableAction;
import org.dishevelled.identify.IdButton;
import org.dishevelled.identify.IdMenuItem;
import org.dishevelled.identify.IdToolBar;

import org.dishevelled.layout.LabelFieldPanel;

public final class BookmarkDialog extends JDialog {
    private boolean dirty = false;
    private final Bookmarks bookmarks;
    private final BookmarkList bookmarkList;
    private final BookmarkTable bookmarkTable;

    /** Open action. */
    private final IdentifiableAction open = new IdentifiableAction("Open bookmarks...", TangoProject.DOCUMENT_OPEN)
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                open();
            }
        };

    /** Save action. */
    private final IdentifiableAction save = new IdentifiableAction("Save bookmarks...", TangoProject.DOCUMENT_SAVE)
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                save();
            }
        };


    public BookmarkDialog(final JFrame frame) {
        super(frame, "Bookmarks");
        bookmarks = Bookmarks.getInstance();
        bookmarkList = new BookmarkList(bookmarks);
        bookmarkTable = new BookmarkTable(bookmarks);

        bookmarks.addListEventListener(new ListEventListener<Bookmark>() {
                @Override
                public void listChanged(final ListEvent<Bookmark> e) {
                    dirty = true;
                    save.setEnabled(true);
                }
            });

        open.setEnabled(true);
        save.setEnabled(false);

        layoutComponents();
        setSize(450, 640);
        setLocationRelativeTo(frame);
    }

    private void layoutComponents() {
        JPopupMenu contextMenu = new JPopupMenu();
        contextMenu.add(open);
        contextMenu.add(save);
        bookmarkList.addMouseListener(new ContextMenuListener(contextMenu));
        bookmarkTable.addMouseListener(new ContextMenuListener(contextMenu));

        IdToolBar toolBar = new IdToolBar();
        IdButton openButton = toolBar.add(open);
        openButton.setBorderPainted(false);
        openButton.setFocusPainted(false);
        IdButton saveButton = toolBar.add(save);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);

        toolBar.displayIcons();
        toolBar.setIconSize(TangoProject.SMALL);

        JPopupMenu toolBarContextMenu = new JPopupMenu();
        for (Object menuItem : toolBar.getDisplayMenuItems())
        {
            toolBarContextMenu.add((JCheckBoxMenuItem) menuItem);
        }
        toolBarContextMenu.addSeparator();
        for (Object iconSize : TangoProject.SIZES)
        {
            toolBarContextMenu.add(toolBar.createIconSizeMenuItem((IconSize) iconSize));
        }
        toolBar.addMouseListener(new ContextMenuListener(toolBarContextMenu));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("As bookmark list", createListPanel());
        tabbedPane.addTab("As bookmark table", createTablePanel());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add("North", toolBar);
        mainPanel.add("Center", tabbedPane);
        setContentPane(mainPanel);
    }

    private JPanel createListPanel() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setBorder(12);
        panel.addFinalField(bookmarkList);
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createTablePanel() {
        LabelFieldPanel panel = new LabelFieldPanel();
        panel.setBorder(12);
        panel.addFinalField(bookmarkTable);
        panel.setOpaque(false);
        return panel;
    }

    private void open() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(getContentPane());

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            for (File file: fileChooser.getSelectedFiles())
            {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String ext = Files.getFileExtension(file.getName());
                    if ("csv".equals(ext)) {
                        bookmarks.readCsvFrom(reader);
                    }
                    else if ("txt".equals(ext) || "tab".equals(ext) || "tsv".equals(ext)) {
                        bookmarks.readTsvFrom(reader);
                    }
                    else if ("json".equals(ext) || "js".equals(ext)) {
                        bookmarks.readJsonFrom(reader);
                    }
                    else {
                        // default to .tsv
                        bookmarks.readTsvFrom(reader);
                    }
                }
                catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    private void save() {
        if (dirty) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(false);
            int returnVal = fileChooser.showSaveDialog(getContentPane());

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fileChooser.getSelectedFile();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    String ext = Files.getFileExtension(file.getName());
                    if ("csv".equals(ext)) {
                        bookmarks.writeCsvTo(writer);
                    }
                    else if ("txt".equals(ext) || "tab".equals(ext) || "tsv".equals(ext)) {
                        bookmarks.writeTsvTo(writer);
                    }
                    else if ("json".equals(ext) || "js".equals(ext)) {
                        bookmarks.writeJsonTo(writer);
                    }
                    else {
                        // default to .tsv
                        bookmarks.writeTsvTo(writer);
                    }
                }
                catch (Exception e) {
                    // ignore
                }
                dirty = false;
                save.setEnabled(false);
            }
        }
    }

    public Bookmarks getBookmarks() {
        return bookmarks;
    }
}
