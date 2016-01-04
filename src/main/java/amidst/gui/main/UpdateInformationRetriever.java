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
import amidst.documentation.Immutable;

@Immutable
public class UpdateInformationRetriever {
	public static final String UPDATE_URL = "https://sites.google.com/site/mothfinder/update.xml";
	public static final String UPDATE_UNSTABLE_URL = "https://sites.google.com/site/mothfinder/update_unstable.xml";

	private static final int MAJOR_INDEX = 0;
	private static final int MINOR_INDEX = 1;

	private final AmidstMetaData metadata;
	private final String updateURL;
	private final int major;
	private final int minor;

	public UpdateInformationRetriever(AmidstMetaData metadata)
			throws MalformedURLException, SAXException, IOException,
			ParserConfigurationException, RuntimeException {
		this.metadata = metadata;
		Document document = getDocument();
		updateURL = getUpdateURL(document);
		int[] versionNumbers = getVersionNumbers(document);
		major = versionNumbers[MAJOR_INDEX];
		minor = versionNumbers[MINOR_INDEX];
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

	private int[] getVersionNumbers(Document document) {
		NodeList version = document.getDocumentElement()
				.getElementsByTagName("version").item(0).getChildNodes();
		int[] result = new int[] { -1, -1 };
		for (int i = 0; i < version.getLength(); i++) {
			Node node = version.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equalsIgnoreCase("major")) {
					result[MAJOR_INDEX] = getVersionNumber(node);
				} else if (node.getNodeName().equalsIgnoreCase("minor")) {
					result[MINOR_INDEX] = getVersionNumber(node);
				}
			}
		}
		if (result[MAJOR_INDEX] == -1 || result[MINOR_INDEX] == -1) {
			throw new RuntimeException("Error parsing version numbers.");
		}
		return result;
	}

	private int getVersionNumber(Node node) {
		return Integer.parseInt(node.getAttributes().item(0).getNodeValue());
	}

	public boolean isNewMajorVersionAvailable() {
		return major > metadata.getMajorVersion();
	}

	public boolean isNewMinorVersionAvailable() {
		return major == metadata.getMajorVersion()
				&& minor > metadata.getMinorVersion();
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
