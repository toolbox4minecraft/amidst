package amidst.mojangapi.world.test;

import org.junit.Assert;
import org.junit.Test;

import amidst.mojangapi.minecraftinterface.RecognisedVersion;

public class RecognisedVersionTest {
	@Test
	public void shouldPreventNameCollisions() {
		RecognisedVersion.generateNameToRecognisedVersionMap();
	}

	@Test
	public void shouldPreventMagicStringCollisions() {
		RecognisedVersion.generateMagicStringToRecognisedVersionMap();
	}

	@Test
	public void shouldPreventDifferencesInIdentifierAndName() {
		for (RecognisedVersion recognisedVersion : RecognisedVersion.values()) {
			if (recognisedVersion.isKnown()) {
				String expectedIdentifier = RecognisedVersion.createEnumIdentifier(recognisedVersion.getName());
				String actualIdentifier = recognisedVersion.name();
				Assert.assertEquals(expectedIdentifier, actualIdentifier);
			}
		}
	}
}
