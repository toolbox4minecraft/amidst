package amidst.mojangapi.minecraftinterface;

import amidst.documentation.Immutable;

/**
 * Information about what each supported version is
 */
@Immutable
public enum RecognisedVersion {
	// @formatter:off
	v15w44b("qtoombkapb[Llq;mn[J[[Jmj"),
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
	V13w39a_or_b("npmp[Lkn;jh[J[J[J[J[J[[J"),
	V13w37b_or_38a("ntmt[Lkm;jg[J[J[J[J[J[[J"),
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
	Vbeta_1_8_1("[Bhwqpyrrviqswdbzdqurkhqrgviwbomnabjrxmafvoeacfer[J[Jaddmkbb"), // Had to rename from V1_8_1 - should it just be removed?
	UNKNOWN(null); // Make sure this is the last entry, so UNKNOWN.isAtLeast(...) returns always false.
	// @formatter:on

	public static RecognisedVersion from(String magicString) {
		for (RecognisedVersion recognisedVersion : RecognisedVersion.values()) {
			if (magicString.equals(recognisedVersion.magicString)) {
				return recognisedVersion;
			}
		}
		return RecognisedVersion.UNKNOWN;
	}

	private final String name;
	private final String magicString;

	private RecognisedVersion(String magicString) {
		this.name = super.toString().replace("_", ".");
		this.magicString = magicString;
	}

	public String getName() {
		return name;
	}

	public String getMagicString() {
		return magicString;
	}

	public boolean isSaveEnabled() {
		return this != V12w21a && this != V12w21b && this != V12w22a
				&& this != UNKNOWN;
	}

	public boolean isAtLeast(RecognisedVersion other) {
		return this.ordinal() <= other.ordinal();
	}
}
