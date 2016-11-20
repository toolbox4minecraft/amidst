package amidst.mojangapi.world.testworld.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import amidst.ResourceLoader;
import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.testworld.TestWorldDeclaration;
import amidst.mojangapi.world.testworld.file.TestWorldDirectory;
import amidst.mojangapi.world.testworld.file.TestWorldDirectoryDeclaration;
import amidst.mojangapi.world.testworld.file.TestWorldEntryDeclaration;

@ThreadSafe
public class TestWorldDirectoryReader {
	private static final String JSON_FILE_EXTENSION = ".json";

	private final TestWorldDirectoryDeclaration directoryDeclaration;

	public TestWorldDirectoryReader(TestWorldDirectoryDeclaration directoryDeclaration) {
		this.directoryDeclaration = directoryDeclaration;
	}

	public TestWorldDirectory read(TestWorldDeclaration worldDeclaration) throws IOException {
		return new TestWorldDirectory(directoryDeclaration, readAll(worldDeclaration));
	}

	private Map<String, Object> readAll(TestWorldDeclaration worldDeclaration) throws IOException {
		Map<String, Object> result = new HashMap<>();
		for (TestWorldEntryDeclaration<?> entryDeclaration : directoryDeclaration.getEntryDeclarations()) {
			if (worldDeclaration.isSupported(entryDeclaration.getName())) {
				readEntry(result, worldDeclaration, entryDeclaration);
			}
		}
		return result;
	}

	private void readEntry(
			Map<String, Object> result,
			TestWorldDeclaration worldDeclaration,
			TestWorldEntryDeclaration<?> entryDeclaration) throws IOException, FileNotFoundException {
		String name = entryDeclaration.getName();
		String zipResourceName = worldDeclaration.getZipResourceName(name);
		try (ZipInputStream zipInputStream = createZipInputStream(zipResourceName)) {
			String actualFilename = zipInputStream.getNextEntry().getName();
			String expectedFilename = name + JSON_FILE_EXTENSION;
			if (actualFilename.equals(expectedFilename)) {
				result.put(name, entryDeclaration.readFromStream(zipInputStream));
			} else {
				throw new FileNotFoundException(zipResourceName + " does not contain the file " + expectedFilename);
			}
		}
	}

	private ZipInputStream createZipInputStream(String zipFilename) {
		return new ZipInputStream(ResourceLoader.getResourceAsStream(zipFilename), StandardCharsets.UTF_8);
	}
}
