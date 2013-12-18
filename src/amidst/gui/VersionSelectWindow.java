package amidst.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;

import MoF.FinderWindow;
import amidst.Amidst;
import amidst.Util;
import amidst.json.InstallInformation;
import amidst.json.LauncherProfile;
import amidst.logging.Log;
import amidst.minecraft.Minecraft;
import amidst.resources.ResourceLoader;
import amidst.version.MinecraftVersion;
import amidst.version.VersionFactory;

import com.google.gson.Gson;

public class VersionSelectWindow extends JFrame {
	private VersionFactory versionFactory = new VersionFactory();
	
	public VersionSelectWindow() {
		super("Profile Selector");
		setIconImage(Amidst.icon);
		Container contentPane = getContentPane();
		contentPane.setLayout(new MigLayout());
		
		final JLabel titleLabel = new JLabel("Please select a Minecraft version:", JLabel.CENTER);
		titleLabel.setFont(new Font("arial", Font.BOLD, 16));
		
		add(titleLabel, "h 20!, growx, pushx, wrap");
		
		final VersionSelectPanel versionSelector = new VersionSelectPanel();
		
		(new Thread(new Runnable() {
			@Override
			public void run() {
				versionFactory.scanForLocalVersions();
				MinecraftVersion[] localVersions = versionFactory.getLocalVersions();
				if (localVersions == null) {
					versionSelector.setEmptyMessage("Empty");
					return;
				}
				for (int i = 0; i < localVersions.length; i++)
					versionSelector.addVersion(new VersionComponent(localVersions[i]));
			}
		})).start();
		
		versionSelector.setEmptyMessage("Scanning...");
		/*versionSelector.addVersion(new VersionComponent(null));
		versionSelector.addVersion(new VersionComponent(null));
		versionSelector.addVersion(new VersionComponent(null));
		versionSelector.addVersion(new VersionComponent(null));*/
		
		JScrollPane scrollPane = new JScrollPane(versionSelector);
		add(scrollPane, "grow, push, h 40::");
		pack();
		setLocation(200, 200);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}
	
}
