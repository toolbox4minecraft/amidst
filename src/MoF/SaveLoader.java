package MoF;
import amidst.Log;
import amidst.Util;
import amidst.map.MapObjectPlayer;
import amidst.nbt.Tag;
import amidst.nbt.TagCompound;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

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
				if (f.isDirectory()) {
					return true;
				}
				String[] st = f.getName().split("\\/");
				return st[st.length - 1].equalsIgnoreCase("level.dat");
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
				TagCompound t = Tag.readFrom(new FileInputStream(out));
				Tag pos = t.findTagByName("Pos");
				Tag<Double>[] pa = (Tag[]) pos.getValue();
				pa[0].setValue((double) x);
				pa[1].setValue((double) 120);
				pa[2].setValue((double) y);
				t.writeTo(new FileOutputStream(out));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			out = file;
			backupFile(out);
			try {
				TagCompound t = Tag.readFrom(new FileInputStream(out));
				Tag pos = t.findTagByName("Pos");
				Tag<Double>[] pa = (Tag[]) pos.getValue();
				pa[0].setValue((double) x);
				pa[1].setValue((double) 120);
				pa[2].setValue((double) y);
				t.writeTo(new FileOutputStream(out));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void backupFile(File inputFile) {
		File outputFile = new File(inputFile.toString() + ".moth");
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
			TagCompound t = Tag.readFrom(new FileInputStream(f));
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
			}
		} catch (Exception e) {
			Util.showError(e);
		}
	}
	
	private void addPlayer(String name, TagCompound ps) {
		Tag pos = ps.findTagByName("Pos");
		Tag<Double>[] pa = (Tag[]) pos.getValue();
		double x = pa[0].getValue();
		double z = pa[2].getValue();
		players.add(new MapObjectPlayer(name, (int) x, (int) z));
	}
}
