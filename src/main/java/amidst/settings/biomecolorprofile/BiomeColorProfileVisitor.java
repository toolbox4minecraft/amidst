package amidst.settings.biomecolorprofile;

public interface BiomeColorProfileVisitor {
	void enterDirectory(String name);

	void visitProfile(BiomeColorProfile profile);

	void leaveDirectory();
}
