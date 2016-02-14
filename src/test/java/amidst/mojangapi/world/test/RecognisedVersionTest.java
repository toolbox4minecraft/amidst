package amidst.mojangapi.world.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;

public class RecognisedVersionTest {
	@Test
	public void shouldPreventMagicStringCollisions() {
		Map<String, RecognisedVersion> magicStringToRecognisedVersion = new HashMap<String, RecognisedVersion>();
		for (RecognisedVersion recognisedVersion : RecognisedVersion.values()) {
			if (magicStringToRecognisedVersion.containsKey(recognisedVersion
					.getMagicString())) {
				RecognisedVersion colliding = magicStringToRecognisedVersion
						.get(recognisedVersion.getMagicString());
				Assert.fail("magic string collision for the recognised versions "
						+ recognisedVersion.getName()
						+ " and "
						+ colliding.getName());
			} else {
				magicStringToRecognisedVersion.put(
						recognisedVersion.getMagicString(), recognisedVersion);
			}
		}
	}
}
