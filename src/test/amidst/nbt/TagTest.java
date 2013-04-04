package amidst.nbt;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

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
	
	//testRead covers this implicitly
	/* Every Tag subclass has to have a constructor with the interface
	 * (String, DataInputStream) throws IOException
	@Test
	public void testConstructors() {
		
	}*/
	
	/** Reads bigtest.nbt from wiki.vg
	 */
	@Test
	public void testRead() throws IOException {
		testReadImpl();
	}
	
	private TagCompound testReadImpl() throws IOException {
		return Tag.readFrom(TagTest.class.getResourceAsStream("bigtest.nbt"));
	}
	
	/** Tests correctness of serialization
	 */
	@Test
	public void testSerialization() throws IOException {
		TagCompound bigTest = testReadImpl();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		bigTest.serialize(ps, 0);
		String serialized = baos.toString();
		
		InputStream reference = TagTest.class.getResourceAsStream("bigtest.nbt.txt");
		Scanner s = new Scanner(reference).useDelimiter("\\A");
		String expected = s.next();
		
		assertEquals(expected, serialized);
	}
}
