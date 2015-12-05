package amidst.mojangapi;

import com.google.gson.annotations.SerializedName;

public enum ReleaseType {
	// @formatter:off
	@SerializedName("snapshot")
	SNAPSHOT("snapshot"),
	@SerializedName("release")
	RELEASE("release"),
	@SerializedName("old_beta")
	OLD_BETA("old_beta"),
	@SerializedName("old_alpha")
	OLD_ALPHA("old_alpha");
	// @formatter:on

	private final String name;

	private ReleaseType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
