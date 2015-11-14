package amidst.map.layers;

import java.util.List;

import MoF.SkinManager;
import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectPlayer;
import amidst.minecraft.world.SaveLoader;

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
			if ((player.globalX >= frag.getBlockX()) &&
				(player.globalX < frag.getBlockX() + Fragment.SIZE) &&
				(player.globalY >= frag.getBlockY()) &&
				(player.globalY < frag.getBlockY() + Fragment.SIZE)) {
				player.parentLayer = this;
				player.parentFragment = frag;
				frag.addObject(player);
			}
		}
	}
	
	@Override
	public void clearMapObjects(Fragment frag) {
		for (int i = 0; i < frag.getObjectsLength(); i++) {
			if (frag.getObjects()[i] instanceof MapObjectPlayer)
				((MapObjectPlayer)frag.getObjects()[i]).parentFragment = null;
			
		}
		super.clearMapObjects(frag);
		
	}
	public void setPlayers(SaveLoader save) {
		saveLoader = save;
		
		for (MapObjectPlayer player : saveLoader.getPlayers())
			skinManager.addPlayer(player);
	}
}
