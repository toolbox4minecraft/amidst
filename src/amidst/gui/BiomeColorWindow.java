package amidst.gui;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import amidst.minecraft.Biome;

public class BiomeColorWindow extends JDialog {
	public BiomeColorWindow(JFrame window) {
		super(window, "Biome Color Preferences", Dialog.ModalityType.DOCUMENT_MODAL);

		setBounds(window.getLocation().x + 50, window.getLocation().y + 50, 500, 700);
		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		
		add(new JLabel(" Note: Some rare biomes on this list may not actually appear in game, or exist."), constraints);
		
		for (int i = 0; i < Biome.length; i++) {
			constraints.gridy = 1 + i;
			constraints.weightx = 0.5;
			
			constraints.gridx = 0;
			add(new JLabel(" " + Biome.biomes[i].name), constraints);
			
			constraints.gridx = 1;
			add(new JLabel(" " + Biome.biomes[i + 128].name), constraints);
			
		}
		setVisible(true);
		
	}
}
