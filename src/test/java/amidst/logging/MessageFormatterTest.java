package amidst.logging;

import org.junit.Assert;
import org.junit.Test;

public class MessageFormatterTest {
	@Test
	public void testEmptyMessage() {
		String message = "";
		String expected = "";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1"));
	}

	@Test
	public void testSimplyMessage() {
		String message = "{}";
		String expected = "p1";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1"));
	}

	@Test
	public void testBeforeMessage() {
		String message = "{}m1";
		String expected = "p1m1";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1"));
	}

	@Test
	public void testAfterMessage() {
		String message = "m1{}";
		String expected = "m1p1";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1"));
	}

	@Test
	public void testBetweenMessages() {
		String message = "m1{}m2";
		String expected = "m1p1m2";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1"));
	}

	@Test
	public void testChain1() {
		String message = "m1{}m2{}m3{}m4{}m5{}";
		String expected = "m1p1m2p2m3p3m4p4m5p5";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1", "p2", "p3", "p4", "p5"));
	}

	@Test
	public void testChain2() {
		String message = "m1{}m2{}m3{}m4{}m5";
		String expected = "m1p1m2p2m3p3m4p4m5";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1", "p2", "p3", "p4"));
	}

	@Test
	public void testChain3() {
		String message = "{}m1{}m2{}m3{}m4{}m5";
		String expected = "p1m1p2m2p3m3p4m4p5m5";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1", "p2", "p3", "p4", "p5"));
	}

	@Test
	public void testChain4() {
		String message = "{}m1{}m2{}m3{}m4{}";
		String expected = "p1m1p2m2p3m3p4m4p5";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1", "p2", "p3", "p4", "p5"));
	}

	@Test
	public void testTooManyParts() {
		String message = "m1{}m2";
		String expected = "m1p1m2";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1", "p2"));
	}

	@Test
	public void testTooFewParts() {
		String message = "m1{}m2{}";
		String expected = "m1p1m2{}";
		Assert.assertEquals(expected, MessageFormatter.format(message, "p1"));
	}
}
