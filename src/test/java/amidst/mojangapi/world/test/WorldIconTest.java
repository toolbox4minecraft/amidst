package amidst.mojangapi.world.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import amidst.mojangapi.world.testdatastorage.TestWorldDeclaration;
import amidst.mojangapi.world.testdatastorage.WTDEntries;
import amidst.mojangapi.world.testdatastorage.WorldTestData;
import amidst.mojangapi.world.testdatastorage.WorldTestDataCache;
import amidst.mojangapi.world.testdatastorage.json.CoordinatesCollectionJson;

public class WorldIconTest {
	@Test
	public void shouldGenerateSameWorldIcons() {
		shouldGenerateSameWorldIcons(WTDEntries.SPAWN);
		shouldGenerateSameWorldIcons(WTDEntries.STRONGHOLDS);
		shouldGenerateSameWorldIcons(WTDEntries.VILLAGES);
		shouldGenerateSameWorldIcons(WTDEntries.WITCH_HUTS);
		shouldGenerateSameWorldIcons(WTDEntries.JUNGLE_TEMPLES);
		shouldGenerateSameWorldIcons(WTDEntries.DESERT_TEMPLES);
		shouldGenerateSameWorldIcons(WTDEntries.IGLOOS);
		shouldGenerateSameWorldIcons(WTDEntries.MINESHAFTS);
		shouldGenerateSameWorldIcons(WTDEntries.OCEAN_MONUMENTS);
		shouldGenerateSameWorldIcons(WTDEntries.NETHER_FORTRESSES);
	}

	private void shouldGenerateSameWorldIcons(String name) {
		for (TestWorldDeclaration declaration : TestWorldDeclaration.values()) {
			WorldTestData testData = WorldTestDataCache.get(declaration);
			CoordinatesCollectionJson expected = testData.getTestData(name,
					CoordinatesCollectionJson.class);
			CoordinatesCollectionJson actual = testData.extractFromWorld(name,
					CoordinatesCollectionJson.class);
			assertTrue(expected.equals(actual));
		}
	}
}
