package amidst.mojangapi.file.json;

import com.google.gson.annotations.SerializedName;

import amidst.documentation.Immutable;

@Immutable
public enum ReleaseType {
	@SerializedName("snapshot")
	SNAPSHOT("snapshot", "S"),
	@SerializedName("release")
	RELEASE("release", "R"),
	@SerializedName("old_beta")
	OLD_BETA("old_beta", "B"),
	@SerializedName("old_alpha")
	OLD_ALPHA("old_alpha", "A");

	private final String name;
	private final String typeChar;

	private ReleaseType(String name, String typeChar) {
		this.name = name;
		this.typeChar = typeChar;
	}

	public String getName() {
		return name;
	}

	public String getTypeChar() {
		return typeChar;
	}
}
