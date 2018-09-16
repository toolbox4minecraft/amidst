package amidst.minetest.world.mapgen;

import javax.vecmath.Point2d;

import java.util.Arrays;
import java.util.LongSummaryStatistics;

import amidst.logging.AmidstLogger;

/**
 * This class uses pre-generated data, so can only provide histogram
 * information for climate noise settings similar to Minetest's default settings.
 */
public class ClimateHistogram implements IHistogram2D {

	protected static final float DEFAULT_SCALE = 50;
	protected static final float DEFAULT_OFFSET = 50;
	protected static final float DEFAULT_BLEND_SCALE = 50;
	protected static final float DEFAULT_BLEND_OFFSET = 50;

	float scaleAdj = 1;
	float offsetAdj = 0;

	double dataSampleCount = 4293525600d * 6; // data is sum of 6 full-world samples
	int dataSampleOffset = 40; // the first value in sampledHistogram_Heat is for heat of -40
	// Bins: 191, range: -40 to 150 (inclusive)
	int[] sampledHistogram_Heat     = new int[] {0, 0, 23, 896, 5273, 18130, 35423, 56932, 85873, 144492, 227887, 330382, 441791, 575273, 785758, 1104880, 1514226, 2008173, 2580628, 3270470, 4070347, 5010230, 6233724, 7665743, 9231443, 11032009, 13054672, 15159146, 17585779, 20431953, 23539132, 26777647, 30049708, 33634225, 37557763, 41702212, 46135583, 50960488, 56066254, 61483250, 67471538, 74036663, 81075916, 88392031, 95671758, 103028198, 110423079, 118133581, 126230683, 134525547, 143089365, 151816479, 160572896, 169173433, 177480719, 185603411, 193673745, 201819880, 210125434, 218400248, 226576667, 234675120, 242660227, 250393165, 257641763, 264310278, 271052198, 277926795, 284991256, 291581211, 297501193, 303087896, 308619131, 313777446, 318058063, 322212943, 326758548, 331475102, 335722459, 338714730, 340704226, 342259956, 344228530, 346336570, 348439520, 350388503, 352166050, 353596154, 355021883, 355344601, 354627956, 353920683, 353523974, 353330547, 353024545, 352813804, 352102001, 350967293, 349073000, 346178826, 343181131, 340158957, 336577363, 332492808, 328136486, 323579081, 319083180, 314922134, 310380729, 305265626, 299550379, 293823815, 287790026, 282008407, 276140320, 269559012, 262131740, 254605986, 246951793, 239251561, 230994017, 222093705, 213071865, 204219912, 195608147, 187251811, 178964522, 170779498, 162561496, 154205050, 145761473, 137219761, 128986832, 121138771, 113558434, 106120928, 98743038, 91314126, 83938278, 77103922, 70855499, 64921287, 59244041, 53844544, 48822704, 43909928, 39348668, 35160583, 31266663, 27623387, 24258537, 21091241, 18212811, 15617700, 13237251, 11059423, 9228541, 7789154, 6607381, 5466830, 4340725, 3364753, 2610074, 2004609, 1486276, 1069920, 750199, 518992, 357847, 267761, 204042, 135284, 70635, 31506, 16563, 14437, 12363, 6219, 1714, 153, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	int[] sampledHistogram_Humidity = new int[] {0, 0, 31, 498, 2506, 4845, 8185, 21468, 59847, 122732, 193470, 286771, 438554, 610779, 784774, 1018080, 1351444, 1762206, 2271350, 2871968, 3617679, 4542319, 5722714, 7087672, 8612730, 10322878, 12252077, 14556399, 17083672, 19781372, 22738287, 25903300, 29414149, 33280369, 37456084, 41913668, 46847975, 52063820, 57365491, 62720169, 68287651, 74188447, 80622299, 87458209, 94297778, 101540429, 109407209, 117764134, 126033776, 134106841, 142075245, 150160062, 158481411, 167046905, 175806629, 184285718, 192691920, 201123443, 209651797, 218157100, 226149374, 234230435, 242366234, 249849182, 257017437, 264178652, 271211835, 277958877, 284189278, 290172767, 296085575, 301995199, 307977042, 313261332, 317868477, 322201082, 326388271, 330472979, 334532622, 338385357, 341846286, 344688035, 346974698, 349233925, 351053588, 352480803, 353676423, 354853005, 355571248, 355946018, 356187678, 355726165, 354284344, 352881704, 351909613, 350696269, 348847862, 346809607, 345187333, 343247921, 340859439, 338049103, 335025305, 331513640, 327718695, 323952706, 320112005, 316183516, 311617470, 306291881, 300848532, 295309226, 289332030, 282903833, 276250749, 269695133, 262942145, 255893757, 248411772, 240532829, 232446716, 224124693, 215607785, 206986511, 198415028, 190161781, 182277837, 174044450, 165348412, 156451475, 147683121, 139285547, 131231683, 123268099, 115122762, 107128906, 99350517, 91804617, 84384451, 77313619, 70743606, 64517248, 58541831, 52903662, 47555609, 42575213, 38003184, 33839429, 30012843, 26495082, 23211478, 20191059, 17507769, 14994437, 12727017, 10648031, 8847912, 7389304, 6184783, 5132563, 4108008, 3175730, 2394676, 1814862, 1395580, 1096984, 856680, 630982, 413928, 233980, 123637, 63560, 37317, 19663, 8361, 1825, 124, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	// See the code in the constructor if you wish to recalculate frequencyAtPercentileTable
	/** A pre-calculation table for percentiles 1 to 100, in 1 percentile increments */
	double[] frequencyAtPercentileTable = new double[] {4.288849623506293E-6, 7.849271137128854E-6, 1.0935481111330147E-5, 1.4125178764733622E-5, 1.710500033323479E-5, 1.9993831214913553E-5, 2.2662552870656077E-5, 2.534060066104869E-5, 2.802187248207058E-5, 3.080643823350056E-5, 3.33861856758062E-5, 3.6068277994214394E-5, 3.862936637606668E-5, 4.095044456557262E-5, 4.3636595467822336E-5, 4.607358640977918E-5, 4.810443669853268E-5, 5.0837165362693565E-5, 5.296404267788116E-5, 5.5325852990715865E-5, 5.7679007766327825E-5, 5.983630193800486E-5, 6.235772026254966E-5, 6.430623785962591E-5, 6.669710263491633E-5, 6.855912078302344E-5, 7.113548845931662E-5, 7.295088808887063E-5, 7.542181961574365E-5, 7.713810513240832E-5, 7.941207236464512E-5, 8.162108120760272E-5, 8.386222016118836E-5, 8.569039655815985E-5, 8.746946795713887E-5, 8.971102202243205E-5, 9.113804797595727E-5, 9.331997156194227E-5, 9.538586351908653E-5, 9.734525496179133E-5, 9.898022955022616E-5, 1.0102734313517474E-4, 1.0297431176541194E-4, 1.0459706284256709E-4, 1.0668836696809265E-4, 1.0855615168551473E-4, 1.1007621103277895E-4, 1.1237809589729694E-4, 1.1365508528516895E-4, 1.1581159959767767E-4, 1.1773346682435759E-4, 1.1928685427735908E-4, 1.2109096765312022E-4, 1.2266224460033028E-4, 1.2460344924158565E-4, 1.2639392703246802E-4, 1.2792145643612953E-4, 1.2983742624993134E-4, 1.3126086274433714E-4, 1.3318981239435993E-4, 1.3466113206470656E-4, 1.3628693214058266E-4, 1.3771262506116956E-4, 1.3927642904768147E-4, 1.4088517346529805E-4, 1.424651674401106E-4, 1.4415914818743247E-4, 1.4584085961605385E-4, 1.4707407523891278E-4, 1.48740812877974E-4, 1.4998644757789717E-4, 1.5182775373866035E-4, 1.5312561398223614E-4, 1.5476944096761824E-4, 1.564096559253218E-4, 1.5747584559976162E-4, 1.592317541086737E-4, 1.6055180720478152E-4, 1.6209298336552515E-4, 1.6335932626023207E-4, 1.6501788334796102E-4, 1.6627703968019814E-4, 1.6756546371099305E-4, 1.6888984543785755E-4, 1.7039589568960874E-4, 1.717707149296396E-4, 1.7299179342349547E-4, 1.744319494242184E-4, 1.7608213347237582E-4, 1.7744060109174724E-4, 1.7877565932783E-4, 1.8007180142527902E-4, 1.8136873249310507E-4, 1.8273003575625964E-4, 1.8385915609410723E-4, 1.8507326100357174E-4, 1.8627491892689703E-4, 1.8740872113945042E-4, 1.8879158216656317E-4, 1.9033705137062054E-4};
	/** A pre-calculation table for percentiles 0 to 0.99 in 0.01 percentile increments */
	double[] frequencyAtPerdimileTable  = new double[] {0.0, 7.906411381395254E-8, 1.4281584607842585E-7, 2.0367198773824541E-7, 2.579481623074614E-7, 3.103739993944631E-7, 3.663211869054443E-7, 4.169483003809781E-7, 4.635227228946929E-7, 5.288508873488246E-7, 5.686991889665327E-7, 6.204826202407989E-7, 6.812762234709958E-7, 7.263347560698961E-7, 7.657426809358803E-7, 8.193734165334142E-7, 8.656717598377384E-7, 9.267166497130238E-7, 9.687699186503285E-7, 1.0100613846028922E-6, 1.040421908192894E-6, 1.0981246626722591E-6, 1.1626975068704957E-6, 1.2072828789096895E-6, 1.245628837090483E-6, 1.2925330631771625E-6, 1.3136720582621303E-6, 1.3539419032545604E-6, 1.4261585444809766E-6, 1.4613406586174258E-6, 1.5325176657436087E-6, 1.5737994074503633E-6, 1.6102602927831794E-6, 1.6548696775342068E-6, 1.6814410653583067E-6, 1.7109022657620896E-6, 1.739729245372117E-6, 1.818909640506737E-6, 1.860652162367776E-6, 1.918256413753668E-6, 1.950305933579097E-6, 2.0142046144063015E-6, 2.0414075345970236E-6, 2.078387302258539E-6, 2.1271279829810187E-6, 2.149899817465148E-6, 2.1755325291439493E-6, 2.204795837375724E-6, 2.2850671080176522E-6, 2.32281076679128E-6, 2.373852151766557E-6, 2.420014414654376E-6, 2.4519443333869577E-6, 2.4886672811746097E-6, 2.5482357788527957E-6, 2.5826040900445513E-6, 2.62377928182969E-6, 2.6443912878365593E-6, 2.6742844225227714E-6, 2.6967536393142994E-6, 2.729072454192572E-6, 2.7697985570250263E-6, 2.8357136937141744E-6, 2.886882959408015E-6, 2.9324497121704535E-6, 2.973268481768577E-6, 3.010129601199772E-6, 3.0440263924820617E-6, 3.097221433350364E-6, 3.153942913939244E-6, 3.1693172695523055E-6, 3.208017787514919E-6, 3.2466586058243742E-6, 3.2873911331250026E-6, 3.300640985971264E-6, 3.3135005090680394E-6, 3.360507790578272E-6, 3.4192720091466502E-6, 3.4414606133149838E-6, 3.480699054050278E-6, 3.5502316470557827E-6, 3.608426792734956E-6, 3.6396339028551223E-6, 3.6863827940008945E-6, 3.7040534454740007E-6, 3.7410242395545514E-6, 3.7825944022123806E-6, 3.847435398836006E-6, 3.873875563025558E-6, 3.9031983332453956E-6, 3.931204415840417E-6, 3.9598830516054155E-6, 3.975995856789808E-6, 3.998365240336164E-6, 4.039172655337482E-6, 4.0632862055249795E-6, 4.0990893986543585E-6, 4.183983009230883E-6, 4.216663091722728E-6, 4.245830038292284E-6};

	// See the code in the constructor if you wish to recalculate processedHistogram_Heat
	long[] processedHistogram_Heat       = new long[] {0, 153, 1768, 7613, 20266, 39237, 68532, 129569, 253672, 466068, 749036, 1118894, 1652120, 2336026, 3177411, 4289864, 5747526, 7589850, 9856728, 12682921, 16136759, 20151942, 24748602, 29931873, 35920626, 43062341, 51271017, 60327682, 70390031, 81495625, 93747434, 106799416, 120743363, 135914606, 152365699, 170101021, 189361871, 209772514, 231217617, 253641954, 277358294, 302642651, 330020944, 358968983, 388063091, 417818461, 448511484, 480304585, 512482974, 545137696, 578609204, 612633066, 646964215, 681044286, 714529707, 747302721, 780388840, 814149746, 848456881, 882775746, 916166774, 948689945, 980390026, 1010742090, 1039733085, 1067743075, 1094655102, 1120797912, 1146302590, 1170887019, 1193985679, 1216640602, 1238594372, 1258144428, 1275121725, 1291945812, 1309002000, 1325954529, 1341857749, 1355308147, 1366591082, 1376374738, 1385463561, 1393347395, 1400442971, 1406379379, 1410776631, 1414661410, 1418401449, 1420937467, 1421631268, 1420937467, 1418401449, 1414661410, 1410776631, 1406379379, 1400442971, 1393347395, 1385463561, 1376374738, 1366591082, 1355308147, 1341857749, 1325954529, 1309002000, 1291945812, 1275121725, 1258144428, 1238594372, 1216640602, 1193985679, 1170887019, 1146302590, 1120797912, 1094655102, 1067743075, 1039733085, 1010742090, 980390026, 948689945, 916166774, 882775746, 848456881, 814149746, 780388840, 747302721, 714529707, 681044286, 646964215, 612633066, 578609204, 545137696, 512482974, 480304585, 448511484, 417818461, 388063091, 358968983, 330020944, 302642651, 277358294, 253641954, 231217617, 209772514, 189361871, 170101021, 152365699, 135914606, 120743363, 106799416, 93747434, 81495625, 70390031, 60327682, 51271017, 43062341, 35920626, 29931873, 24748602, 20151942, 16136759, 12682921, 9856728, 7589850, 5747526, 4289864, 3177411, 2336026, 1652120, 1118894, 749036, 466068, 253672, 129569, 68532, 39237, 20266, 7613, 1768, 153, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	double processedDataSampleCount_Heat = dataSampleCount * 4; // we doubled the sample data by mirroring it horizontally, and doubled that by mirroring vertically

	// Heat and humidity use the same noise function, so copy the processed values to humidity 
	long[] processedHistogram_Humidity           = processedHistogram_Heat;	
	double processedDataSampleCount_Humidity     = processedDataSampleCount_Heat;
		
	int    processedDataSampleOffset_Heat        = dataSampleOffset;
	int    processedDataSampleOffset_Humidity    = dataSampleOffset;
	
	Point2d sampleMean = null;


	public double FrequencyOfTemperature(float temperature) {
		int index = Math.round((temperature / scaleAdj) + offsetAdj) + processedDataSampleOffset_Heat;

		if (index >= 0 && index < processedHistogram_Heat.length) {
			//return sampledHistogram_Heat[index] / dataSampleCount;
			return processedHistogram_Heat[index] / processedDataSampleCount_Heat;
		} else {
			// Our sampling range covers all non-zero values, so the answer to return is zero,
			// however NaN should help highlight if range bugs are occurring elsewhere in the app.
			return Double.NaN;
		}
	}

	public double FrequencyOfHumidity(float humidity) {
		int index = Math.round((humidity / scaleAdj) + offsetAdj) + processedDataSampleOffset_Humidity;

		if (index >= 0 && index < processedHistogram_Humidity.length) {
			//return sampledHistogram_Humidity[index] / dataSampleCount;
			return processedHistogram_Humidity[index] / processedDataSampleCount_Humidity;
		} else {
			// Our sampling range covers all non-zero values, so the answer to return is zero,
			// however NaN should help highlight if range bugs are occurring elsewhere in the app.
			return Double.NaN;
		}
	}

	/**
	 * Returns a value between 0 and 1 which represents how frequently a
	 * block should have a climate of (temperature, humidity).
	 * (uses linear interpolation)
	 */
	@Override
	public double frequencyOfOccurance(float temperature, float humidity) {

		// Linearly-interpolate instead of rounding temperature and humidity
		float temperature_floor = (float)Math.floor(temperature);
		float humidity_floor = (float)Math.floor(humidity);

		double freq_floor = FrequencyOfTemperature(temperature_floor);
		double freq_ceil  = FrequencyOfTemperature((float)Math.ceil(temperature));

		if (!(freq_floor > 0 && freq_ceil > 0) && FrequencyOfTemperature(temperature) == 0) return 0; // Don't interpolate the frequency if it's a value that cannot ever happen (the first two checks are just an optimization to prevent unnecessarily invoking FrequencyOfTemperature)
		double temperature_interp = freq_floor + (freq_ceil - freq_floor) * (temperature - temperature_floor);

		freq_floor = FrequencyOfHumidity(humidity_floor);
		freq_ceil  = FrequencyOfHumidity((float)Math.ceil(humidity));

		if (!(freq_floor > 0 && freq_ceil > 0) && FrequencyOfHumidity(humidity) == 0) return 0; // Don't interpolate the frequency if it's a value that cannot ever happen (the first two checks are just an optimization to prevent unnecessarily invoking FrequencyOfTemperature)
		double humidity_interp = freq_floor + (freq_ceil - freq_floor) * (humidity - humidity_floor);

		return temperature_interp * humidity_interp;
	}

	/**
	 * Returns the "FrequencyOfOccurance" value at which 'percentile' amount of
	 * samples will fall beneath.
	 * So if percentile was 10, then a value between 0 and 1 would be returned such
	 * that 10% of results from FrequencyOfOccurance() would fall below it.
	 */
	@Override
	public double frequencyAtPercentile(double percentile) {
		// use the lookup-table
		if (percentile >= 0.995) {
			return frequencyAtPercentileTable[(int)Math.round(Math.max(1, Math.min(100, percentile))) - 1];
		} else {
			return frequencyAtPerdimileTable[(int)Math.round(Math.max(0, Math.min(100, percentile * 100)))];
		}
	}

	@Override
	public Point2d getSampleMean() {
		// We know the true mean of the noise, so use that.
		if (sampleMean == null) sampleMean = new Point2d(DEFAULT_OFFSET + offsetAdj, DEFAULT_OFFSET + offsetAdj);
		return sampleMean;
	}

	/**
	 * The data from the 3 octaves of Perlin noise plus 2 octaves blending is
	 * *not* of a Normal Distribution, and any math that assumes it is will fail.
	 * This function calculates what the the mean the standard deviation
	 * would be if it was a normal distribution.
	 * (Not used, but left in for reference)
	 *
	 * Spoiler: it's ~26 (perhaps exactly 26)
	 */
	private double getStandardDeviation(int[] histogram) {

		// Work out the Mean (we actually know it should be 50)
		double total = 0;
		long sampleCount = 0;
		for (int i = histogram.length - 1; i >= 0; i--) {
			long sample = i - dataSampleOffset;
			total += (sample * histogram[i]);
			sampleCount += histogram[i];
		}
		double mean = total / (double)sampleCount;

		// Then for each number: subtract the Mean and square the result
		double squaredDifferenceTotal = 0;
		for (int i = 0; i < histogram.length; i++) {
			long sample = i - dataSampleOffset;
			double squaredDifference = (sample - mean) * (sample - mean);
			squaredDifferenceTotal += squaredDifference * histogram[i];
		}

		// Then work out the mean of those squared differences.
		double meanOfSquaredDifference = squaredDifferenceTotal / ((double)sampleCount - 1); // -1 for Bessel's correction

		// Take the square root of that and we are done!
		return Math.sqrt(meanOfSquaredDifference);
	}


	/**
	 * Returns the percentage of sample locations/bins that occur at or below the given frequency_of_occurance
	 * @see PercentileAtFrequency_Processed
	 */
	private double PercentileAtFrequency_Sampled(double frequency_of_occurance) {

		double probabilityUnderFrequency = 0;
		for (int y = sampledHistogram_Humidity.length - 1; y >= 0; y--) {
			for (int x = sampledHistogram_Heat.length - 1; x >= 0; x--) {
				double probability = (sampledHistogram_Heat[x] / dataSampleCount) * (sampledHistogram_Humidity[y] / dataSampleCount);
				if (probability <= frequency_of_occurance) {
					probabilityUnderFrequency += probability;
				}
			}
		}
		return probabilityUnderFrequency * 100;
	}

	/**
	 * Returns the percentage of sample locations/bins that occur at or below the given frequency_of_occurance,
	 * however it uses the processed data, rather than the real samples.
	 * @see PercentileAtFrequency_Sampled
	 */
	private double PercentileAtFrequency_Processed(double frequency_of_occurance) {

		double probabilityUnderFrequency = 0;
		for (int y = processedHistogram_Humidity.length - 1; y >= 0; y--) {
			for (int x = processedHistogram_Heat.length - 1; x >= 0; x--) {
				double probability = (processedHistogram_Heat[x] / processedDataSampleCount_Heat) * (processedHistogram_Humidity[y] / processedDataSampleCount_Humidity);
				if (probability <= frequency_of_occurance) {
					probabilityUnderFrequency += probability;
				}
			}
		}
		return probabilityUnderFrequency * 100;
	}

	/**
	 * Performs a logrithmic search for a frequency_of_occurance which will split the population of
	 * samples along the given percentile line.
	 * It uses the processed data, rather than the real samples.
	 * It's unlikely to find an exact match - such a value might not exist, but it should get close.
	 * @param percentile - should be between 0 and 100
	 * @param upper_freq_bound - note this is an exclusive upper bound, the search alg will never try it, though it should get close enough to not matter.
	 * @param lower_freq_bound - note this is an exclusive lower bound, the search alg will never try it, though it should get close enough to not matter.
	 * @param search_depth - recurse depth, and how many calls to FrequencyAtPercentile() will be made.
	 */
	private double SearchForFrequencyAtPercentile(double percentile, double upper_freq_bound, double lower_freq_bound, int search_depth) {

		double midPointFrequency = lower_freq_bound + ((upper_freq_bound - lower_freq_bound) / 2.0d);
		double midpointPercentile = PercentileAtFrequency_Processed(midPointFrequency);
		if (search_depth == 0) {
			AmidstLogger.warn("Could not find exact FrequencyAtPercentile for " + percentile + ", returning " + midPointFrequency);
			return midPointFrequency;
		}
		if (midpointPercentile < percentile) {
			return SearchForFrequencyAtPercentile(percentile, upper_freq_bound, midPointFrequency, search_depth - 1);
		} else if (midpointPercentile > percentile) {
			return SearchForFrequencyAtPercentile(percentile, midPointFrequency, lower_freq_bound, search_depth - 1);
		} else {
			return midPointFrequency;
		}
	}

	/**
	 * Processes the sample counts from sampledHistogram_Heat & sampledHistogram_Humidity and
	 * uses what we know about the climate noise algorithm (the center and symmetry) to create
	 * data likely to be closer to the true distribution.
	 *
	 * This matters if you want quartile lines to look smooth.
	 *
	 * The processed data is written to processedHistogram_Heat/processedHistogram_Humidity
	 */
	private void processSamples() {

		int center = Math.round(DEFAULT_OFFSET + offsetAdj);
		int distFromCenter = 0;
		int maxDistFromCenter = Math.max(center - dataSampleOffset, Math.max(sampledHistogram_Heat.length - center - 1, sampledHistogram_Humidity.length - center - 1));
		while (distFromCenter <= maxDistFromCenter) {
			int indexLeft  = (center + dataSampleOffset) - distFromCenter;
			int indexRight = (center + dataSampleOffset) + distFromCenter;

			if (indexRight > 0 && indexRight < processedHistogram_Heat.length) {
				processedHistogram_Heat[indexRight] = (indexRight < sampledHistogram_Heat.length) ? sampledHistogram_Heat[indexRight] : 0;
				if (indexRight < sampledHistogram_Humidity.length) processedHistogram_Heat[indexRight] += sampledHistogram_Humidity[indexRight];

				if (indexLeft > 0) {
					processedHistogram_Heat[indexRight] += sampledHistogram_Heat[indexLeft];
					processedHistogram_Heat[indexRight] += sampledHistogram_Humidity[indexLeft];

					// Mirror the processed value to the left side of processedHistogram_Heat
					processedHistogram_Heat[indexLeft] = processedHistogram_Heat[indexRight];
				}
			}
			distFromCenter++;
		}
		// each value in processedHistogram_Heat is now the left+right of both heat+humidy added together
		processedDataSampleCount_Heat = dataSampleCount * 4;

		AmidstLogger.info("processedHistogram_Heat: " + Arrays.toString(processedHistogram_Heat));
	}

	/**
	 * Caches results from SearchForFrequencyAtPercentile() into frequencyAtPercentileTable and
	 * frequencyAtPerdimileTable.
	 * @see SearchForFrequencyAtPercentile
	 */
	protected void calculatePercentileTables(int search_depth) {
		LongSummaryStatistics heatStats     = Arrays.stream(processedHistogram_Heat).summaryStatistics();
		LongSummaryStatistics humidityStats = Arrays.stream(processedHistogram_Humidity).summaryStatistics();

		frequencyAtPerdimileTable[0]   = (heatStats.getMin() / (double)processedDataSampleCount_Heat) * (humidityStats.getMin() / (double)processedDataSampleCount_Humidity);
		frequencyAtPercentileTable[99] = (heatStats.getMax() / (double)processedDataSampleCount_Heat) * (humidityStats.getMax() / (double)processedDataSampleCount_Humidity);
		double lowerBound_percentile = frequencyAtPerdimileTable[0];
		double lowerBound_perdimile  = frequencyAtPerdimileTable[0];
		for(int i = 1; i < 100; i++) {
			frequencyAtPercentileTable[i - 1] = SearchForFrequencyAtPercentile(i, 1.00, lowerBound_percentile, search_depth);
			frequencyAtPerdimileTable[i]      = SearchForFrequencyAtPercentile(i / 100d, frequencyAtPercentileTable[0], lowerBound_perdimile, search_depth);
			
			/* This optimization is commented out because it assumes a standard-ish shaped
			 * distribution, but the ClimateHistogram_ValleysHumidRivers subclass uses this method
			 * and it has a hump shaped humidity distribution.
			lowerBound_percentile = frequencyAtPercentileTable[i];
			lowerBound_perdimile  = frequencyAtPerdimileTable[i];
			 */
		}

		AmidstLogger.info("frequencyAtPercentileTable: " + Arrays.toString(frequencyAtPercentileTable));
		AmidstLogger.info("frequencyAtPerdimileTable: "  + Arrays.toString(frequencyAtPerdimileTable));
	}

	/**
	 * Constructor for Minetest's default climate
	 */
	public ClimateHistogram() {

		if (processedDataSampleCount_Heat == 0) {
			// the samples are already processed, and the percentile tables already
			// calculated, but I leave this code here in case someone wants to update
			// sampledHistogram_Heat and sampledHistogram_Humidity with their own sampled
			// data and recalculate.
			processSamples();
			calculatePercentileTables(40);
		}
	}

	/**
	 * Constructor for climates which have the same frequency distribution as Minetest's default climate,
	 * but may have been scaled or translated.
	 */
	public ClimateHistogram(NoiseParams heat, NoiseParams heat_blend, NoiseParams humidity, NoiseParams humidity_blend) {

		// Ensure these noise settings are close enough to Minetest's default settings
		// that we can use the pre-generated data.
		//
		// Defaults:
		// np_heat           = new NoiseParams(50,   50, new Vector3f(1000, 1000, 1000),  5349, (short)3, 0.5f, 2.0f);
		// np_humidity       = new NoiseParams(50,   50, new Vector3f(1000, 1000, 1000),   842, (short)3, 0.5f, 2.0f);
		// np_heat_blend     = new NoiseParams( 0, 1.5f, new Vector3f(   8,    8,    8),    13, (short)2, 1.0f, 2.0f);
		// np_humidity_blend = new NoiseParams( 0, 1.5f, new Vector3f(   8,    8,    8), 90003, (short)2, 1.0f, 2.0f);

		// TODO: Check octaves, persist, and lacunarity are all the same, and that scale and offset
		//       is the same or can be adjusted for, and that spread is appropriate.
		scaleAdj  = heat.scale / DEFAULT_SCALE;
		offsetAdj = DEFAULT_OFFSET - heat.offset;
		float scaleAdj_blend = heat_blend.scale / DEFAULT_BLEND_SCALE;
		float offsetAdj_blend = DEFAULT_BLEND_OFFSET - heat_blend.offset;

		if (heat.scale != humidity.scale || heat.offset != humidity.offset ||
			heat_blend.scale != humidity_blend.scale || heat_blend.offset != humidity_blend.offset ||
			scaleAdj != scaleAdj_blend || offsetAdj != offsetAdj_blend ||
			heat.octaves    != 3    || humidity.octaves    != 3    || heat_blend.octaves    != 2    || humidity_blend.octaves    != 2 ||
			heat.persist    != 0.5f || humidity.persist    != 0.5f || heat_blend.persist    != 1.0f || humidity_blend.persist    != 1.0f ||
			heat.lacunarity != 2.0f || humidity.lacunarity != 2.0f || heat_blend.lacunarity != 2.0f || humidity_blend.lacunarity != 2.0f) {

			AmidstLogger.error("Non-standant climate noise in use, current ClimateHistogram instance will give wrong data.");
		}
	}
}
