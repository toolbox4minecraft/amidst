package MoF;


import java.awt.Color;

public class Biome {
	public static Color[] colors = new Color[] { 
		new Color(13 ,51 ,219), //Ocean;
		new Color(104,222,104), //Plains;
		new Color(226,242,131), //Desert;
		
		new Color(171,105,34 ), //Extreme Hills;
		new Color(40 ,181,22 ), //Forest;
		new Color(32 ,110,22 ), //Taiga;
		
		new Color(108,158,79), //Swampland;
		new Color(14,127,227), //River;
		
		new Color(143,25 ,10 ), //Hell;
		new Color(209,233,235), //Sky;
		
		new Color(70 ,104,199), //FrozenOcean;
		new Color(171,216,255), //FrozenRiver;
		new Color(156,214,190), //Ice Plains;
		new Color(151,162,130), //Ice Mountains;
		
		new Color(219,196,164), //MushroomIsland;
		new Color(242,216,179), //MushroomIslandShore;
		
		new Color(255,254,189), //Beach
		new Color(230,202, 78), //DesertHills
		new Color(89 ,176, 32), //ForestHills
		new Color(66 ,110, 22), //TaigaHills
		new Color(186,159, 39),  //Extreme Hills Edge
		
		new Color(26 ,87 ,34 ),
		new Color(73 ,105,33 )
	};
	public static final Biome[] a = new Biome[256];
	public static final Biome b = new Biome(0).b(112).a("Ocean").b(-1.0F, 0.4F);
	public static final Biome c = new Biome(1).b(9286496).a("Plains").a(0.8F, 0.4F);
	public static final Biome d = new Biome(2).b(16421912).a("Desert").g().a(2.0F, 0.0F).b(0.1F, 0.2F);

	public static final Biome e = new Biome(3).b(6316128).a("Extreme Hills").b(0.2F, 1.8F).a(0.2F, 0.3F);
	public static final Biome f = new Biome(4).b(353825).a("Forest").a(5159473).a(0.7F, 0.8F);
	public static final Biome g = new Biome(5).b(747097).a("Taiga").a(5159473).a(0.3F, 0.8F).b(0.1F, 0.4F);

	public static final Biome h = new Biome(6).b(522674).a("Swampland").a(9154376).b(-0.2F, 0.1F).a(0.8F, 0.9F);
	public static final Biome i = new Biome(7).b(255).a("River").b(-0.5F, 0.0F);

	public static final Biome j = new Biome(8).b(16711680).a("Hell").g().a(2.0F, 0.0F);
	public static final Biome k = new Biome(9).b(8421631).a("Sky").g();

	public static final Biome l = new Biome(10).b(9474208).a("FrozenOcean").b(-1.0F, 0.5F).a(0.0F, 0.5F);
	public static final Biome m = new Biome(11).b(10526975).a("FrozenRiver").b(-0.5F, 0.0F).a(0.0F, 0.5F);
	public static final Biome n = new Biome(12).b(16777215).a("Ice Plains").a(0.0F, 0.5F);
	public static final Biome o = new Biome(13).b(10526880).a("Ice Mountains").b(0.2F, 1.8F).a(0.0F, 0.5F);

	public static final Biome p = new Biome(14).b(16711935).a("MushroomIsland").a(0.9F, 1.0F).b(0.2F, 1.0F);
	public static final Biome q = new Biome(15).b(10486015).a("MushroomIslandShore").a(0.9F, 1.0F).b(-1.0F, 0.1F);
	
	public static final Biome r = new Biome(16).b(16440917).a("Beach").a(0.8F, 0.4F).b(0.0F, 0.1F);
	public static final Biome s = new Biome(17).b(13786898).a("DesertHills").g().a(2.0F, 0.0F).b(0.2F, 0.7F);
	public static final Biome t = new Biome(18).b(2250012).a("ForestHills").a(5159473).a(0.7F, 0.8F).b(0.2F, 0.6F);
	public static final Biome u = new Biome(19).b(1456435).a("TaigaHills").a(5159473).a(0.05F, 0.8F).b(0.2F, 0.7F);
	public static final Biome v = new Biome(20).b(7501978).a("Extreme Edge").b(0.2F, 0.8F).a(0.2F, 0.3F);
	public static final Biome w = new Biome(21).b(5470985).a("Jungle").a(5470985).a(1.2F, 0.9F).b(0.2F, 0.4F);
	public static final Biome x = new Biome(22).b(2900485).a("JungleHills").a(5470985).a(1.2F, 0.9F).b(1.8F, 0.2F);
	  
	public String name;
	
	public Biome(int number) {
		a[number] = this;
	}
	public Biome b(int l) {
		return this;
	}
	public Biome a(String name) {
		this.name = name;
		return this;
	}
	
	public Biome a(float f1, float f2) {
		return this;
	}
	public Biome b(float f1, float f2) {
		return this;
	}
	public Biome g() {
		return this;
	}
	public Biome a(int a) {
		return this;
	}
}
