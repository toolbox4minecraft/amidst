package amidst.byteclass;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import amidst.utilties.FileSystemUtils;

public class ByteClasses {
	private ByteClasses() {
	}

	public static ByteClass fromByteArray(String byteClassName, byte[] classData) {
		return ByteClass.newInstance(byteClassName, classData);
	}

	public static List<ByteClass> fromJarFile(File jarFile) {
		return readByteClassesFromJarFile(jarFile);
	}

	private static List<ByteClass> readByteClassesFromJarFile(File jarFile) {
		if (!jarFile.exists()) {
			throw new RuntimeException("Attempted to load jar file at: "
					+ jarFile + " but it does not exist.");
		}
		try {
			ZipFile jar = new ZipFile(jarFile);
			List<ByteClass> byteClasses = readJarFile(jar);
			jar.close();
			return byteClasses;
		} catch (IOException e) {
			throw new RuntimeException("Error extracting jar data.", e);
		}
	}

	private static List<ByteClass> readJarFile(ZipFile jar) throws IOException {
		Enumeration<? extends ZipEntry> enu = jar.entries();
		List<ByteClass> byteClassList = new ArrayList<ByteClass>();
		while (enu.hasMoreElements()) {
			ByteClass entry = readJarFileEntry(jar, enu.nextElement());
			if (entry != null) {
				byteClassList.add(entry);
			}
		}
		return byteClassList;
	}

	private static ByteClass readJarFileEntry(ZipFile jar, ZipEntry entry)
			throws IOException {
		String byteClassName = FileSystemUtils.getFileNameWithoutExtension(
				entry.getName(), "class");
		if (!entry.isDirectory() && byteClassName != null) {
			BufferedInputStream is = new BufferedInputStream(
					jar.getInputStream(entry));
			// TODO: Double check that this filter won't mess anything up.
			if (is.available() < 8000) {
				byte[] classData = new byte[is.available()];
				is.read(classData);
				is.close();
				return ByteClass.newInstance(byteClassName, classData);
			}
		}
		return null;
	}
}
