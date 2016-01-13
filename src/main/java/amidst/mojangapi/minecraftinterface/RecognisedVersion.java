package amidst.mojangapi.minecraftinterface;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Objects;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.Log;

/**
 * Information about what each supported version is
 */
@Immutable
public enum RecognisedVersion {
	// @formatter:off
	UNKNOWN(null), // Make sure this is the first entry, so it is always considered newer than all other versions, since an unknown version is most likely a new snapshot
	V15w44b("qtoombkapb[Llq;mn[J[[Jmj"),
	V15w43c("qsoombkapb[Llq;mn[J[[Jmj"),
	V15w31c("oxnvlnjt[Llg;lz[J[[Jlv"),
	V1_8_8("orntlljs[Lle;lx[J[[Jlt"), // 1.8.4, 1.8.5, 1.8.6, 1.8.7, and 1.8.8 all have the same typeDump version ID. They are all security issue fixes.
	V1_8_3("osnulmjt[Llf;ly[J[[Jlu"), // 1.8.3 and 1.8.2 have the same typeDump version ID - probably because 1.8.2 -> 1.8.3 was a fix for a server-side bug (https://mojang.com/2015/02/minecraft-1-8-2-is-now-available/)
	V1_8_1("wduyrdnq[Lqu;sp[J[[Jsa"),
	V1_8("wbuwrcnp[Lqt;sn[J[[Jry"),
	V14w21b("tjseoylw[Loq;qd[J[[Jpo"),
	V1_7_10("riqinckb[Lmt;oi[J[[Jns"),
	V1_7_9("rhqhnbkb[Lms;oh[J[[Jnr"),
	V14w02a("qrponkki[Lnb;lv[J[[J"),
	V1_7_4("pzozmvjs[Lmm;lg[J[[J"),
	V1_7_2("pvovmsjp[Lmj;ld[J[[J"),
	V13w39a("npmp[Lkn;jh[J[J[J[J[J[[J"), // or 13w39b
	V13w37b("ntmt[Lkm;jg[J[J[J[J[J[[J"), // or 13w38a
	V13w37a("nsms[Lkl;jf[J[J[J[J[J[[J"),
	V13w36b("nkmk[Lkd;hw[J[J[J[J[J[[J"),
	V13w36a("nkmk[Lkd;hx[J[J[J[J[J[[J"),
	V1_6_4("mvlv[Ljs;hn[J[J[J[J[J[[J"),
	V1_6_2("mulu[Ljr;hm[J[J[J[J[J[[J"),
	V1_6_1("msls[Ljp;hk[J[J[J[J[J[[J"),
	V1_5_2("[Bbdzbdrbawemabdsbfybdvngngbeuawfbgeawvawvaxrawbbfqausbjgaycawwaraavybkcavwbjubkila"),
	V1_5_1("[Bbeabdsbawemabdtbfzbdwngngbevawfbgfawvawvaxrawbbfrausbjhaycawwaraavybkdavwbjvbkila"),
	V1_5_0("Invalid"), // TODO: This makes no sense? 1.5.0 is not on the version list!
	V1_4_6("[Baywayoaaszleaypbavaysmdazratabbaatqatqaulaswbanarnbdzauwatraohastbevasrbenbezbdmbdjkh"), // Includes 1.4.7
	V1_4_5("[Bayoaygaasrleayhbakaykmdazfassbapatjatjaueasobacarfbdoaupatkanzaslbekasjbecbenbdbbcykh"),
	V1_4_2("[Baxgawyaarjkpawzayyaxclnaxxarkazcasbasbaswargaytaqabcbathascamuardbcxarbbcpbdabbobbljy"),
	V1_3_2("[Batkatcaaofjbatdavbatgjwaubaogavfaovaovapnaocauwamxaxvapyaowajqanzayqanxayjaytaxkaxhik"),
	V1_3_1("adb"),
	V1_3pre("acl"),
	V12w27a("acs"),
	V12w26a("acl"),
	V12w25a("acg"),
	V12w24a("aca"),
	V12w23b("acg"),
	V12w22a("ace"),
	V12w21b("aby"),
	V12w21a("abm"),
	V12w19a("aau"),
	V1_2_4("[Bkivmaftxdlvqacqcwfcaawnlnlvpjclrckqdaiyxgplhusdakagi[J[Jalfqabv"), // Includes 1.2.5
	V1_2_2("wl"),
	V12w08a("wj"),
	V12w07b("wd"),
	V12w06a("wb"),
	V12w05a("vy"),
	V12w04a("vu"),
	V12w03a("vj"),
	V1_1("[Bjsudadrvqluhaarcqevyzmqmqugiokzcepgagqvsonhhrgahqfy[J[Jaitpdbo"),
	V1_0("[Baesmmaijryafvdinqfdrzhabeabexexwadtnglkqdfagvkiahmhsadk[J[Jtkgkyu"),
	V1_9pre6("uk"), // TODO: Remove these versions?
	V1_9pre5("ug"),
	V1_9pre4("uh"),  //TODO stronghold reset??
	V1_9pre3("to"),
	V1_9pre2("sv"),
	V1_9pre1("sq"),
	V1_8_1beta("[Bhwqpyrrviqswdbzdqurkhqrgviwbomnabjrxmafvoeacfer[J[Jaddmkbb"); // Had to rename from V1_8_1 - should it just be removed?
	// @formatter:on

	public static RecognisedVersion from(URLClassLoader classLoader)
			throws ClassNotFoundException {
		return from(getMainClassFields(classLoader));
	}

	@NotNull
	private static Field[] getMainClassFields(URLClassLoader classLoader)
			throws ClassNotFoundException {
		if (classLoader.findResource(CLIENT_CLASS_RESOURCE) != null) {
			return classLoader.loadClass(CLIENT_CLASS).getDeclaredFields();
		} else if (classLoader.findResource(SERVER_CLASS_RESOURCE) != null) {
			return classLoader.loadClass(SERVER_CLASS).getDeclaredFields();
		} else {
			throw new ClassNotFoundException(
					"unable to find the main class in the given jar file");
		}
	}

	public static RecognisedVersion from(Field[] fields) {
		return from(generateMagicString(fields));
	}

	private static String generateMagicString(Field[] fields) {
		String result = "";
		for (Field field : fields) {
			String typeString = field.getType().toString();
			if (typeString.startsWith("class ") && !typeString.contains(".")) {
				result += typeString.substring(6);
			}
		}
		return result;
	}

	@NotNull
	public static RecognisedVersion from(String magicString) {
		for (RecognisedVersion recognisedVersion : RecognisedVersion.values()) {
			if (magicString.equals(recognisedVersion.magicString)) {
				Log.i("Recognised Minecraft Version "
						+ recognisedVersion.getName()
						+ " with the magic string \"" + magicString + "\".");
				return recognisedVersion;
			}
		}
		Log.i("Unable to recognise Minecraft Version with the magic string \""
				+ magicString + "\".");
		return RecognisedVersion.UNKNOWN;
	}

	public static boolean isNewerOrEqualTo(RecognisedVersion version1,
			RecognisedVersion version2) {
		return compareNewerIsLower(version1, version2) <= 0;
	}

	public static boolean isNewer(RecognisedVersion version1,
			RecognisedVersion version2) {
		return compareNewerIsLower(version1, version2) < 0;
	}

	public static boolean isOlderOrEqualTo(RecognisedVersion version1,
			RecognisedVersion version2) {
		return compareNewerIsLower(version1, version2) >= 0;
	}

	public static boolean isOlder(RecognisedVersion version1,
			RecognisedVersion version2) {
		return compareNewerIsLower(version1, version2) > 0;
	}

	public static int compareNewerIsGreater(RecognisedVersion version1,
			RecognisedVersion version2) {
		return compareNewerIsLower(version2, version1);
	}

	public static int compareNewerIsLower(RecognisedVersion version1,
			RecognisedVersion version2) {
		Objects.requireNonNull(version1);
		Objects.requireNonNull(version2);
		return version1.ordinal() - version2.ordinal();
	}

	private static String getName(String string) {
		if (string.toLowerCase().startsWith("v")) {
			return string.substring(1).replace("_", ".");
		} else {
			return string;
		}
	}

	private static final String CLIENT_CLASS_RESOURCE = "net/minecraft/client/Minecraft.class";
	private static final String CLIENT_CLASS = "net.minecraft.client.Minecraft";
	private static final String SERVER_CLASS_RESOURCE = "net/minecraft/server/MinecraftServer.class";
	private static final String SERVER_CLASS = "net.minecraft.server.MinecraftServer";

	private final String name;
	private final String magicString;

	private RecognisedVersion(String magicString) {
		this.name = getName(super.toString());
		this.magicString = magicString;
	}

	public String getName() {
		return name;
	}

	public String getMagicString() {
		return magicString;
	}
}
