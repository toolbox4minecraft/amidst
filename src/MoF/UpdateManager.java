package MoF;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import amidst.Amidst;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException; 

public class UpdateManager extends Thread {
	public static final String updateURL = "https://sites.google.com/site/mothfinder/update.xml";
	public static final String updateUnstableURL = "https://sites.google.com/site/mothfinder/update_unstable.xml";
	private JFrame window;
	private boolean silent;
	public UpdateManager(JFrame window) {
		this.setWindow(window);
		silent = false;
	}
	public UpdateManager(JFrame window, boolean silence) {
		this.setWindow(window);
		silent = silence;
	}
	public void run() {
		
		try {
			URL url = new URL(updateURL);
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(url.openStream());
		   
			doc.getDocumentElement().normalize();
			NodeList vlist = doc.getDocumentElement().getElementsByTagName("version");
			NodeList version = vlist.item(0).getChildNodes();
			
			int major = 0;
			int minor = 0;
			String updateURL = doc.getFirstChild().getAttributes().item(0).getNodeValue();
			for (int i = 0; i < version.getLength(); i++) {
				Node v = version.item(i);
				if (v.getNodeType() == Node.ELEMENT_NODE) {
					if (v.getNodeName().toLowerCase().equals("major")) {
						major = Integer.parseInt(v.getAttributes().item(0).getNodeValue());
					} else if (v.getNodeName().toLowerCase().equals("minor")) {
						minor = Integer.parseInt(v.getAttributes().item(0).getNodeValue());
					}
				}
			}
			int n = JOptionPane.NO_OPTION;
			
			if (major > Amidst.version_major) {
				n = JOptionPane.showConfirmDialog(
					window,
					"A new version was found. Would you like to update?",
					"Update Found",
					JOptionPane.YES_NO_OPTION);
			} else if ((major == Amidst.version_major) && (minor > Amidst.version_minor)) {
				n = JOptionPane.showConfirmDialog(
					window,
					"A minor revision was found. Update?",
					"Update Found",
					JOptionPane.YES_NO_OPTION);
			} else if (!silent)
				JOptionPane.showMessageDialog(window, "There are no new updates.");
			
			if (n==0) {
				if( !java.awt.Desktop.isDesktopSupported()) {
					JOptionPane.showMessageDialog(window, "Error unable to open browser.");
				}
				
				java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
				
				if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
					JOptionPane.showMessageDialog(window, "Error unable to open browser page.");
				}
				java.net.URI uri = new java.net.URI(updateURL);
				desktop.browse(uri);
			}
		} catch (MalformedURLException e1) {
			if (!silent)
				JOptionPane.showMessageDialog(window, "Error connecting to update server: Malformed URL.");
		} catch (IOException e1) {
			if (!silent)
				JOptionPane.showMessageDialog(window, "Error reading update data.");
		}  catch (ParserConfigurationException e) {
			if (!silent)
				JOptionPane.showMessageDialog(window, "Error with XML parser configuration.");
		}  catch (SAXException e) {
			if (!silent)
				JOptionPane.showMessageDialog(window, "Error parsing update file.");
		} catch (NumberFormatException e) {
			if (!silent)
				JOptionPane.showMessageDialog(window, "Error parsing version numbers.");
		} catch (NullPointerException e) {
			if (!silent)
				JOptionPane.showMessageDialog(window, "Error \"NullPointerException\" in update.");
		} catch (URISyntaxException e) {
			if (!silent)
				JOptionPane.showMessageDialog(window, "Error parsing update URL.");
		}
		
	}
	public JFrame getWindow() {
		return window;
	}
	public void setWindow(JFrame window) {
		this.window = window;
	}
}
