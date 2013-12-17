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

import com.google.gson.Gson;

public class VersionSelectWindow extends JFrame {
	public VersionSelectWindow() {
		super("Profile Selector");
		setIconImage(Amidst.icon);
		Container contentPane = getContentPane();
		contentPane.setLayout(new MigLayout());
		
		final JLabel titleLabel = new JLabel("Please select a Minecraft version:", JLabel.CENTER);
		titleLabel.setFont(new Font("arial", Font.BOLD, 16));
		
		add(titleLabel, "w 300!,wrap");
		
		VersionSelectPanel versionSelector = new VersionSelectPanel();
		add(versionSelector);
		versionSelector.addVersion(new VersionComponent(null));
		
		pack();
		setLocation(200, 200);
		setVisible(true);
	}
	
}
