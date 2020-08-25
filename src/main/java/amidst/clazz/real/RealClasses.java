package amidst.clazz.real;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import amidst.documentation.Immutable;
import amidst.parsing.URIUtils;

@Immutable
public enum RealClasses {
	;

	private static final int MAXIMUM_CLASS_BYTES = 32 * 1024;
	private static final RealClassBuilder REAL_CLASS_BUILDER = new RealClassBuilder();

	public static List<RealClass> fromJarFile(Path jarFile) throws FileNotFoundException, JarFileParsingException {
		return readRealClassesFromJarFile(jarFile);
	}

	private static List<RealClass> readRealClassesFromJarFile(Path jarFile)
			throws JarFileParsingException,
			FileNotFoundException {
		if (!Files.exists(jarFile)) {
			throw new FileNotFoundException("Attempted to load jar file at: " + jarFile + " but it does not exist.");
		}

		try (FileSystem jarContents = URIUtils.openZipFile(jarFile.toUri())){
			return readJarFile(jarContents);
		} catch (IOException | RealClassCreationException | URISyntaxException e) {
			throw new JarFileParsingException("Error extracting jar data.", e);
		}
	}

	private static List<RealClass> readJarFile(FileSystem zipFile) throws IOException, RealClassCreationException {
		List<RealClass> result = new ArrayList<>();
		for (Path root: zipFile.getRootDirectories()) {
			readJarFileDirectory(root, result);
		}
		return result;
	}

	private static void readJarFileDirectory(Path directory, List<RealClass> result)
			throws IOException,
			RealClassCreationException {
		for (Path path: (Iterable<Path>) Files.list(directory)::iterator) {
			String realClassName = getFileNameWithoutExtension(path.getFileName().toString(), "class");
			if (Files.isDirectory(path)) {
				readJarFileDirectory(path, result);
			} else if (realClassName != null) {
				RealClass realClass = readRealClass(realClassName, new BufferedInputStream(Files.newInputStream(path)));
				if (realClass != null) {
					result.add(realClass);
				}
			}
		}
	}

	private static RealClass readRealClass(String realClassName, BufferedInputStream stream)
			throws IOException,
			RealClassCreationException {
		try (BufferedInputStream theStream = stream) {
			// TODO: Double check that this filter won't mess anything up.
			if (theStream.available() < MAXIMUM_CLASS_BYTES) {
				byte[] classData = new byte[theStream.available()];
				theStream.read(classData);
				return REAL_CLASS_BUILDER.construct(realClassName, classData);
			}
		}
		return null;
	}

	private static String getFileNameWithoutExtension(String fileName, String extension) {
		String[] split = fileName.split("\\.");
		if (split.length == 2 && split[0].indexOf('/') == -1 && split[1].equals(extension)) {
			return split[0];
		} else {
			return null;
		}
	}
}
