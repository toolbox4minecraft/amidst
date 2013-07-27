package amidst.map.layers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import MoF.Biome;
import MoF.ChunkManager;
import MoF.SaveLoader;
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
	public PlayerLayer(SaveLoader saveLoader) {
		super("players");
		setVisibilityPref(Options.instance.showIcons);
		this.saveLoader = saveLoader;
	}
	public void generateMapObjects(Fragment frag) {
		/*List<Player> players =  saveLoader.getPlayers();
		for (Player player : players) {
			if ((player.posX > frag.blockX) &&
				(player.posX < frag.blockX + Fragment.SIZE) &&
				(player.posZ > frag.blockY) &&
				(player.posZ < frag.blockY + Fragment.SIZE)) {
				frag.addObject((new MapObjectPlayer(player.name, (int)(player.posX - frag.blockX), (int)(player.posZ - frag.blockY))));
			}
		}*/
	}
}
