package amidst.map.widget;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;

import amidst.minecraft.Biome;
import MoF.MapViewer;

public class BiomeWidget extends PanelWidget {
	private static Color innerBoxBgColor = new Color(0.3f, 0.3f, 0.3f, 0.3f);
	private static Color biomeBgColor1 = new Color(0.8f, 0.8f, 0.8f, 0.2f);
	private static Color biomeBgColor2 = new Color(0.6f, 0.6f, 0.6f, 0.2f);
	private static Color innerBoxBorderColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	private ArrayList<Biome> biomes = new ArrayList<Biome>();
	private int maxNameWidth = 0;
	private Rectangle innerBox = new Rectangle(0, 0, 1, 1);
	
	
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
		g2d.setFont(textFont);
		g2d.drawString("Highlight Biomes", x + 10, y + 20);
		
		innerBox.x = x + 10;
		innerBox.y = y + 30;
		innerBox.width = width - 20;
		innerBox.height = height - 40;
		g2d.setColor(innerBoxBgColor);
		g2d.fillRect(innerBox.x, innerBox.y, innerBox.width, innerBox.height);
		g2d.setColor(innerBoxBorderColor);
		g2d.drawRect(innerBox.x - 1, innerBox.y - 1, innerBox.width + 1, innerBox.height + 1);
		g2d.setClip(innerBox);
		
		for (int i = 0; i < biomes.size(); i++) {
			Biome biome = biomes.get(i);
			g2d.setColor(((i % 2) == 1)?biomeBgColor1:biomeBgColor2);
			g2d.fillRect(innerBox.x, innerBox.y + i * 16,innerBox.width, 16);
			g2d.setColor(new Color(biome.color));
			g2d.fillRect(x + 10, innerBox.y + i*16, 20, 16);
			g2d.setColor(Color.white);
			g2d.drawString(biome.name, x + 35, innerBox.y + 13 + i*16);
		}
		
		g2d.setClip(null);
	}
	
	@Override
	public boolean onMouseWheelMoved(int mouseX, int mouseY, int notches) {
		if ((mouseX > innerBox.x) &&
			(mouseX < innerBox.x + innerBox.width) &&
			(mouseY > innerBox.y) &&
			(mouseY < innerBox.y + innerBox.height)) {
			
		}
		return true;
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
