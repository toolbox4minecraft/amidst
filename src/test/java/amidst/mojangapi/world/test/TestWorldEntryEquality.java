package amidst.mojangapi.world.test;

import static org.junit.Assert.fail;

import org.junit.Test;

import amidst.mojangapi.world.testworld.TestWorld;
import amidst.mojangapi.world.testworld.TestWorldCache;
import amidst.mojangapi.world.testworld.TestWorldDeclaration;

public class TestWorldEntryEquality {
	@Test
	public void shouldGenerateSameEntries() {
		for (TestWorldDeclaration worldDeclaration : TestWorldDeclaration.values()) {
			TestWorld testWorld = TestWorldCache.get(worldDeclaration);
			for (String name : worldDeclaration.getSupportedEntryNames()) {
				if (!testWorld.isDirectoryAndWorldEntryEqual(name)) {
					fail("entry changed: [world: " + worldDeclaration + ", entry: " + name + "]");
				}
			}
		}
	}
}
