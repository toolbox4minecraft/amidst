package amidst.gui;

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import amidst.Amidst;
import net.miginfocom.swing.MigLayout;

public class LicenseWindow extends JFrame {
	private static final long serialVersionUID = 3936119740592768287L;
	private ArrayList<License> licenses = new ArrayList<License>();
	private JList licenseList;
	private JTextArea licenseText = new JTextArea();
	public LicenseWindow() {
		super("Licenses");
		setIconImage(Amidst.icon);
		licenseText.setEditable(false);
		licenseText.setLineWrap(true);
		licenseText.setWrapStyleWord(true);
		
		licenses.add(new License("AMIDST",		     "licenses/amidst.txt"));
		licenses.add(new License("Args4j",		     "licenses/args4j.txt"));
		licenses.add(new License("Gson",			 "licenses/gson.txt"));
		licenses.add(new License("JGoogleAnalytics", "licenses/jgoogleanalytics.txt"));
		licenses.add(new License("JNBT",			 "licenses/jnbt.txt"));
		licenses.add(new License("Kryonet",          "licenses/kryonet.txt"));
		licenses.add(new License("MiG Layout",	     "licenses/miglayout.txt"));
		licenses.add(new License("Rhino",			 "licenses/rhino.txt"));
		licenseList = new JList(licenses.toArray());
		licenseList.setBorder(new LineBorder(Color.darkGray, 1));
		Container contentPane = this.getContentPane();
		MigLayout layout = new MigLayout();
		contentPane.setLayout(layout);
		contentPane.add(licenseList, "w 100!, h 0:2400:2400");
		JScrollPane scrollPane = new JScrollPane(licenseText);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		contentPane.add(scrollPane, "w 0:4800:4800, h 0:2400:2400");
		setSize(870, 550);
		setVisible(true);
		licenseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		licenseList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				License license = (License)licenseList.getSelectedValue();
				license.load();
				
				if (license.isLoaded()) {
					licenseText.setText(license.getContents());
					licenseText.setCaretPosition(0);
				}
			}
		});
		licenseList.setSelectedIndex(0);
	}
	
	public void addLicense(License license) {
		licenses.add(license);
		licenseList.setListData(licenses.toArray());
	}
}
