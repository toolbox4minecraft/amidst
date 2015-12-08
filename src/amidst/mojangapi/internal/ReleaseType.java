package amidst.mojangapi.internal;

import com.google.gson.annotations.SerializedName;

public enum ReleaseType {
	// @formatter:off
	@SerializedName("snapshot")
	SNAPSHOT("snapshot", "S"),
	@SerializedName("release")
	RELEASE("release", "R"),
	@SerializedName("old_beta")
	OLD_BETA("old_beta", "B"),
	@SerializedName("old_alpha")
	OLD_ALPHA("old_alpha", "A");
	// @formatter:on

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
