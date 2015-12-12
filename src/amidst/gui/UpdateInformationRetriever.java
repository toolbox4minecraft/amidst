package amidst.gui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import amidst.AmidstMetaData;

public class UpdateInformationRetriever {
	public static final String UPDATE_URL = "https://sites.google.com/site/mothfinder/update.xml";
	public static final String UPDATE_UNSTABLE_URL = "https://sites.google.com/site/mothfinder/update_unstable.xml";

	private String updateURL;
	private int major;
	private int minor;

	private boolean successful = false;
	private String errorMessage;

	public void check() {
		resetErrorState();
		try {
			updateVersionInformation(getDocument());
		} catch (MalformedURLException e) {
			error("Error connecting to update server: Malformed URL.");
		} catch (SAXException e) {
			error("Error parsing update file.");
		} catch (IOException e) {
			error("Error reading update data.");
		} catch (ParserConfigurationException e) {
			error("Error with XML parser configuration.");
		} catch (NumberFormatException e) {
			error("Error parsing version numbers.");
		} catch (NullPointerException e) {
			error("Error \"NullPointerException\" in update.");
		}
	}

	private void resetErrorState() {
		successful = true;
		errorMessage = null;
	}

	private void error(String errorMessage) {
		this.errorMessage = errorMessage;
		this.successful = false;
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
		major = -1;
		minor = -1;
		for (int i = 0; i < version.getLength(); i++) {
			Node node = version.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equalsIgnoreCase("major")) {
					major = getVersionNumber(node);
				} else if (node.getNodeName().equalsIgnoreCase("minor")) {
					minor = getVersionNumber(node);
				}
			}
		}
		if (major == -1 || minor == -1) {
			error("Error parsing version numbers.");
		}
	}

	private int getVersionNumber(Node node) {
		return Integer.parseInt(node.getAttributes().item(0).getNodeValue());
	}

	public boolean isNewMajorVersionAvailable() {
		return major > AmidstMetaData.MAJOR_VERSION;
	}

	public boolean isNewMinorVersionAvailable() {
		return major == AmidstMetaData.MAJOR_VERSION
				&& minor > AmidstMetaData.MINOR_VERSION;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getUpdateURL() {
		return updateURL;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}
}
