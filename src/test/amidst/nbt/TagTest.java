package amidst.nbt;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static amidst.nbt.Tag.Type.*;

/** Tests NBT Tag class.
 */
public class TagTest extends Assert {
	public static Tag.Type[] types;
	
	@Before
	public void setUp() throws Exception {
		types = values();
	}
	
	/** Enum has to be ordered correctly!
	 */
	@Test
	public void testOrder() {
		assertEquals(TAG_End,        types[0]);
		assertEquals(TAG_Byte,       types[1]);
		assertEquals(TAG_Short,      types[2]);
		assertEquals(TAG_Int,        types[3]);
		assertEquals(TAG_Long,       types[4]);
		assertEquals(TAG_Float,      types[5]);
		assertEquals(TAG_Double,     types[6]);
		assertEquals(TAG_Byte_Array, types[7]);
		assertEquals(TAG_String,     types[8]);
		assertEquals(TAG_List,       types[9]);
		assertEquals(TAG_Compound,   types[10]);
		assertEquals(TAG_Int_Array,  types[11]);
	}
}
