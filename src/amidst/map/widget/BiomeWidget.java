package amidst.map.widget;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import amidst.minecraft.Biome;
import MoF.MapViewer;

public class BiomeWidget extends PanelWidget {
	private ArrayList<Biome> biomes = new ArrayList<Biome>();
	private int maxNameWidth = 0;
	public BiomeWidget(MapViewer mapViewer) {
		super(mapViewer);
		FontMetrics fontMetrics = mapViewer.getFontMetrics(textFont);
		for (int i = 0; i < Biome.biomes.length;i++) {
			if (Biome.biomes[i] != null) {
				biomes.add(Biome.biomes[i]);
				int nameWidth = fontMetrics.stringWidth(Biome.biomes[i].name);
				maxNameWidth = Math.max(nameWidth, maxNameWidth);
			}
		}
		setDimensions(maxNameWidth + 20, biomes.size() * 10);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		super.draw(g2d, time);
	}
	
	@Override
	public void onClick(int x, int y) {
		(new Thread(new Runnable() {

			@Override
			public void run() {
				mapViewer.getMap().resetFragments();
			} 
			
		})).start();
	}
}
