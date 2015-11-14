package MoF;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import amidst.Amidst;

public class UpdateManager extends Thread {
	public static final String UPDATE_URL = "https://sites.google.com/site/mothfinder/update.xml";
	public static final String UPDATE_UNSTABLE_URL = "https://sites.google.com/site/mothfinder/update_unstable.xml";

	private JFrame frame;
	private boolean silent;
	private String updateURL;
	private int major;
	private int minor;

	public UpdateManager(JFrame frame) {
		this(frame, false);
	}

	public UpdateManager(JFrame frame, boolean silent) {
		this.frame = frame;
		this.silent = silent;
	}

	public void run() {
		try {
			doRun();
		} catch (MalformedURLException e1) {
			errorIfNotSilent("Error connecting to update server: Malformed URL.");
		} catch (IOException e1) {
			errorIfNotSilent("Error reading update data.");
		} catch (ParserConfigurationException e) {
			errorIfNotSilent("Error with XML parser configuration.");
		} catch (SAXException e) {
			errorIfNotSilent("Error parsing update file.");
		} catch (NumberFormatException e) {
			errorIfNotSilent("Error parsing version numbers.");
		} catch (NullPointerException e) {
			errorIfNotSilent("Error \"NullPointerException\" in update.");
		} catch (URISyntaxException e) {
			errorIfNotSilent("Error parsing update URL.");
		}
	}

	private void errorIfNotSilent(String message) {
		if (!silent) {
			error(message);
		}
	}

	private void doRun() throws MalformedURLException,
			ParserConfigurationException, SAXException, IOException,
			URISyntaxException {
		updateVersionInformation(getDocument());
		if (getUserChoice() == JOptionPane.OK_OPTION) {
			openUpdateURL();
		}
	}

	private Document getDocument() throws MalformedURLException, SAXException,
			IOException, ParserConfigurationException {
		URL url = new URL(UPDATE_URL);
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(url.openStream());
		document.getDocumentElement().normalize();
		return document;
	}

	private void updateVersionInformation(Document document) {
		updateUpdateURL(document);
		updateVersionNumber(document);
	}

	private void updateUpdateURL(Document document) {
		updateURL = document.getFirstChild().getAttributes().item(0)
				.getNodeValue();
	}

	private void updateVersionNumber(Document document) {
		NodeList version = document.getDocumentElement()
				.getElementsByTagName("version").item(0).getChildNodes();
		major = 0;
		minor = 0;
		for (int i = 0; i < version.getLength(); i++) {
			Node v = version.item(i);
			if (v.getNodeType() == Node.ELEMENT_NODE) {
				if (v.getNodeName().toLowerCase().equals("major")) {
					major = Integer.parseInt(v.getAttributes().item(0)
							.getNodeValue());
				} else if (v.getNodeName().toLowerCase().equals("minor")) {
					minor = Integer.parseInt(v.getAttributes().item(0)
							.getNodeValue());
				}
			}
		}
	}

	private int getUserChoice() {
		if (isNewMajorVersionAvailable()) {
			return askToConfirm(
					"A new version was found. Would you like to update?",
					"Update Found");
		} else if (isNewMinorVersionAvailable()) {
			return askToConfirm("A minor revision was found. Update?",
					"Update Found");
		} else if (!silent) {
			JOptionPane.showMessageDialog(frame, "There are no new updates.");
		}
		return JOptionPane.NO_OPTION;
	}

	private boolean isNewMajorVersionAvailable() {
		return major > Amidst.version_major;
	}

	private boolean isNewMinorVersionAvailable() {
		return major == Amidst.version_major && minor > Amidst.version_minor;
	}

	private int askToConfirm(String message, String title) {
		return JOptionPane.showConfirmDialog(frame, message, title,
				JOptionPane.YES_NO_OPTION);
	}

	private void openUpdateURL() throws IOException, URISyntaxException {
		if (!Desktop.isDesktopSupported()) {
			error("Error unable to open browser.");
		} else {
			Desktop desktop = Desktop.getDesktop();
			if (!desktop.isSupported(Desktop.Action.BROWSE)) {
				error("Error unable to open browser page.");
			} else {
				desktop.browse(new URI(updateURL));
			}
		}
	}

	private void error(String message) {
		JOptionPane.showMessageDialog(frame, message);
	}
}
