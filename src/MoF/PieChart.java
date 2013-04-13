package MoF;
import java.awt.Graphics2D;

public class PieChart {
	private float[] slices;
	private int sources;
	public PieChart(int numOfSlices, int numSources) {
		slices = new float[numOfSlices];
		sources = numSources;
	}
	
	public void addData(float[] sData) {
		for (int i = 0; i < slices.length; i++)
			slices[i] += sData[i] / (float)sources;
	}
	
	public void setSources(int num) {
		sources = num;
	}
	
	public void clearData() {
		for (int i = 0; i < slices.length; i++) {
			slices[i] = 0;
		}
	}
	
	public void dispose() {
		this.slices = null;
	}
	
	public void paint(Graphics2D g2d, int x, int y, int width, int height) {
		float max = 0.0f;
		for (float slice : slices)
			max += slice;
		int angle = 0;
		float tp = 360f;
		for (int i = 0; i < slices.length; i++) {
			g2d.setColor(Biome.colors[i]);
			int newangle =  (int)((slices[i]/max)*tp);
			if (i == slices.length - 1)
				newangle = 360 - angle;
			g2d.fillArc(x, y, width, height, angle, newangle);
			angle += newangle;
		}
	}
}
