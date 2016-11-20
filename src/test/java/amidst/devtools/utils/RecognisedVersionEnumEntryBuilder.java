package amidst.devtools.utils;

import java.util.LinkedList;
import java.util.List;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

@NotThreadSafe
public class RecognisedVersionEnumEntryBuilder {
	public static RecognisedVersionEnumEntryBuilder known(RecognisedVersion recognisedVersion) {
		return new RecognisedVersionEnumEntryBuilder(
				true,
				recognisedVersion.getName(),
				recognisedVersion.getMagicString());
	}

	public static RecognisedVersionEnumEntryBuilder unknown(String versionId, String magicString) {
		RecognisedVersionEnumEntryBuilder result = new RecognisedVersionEnumEntryBuilder(false, versionId, magicString);
		result.addMatch(versionId);
		return result;
	}

	private final boolean isKnown;
	private final String knownName;
	private final String magicString;
	private final List<String> matches = new LinkedList<>();
	private String declaration;

	public RecognisedVersionEnumEntryBuilder(boolean isKnown, String knownName, String magicString) {
		this.isKnown = isKnown;
		this.knownName = knownName;
		this.magicString = magicString;
	}

	public boolean isKnown() {
		return isKnown;
	}

	public String getName() {
		if (matches.isEmpty()) {
			return knownName;
		} else {
			return matches.get(0);
		}
	}

	public String getMagicString() {
		return magicString;
	}

	public boolean isRenamed() {
		return !getName().equals(knownName);
	}

	public void addMatch(String versionId) {
		matches.add(versionId);
	}

	public int renderDeclarationAndGetLength(int maxNameLength) {
		String enumIdentifier = createEnumIdentifier(maxNameLength);
		String name = quoteName(maxNameLength);
		declaration = enumIdentifier + "(" + name + "\"" + magicString + "\"),";
		return declaration.length();
	}

	private String createEnumIdentifier(int maxNameLength) {
		return addPadding(RecognisedVersion.createEnumIdentifier(getName()), maxNameLength + 1);
	}

	private String quoteName(int maxNameLength) {
		return addPadding("\"" + getName() + "\",", maxNameLength + 4);
	}

	public String renderLine(int maxNameLength, int maxDeclarationLength) {
		return addPadding(declaration, maxDeclarationLength) + getComment(maxNameLength);
	}

	private String getComment(int maxNameLength) {
		if (matches.isEmpty()) {
			return " // matches the launcher version id: ";
		} else {
			StringBuilder b = new StringBuilder();
			b.append(" // matches the launcher version id: ");
			int length = maxNameLength + 3;
			b.append(addPadding(matches.get(0), length));
			for (int i = 1; i < matches.size(); i++) {
				b.append(addPadding(matches.get(i), length));
			}
			return b.toString();
		}
	}

	private String addPadding(String declaration, int length) {
		return declaration + getStringOfLength(' ', length - declaration.length());
	}

	private String getStringOfLength(char c, int length) {
		String result = "";
		for (int i = 0; i < length; i++) {
			result += c;
		}
		return result;
	}
}
