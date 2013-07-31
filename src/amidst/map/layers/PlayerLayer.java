package amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import MoF.Biome;
import MoF.ChunkManager;
import MoF.SaveLoader;
import MoF.SkinManager;
import amidst.Log;
import amidst.Options;
import amidst.map.Fragment;
import amidst.map.IconLayer;
import amidst.map.MapObjectNether;
import amidst.map.MapObjectPlayer;
import amidst.map.MapObjectStronghold;
import amidst.map.MapObjectVillage;

public class PlayerLayer extends IconLayer {
	SaveLoader saveLoader;
	SkinManager skinManager = new SkinManager();
	public PlayerLayer(SaveLoader saveLoader) {
		super("players");
		Log.i("players added");
		setVisibilityPref(Options.instance.showIcons);
		this.saveLoader = saveLoader;
		skinManager.start();
		
		for (MapObjectPlayer player : saveLoader.getPlayers())
			skinManager.addPlayer(player);
	}
	public void generateMapObjects(Fragment frag) {
		List<MapObjectPlayer> players =  saveLoader.getPlayers();
		for (MapObjectPlayer player : players) {
			if ((player.globalX > frag.blockX) &&
				(player.globalX < frag.blockX + Fragment.SIZE) &&
				(player.globalY > frag.blockY) &&
				(player.globalY < frag.blockY + Fragment.SIZE)) {
				player.parentLayer = this;
				frag.addObject(player);
			}
		}
	}
}
