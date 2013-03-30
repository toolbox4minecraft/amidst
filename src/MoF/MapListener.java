package MoF;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MapListener implements ActionListener {
	private MapViewer map;
	private String name;
	public MapListener(MapViewer p, String name) {
		this.name = name;
		map = p;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		map.movePlayer(name, e);
	}

}
