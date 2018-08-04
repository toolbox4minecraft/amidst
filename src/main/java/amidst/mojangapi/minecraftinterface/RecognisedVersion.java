package amidst.mojangapi.minecraftinterface;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import amidst.documentation.Immutable;
import amidst.documentation.NotNull;
import amidst.logging.AmidstLogger;import amidst.mojangapi.world.versionfeatures.VersionFeatures;

/**
 * Information about what each supported version is
 */
@Immutable
public enum RecognisedVersion {
	// @formatter:off
	// Make sure UNKNOWN is the first entry, so it is always considered newer than all other versions, since an unknown version is most likely a new snapshot.
	// The 128 stronghold algorithm changes in version 16w06a. However, we cannot uniquely identify this version.
	// 1.8.4, 1.8.5, 1.8.6, 1.8.7, and 1.8.8 all have the same typeDump version ID. They are all security issue fixes.
	// 1.8.3 and 1.8.2 have the same typeDump version ID - probably because 1.8.2 -> 1.8.3 was a fix for a server-side bug (https://mojang.com/2015/02/minecraft-1-8-2-is-now-available/)
	// TODO: Remove these versions before V1_0?
	// TODO: stronghold reset on V1_9pre4?
	UNKNOWN,
	_18w31a    ("18w31a",     "aduxsuconvq[Jvmuyuwbvavkxcpspwccqpppq"),                                                   // matches the launcher version id: 18w31a
	_18w30b    ("18w30b",     "adtxruaom[Ltc;vo[J[[Jvkuwuubvavjxaprpvcclpopp"),                                           // matches the launcher version id: 18w30b
	_18w30a    ("18w30a",     "adsxquaom[Ltc;vo[J[[Jvkuwuubvavixaprpvcckpopp"),                                           // matches the launcher version id: 18w30a
	_1_13      ("1.13",       "adrxquaom[Ltc;vo[J[[Jvkuwuubvavhxaprpvccipopp"),                                           // matches the launcher version id: 1.13
	_1_13_pre10("1.13-pre10", "adpxquaom[Ltc;vo[J[[Jvkuwuubvavfxaprpvccgpopp"),                                           // matches the launcher version id: 1.13-pre10
	_1_13_pre8 ("1.13-pre8",  "adoxquaom[Ltc;vo[J[[Jvkuwuubvavexaprpvccgpopp"),                                           // matches the launcher version id: 1.13-pre8	   1.13-pre9
	_1_13_pre7 ("1.13-pre7",  "adixquaom[Ltc;vo[J[[Jvkuwuubvauyxaprpvcbxpopp"),                                           // matches the launcher version id: 1.13-pre7
	_1_13_pre6 ("1.13-pre6",  "adexntxoj[Lsz;vl[J[[Jvhuturbvautwxpopscbtplpm"),                                           // matches the launcher version id: 1.13-pre6
	_1_13_pre5 ("1.13-pre5",  "adbxltvoh[Lsx;vj[J[[Jvfurupbvauqwvpmpqcbqpjpk"),                                           // matches the launcher version id: 1.13-pre5
	_1_13_pre4 ("1.13-pre4",  "ahyxntvohya[Lsx;vj[J[[Jvfurupbvazowvpmpqcgnpjpk"),                                         // matches the launcher version id: 1.13-pre4
	_1_13_pre3 ("1.13-pre3",  "ahqxftnnzxs[Lsp;vb[J[[Juxujuhbvazgwnpepicgepbpc"),                                         // matches the launcher version id: 1.13-pre3
	_1_13_pre2 ("1.13-pre2",  "ahixdtlnxxp[Lsn;uz[J[[Juvuhufbvayywlpcpgcfvozpa"),                                         // matches the launcher version id: 1.13-pre2
	_1_13_pre1 ("1.13-pre1",  "ahhxctknwxo[Lsm;uy[J[[Juuuguebuayxwkpbpfcfsoyoz"),                                         // matches the launcher version id: 1.13-pre1
	_18w22c    ("18w22c",     "ahfxctknwxo[Lsm;uy[J[[Juuuguebuayvwkpbpfcfqoyoz"),                                         // matches the launcher version id: 18w22c
	_18w21b    ("18w21b",     "ahdxbtjnvxn[Lsl;ux[J[[Jutufudbtaytwjpapecfooxoy"),                                         // matches the launcher version id: 18w21b
	_18w20c    ("18w20c",     "ahcxatinuxm[Lsk;uw[J[[Jusueucbtayswiozpdcexowox"),                                         // matches the launcher version id: 18w20c
	_18w19b    ("18w19b",     "agwwzthntxm[Lsj;uv[J[[Jurudubbsaymwhoypccerovow"),                                         // matches the launcher version id: 18w19b
	_18w16a    ("18w16a",     "aavwutfnsxf[Lsh;ut[J[[Jupubtzbsaskweowpabyhotou"),                                         // matches the launcher version id: 18w16a
	_18w15a    ("18w15a",     "aauwttensxe[Lsh;us[J[[Juouatybsasjwdowpabxrotou"),                                         // matches the launcher version id: 18w15a
	_18w14b    ("18w14b",     "aauwttensxe[Lsh;us[J[[Juouatybsasawdowpabwyotou"),                                         // matches the launcher version id: 18w14b       
	_18w11a    ("18w11a",     "aaqwqtbnpxb[Lse;up[J[[Jultxtvbparwwaotoxbwroqor"),                                         // matches the launcher version id: 18w11a       
	_18w10d    ("18w10d",     "aaqwqtbnpxb[Lse;up[J[[Jultxtvbparvwaotoxbwmoqor"),                                         // matches the launcher version id: 18w10d       
	_18w09a    ("18w09a",     "aakwlswnkww[Lrz;uk[J[[Jugtstqbparovvooosbvkolom"),                                         // matches the launcher version id: 18w09a       
	_18w08b    ("18w08b",     "aaiwjsuniwu[Lrx;ui[J[[Juetqtobparmvtomoqbvdojok"),                                         // matches the launcher version id: 18w08b       
	_18w07c    ("18w07c",     "aahwistniwt[Lrx;uh[J[[Judtptnbparfvsomoqbuhojok"),                                         // matches the launcher version id: 18w07c       
	_18w06a    ("18w06a",     "aalwlswniwx[Lsa;uk[J[[Jugtstqbpargvvonorbuaokol"),                                         // matches the launcher version id: 18w06a       
	_18w05a    ("18w05a",     "znvssdnfwe[Lrs;tr[J[[Jtnszsxbnaqgvcoiombliofog"),                                          // matches the launcher version id: 18w05a       
	_18w03b    ("18w03b",     "zjvorznfwa[Lro;tn[J[[Jtjsvstbnaqcuyoibleofog"),                                            // matches the launcher version id: 18w03b       18w02a       
	_18w01a    ("18w01a",     "zhvnrynevz[Lrn;tm[J[[Jtisussbnapsuxohbkyoeof"),                                            // matches the launcher version id: 18w01a       
	_17w50a    ("17w50a",     "ykutremkvf[Lqt;ss[J[[Jsosarybnaovud"),                                                     // matches the launcher version id: 17w50a       
	_17w49b    ("17w49b",     "yiusrdmjve[Lqs;sr[J[[Jsnrzrxbnaoquc"),                                                     // matches the launcher version id: 17w49b       
	_17w48a    ("17w48a",     "xvugqxmdus[Lqm;sl[J[[Jshrtrrblaoe"),                                                       // matches the launcher version id: 17w48a       
	_17w47b    ("17w47b",     "xuufqwmcur[Lql;sk[J[[Jsgrsrqbl"),                                                          // matches the launcher version id: 17w47b       
	_17w46a    ("17w46a",     "xiugqslyut[Lqh;sg[J[[Jscrormbl"),                                                          // matches the launcher version id: 17w46a       
	_17w45b    ("17w45b",     "wvttqflmug[Lpu;rt[J[[Jrprbqzbl"),                                                          // matches the launcher version id: 17w45b       
	_17w43b    ("17w43b",     "vosnozmtta[Loo;qn[J[[Jqjpvpt"),                                                            // matches the launcher version id: 17w43b       
	_1_12_2    ("1.12.2",     "ulrlozmtry[Loo;pl[J[[Jph"),                                                                // matches the launcher version id: 1.12.2       1.12.1       
	_1_12      ("1.12",       "ujrjoxmsrw[Lom;pj[J[[Jpf"),                                                                // matches the launcher version id: 1.12         
	_1_12_pre2 ("1.12-pre2",  "uhrhovmqru[Lok;ph[J[[Jpd"),                                                                // matches the launcher version id: 1.12-pre2    
	_1_12_pre1 ("1.12-pre1",  "ugrgoumprt[Loj;pg[J[[Jpc"),                                                                // matches the launcher version id: 1.12-pre1    
	_17w18b    ("17w18b",     "tyqyommirl[Lob;oy[J[[Jou"),                                                                // matches the launcher version id: 17w18b       
	_17w17b    ("17w17b",     "tpqroemare[Lnt;oq[J[[Jom"),                                                                // matches the launcher version id: 17w17b       
	_17w16b    ("17w16b",     "tnqpoclyrc[Lnr;oo[J[[Jok"),                                                                // matches the launcher version id: 17w16b       
	_17w15a    ("17w15a",     "tlqnoalwra[Lnp;om[J[[Joi"),                                                                // matches the launcher version id: 17w15a       
	_17w14a    ("17w14a",     "tkqmoalwqz[Lnp;om[J[[Joi"),                                                                // matches the launcher version id: 17w14a       
	_17w13b    ("17w13b",     "tgqinwlsqv[Lnl;oi[J[[Joe"),                                                                // matches the launcher version id: 17w13b       
	_1_11_2    ("1.11.2",     "rsoumhkfph[Llw;mt[J[[Jmp"),                                                                // matches the launcher version id: 1.11.2       1.11.1       
	_1_11      ("1.11",       "rroumhkfph[Llw;mt[J[[Jmp"),                                                                // matches the launcher version id: 1.11         1.11-pre1    
	_16w44a    ("16w44a",     "rqotmgkfpg[Llv;ms[J[[Jmo"),                                                                // matches the launcher version id: 16w44a       
	_16w43a    ("16w43a",     "rpotmgkfpg[Llv;ms[J[[Jmo"),                                                                // matches the launcher version id: 16w43a       16w42a       16w41a       16w40a       16w39c       
	_16w38a    ("16w38a",     "rlosmfkepf[Llu;mr[J[[Jmn"),                                                                // matches the launcher version id: 16w38a       
	_16w36a    ("16w36a",     "rkosmfkepf[Llu;mr[J[[Jmn"),                                                                // matches the launcher version id: 16w36a       
	_16w35a    ("16w35a",     "rjosmfkepf[Llu;mr[J[[Jmn"),                                                                // matches the launcher version id: 16w35a       16w33a       16w32b       
	_1_10_2    ("1.10.2",     "rboqmdkcpd[Lls;mp[J[[Jml"),                                                                // matches the launcher version id: 1.10.2       1.10.1       1.10         
	_16w21b    ("16w21b",     "qzopmckbpc[Llr;mo[J[[Jmk"),                                                                // matches the launcher version id: 16w21b       
	_16w20a    ("16w20a",     "qxopmckbpc[Llr;mo[J[[Jmk"),                                                                // matches the launcher version id: 16w20a       
	_1_9_4     ("1.9.4",      "qwoombkapb[Llq;mn[J[[Jmj"),                                                                // matches the launcher version id: 1.9.4        1.9.3        
	_1_9_2     ("1.9.2",      "qwoomajzpb[Llp;mm[J[[Jmi"),                                                                // matches the launcher version id: 1.9.2        1.9.1        1.9          
	_1_9_pre2  ("1.9-pre2",   "qvoomajzpb[Llp;mm[J[[Jmi"),                                                                // matches the launcher version id: 1.9-pre2     1.9-pre1     16w07b       16w06a       16w05b       16w04a       16w03a       16w02a       
	_15w51b    ("15w51b",     "quonmajzpa[Llp;mm[J[[Jmi"),                                                                // matches the launcher version id: 15w51b       
	_15w50a    ("15w50a",     "qtonmajzpa[Llp;mm[J[[Jmi"),                                                                // matches the launcher version id: 15w50a       15w49b       15w47c       
	_15w46a    ("15w46a",     "qsonmajzpa[Llp;mm[J[[Jmi"),                                                                // matches the launcher version id: 15w46a       
	_15w45a    ("15w45a",     "qtoombkapb[Llq;mn[J[[Jmj"),                                                                // matches the launcher version id: 15w45a       15w44b       
	_15w43c    ("15w43c",     "qsoombkapb[Llq;mn[J[[Jmj"),                                                                // matches the launcher version id: 15w43c       
	_15w42a    ("15w42a",     "qnojlzjzow[Llp;ml[J[[Jmh"),                                                                // matches the launcher version id: 15w42a       
	_15w41b    ("15w41b",     "qmoilyjyov[Llo;mk[J[[Jmg"),                                                                // matches the launcher version id: 15w41b       
	_15w40b    ("15w40b",     "qhoelujuor[Llk;mg[J[[Jmc"),                                                                // matches the launcher version id: 15w40b       15w39c       15w38b       15w37a       
	_15w36d    ("15w36d",     "qgodltjuoq[Lll;mf[J[[Jmb"),                                                                // matches the launcher version id: 15w36d       
	_15w35e    ("15w35e",     "qeoclsjuop[Llk;me[J[[Jma"),                                                                // matches the launcher version id: 15w35e       
	_15w34d    ("15w34d",     "qdoblsjuoo[Lll;me[J[[Jma"),                                                                // matches the launcher version id: 15w34d       
	_15w33c    ("15w33c",     "qanzlrjtom[Llk;md[J[[Jlz"),                                                                // matches the launcher version id: 
	_15w32c    ("15w32c",     "pmnvlnjt[Llg;lz[J[[Jlv"),                                                                  // matches the launcher version id: 
	_15w31c    ("15w31c",     "oxnvlnjt[Llg;lz[J[[Jlv"),                                                                  // matches the launcher version id: 
	_1_8_9     ("1.8.9",      "orntlljs[Lle;lx[J[[Jlt"),                                                                  // matches the launcher version id: 1.8.9        1.8.8        1.8.7        1.8.6        1.8.5        1.8.4        
	_1_8_3     ("1.8.3",      "osnulmjt[Llf;ly[J[[Jlu"),                                                                  // matches the launcher version id: 1.8.3        1.8.2        
	_1_8_1     ("1.8.1",      "wduyrdnq[Lqu;sp[J[[Jsa"),                                                                  // matches the launcher version id: 1.8.1        
	_1_8       ("1.8",        "wbuwrcnp[Lqt;sn[J[[Jry"),                                                                  // matches the launcher version id: 1.8          
	_14w21b    ("14w21b",     "tjseoylw[Loq;qd[J[[Jpo"),                                                                  // matches the launcher version id: 
	_1_7_10    ("1.7.10",     "riqinckb[Lmt;oi[J[[Jns"),                                                                  // matches the launcher version id: 1.7.10       
	_1_7_9     ("1.7.9",      "rhqhnbkb[Lms;oh[J[[Jnr"),                                                                  // matches the launcher version id: 1.7.9        1.7.8        1.7.7        1.7.6        
	_14w02a    ("14w02a",     "qrponkki[Lnb;lv[J[[J"),                                                                    // matches the launcher version id: 
	_1_7_5     ("1.7.5",      "qfpfnbjy[Lms;lm[J[[J"),                                                                    // matches the launcher version id: 1.7.5        
	_1_7_4     ("1.7.4",      "pzozmvjs[Lmm;lg[J[[J"),                                                                    // matches the launcher version id: 1.7.4        1.7.3        
	_1_7_2     ("1.7.2",      "pvovmsjp[Lmj;ld[J[[J"),                                                                    // matches the launcher version id: 1.7.2        
	_13w39a    ("13w39a",     "npmp[Lkn;jh[J[J[J[J[J[[J"),                                                                // matches the launcher version id: 
	_13w37b    ("13w37b",     "ntmt[Lkm;jg[J[J[J[J[J[[J"),                                                                // matches the launcher version id: 
	_13w37a    ("13w37a",     "nsms[Lkl;jf[J[J[J[J[J[[J"),                                                                // matches the launcher version id: 
	_13w36b    ("13w36b",     "nkmk[Lkd;hw[J[J[J[J[J[[J"),                                                                // matches the launcher version id: 
	_13w36a    ("13w36a",     "nkmk[Lkd;hx[J[J[J[J[J[[J"),                                                                // matches the launcher version id: 
	_1_6_4     ("1.6.4",      "mvlv[Ljs;hn[J[J[J[J[J[[J"),                                                                // matches the launcher version id: 1.6.4        
	_1_6_2     ("1.6.2",      "mulu[Ljr;hm[J[J[J[J[J[[J"),                                                                // matches the launcher version id: 1.6.2        
	_1_6_1     ("1.6.1",      "msls[Ljp;hk[J[J[J[J[J[[J"),                                                                // matches the launcher version id: 1.6.1        
	_1_5_2     ("1.5.2",      "[Bbdzbdrbawemabdsbfybdvngngbeuawfbgeawvawvaxrawbbfqausbjgaycawwaraavybkcavwbjubkila"),     // matches the launcher version id: 1.5.2        
	_1_5_1     ("1.5.1",      "[Bbeabdsbawemabdtbfzbdwngngbevawfbgfawvawvaxrawbbfrausbjhaycawwaraavybkdavwbjvbkila"),     // matches the launcher version id: 1.5.1        
	_1_4_7     ("1.4.7",      "[Baywayoaaszleaypbavaysmdazratabbaatqatqaulaswbanarnbdzauwatraohastbevasrbenbezbdmbdjkh"), // matches the launcher version id: 1.4.7        1.4.6        
	_1_4_5     ("1.4.5",      "[Bayoaygaasrleayhbakaykmdazfassbapatjatjaueasobacarfbdoaupatkanzaslbekasjbecbenbdbbcykh"), // matches the launcher version id: 1.4.5        1.4.4        
	_1_4_2     ("1.4.2",      "[Baxgawyaarjkpawzayyaxclnaxxarkazcasbasbaswargaytaqabcbathascamuardbcxarbbcpbdabbobbljy"), // matches the launcher version id: 1.4.2        
	_1_3_2     ("1.3.2",      "[Batkatcaaofjbatdavbatgjwaubaogavfaovaovapnaocauwamxaxvapyaowajqanzayqanxayjaytaxkaxhik"), // matches the launcher version id: 1.3.2        
	_1_3_1     ("1.3.1",      "[Batjatbaaoejaatcavaatfjvauaaofaveaouaouapmaobauvamwaxuapxaovajpanyaypanwayiaysaxjaxgij"), // matches the launcher version id: 1.3.1        
	_1_3pre    ("1.3pre",     "acl"),                                                                                     // matches the launcher version id: 
	_12w27a    ("12w27a",     "acs"),                                                                                     // matches the launcher version id: 
	_12w25a    ("12w25a",     "acg"),                                                                                     // matches the launcher version id: 
	_12w24a    ("12w24a",     "aca"),                                                                                     // matches the launcher version id: 
	_12w22a    ("12w22a",     "ace"),                                                                                     // matches the launcher version id: 
	_12w21b    ("12w21b",     "aby"),                                                                                     // matches the launcher version id: 
	_12w21a    ("12w21a",     "abm"),                                                                                     // matches the launcher version id: 
	_12w19a    ("12w19a",     "aau"),                                                                                     // matches the launcher version id: 
	_1_2_5     ("1.2.5",      "[Bkivmaftxdlvqacqcwfcaawnlnlvpjclrckqdaiyxgplhusdakagi[J[Jalfqabv"),                       // matches the launcher version id: 1.2.5        1.2.4        
	_1_2_3     ("1.2.3",      "[Bkfviafowzlvmaclcueyaarninivlizlocipzaisxcphhrrzajugf[J[Jakzpwbt"),                       // matches the launcher version id: 1.2.3        1.2.2        1.2.1        
	_12w08a    ("12w08a",     "wj"),                                                                                      // matches the launcher version id: 
	_12w07b    ("12w07b",     "wd"),                                                                                      // matches the launcher version id: 
	_12w06a    ("12w06a",     "wb"),                                                                                      // matches the launcher version id: 
	_12w05a    ("12w05a",     "vy"),                                                                                      // matches the launcher version id: 
	_12w04a    ("12w04a",     "vu"),                                                                                      // matches the launcher version id: 
	_12w03a    ("12w03a",     "vj"),                                                                                      // matches the launcher version id: 
	_1_1       ("1.1",        "[Bjsudadrvqluhaarcqevyzmqmqugiokzcepgagqvsonhhrgahqfy[J[Jaitpdbo"),                        // matches the launcher version id: 1.1          
	_1_0       ("1.0",        "[Baesmmaijryafvdinqfdrzhabeabexexwadtnglkqdfagvkiahmhsadk[J[Jtkgkyu"),                     // matches the launcher version id: 1.0          
	_b1_9_pre6 ("b1.9-pre6",  "uk"),                                                                                      // matches the launcher version id: 
	_b1_9_pre5 ("b1.9-pre5",  "ug"),                                                                                      // matches the launcher version id: 
	_b1_9_pre4 ("b1.9-pre4",  "uh"),                                                                                      // matches the launcher version id: 
	_b1_9_pre3 ("b1.9-pre3",  "to"),                                                                                      // matches the launcher version id: 
	_b1_9_pre2 ("b1.9-pre2",  "sv"),                                                                                      // matches the launcher version id: 
	_b1_9_pre1 ("b1.9-pre1",  "sq"),                                                                                      // matches the launcher version id: 
	_b1_8_1    ("b1.8.1",     "[Bhwqpyrrviqswdbzdqurkhqrgviwbomnabjrxmafvoeacfer[J[Jaddmkbb"),                            // matches the launcher version id: b1.8.1       b1.8         
	_b1_7_3    ("b1.7.3",     "[Bobcxpyfdndclsdngrjisjdamkpxczvuuqfhvfkvyovyik[J[Jxivscg"),                               // matches the launcher version id: b1.7.3       b1.7.2       b1.7         
	_b1_6_6    ("b1.6.6",     "[Bnxcvpufbmdalodlgpjfsecymgptcxvmukffuxkryfvqih[J[Jwzvkce"),                               // matches the launcher version id: b1.6.6       b1.6.5       b1.6.4       b1.6.3       b1.6.2       b1.6.1       b1.6         
	_b1_5_01   ("b1.5_01",    "nfcpozetmcukwdfggiprfcslooycruntlextyjzxeurhv[J[Jvyulbz"),                                 // matches the launcher version id: b1.5_01      b1.5         
	_b1_4_01   ("b1.4_01",    "lncdmxebichjmcsfkhooxcfkcmwcerqqvefrkisujsbgw[J[Jtervbo"),                                 // matches the launcher version id: b1.4_01      
	_b1_4      ("b1.4",       "lncdmxebichjmcsfkhooxcfkcmwcerpqvefrkisujsagw[J[Jterubo"),                                 // matches the launcher version id: b1.4         
	_b1_3_01   ("b1.3_01",    "kybymidthccizcnfbhfoicbjpmhbzqfdxquigtmrhgn[J[Jrbbk"),                                     // matches the launcher version id: b1.3_01      
	_b1_3b     ("b1.3b",      "kybymidthccizcnfbhfoicbjpmhbzqgdxqvigtnrign[J[Jrcbk"),                                     // matches the launcher version id: b1.3b        
	_b1_2_02   ("b1.2_02",    "kbbvlmdnhbzcjesgsnhbyiwllbwpedrprhqsgqega[J[Jpybj"),                                       // matches the launcher version id: b1.2_02      b1.2_01      b1.2         
	_b1_1_02   ("b1.1_02",    "jjboksddfbsccehgemjbrifkrbpobdhonhbqvoyfo[J[Joubc"),                                       // matches the launcher version id: b1.1_02      b1.1_01      
	_b1_0_2    ("b1.0.2",     "jibokrddfbscceggdmibriekqbpoadhomhaquoxfn[J[Jotbc"),                                       // matches the launcher version id: b1.0.2       b1.0_01      b1.0         
	_a1_2_6    ("a1.2.6",     "ivbmkccyfbqbzeafulsbphukbbnnldcnxgqqgoiff[J[Joeba"),                                       // matches the launcher version id: a1.2.6       
	_a1_2_5    ("a1.2.5",     "iubmkbcxfbqbydzftlrbphtkabnnkdbnwgpqfohfe[J[Jodba"),                                       // matches the launcher version id: a1.2.5       a1.2.4_01    
	_a1_2_3_04 ("a1.2.3_04",  "iubmkbcxfbqbydzftlqbphtkabnnjdbnvgpqeogfe[J[Jocba"),                                       // matches the launcher version id: a1.2.3_04    a1.2.3_02    a1.2.3_01    a1.2.3       
	_a1_2_2b   ("a1.2.2b",    "isbmjycwfbqbydyfrlnbphrjxbnngdansgnqbodfd[J[Jnzba"),                                       // matches the launcher version id: a1.2.2b      a1.2.2a      
	_a1_2_1_01 ("a1.2.1_01",  "imbkjrcudbobwdufmlgbnhmjqblmzcynlgiptnv[J[Jnray"),                                         // matches the launcher version id: a1.2.1_01    a1.2.1       a1.2.0_02    a1.2.0_01    a1.2.0       
	_a1_1_2_01 ("a1.1.2_01",  "hqbeircnebibqdleykdbhgriqbflucrmffrofmp[Jmlat"),                                           // matches the launcher version id: a1.1.2_01    a1.1.2       
	_a1_1_0    ("a1.1.0",     "hqbeircnebibqdleykdbhgriqbflucrmffroemo[Jmlat"),                                           // matches the launcher version id: a1.1.0       
	_a1_0_17_04("a1.0.17_04", "hpbdiqcmebhbpdkexkbbggqipbeltcqmdfqobmm[Jmjar"),                                           // matches the launcher version id: a1.0.17_04   a1.0.17_02   
	_a1_0_16   ("a1.0.16",    "hgazihcjebebmdferjtbdgiigbblkcnlvfinrmd[Jmbap"),                                           // matches the launcher version id: a1.0.16      
	_a1_0_15   ("a1.0.15",    "hfazigcjebebmdferjsbdgiifbbljcnlufinqmc[Jmaap"),                                           // matches the launcher version id: a1.0.15      
	_a1_0_14   ("a1.0.14",    "hcazidcjebebmdfeqjpbdghicbblfcnlpfhnmly[Jlwap"),                                           // matches the launcher version id: a1.0.14      
	_a1_0_11   ("a1.0.11",    "haaziacjebebmddenjlbdgfhzbbkzcnljfenels[Jlqap");                                           // matches the launcher version id: a1.0.11      
	// @formatter:on

	@NotNull
	public static RecognisedVersion from(URLClassLoader classLoader) throws ClassNotFoundException {
		return from(generateMagicString(classLoader));
	}

	@NotNull
	public static String generateMagicString(URLClassLoader classLoader) throws ClassNotFoundException {
		return generateMagicString(getMainClassFields(classLoader));
	}

	@NotNull
	private static Field[] getMainClassFields(URLClassLoader classLoader) throws ClassNotFoundException {
		try {
			if (classLoader.findResource(CLIENT_CLASS_RESOURCE) != null) {
				return classLoader.loadClass(CLIENT_CLASS).getDeclaredFields();
			} else if (classLoader.findResource(SERVER_CLASS_RESOURCE) != null) {
				return classLoader.loadClass(SERVER_CLASS).getDeclaredFields();
			} else {
				throw new ClassNotFoundException("unable to find the main class in the given jar file");
			}
		} catch (NoClassDefFoundError e) {
			throw new ClassNotFoundException("error while loading main class; are some libraries missing?", e);
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
		AmidstLogger.info("Unable to recognise Minecraft Version with the magic string \"" + magicString + "\".");
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
		AmidstLogger.info("Unable to recognise Minecraft Version with the name \"" + name + "\".");
		return RecognisedVersion.UNKNOWN;
	}

	private static void logFound(RecognisedVersion recognisedVersion) {
		AmidstLogger.info(
				"Recognised Minecraft Version " + recognisedVersion.name + " with the magic string \""
						+ recognisedVersion.magicString + "\".");
	}

	public static boolean isNewerOrEqualTo(RecognisedVersion version1, RecognisedVersion version2) {
		return compareNewerIsLower(version1, version2) <= 0;
	}

	public static boolean isNewer(RecognisedVersion version1, RecognisedVersion version2) {
		return compareNewerIsLower(version1, version2) < 0;
	}

	public static boolean isOlderOrEqualTo(RecognisedVersion version1, RecognisedVersion version2) {
		return compareNewerIsLower(version1, version2) >= 0;
	}

	public static boolean isOlder(RecognisedVersion version1, RecognisedVersion version2) {
		return compareNewerIsLower(version1, version2) > 0;
	}

	public static int compareNewerIsGreater(RecognisedVersion version1, RecognisedVersion version2) {
		return compareNewerIsLower(version2, version1);
	}

	public static int compareNewerIsLower(RecognisedVersion version1, RecognisedVersion version2) {
		Objects.requireNonNull(version1);
		Objects.requireNonNull(version2);
		return version1.ordinal() - version2.ordinal();
	}

	public static Map<String, RecognisedVersion> generateNameToRecognisedVersionMap() {
		Map<String, RecognisedVersion> result = new LinkedHashMap<>();
		for (RecognisedVersion recognisedVersion : RecognisedVersion.values()) {
			if (result.containsKey(recognisedVersion.getName())) {
				RecognisedVersion colliding = result.get(recognisedVersion.getName());
				throw new RuntimeException(
						"name collision for the recognised versions " + recognisedVersion.getName() + " and "
								+ colliding.getName());
			} else {
				result.put(recognisedVersion.getName(), recognisedVersion);
			}
		}
		return result;
	}

	public static Map<String, RecognisedVersion> generateMagicStringToRecognisedVersionMap() {
		Map<String, RecognisedVersion> result = new LinkedHashMap<>();
		for (RecognisedVersion recognisedVersion : RecognisedVersion.values()) {
			if (result.containsKey(recognisedVersion.getMagicString())) {
				RecognisedVersion colliding = result.get(recognisedVersion.getMagicString());
				throw new RuntimeException(
						"magic string collision for the recognised versions " + recognisedVersion.getName() + " and "
								+ colliding.getName());
			} else {
				result.put(recognisedVersion.getMagicString(), recognisedVersion);
			}
		}
		return result;
	}

	public static String createEnumIdentifier(String name) {
		return "_" + name.replaceAll("[^a-zA-Z0-9]", "_");
	}

	private static final String CLIENT_CLASS_RESOURCE = "net/minecraft/client/Minecraft.class";
	private static final String CLIENT_CLASS = "net.minecraft.client.Minecraft";
	private static final String SERVER_CLASS_RESOURCE = "net/minecraft/server/MinecraftServer.class";
	private static final String SERVER_CLASS = "net.minecraft.server.MinecraftServer";

	private final boolean isKnown;
	private final String name;
	private final String magicString;

	private RecognisedVersion() {
		this.isKnown = false;
		this.name = "UNKNOWN";
		this.magicString = null;
	}

	private RecognisedVersion(String name, String magicString) {
		this.isKnown = true;
		this.name = name;
		this.magicString = magicString;
	}

	public boolean isKnown() {
		return isKnown;
	}

	public String getName() {
		return name;
	}

	public String getMagicString() {
		return magicString;
	}
}
