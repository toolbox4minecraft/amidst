package amidst.json;

import java.util.ArrayList;

public class JarProfile {
	public String id, time, releaseTime, type, minecraftArguments;
	public ArrayList<JarLibrary> libraries = new ArrayList<JarLibrary>();
	
	public JarProfile() {
	}
}
