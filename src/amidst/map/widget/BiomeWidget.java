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
	public BiomeWidget(MapViewer mapViewer) {
		super(mapViewer);
		
		FontMetrics fontMetrics = mapViewer.getFontMetrics(textFont);
		for (int i = 0; i < Biome.biomes.length;i++) {
			if (Biome.biomes[i] != null) {
				biomes.add(Biome.biomes[i]);
				maxNameWidth = Math.max(fontMetrics.stringWidth(Biome.biomes[i].name), maxNameWidth);
			}
		}
		setDimensions(250, 400);
		y = 100;
		forceVisibility(false);
	}

	@Override
	public void draw(Graphics2D g2d, float time) {
		x = mapViewer.getWidth() - width;
		super.draw(g2d, time);
		g2d.setColor(textColor);
	}
	
	@Override
	public boolean onClick(int x, int y) {
		/*int id = y / 18;
		Log.i(id-20);
		BiomeLayer.instance.toggleBiomeSelect(id);
		(new Thread(new Runnable() {
			@Override
			public void run() {
				map.resetImageLayer(BiomeLayer.instance.getLayerId());
			}
		})).start();*/
		return true;
	}
	
	@Override
	public boolean onVisibilityCheck() {
		height = Math.max(200, mapViewer.getHeight() - 200);
		return BiomeToggleWidget.instance.isBiomeWidgetVisible & (height > 200);
	}
}
