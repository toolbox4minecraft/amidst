package amidst.gui.license;

import java.awt.Color;
import java.awt.Container;
import java.util.Arrays;
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

import amidst.AmidstMetaData;
import amidst.documentation.Immutable;

@Immutable
public class LicenseWindow {
	private static final String LICENSES_DIRECTORY = "/amidst/gui/license/";

	private final AmidstMetaData metadata;

	public LicenseWindow(AmidstMetaData metadata) {
		this.metadata = metadata;
		License[] licenses = createLicenses();
		JTextArea textArea = createLicenseTextArea();
		JScrollPane scrollPane = createScrollPane(textArea);
		JList<License> licenseList = createLicenseList(licenses, textArea);
		createFrame(licenseList, scrollPane);
	}

	private License[] createLicenses() {
		List<License> result = Arrays.asList(
				createLicense("Amidst", "amidst.txt"),
				createLicense("Args4j", "args4j.txt"),
				createLicense("Gson", "gson.txt"),
				createLicense("MiG Layout", "miglayout.txt"),
				createLicense("Querz-NBT", "querz-nbt.txt"));
		return result.toArray(new License[result.size()]);
	}

	private License createLicense(String name, String path) {
		return new License(name, LICENSES_DIRECTORY + path);
	}

	private JTextArea createLicenseTextArea() {
		JTextArea result = new JTextArea();
		result.setEditable(false);
		result.setLineWrap(true);
		result.setWrapStyleWord(true);
		return result;
	}

	private JScrollPane createScrollPane(JTextArea textArea) {
		JScrollPane result = new JScrollPane(textArea);
		result.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return result;
	}

	private JList<License> createLicenseList(License[] licenses, final JTextArea textArea) {
		final JList<License> result = new JList<>(licenses);
		result.setBorder(new LineBorder(Color.darkGray, 1));
		result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		result.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				textArea.setText(result.getSelectedValue().getLicenseText());
				textArea.setCaretPosition(0);
			}
		});
		result.setSelectedIndex(0);
		return result;
	}

	private JFrame createFrame(JList<License> licenseList, JScrollPane scrollPane) {
		JFrame frame = new JFrame("Licenses");
		initContentPane(frame.getContentPane(), licenseList, scrollPane);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImages(metadata.getIcons());
		frame.setSize(870, 550);
		frame.setVisible(true);
		return frame;
	}

	private void initContentPane(Container contentPane, JList<License> licenseList, JScrollPane scrollPane) {
		contentPane.setLayout(new MigLayout());
		contentPane.add(licenseList, "w 150!, h 0:2400:2400");
		contentPane.add(scrollPane, "w 0:4800:4800, h 0:2400:2400");
	}
}
