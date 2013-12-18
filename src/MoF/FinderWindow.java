package MoF;


import amidst.Amidst;
import amidst.Options;
import amidst.Util;
import amidst.gui.menu.AmidstMenu;
import amidst.logging.Log;
import amidst.resources.ResourceLoader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.Point;
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
	public static FinderWindow instance;
	private Container pane;
	public Project curProject;  //TODO
	public static boolean dataCollect;
	private final AmidstMenu menuBar;
	public FinderWindow() {
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
		setIconImage(Amidst.icon);
		instance = this;
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}
	
	public void clearProject() {
		// FIXME Release resources.
		if (curProject != null) {
			removeKeyListener(curProject.getKeyListener());
			curProject.dispose();
			pane.remove(curProject);
			System.gc();
		}
	}
	public void setProject(Project ep) {
		menuBar.mapMenu.setEnabled(true);
		curProject = ep;

		addKeyListener(ep.getKeyListener());
		pane.add(curProject, BorderLayout.CENTER);
		
		this.validate();
	}
}
