package amidst.mojangapi.world.testworld.io;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.world.testworld.TestWorldDeclaration;
import amidst.mojangapi.world.testworld.file.TestWorldDirectory;
import amidst.mojangapi.world.testworld.file.TestWorldDirectoryDeclaration;
import amidst.mojangapi.world.testworld.file.TestWorldEntryDeclaration;

@ThreadSafe
public class TestWorldDirectoryWriter {
	private static final String JSON_FILE_EXTENSION = ".json";

	private final TestWorldDirectoryDeclaration directoryDeclaration;

	public TestWorldDirectoryWriter(TestWorldDirectoryDeclaration directoryDeclaration) {
		this.directoryDeclaration = directoryDeclaration;
	}

	public void write(TestWorldDeclaration worldDeclaration, TestWorldDirectory directory)
			throws FileNotFoundException,
			IOException {
		worldDeclaration.createDirectoryIfNecessary();
		writeAll(worldDeclaration, directory.getData());
	}

	private void writeAll(TestWorldDeclaration worldDeclaration, Map<String, Object> data)
			throws FileNotFoundException,
			IOException {
		for (TestWorldEntryDeclaration<?> entryDeclaration : directoryDeclaration.getEntryDeclarations()) {
			if (worldDeclaration.isSupported(entryDeclaration.getName())) {
				writeEntry(data, worldDeclaration, entryDeclaration);
			}
		}
	}

	private void writeEntry(
			Map<String, Object> data,
			TestWorldDeclaration worldDeclaration,
			TestWorldEntryDeclaration<?> entryDeclaration) throws IOException, FileNotFoundException {
		String name = entryDeclaration.getName();
		Path zipFile = worldDeclaration.getZipFile(name);
		try (ZipOutputStream zipOutputStream = createZipOutputStream(zipFile)) {
			zipOutputStream.putNextEntry(createZipEntry(name));
			entryDeclaration.writeToStream(zipOutputStream, data);
			zipOutputStream.closeEntry();
		}
	}

	private ZipOutputStream createZipOutputStream(Path zipFile) throws IOException {
		return new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipFile)), StandardCharsets.UTF_8);
	}

	private ZipEntry createZipEntry(String name) {
		ZipEntry result = new ZipEntry(name + JSON_FILE_EXTENSION);
		result.setTime(0);
		return result;
	}
}
