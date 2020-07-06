package amidst.gui.main.bookmarks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import javax.swing.border.EmptyBorder;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.dishevelled.layout.ButtonPanel;

class BookmarkChooserDialog extends JDialog {
    private boolean canceled = true;

    private final AbstractAction cancel = new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                cancel();
            }
        };
    private final AbstractAction ok = new AbstractAction("OK") {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                ok();
            }
        };

    private final JButton okButton;
    private final BookmarkChooser chooser;

    BookmarkChooserDialog(final Dialog owner, final String title, final boolean modal, final BookmarkChooser chooser)
    {
        super(owner, title, modal);

        ok.setEnabled(false);
        okButton = new JButton(ok);
        this.chooser = chooser;

        initialize();
    }

    BookmarkChooserDialog(final Frame owner, final String title, final boolean modal, final BookmarkChooser chooser)
    {
        super(owner, title, modal);

        ok.setEnabled(false);
        okButton = new JButton(ok);
        this.chooser = chooser;

        initialize();
    }

    private void initialize()
    {
        getRootPane().setDefaultButton(okButton);
        createListeners();
        layoutComponents();
        setSize(320, 200);
    }

    private void createListeners()
    {
        DocumentListener l = new DocumentListener() {
                @Override
                public void changedUpdate(final DocumentEvent e) {
                    ok.setEnabled(chooser.ready());
                }

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    ok.setEnabled(chooser.ready());
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    ok.setEnabled(chooser.ready());
                }
            };

        chooser.x().getDocument().addDocumentListener(l);
        chooser.z().getDocument().addDocumentListener(l);
        chooser.label().getDocument().addDocumentListener(l);
    }

    private void layoutComponents()
    {
        ButtonPanel buttonPanel = new ButtonPanel();
        buttonPanel.setBorder(new EmptyBorder(0, 12, 12, 12));
        buttonPanel.add(cancel);
        buttonPanel.add(okButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add("Center", chooser);
        mainPanel.add("South", buttonPanel);

        setContentPane(mainPanel);
    }

    private void cancel()
    {
        canceled = true;
        hide();
    }

    private void ok()
    {
        canceled = false;
        hide();
    }

    boolean wasCanceled()
    {
        return canceled;
    }

    Bookmark getBookmark()
    {
        return wasCanceled() ? null : chooser.getBookmark();
    }
}
