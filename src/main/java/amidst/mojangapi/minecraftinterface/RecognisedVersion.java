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
	// Make sure UNKNOWN is the first entry, so it is always considered newer than all other versions, since an unknown version is most likely a new snapshot.
	// 1.8.4, 1.8.5, 1.8.6, 1.8.7, and 1.8.8 all have the same typeDump version ID. They are all security issue fixes.
	// 1.8.3 and 1.8.2 have the same typeDump version ID - probably because 1.8.2 -> 1.8.3 was a fix for a server-side bug (https://mojang.com/2015/02/minecraft-1-8-2-is-now-available/)
	// TODO: Remove these versions before V1_0?
	// TODO: stronghold reset on V1_9pre4?
	UNKNOWN(null),
	V16w06a     ("qvoomajzpb[Llp;mm[J[[Jmi"),                                                                       // matches the versions 16w06a       16w05b       16w04a       16w03a       16w02a (A shame it matches so many, because the stronghold alg. is different starting from 16w06a)      
	V16w02a     ("qvoomajzpb[Llp;mm[J[[Jmi"),                                                                       // matches the versions 16w02a      
	V15w51b     ("quonmajzpa[Llp;mm[J[[Jmi"),                                                                       // matches the versions 15w51b       15w51a      
	V15w50a     ("qtonmajzpa[Llp;mm[J[[Jmi"),                                                                       // matches the versions 15w50a       15w49b       15w47c      
	V15w46a     ("qsonmajzpa[Llp;mm[J[[Jmi"),                                                                       // matches the versions 15w46a      
	V15w45a     ("qtoombkapb[Llq;mn[J[[Jmj"),                                                                       // matches the versions 15w45a       15w44b      
	V15w43c     ("qsoombkapb[Llq;mn[J[[Jmj"),                                                                       // matches the versions 15w43c      
	V15w42a     ("qnojlzjzow[Llp;ml[J[[Jmh"),                                                                       // matches the versions 15w42a      
	V15w41b     ("qmoilyjyov[Llo;mk[J[[Jmg"),                                                                       // matches the versions 15w41b      
	V15w40b     ("qhoelujuor[Llk;mg[J[[Jmc"),                                                                       // matches the versions 15w40b       15w39c       15w38b       15w37a      
	V15w36d     ("qgodltjuoq[Lll;mf[J[[Jmb"),                                                                       // matches the versions 15w36d      
	V15w35e     ("qeoclsjuop[Llk;me[J[[Jma"),                                                                       // matches the versions 15w35e      
	V15w34d     ("qdoblsjuoo[Lll;me[J[[Jma"),                                                                       // matches the versions 15w34d      
	V15w33c     ("qanzlrjtom[Llk;md[J[[Jlz"),                                                                       // matches the versions 15w33c      
	V15w32c     ("pmnvlnjt[Llg;lz[J[[Jlv"),                                                                         // matches the versions 15w32c      
	V15w31c     ("oxnvlnjt[Llg;lz[J[[Jlv"),                                                                         // matches the versions 15w31c      
	V1_8_9      ("orntlljs[Lle;lx[J[[Jlt"),                                                                         // matches the versions 1.8.9        1.8.8        1.8.7        1.8.6        1.8.5        1.8.4       
	V1_8_3      ("osnulmjt[Llf;ly[J[[Jlu"),                                                                         // matches the versions 1.8.3        1.8.2       
	V1_8_1      ("wduyrdnq[Lqu;sp[J[[Jsa"),                                                                         // matches the versions 1.8.1       
	V1_8        ("wbuwrcnp[Lqt;sn[J[[Jry"),                                                                         // matches the versions 1.8         
	V14w21b     ("tjseoylw[Loq;qd[J[[Jpo"),                                                                         // not generated
	V1_7_10     ("riqinckb[Lmt;oi[J[[Jns"),                                                                         // matches the versions 1.7.10      
	V1_7_9      ("rhqhnbkb[Lms;oh[J[[Jnr"),                                                                         // matches the versions 1.7.9       
	V14w02a     ("qrponkki[Lnb;lv[J[[J"),                                                                           // not generated
	V1_7_5      ("qfpfnbjy[Lms;lm[J[[J"),                                                                           // matches the versions 1.7.5       
	V1_7_4      ("pzozmvjs[Lmm;lg[J[[J"),                                                                           // matches the versions 1.7.4       
	V1_7_2      ("pvovmsjp[Lmj;ld[J[[J"),                                                                           // matches the versions 1.7.2       
	V13w39a     ("npmp[Lkn;jh[J[J[J[J[J[[J"),                                                                       // not generated or 13w39b
	V13w37b     ("ntmt[Lkm;jg[J[J[J[J[J[[J"),                                                                       // not generated or 13w38a
	V13w37a     ("nsms[Lkl;jf[J[J[J[J[J[[J"),                                                                       // not generated
	V13w36b     ("nkmk[Lkd;hw[J[J[J[J[J[[J"),                                                                       // not generated
	V13w36a     ("nkmk[Lkd;hx[J[J[J[J[J[[J"),                                                                       // not generated
	V1_6_4      ("mvlv[Ljs;hn[J[J[J[J[J[[J"),                                                                       // matches the versions 1.6.4       
	V1_6_2      ("mulu[Ljr;hm[J[J[J[J[J[[J"),                                                                       // matches the versions 1.6.2       
	V1_6_1      ("msls[Ljp;hk[J[J[J[J[J[[J"),                                                                       // matches the versions 1.6.1       
	V1_5_2      ("[Bbdzbdrbawemabdsbfybdvngngbeuawfbgeawvawvaxrawbbfqausbjgaycawwaraavybkcavwbjubkila"),            // matches the versions 1.5.2       
	V1_5_1      ("[Bbeabdsbawemabdtbfzbdwngngbevawfbgfawvawvaxrawbbfrausbjhaycawwaraavybkdavwbjvbkila"),            // matches the versions 1.5.1       
	V1_4_7      ("[Baywayoaaszleaypbavaysmdazratabbaatqatqaulaswbanarnbdzauwatraohastbevasrbenbezbdmbdjkh"),        // matches the versions 1.4.7        1.4.6       
	V1_4_5      ("[Bayoaygaasrleayhbakaykmdazfassbapatjatjaueasobacarfbdoaupatkanzaslbekasjbecbenbdbbcykh"),        // matches the versions 1.4.5        1.4.4       
	V1_4_2      ("[Baxgawyaarjkpawzayyaxclnaxxarkazcasbasbaswargaytaqabcbathascamuardbcxarbbcpbdabbobbljy"),        // matches the versions 1.4.2       
	V1_3_2      ("[Batkatcaaofjbatdavbatgjwaubaogavfaovaovapnaocauwamxaxvapyaowajqanzayqanxayjaytaxkaxhik"),        // matches the versions 1.3.2       
	V1_3_1      ("[Batjatbaaoejaatcavaatfjvauaaofaveaouaouapmaobauvamwaxuapxaovajpanyaypanwayiaysaxjaxgij"),        // matches the versions 1.3.1       
	V1_3pre     ("acl"),                                                                                            // not generated
	V12w27a     ("acs"),                                                                                            // not generated
	V12w26a     ("acl"),                                                                                            // not generated
	V12w25a     ("acg"),                                                                                            // not generated
	V12w24a     ("aca"),                                                                                            // not generated
	V12w23b     ("acg"),                                                                                            // not generated
	V12w22a     ("ace"),                                                                                            // not generated
	V12w21b     ("aby"),                                                                                            // not generated
	V12w21a     ("abm"),                                                                                            // not generated
	V12w19a     ("aau"),                                                                                            // not generated
	V1_2_5      ("[Bkivmaftxdlvqacqcwfcaawnlnlvpjclrckqdaiyxgplhusdakagi[J[Jalfqabv"),                              // matches the versions 1.2.5        1.2.4       
	V1_2_3      ("[Bkfviafowzlvmaclcueyaarninivlizlocipzaisxcphhrrzajugf[J[Jakzpwbt"),                              // matches the versions 1.2.3        1.2.2        1.2.1       
	V12w08a     ("wj"),                                                                                             // not generated
	V12w07b     ("wd"),                                                                                             // not generated
	V12w06a     ("wb"),                                                                                             // not generated
	V12w05a     ("vy"),                                                                                             // not generated
	V12w04a     ("vu"),                                                                                             // not generated
	V12w03a     ("vj"),                                                                                             // not generated
	V1_1        ("[Bjsudadrvqluhaarcqevyzmqmqugiokzcepgagqvsonhhrgahqfy[J[Jaitpdbo"),                               // matches the versions 1.1         
	V1_0        ("[Baesmmaijryafvdinqfdrzhabeabexexwadtnglkqdfagvkiahmhsadk[J[Jtkgkyu"),                            // matches the versions 1.0         
	V1_9pre6    ("uk"),                                                                                             // not generated
	V1_9pre5    ("ug"),                                                                                             // not generated
	V1_9pre4    ("uh"),                                                                                             // not generated
	V1_9pre3    ("to"),                                                                                             // not generated
	V1_9pre2    ("sv"),                                                                                             // not generated
	V1_9pre1    ("sq"),                                                                                             // not generated
	Vb1_8_1     ("[Bhwqpyrrviqswdbzdqurkhqrgviwbomnabjrxmafvoeacfer[J[Jaddmkbb"),                                   // matches the versions b1.8.1       b1.8        
	Vb1_7_3     ("[Bobcxpyfdndclsdngrjisjdamkpxczvuuqfhvfkvyovyik[J[Jxivscg"),                                      // matches the versions b1.7.3       b1.7.2       b1.7        
	Vb1_6_6     ("[Bnxcvpufbmdalodlgpjfsecymgptcxvmukffuxkryfvqih[J[Jwzvkce"),                                      // matches the versions b1.6.6       b1.6.5       b1.6.4       b1.6.3       b1.6.2       b1.6.1       b1.6        
	Vb1_5_01    ("nfcpozetmcukwdfggiprfcslooycruntlextyjzxeurhv[J[Jvyulbz"),                                        // matches the versions b1.5_01      b1.5        
	Vb1_4_01    ("lncdmxebichjmcsfkhooxcfkcmwcerqqvefrkisujsbgw[J[Jtervbo"),                                        // matches the versions b1.4_01     
	Vb1_4       ("lncdmxebichjmcsfkhooxcfkcmwcerpqvefrkisujsagw[J[Jterubo"),                                        // matches the versions b1.4        
	Vb1_3_01    ("kybymidthccizcnfbhfoicbjpmhbzqfdxquigtmrhgn[J[Jrbbk"),                                            // matches the versions b1.3_01     
	Vb1_3b      ("kybymidthccizcnfbhfoicbjpmhbzqgdxqvigtnrign[J[Jrcbk"),                                            // matches the versions b1.3b       
	Vb1_2_02    ("kbbvlmdnhbzcjesgsnhbyiwllbwpedrprhqsgqega[J[Jpybj"),                                              // matches the versions b1.2_02      b1.2_01      b1.2        
	Vb1_1_02    ("jjboksddfbsccehgemjbrifkrbpobdhonhbqvoyfo[J[Joubc"),                                              // matches the versions b1.1_02      b1.1_01     
	Vb1_0_2     ("jibokrddfbscceggdmibriekqbpoadhomhaquoxfn[J[Jotbc"),                                              // matches the versions b1.0.2       b1.0_01      b1.0        
	Va1_2_6     ("ivbmkccyfbqbzeafulsbphukbbnnldcnxgqqgoiff[J[Joeba"),                                              // matches the versions a1.2.6      
	Va1_2_5     ("iubmkbcxfbqbydzftlrbphtkabnnkdbnwgpqfohfe[J[Jodba"),                                              // matches the versions a1.2.5       a1.2.4_01   
	Va1_2_3_04  ("iubmkbcxfbqbydzftlqbphtkabnnjdbnvgpqeogfe[J[Jocba"),                                              // matches the versions a1.2.3_04    a1.2.3_02    a1.2.3_01    a1.2.3      
	Va1_2_2b    ("isbmjycwfbqbydyfrlnbphrjxbnngdansgnqbodfd[J[Jnzba"),                                              // matches the versions a1.2.2b      a1.2.2a     
	Va1_2_1_01  ("imbkjrcudbobwdufmlgbnhmjqblmzcynlgiptnv[J[Jnray"),                                                // matches the versions a1.2.1_01    a1.2.1       a1.2.0_02    a1.2.0_01    a1.2.0      
	Va1_1_2_01  ("hqbeircnebibqdleykdbhgriqbflucrmffrofmp[Jmlat"),                                                  // matches the versions a1.1.2_01    a1.1.2      
	Va1_1_0     ("hqbeircnebibqdleykdbhgriqbflucrmffroemo[Jmlat"),                                                  // matches the versions a1.1.0      
	Va1_0_17_04 ("hpbdiqcmebhbpdkexkbbggqipbeltcqmdfqobmm[Jmjar"),                                                  // matches the versions a1.0.17_04   a1.0.17_02  
	Va1_0_16    ("hgazihcjebebmdferjtbdgiigbblkcnlvfinrmd[Jmbap"),                                                  // matches the versions a1.0.16     
	Va1_0_15    ("hfazigcjebebmdferjsbdgiifbbljcnlufinqmc[Jmaap"),                                                  // matches the versions a1.0.15     
	Va1_0_14    ("hcazidcjebebmdfeqjpbdghicbblfcnlpfhnmly[Jlwap"),                                                  // matches the versions a1.0.14     
	Va1_0_11    ("haaziacjebebmddenjlbdgfhzbbkzcnljfenels[Jlqap");                                                  // matches the versions a1.0.11     
	// @formatter:on

	@NotNull
	public static RecognisedVersion from(URLClassLoader classLoader)
			throws ClassNotFoundException {
		return from(generateMagicString(classLoader));
	}

	@NotNull
	public static String generateMagicString(URLClassLoader classLoader)
			throws ClassNotFoundException {
		return generateMagicString(getMainClassFields(classLoader));
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

	@NotNull
	public static RecognisedVersion from(Field[] fields) {
		return from(generateMagicString(fields));
	}

	@NotNull
	public static String generateMagicString(Field[] fields) {
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
				logFound(recognisedVersion);
				return recognisedVersion;
			}
		}
		Log.i("Unable to recognise Minecraft Version with the magic string \""
				+ magicString + "\".");
		return RecognisedVersion.UNKNOWN;
	}

	@NotNull
	public static RecognisedVersion fromName(String name) {
		for (RecognisedVersion recognisedVersion : RecognisedVersion.values()) {
			if (name.equals(recognisedVersion.name)) {
				logFound(recognisedVersion);
				return recognisedVersion;
			}
		}
		Log.i("Unable to recognise Minecraft Version with the name \"" + name
				+ "\".");
		return RecognisedVersion.UNKNOWN;
	}

	private static void logFound(RecognisedVersion recognisedVersion) {
		Log.i("Recognised Minecraft Version " + recognisedVersion.name
				+ " with the magic string \"" + recognisedVersion.magicString
				+ "\".");
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
