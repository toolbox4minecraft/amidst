package amidst.gui;

import java.awt.Color;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import amidst.Amidst;

public class LicenseWindow {
	private static final String LICENSES_DIRECTORY = "licenses";

	private List<License> licenses = new ArrayList<License>();

	private JFrame frame;
	private JList<License> licenseList;
	private JTextArea licenseTextArea;

	public LicenseWindow() {
		createLicenses();
		createLicenseTextArea();
		createLicenseList();
		createFrame();
	}

	private void createLicenses() {
		licenses.add(createLicense("AMIDST", "amidst.txt"));
		licenses.add(createLicense("Args4j", "args4j.txt"));
		licenses.add(createLicense("Gson", "gson.txt"));
		licenses.add(createLicense("JGoogleAnalytics", "jgoogleanalytics.txt"));
		licenses.add(createLicense("JNBT", "jnbt.txt"));
		licenses.add(createLicense("Kryonet", "kryonet.txt"));
		licenses.add(createLicense("MiG Layout", "miglayout.txt"));
		licenses.add(createLicense("Rhino", "rhino.txt"));
	}

	private License createLicense(String name, String path) {
		return new License(name, LICENSES_DIRECTORY + "/" + path);
	}

	private void createLicenseTextArea() {
		licenseTextArea = new JTextArea();
		licenseTextArea.setEditable(false);
		licenseTextArea.setLineWrap(true);
		licenseTextArea.setWrapStyleWord(true);
	}

	private void createLicenseList() {
		licenseList = new JList<License>(getLicensesArray());
		licenseList.setBorder(new LineBorder(Color.darkGray, 1));
		licenseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		licenseList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				licenseTextArea.setText(licenseList.getSelectedValue()
						.getLicenseText());
				licenseTextArea.setCaretPosition(0);
			}
		});
		licenseList.setSelectedIndex(0);
	}

	private License[] getLicensesArray() {
		return licenses.toArray(new License[licenses.size()]);
	}

	private void createFrame() {
		frame = new JFrame("Licenses");
		initContentPane();
		frame.setIconImage(Amidst.icon);
		frame.setSize(870, 550);
		frame.setVisible(true);
	}

	private void initContentPane() {
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new MigLayout());
		contentPane.add(licenseList, "w 100!, h 0:2400:2400");
		contentPane.add(createScrollPane(), "w 0:4800:4800, h 0:2400:2400");
	}

	private JScrollPane createScrollPane() {
		JScrollPane result = new JScrollPane(licenseTextArea);
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return result;
	}
}
