package amidst.json;

public class InstallInformation {
	public String name;
	public String lastVersionId = "latest";
	public String javaDir;
	public String javaArgs;
	public String playerUUID;
	public Resolution resolution;
	public String[] allowedReleaseTypes = new String[] { "release" };
}