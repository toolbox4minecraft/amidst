package amidst.mojangapi.file.json.launcherprofiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

import amidst.documentation.Immutable;
import amidst.mojangapi.file.json.ReleaseType;

/** Profiles types, as used in the new profile format (2) used by the Windows launcher.
 *  A typical installation has to profiles created by default, of type
 *  LATEST_RELEASE and LATEST_SNAPSHOT, and their names can't be changed.
 *  
 *  The LEGACY profile represents a profile in the format used by the old launcher (1).
 */
@Immutable
public enum ProfileType {
	@SerializedName("latest-release")
	LATEST_RELEASE("Latest version", ReleaseType.RELEASE),
	@SerializedName("latest-snapshot")
	LATEST_SNAPSHOT("Latest snapshot", ReleaseType.RELEASE, ReleaseType.SNAPSHOT),
	@SerializedName("custom")
	CUSTOM(null),
	@SerializedName("")
	LEGACY(null),
	;
	
	private Optional<String> defaultName;
	private Optional<List<ReleaseType>> allowedReleaseTypes;
	
	private ProfileType(String defaultName, ReleaseType... releaseTypes) {
		this.defaultName = Optional.ofNullable(defaultName);
		if(releaseTypes.length > 0) {
			this.allowedReleaseTypes = Optional.of(Collections.unmodifiableList(Arrays.asList(releaseTypes)));
		} else {
			this.allowedReleaseTypes = Optional.empty();
		}
	}
	
	public Optional<String> getDefaultName() {
		return defaultName;
	}
	
	public Optional<List<ReleaseType>> getAllowedReleaseTypes() {
		return allowedReleaseTypes;
	}
	
}
