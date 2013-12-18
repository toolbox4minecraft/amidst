package amidst.gui;

// Added for debugging while the new VersionSelectWindow is being made.
import java.awt.Dimension;
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

import MoF.FinderWindow;
import amidst.Amidst;
import amidst.Util;
import amidst.json.InstallInformation;
import amidst.json.LauncherProfile;
import amidst.logging.Log;
import amidst.minecraft.Minecraft;
import amidst.resources.ResourceLoader;

import com.google.gson.Gson;

public class OldVersionSelectWindow extends JFrame {
	public OldVersionSelectWindow() {
		/*setIconImage(Amidst.icon);
		
		File profileJsonFile = new File(Util.minecraftDirectory + "/launcher_profiles.json");
		
		Object[] profileArray = null;
		try {
			LauncherProfile profile = Util.readObject(profileJsonFile, LauncherProfile.class);
			// TODO This should use latest, not a preset version.
			profile.profiles.put("(Default)",  new InstallInformation("(Default) ", "1.7.4"));
			profileArray = profile.profiles.values().toArray();
		} catch (Exception e) { // TODO This is a very broad exception to catch for
			e.printStackTrace();
			dispose();
			try {
				new Minecraft();
				new FinderWindow();
			} catch (MalformedURLException e1) {
				Log.crash(e1, "MalformedURLException when creating Minecraft object.");
			}
			return; // TODO Do stuff here
		}
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		final JComboBox profileBox = new JComboBox(profileArray);
		profileBox.selectWithKeyChar('(');
		JButton btnConfirm = new JButton("Okay");
		this.setLayout(new GridLayout(3,1));
		getContentPane().add(new JLabel("  Multiple profiles detected, please select one."));
		getContentPane().add(profileBox);
		getContentPane().add(btnConfirm);
		final JFrame window = this;
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Amidst.installInformation = (InstallInformation)profileBox.getSelectedItem();
				boolean isValid = Amidst.installInformation.validate();
				if (!isValid) {
					Log.e("\"Latest\" profile option detected, but there was an issue loading the version list.\nUsing alternative jar.");
				} else {
					try {
						new Minecraft();
					} catch (MalformedURLException e1) {
						Log.crash(e1, "MalformedURLException when creating Minecraft object.");
					}
					window.dispose();
					new FinderWindow();
				}
			}
		});
		getRootPane().setDefaultButton(btnConfirm);
		btnConfirm.requestFocus();
		setTitle("Temporary profile selector");
		setSize(300, 100);
		setLocation(200, 200);
		setVisible(true);*/
	}
	
}