package amidst.clazz.real;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import amidst.utilties.FileSystemUtils;

public class RealClasses {
	private RealClasses() {
	}

	public static RealClass fromByteArray(String realClassName, byte[] classData) {
		return RealClass.newInstance(realClassName, classData);
	}

	public static List<RealClass> fromJarFile(File jarFile) {
		return readByteClassesFromJarFile(jarFile);
	}

	private static List<RealClass> readByteClassesFromJarFile(File jarFile) {
		if (!jarFile.exists()) {
			throw new RuntimeException("Attempted to load jar file at: "
					+ jarFile + " but it does not exist.");
		}
		try {
			ZipFile jar = new ZipFile(jarFile);
			List<RealClass> byteClasses = readJarFile(jar);
			jar.close();
			return byteClasses;
		} catch (IOException e) {
			throw new RuntimeException("Error extracting jar data.", e);
		}
	}

	private static List<RealClass> readJarFile(ZipFile jar) throws IOException {
		Enumeration<? extends ZipEntry> enu = jar.entries();
		List<RealClass> byteClassList = new ArrayList<RealClass>();
		while (enu.hasMoreElements()) {
			RealClass entry = readJarFileEntry(jar, enu.nextElement());
			if (entry != null) {
				byteClassList.add(entry);
			}
		}
		return byteClassList;
	}

	private static RealClass readJarFileEntry(ZipFile jar, ZipEntry entry)
			throws IOException {
		String realClassName = FileSystemUtils.getFileNameWithoutExtension(
				entry.getName(), "class");
		if (!entry.isDirectory() && realClassName != null) {
			BufferedInputStream is = new BufferedInputStream(
					jar.getInputStream(entry));
			// TODO: Double check that this filter won't mess anything up.
			if (is.available() < 8000) {
				byte[] classData = new byte[is.available()];
				is.read(classData);
				is.close();
				return RealClass.newInstance(realClassName, classData);
			}
		}
		return null;
	}
}
