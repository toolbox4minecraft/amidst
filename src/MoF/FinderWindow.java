package MoF;


import amidst.Amidst;
import amidst.Log;
import amidst.Options;
import amidst.Util;
import amidst.gui.AmidstMenu;
import amidst.resources.ResourceLoader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class FinderWindow extends JFrame {
	private static final long serialVersionUID = 196896954675968191L;
	private Container pane;
	public Project curProject;  //TODO
	public static boolean dataCollect;
	private final AmidstMenu menuBar;
	public FinderWindow() throws IOException {
		//Initialize window
		super("Amidst v" + Amidst.version());
		
		setSize(800,800);
		//setLookAndFeel();
		pane = getContentPane();
		//UI Manager:
		pane.setLayout(new BorderLayout());
		new UpdateManager(this, true).start();
		setJMenuBar(menuBar = new AmidstMenu(this));
		setVisible(true);
		Image icon = ResourceLoader.getImage("icon.png");
		setIconImage(icon);
		//OnClose
		/*
		boolean dcFirst = pref.getBoolean("datacheckfirst", false);
		if (!dcFirst) {
			int result = JOptionPane.YES_OPTION;
			result = JOptionPane.showConfirmDialog(null, "AMIDST would like to collect data about the maps you search, anonymously.\n You will only be prompted for this once:\n Would you like to allow data to be collected?", "Important alert!", JOptionPane.YES_NO_OPTION);
			pref.putBoolean("datacollect", (result==0));
		}
		dataCollect = pref.getBoolean("datacollect", false);
		*/addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				dispose();
				System.exit(0);
			}
		});
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_EQUALS) {
					if (curProject!=null)
						curProject.map.scaleBy(2);
				} else if (arg0.getKeyCode() == KeyEvent.VK_MINUS) {
					if (curProject!=null)
						curProject.map.scaleBy(0.5);
				}
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {}
			
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
	    
	}
	
	public void setProject(Project ep) {
		// FIXME Release resources.
		if (curProject != null) {
			curProject.dispose();
			pane.remove(curProject);
			System.gc();
		}
		menuBar.mapMenu.setEnabled(true);
		curProject = ep;
		pane.add(curProject, BorderLayout.CENTER);
		
		this.validate();
	}
}
