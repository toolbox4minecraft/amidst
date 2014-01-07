package amidst.map.widget;

import java.awt.Graphics2D;

import MoF.MapViewer;

public class BiomeWidget extends PanelWidget {
	
	public BiomeWidget(MapViewer mapViewer) {
		super(mapViewer);
		setDimensions(30, 30);
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
