package amidst.map.layers;

import java.util.List;
import MoF.SaveLoader;
import MoF.SkinManager;
import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectPlayer;

public class PlayerLayer extends IconLayer {
	public SaveLoader saveLoader;
	public static SkinManager skinManager = new SkinManager();
	public boolean isEnabled;
	static {
		skinManager.start();
	}
	public PlayerLayer() {
		
	}
	
	@Override
	public boolean isVisible() {
		return Options.instance.showPlayers.get();		
	}
	
	@Override
	public void generateMapObjects(Fragment frag) {
		if (!isEnabled) return;
		List<MapObjectPlayer> players =  saveLoader.getPlayers();
		for (MapObjectPlayer player : players) {
			if ((player.globalX >= frag.blockX) &&
				(player.globalX < frag.blockX + Fragment.SIZE) &&
				(player.globalY >= frag.blockY) &&
				(player.globalY < frag.blockY + Fragment.SIZE)) {
				player.parentLayer = this;
				player.parentFragment = frag;
				frag.addObject(player);
			}
		}
	}
	
	@Override
	public void clearMapObjects(Fragment frag) {
		for (int i = 0; i < frag.objectsLength; i++) {
			if (frag.objects[i] instanceof MapObjectPlayer)
				((MapObjectPlayer)frag.objects[i]).parentFragment = null;
			
		}
		super.clearMapObjects(frag);
		
	}
	public void setPlayers(SaveLoader save) {
		saveLoader = save;
		
		for (MapObjectPlayer player : saveLoader.getPlayers())
			skinManager.addPlayer(player);
	}
}
