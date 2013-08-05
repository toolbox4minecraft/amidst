package amidst.foreign;

import MoF.Biome;

import java.util.Arrays;
import java.util.List;

/** Information about what each supported version is
 */
public enum VersionInfo {
	V1_6_2("akn", "akl"),
	V1_6_1("akj", "akh"),
	V1_5_1("ait", "air"),
	V1_5_0("ain", "ail"),
	V1_4_6("agw", "agu"),
	V1_4_5("agp", "agn"),
	V1_4_2("afu", "afs"),
	V1_3_2("adc", "ada"),
	V1_3_1("adb", "acz"),
	V1_3pre("acl", "acj"),
	V12w27a("acs", "av"),
	V12w26a("acl","av"),
	V12w25a("acg", "av"),
	V12w24a("aca", "av"),
	V12w23b("acg", "ay"),
	V12w22a("ace", "ay"),
	V12w21b("aby", "ax"),
	V12w21a("abm", "ar"),
	V12w19a("aau", "ao"),
	V1_2_4("wp", "ad"),
	V1_2_2("wl", "ac"),
	V12w08a("wj", "ac"),
	V12w07b("wd", "ab"),
	V12w06a("wb", "ab"),
	V12w05a("vy", "ab"),
	V12w04a("vu", "ab"),
	V12w03a("vj", "ab"),
	V1_1("vc", "ab"),
	V1_0("jx", "bm"),
	V1_9pre6("uk", "z"),
	V1_9pre5("ug", "y"),
	V1_9pre4("uh", "y"),  //TODO stronghold reset??
	V1_9pre3("to", "x"),
	V1_9pre2("sv", "x"),
	V1_9pre1("sq", "x"),
	V1_8_1("rj", "w"),
	unknown(null, "ab");
	
	public final String biomeName;
	public final String intCacheName;
	
	VersionInfo(String biomeName, String intCacheName) {
		this.biomeName = biomeName;
		this.intCacheName = intCacheName;
	}
	
	public boolean saveEnabled() {
		return this != V12w21a && this != V12w21b && this != V12w22a;
	}
	
	@Override
	public String toString() {
		return super.toString().replace("_", ".");
	}
	
	public boolean isAtLeast(VersionInfo other) {
		return this.ordinal() <= other.ordinal(); 
	}
}