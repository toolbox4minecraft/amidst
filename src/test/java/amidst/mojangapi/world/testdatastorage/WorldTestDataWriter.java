package amidst.mojangapi.world.testdatastorage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

import com.google.gson.Gson;

@ThreadSafe
public class WorldTestDataWriter {
	private static final Gson GSON = new Gson();
	private static final String ZIP_FILE_EXTENSION = ".zip";
	private static final String JSON_FILE_EXTENSION = ".json";

	private static final File BASE_DIRECTORY = Paths.get("src", "test",
			"resources", "amidst", "mojangapi", "world", "testdatastorage")
			.toFile();

	public void write(TestWorldDeclaration declaration, WorldTestData testData)
			throws FileNotFoundException, IOException {
		long seed = declaration.getWorldSeed().getLong();
		RecognisedVersion recognisedVersion = declaration
				.getRecognisedVersion();
		File directory = new File(BASE_DIRECTORY, recognisedVersion.getName());
		File zipFile = new File(directory, seed + ZIP_FILE_EXTENSION);
		directory.mkdirs();
		write(zipFile, seed + "/", testData.getData());
	}

	private void write(File zipFile, String prefix, Map<String, Object> data)
			throws FileNotFoundException, IOException {
		ZipOutputStream zipOutputStream = createZipOutputStream(zipFile);
		try (OutputStreamWriter writer = new OutputStreamWriter(
				zipOutputStream, StandardCharsets.UTF_8)) {
			for (Entry<String, Object> entry : data.entrySet()) {
				zipOutputStream.putNextEntry(createZipEntry(prefix,
						entry.getKey()));
				GSON.toJson(entry.getValue(), writer);
				writer.flush();
				zipOutputStream.closeEntry();
			}
		}
	}

	private ZipEntry createZipEntry(String prefix, String name) {
		return new ZipEntry(prefix + name + JSON_FILE_EXTENSION);
	}

	private ZipOutputStream createZipOutputStream(File zipFile)
			throws FileNotFoundException {
		return new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFile)), StandardCharsets.UTF_8);
	}
}
