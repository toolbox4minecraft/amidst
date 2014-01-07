package amidst.map.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import amidst.logging.Log;
import amidst.map.layers.BiomeLayer;
import amidst.minecraft.Biome;
import MoF.MapViewer;

public class BiomeWidget extends PanelWidget {
	private ArrayList<Biome> biomes = new ArrayList<Biome>();
	private int maxNameWidth = 0;
	private int columns = 1;
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
		forceVisibility(false);
		setDimensions(maxNameWidth * columns + 20 + columns * 20, biomes.size() * 18 / columns + 15);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		super.draw(g2d, time);
		g2d.setColor(textColor);
		for (int i = 0; i < biomes.size(); i++) {
			Biome biome = biomes.get(i);
			int column = i / (biomes.size() / columns);
			int row = i % (biomes.size() / columns);
			
			int drawX = 10 + x + column * 20 + column * maxNameWidth;
			int drawY = row*18 + y + 20;
			
			g2d.setColor(new Color(biome.color));
			g2d.drawString(biome.name, drawX, drawY);
		}
	}
	
	@Override
	public void onClick(int x, int y) {
		int id = y / 18;
		Log.i(id-20);
		BiomeLayer.instance.toggleBiomeSelect(id);
		(new Thread(new Runnable() {
			@Override
			public void run() {
				map.resetImageLayer(BiomeLayer.instance.getLayerId());
			}
		})).start();
	}
	
	@Override
	public boolean onVisibilityCheck() {
		return BiomeToggleWidget.instance.isBiomeWidgetVisible;
	}
}
