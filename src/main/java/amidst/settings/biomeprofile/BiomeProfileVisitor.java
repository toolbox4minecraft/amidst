package amidst.settings.biomeprofile;

public interface BiomeProfileVisitor {
	void enterDirectory(String name);

	void visitProfile(BiomeProfile profile);

	void leaveDirectory();
}
