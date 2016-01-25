package amidst.mojangapi.world.testdatastorage;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import amidst.ResourceLoader;
import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.mocking.CloseFixedZipInputStream;

import com.google.gson.Gson;

@ThreadSafe
public class WorldTestDataReader {
	private static final Gson GSON = new Gson();
	private static final String ZIP_DIRECTORY = "/amidst/mojangapi/world/testdatastorage/";
	private static final String ZIP_FILE_EXTENSION = ".zip";
	private static final String JSON_FILE_EXTENSION = ".json";

	private final WorldTestDataBuilder builder;
	private final WorldTestDataZipFileDeclaration zipFileDeclaration;

	public WorldTestDataReader(WorldTestDataBuilder worldTestDataBuilder,
			WorldTestDataZipFileDeclaration zipFileDeclaration) {
		this.builder = worldTestDataBuilder;
		this.zipFileDeclaration = zipFileDeclaration;
	}

	public WorldTestData read(TestWorldDeclaration declaration)
			throws IOException, MinecraftInterfaceException {
		RecognisedVersion recognisedVersion = declaration
				.getRecognisedVersion();
		long seed = declaration.getWorldSeed().getLong();
		return builder.create(
				declaration,
				read(seed + "/", JSON_FILE_EXTENSION,
						getZipFilename(recognisedVersion, seed)));
	}

	private String getZipFilename(RecognisedVersion recognisedVersion, long seed) {
		return ZIP_DIRECTORY + recognisedVersion.getName() + "/" + seed
				+ ZIP_FILE_EXTENSION;
	}

	private Map<String, Object> read(String prefix, String suffix,
			String zipFilename) throws IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		CloseFixedZipInputStream zipInputStream = createZipInputStream(zipFilename);
		try (Closeable zipInputStreamCloser = zipInputStream.getCloseable()) {
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while (zipEntry != null) {
				String name = zipEntry.getName();
				if (name.startsWith(prefix) && name.endsWith(suffix)) {
					String key = name.substring(prefix.length(), name.length()
							- suffix.length());
					if (zipFileDeclaration.containsEntry(key)) {
						result.put(key, readValue(zipInputStream, key));
					}
				}
				zipEntry = zipInputStream.getNextEntry();
			}
		}
		return result;
	}

	private CloseFixedZipInputStream createZipInputStream(String zipFilename) {
		return new CloseFixedZipInputStream(
				ResourceLoader.getResourceAsStream(zipFilename),
				StandardCharsets.UTF_8);
	}

	private Object readValue(ZipInputStream zipInputStream, String key)
			throws IOException {
		try (InputStreamReader reader = new InputStreamReader(zipInputStream,
				StandardCharsets.UTF_8)) {
			return GSON.fromJson(reader, zipFileDeclaration.getClazz(key));
		}
	}
}
