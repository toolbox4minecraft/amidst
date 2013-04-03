package MoF;
import amidst.nbt.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;


public class SaveLoader {
	public static String genType = "default";
	public static FileFilter getFilter() {
		return (new FileFilter() {
			public boolean accept(File f) {
				
			    if (f.isDirectory()) {
			        return true;
			    }
			    String[] st = f.getName().split("\\/");
			    if (st[st.length-1].toLowerCase().equals("level.dat"))
			    	return true;
			    return false;
			}

			@Override
			public String getDescription() {
				return "Minecraft Data File (level.dat)";
			}
		});
	}
	public static File getPath(String post) {
		return new File(getDefaultPath() + post);
	}
	public static String getDefaultPath() {
		String defaultPath = System.getProperty("user.home", ".");
		String os = System.getProperty("os.name").toLowerCase();
	    if (os.contains("win")) {
	    	String appdata = System.getenv("APPDATA");
	    	if (appdata!=null) {
	    		return appdata + "/.minecraft/";
	    	} else {
	    		return defaultPath + "/.minecraft/";
	    	}
	    } else if (os.contains("mac"))  {
	    	return defaultPath + "/Library/Application Support/minecraft/";
	    }
	    return defaultPath + "/.minecraft/";
	}
	
	private File file;
	private ArrayList<Player> players;
	public long seed;
	private boolean multi;
	private ArrayList<String> back;
	public ArrayList<Player> getPlayers() {
		return players;
	}
	public void movePlayer(String name, int x, int y) {
		File out = null;
		if (multi) {
			String outpath = file.getParent() + "/players/" + name +".dat";
			out = new File(outpath);
			backupFile(out);
			try {
				Tag t = Tag.readFrom(new FileInputStream(out));
				Tag pos = t.findTagByName("Pos");
				Tag[] pa = (Tag[]) pos.getValue();
				pa[0].setValue((double)x);
				pa[1].setValue((double)120);
				pa[2].setValue((double)y);
				t.writeTo(new FileOutputStream(out));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			out = file;
			backupFile(out);
			try {
				Tag t = Tag.readFrom(new FileInputStream(out));
				Tag pos = t.findTagByName("Pos");
				Tag[] pa = (Tag[]) pos.getValue();
				pa[0].setValue((double)x);
				pa[1].setValue((double)120);
				pa[2].setValue((double)y);
				t.writeTo(new FileOutputStream(out));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void backupFile(File f) {
	    File inputFile = f;
	    File outputFile = new File(f.toString() + ".moth");
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
		    } catch (Exception e) {
		    }
	    }
	}
	
	public SaveLoader(File f) {
		file = f;
		players = new ArrayList<Player>();
		back = new ArrayList<String>();
		try {
			Tag t = Tag.readFrom(new FileInputStream(f));
			Tag pTag = t.findTagByName("Player");
			seed = (Long)t.findTagByName("RandomSeed").getValue();
			genType = (String)t.findTagByName("generatorName").getValue();
			System.out.println("Gen Type: " + genType);
			if (pTag!=null) {
				multi = false;
				Tag pos = pTag.findTagByName("Pos");
				Tag[] pa = (Tag[]) pos.getValue();
				double x = (Double) pa[0].getValue();
				double z = (Double) pa[2].getValue();
				players.add(new Player("Player", (int)x, (int)z));
				
			} else {
				multi = true;
				File[] listing = new File(f.getParent() + "/players").listFiles();
				Tag ps;
				for (int i = 0; i < listing.length; i++) {
					ps = Tag.readFrom(new FileInputStream(listing[i]));
					Tag pos = ps.findTagByName("Pos");
					Tag[] pa = (Tag[]) pos.getValue();
					double x = (Double) pa[0].getValue();
					double z = (Double) pa[2].getValue();
					players.add(new Player(listing[i].getName().split("\\.")[0], (int)x, (int)z));
					
				}
			}
		} catch (Exception e) {
			// FIXME Dialog when file read fails.
		}
	}
}
