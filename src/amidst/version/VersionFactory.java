package amidst.version;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.gson.JsonSyntaxException;

import MoF.FinderWindow;
import amidst.Amidst;
import amidst.Util;
import amidst.json.InstallInformation;
import amidst.json.LauncherProfile;
import amidst.logging.Log;
import amidst.minecraft.Minecraft;
import amidst.utilties.ProgressMeter;

public class VersionFactory {
	public VersionFactory() {
		
	}
	
	public void load() {
		
	}
	
	private void scanForLocalVersions() {
		
	}
	
	private void loadProfileList() {
		File profileJsonFile = new File(Util.minecraftDirectory + "/launcher_profiles.json");
		Object[] profileArray = null;
		try {
			LauncherProfile profile = Util.readObject(profileJsonFile, LauncherProfile.class);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
