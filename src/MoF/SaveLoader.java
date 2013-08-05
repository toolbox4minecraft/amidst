package MoF;
import amidst.Log;
import amidst.Util;
import amidst.map.MapObjectPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileFilter;

import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

public class SaveLoader {
	public static Type genType = Type.DEFAULT;
	
	public enum Type {
		DEFAULT("default"), FLAT("flat"), LARGE_BIOMES("largeBiomes");
		private final String s;
		
		Type(String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return s;
		}
		
		public static Type fromMixedCase(String name) {
			for (Type t : values())
				if (t.s.equals(name))
					return t;
			throw new IllegalArgumentException("Value " + name + " not implemented");
		}
	}
	
	public static FileFilter getFilter() {
		return (new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory();
			}
			
			@Override
			public String getDescription() {
				return "Minecraft Data File (level.dat)";
			}
		});
	}
	
	private File file;
	private List<MapObjectPlayer> players;
	public long seed;
	private boolean multi;
	private List<String> back;
	public List<MapObjectPlayer> getPlayers() {
		return players;
	}
	public void movePlayer(String name, int x, int y) {
		File out;
		if (multi) {
			String outPath = file.getParent() + "/players/" + name +".dat";
			out = new File(outPath);
			backupFile(out);
			try {
				NBTInputStream inStream = new NBTInputStream(new FileInputStream(out));
				CompoundTag root = (CompoundTag)inStream.readTag();
				inStream.close();
				
				HashMap<String, Tag> rootMap = new HashMap<String, Tag>(root.getValue());
				ArrayList<Tag> posTag = new ArrayList<Tag>(((ListTag)rootMap.get("Pos")).getValue());
				posTag.set(0, new DoubleTag("x", x));
				posTag.set(1, new DoubleTag("y", 120));
				posTag.set(2, new DoubleTag("z", y));
				rootMap.put("Pos", new ListTag("Pos", DoubleTag.class, posTag));
				root = new CompoundTag("Data", rootMap);
				NBTOutputStream outStream = new NBTOutputStream(new FileOutputStream(out));
				outStream.writeTag(root);
				outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			out = file;
			backupFile(out);
			try {
				NBTInputStream inStream = new NBTInputStream(new FileInputStream(out));
				CompoundTag root = (CompoundTag)(((CompoundTag)inStream.readTag()).getValue().get("Data"));
				inStream.close();
				
				HashMap<String, Tag> rootMap = new HashMap<String, Tag>(root.getValue());
				HashMap<String, Tag> playerMap = new HashMap<String, Tag>(((CompoundTag)rootMap.get("Player")).getValue());
				ArrayList<Tag> posTag = new ArrayList<Tag>(((ListTag)playerMap.get("Pos")).getValue());
				posTag.set(0, new DoubleTag("x", x));
				posTag.set(1, new DoubleTag("y", 120));
				posTag.set(2, new DoubleTag("z", y));
				rootMap.put("Player", new CompoundTag("Player", playerMap));
				playerMap.put("Pos", new ListTag("Pos", DoubleTag.class, posTag));
				root = new CompoundTag("Data", rootMap);
				HashMap<String, Tag> base = new HashMap<String, Tag>();
				base.put("Data", root);
				root = new CompoundTag("Base", base);
				NBTOutputStream outStream = new NBTOutputStream(new FileOutputStream(out));
				outStream.writeTag(root);
				outStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void backupFile(File inputFile) {
		File backupFolder = new File(inputFile.getParentFile() + "/amidst_backup/");
		if (!backupFolder.exists())
			backupFolder.mkdir();
		
		File outputFile = new File(backupFolder + "/" + inputFile.getName());
		if (!back.contains(outputFile.toString())) {
			try {
				FileReader in = new FileReader(inputFile);
				FileWriter out = new FileWriter(outputFile);
				int c;
				
				while ((c = in.read()) != -1)
					out.write(c);
		 		
				in.close();
				out.close();
				back.add(outputFile.toString());
			} catch (Exception ignored) {}
	    }
	}
	
	public SaveLoader(File f) {
		file = f;
		players = new ArrayList<MapObjectPlayer>();
		back = new ArrayList<String>();
		try {
			NBTInputStream inStream = new NBTInputStream(new FileInputStream(f));
			CompoundTag root = (CompoundTag) ((CompoundTag)inStream.readTag()).getValue().get("Data");
			//inStream.close();
			seed = (Long)(root.getValue().get("RandomSeed").getValue());
			genType = Type.fromMixedCase((String)(root.getValue().get("generatorName").getValue()));
			
			CompoundTag playerTag = (CompoundTag)root.getValue().get("Player");
			
			multi = (playerTag == null);
			Log.i(multi);
			if (!multi) {
				addPlayer("Player", playerTag);
			} else {
				File[] listing = new File(f.getParent(), "players").listFiles();
				for (int i = 0; i < (listing != null ? listing.length : 0); i++) {
					if (listing[i].isFile()) {
						NBTInputStream playerInputStream = new NBTInputStream(new FileInputStream(listing[i]));
						addPlayer(listing[i].getName().split("\\.")[0], (CompoundTag) ((CompoundTag)playerInputStream.readTag()));
						playerInputStream.close();
					}
				}
				
			}
			/*TagCompound t = Tag.readFrom(new FileInputStream(f));
			TagCompound pTag = (TagCompound) t.findTagByName("MapObjectPlayer");
			seed = (Long) t.findTagByName("RandomSeed").getValue();
			genType = Type.fromMixedCase((String) t.findTagByName("generatorName").getValue());
			Log.debug("Gen Type:", genType);
			multi = pTag == null;
			if (!multi) {
				addPlayer("MapObjectPlayer", pTag);
			} else {
				File[] listing = new File(f.getParent(), "players").listFiles();
				for (int i = 0; i < (listing != null ? listing.length : 0); i++) {
					TagCompound ps = Tag.readFrom(new FileInputStream(listing[i]));
					addPlayer(listing[i].getName().split("\\.")[0], ps);
				}
			}*/
		} catch (Exception e) {
			Util.showError(e);
		}
	}
	
	private void addPlayer(String name, CompoundTag ps) {
		List<Tag> pos = ((ListTag)(ps.getValue().get("Pos"))).getValue();
		double x = (Double)pos.get(0).getValue();
		double z = (Double)pos.get(2).getValue();
		players.add(new MapObjectPlayer(name, (int) x, (int) z));
	}
}
