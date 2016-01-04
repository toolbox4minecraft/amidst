package amidst.gui.main;

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
import amidst.AmidstVersion;
import amidst.documentation.Immutable;

@Immutable
public class UpdateInformationRetriever {
	public static final String UPDATE_URL = "https://sites.google.com/site/mothfinder/update.xml";
	public static final String UPDATE_UNSTABLE_URL = "https://sites.google.com/site/mothfinder/update_unstable.xml";

	private final AmidstMetaData metadata;
	private final String updateURL;
	private final AmidstVersion version;

	public UpdateInformationRetriever(AmidstMetaData metadata)
			throws MalformedURLException, SAXException, IOException,
			ParserConfigurationException, RuntimeException {
		this.metadata = metadata;
		Document document = getDocument();
		updateURL = getUpdateURL(document);
		version = getVersionNumbers(document);
	}

	private Document getDocument() throws MalformedURLException, SAXException,
			IOException, ParserConfigurationException {
		URL url = new URL(UPDATE_URL);
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(url.openStream());
		document.getDocumentElement().normalize();
		return document;
	}

	private String getUpdateURL(Document document) {
		return document.getFirstChild().getAttributes().item(0).getNodeValue();
	}

	private AmidstVersion getVersionNumbers(Document document) {
		NodeList version = document.getDocumentElement()
				.getElementsByTagName("version").item(0).getChildNodes();
		int major = -1;
		int minor = -1;
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
			throw new RuntimeException("Error parsing version numbers.");
		}
		return new AmidstVersion(major, minor);
	}

	private int getVersionNumber(Node node) {
		return Integer.parseInt(node.getAttributes().item(0).getNodeValue());
	}

	public String getUpdateURL() {
		return updateURL;
	}

	public boolean isNewMajorVersionAvailable() {
		return version.isNewerMajorVersionThan(metadata.getVersion());
	}

	public boolean isNewMinorVersionAvailable() {
		return version.isNewerMinorVersionThan(metadata.getVersion());
	}
}
