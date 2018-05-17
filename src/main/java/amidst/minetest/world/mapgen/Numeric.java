package amidst.minetest.world.mapgen;

import java.nio.charset.Charset;
import java.util.AbstractMap.SimpleEntry;

import amidst.mojangapi.world.WorldSeed;

public class Numeric {

	/**
	 * @return A pair of values: the seed as a long, and whether it was 
	 * parsed as a numeric or text string 
	 */
	public static SimpleEntry<Long, Boolean> stringToSeed(String input)
	{
		long seed;
		boolean fromText = false;
		
		try {		
			if (input.startsWith("0x")) {
				seed = Long.decode(input);
			} else {
				try {
					seed = Long.parseUnsignedLong(input);
				} catch(NumberFormatException ex) {
					// perhaps they entered a signed seed? (even though Minetest seeds aren't) 				
					seed = Long.parseLong(input);
				}
			}
		} catch (NumberFormatException err) {
			byte[] stringAsBytes = input.getBytes(Charset.forName("UTF-8"));
			seed = murmurhash2_hash64(stringAsBytes, stringAsBytes.length, 0x1337);
			fromText = true;
		}
		return new SimpleEntry<Long, Boolean>(seed, fromText);
	}	
	
	/** 
     * Generates 64 bit hash from byte array of the given length and seed.
     * 
     * @param data byte array to hash
     * @param length length of the array to hash
     * @param seed initial seed value
     * @return 64 bit hash of the given array
     */
    public static long murmurhash2_hash64(final byte[] data, int length, int seed) {
        final long m = 0xc6a4a7935bd1e995L;
        final int r = 47;

        long h = (seed&0xffffffffl)^(length*m);

        int length8 = length/8;

        for (int i=0; i<length8; i++) {
            final int i8 = i*8;
            long k =  ((long)data[i8+0]&0xff)      +(((long)data[i8+1]&0xff)<<8)
                    +(((long)data[i8+2]&0xff)<<16) +(((long)data[i8+3]&0xff)<<24)
                    +(((long)data[i8+4]&0xff)<<32) +(((long)data[i8+5]&0xff)<<40)
                    +(((long)data[i8+6]&0xff)<<48) +(((long)data[i8+7]&0xff)<<56);
            
            k *= m;
            k ^= k >>> r;
            k *= m;
            
            h ^= k;
            h *= m; 
        }
        
        switch (length%8) {
        case 7: h ^= (long)(data[(length&~7)+6]&0xff) << 48;
        case 6: h ^= (long)(data[(length&~7)+5]&0xff) << 40;
        case 5: h ^= (long)(data[(length&~7)+4]&0xff) << 32;
        case 4: h ^= (long)(data[(length&~7)+3]&0xff) << 24;
        case 3: h ^= (long)(data[(length&~7)+2]&0xff) << 16;
        case 2: h ^= (long)(data[(length&~7)+1]&0xff) << 8;
        case 1: h ^= (long)(data[length&~7]&0xff);
                h *= m;
        };
     
        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        return h;
    }	
}
