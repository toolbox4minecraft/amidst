package amidst.mojangapi.world.loader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;

public enum NBTUtils {
	;

	public static CompoundTag readTagFromFile(File file) throws IOException,
			FileNotFoundException {
		NBTInputStream stream = createNBTInputStream(file);
		CompoundTag result = (CompoundTag) stream.readTag();
		stream.close();
		return result;
	}

	public static NBTInputStream createNBTInputStream(File file)
			throws IOException, FileNotFoundException {
		return new NBTInputStream(new BufferedInputStream(new FileInputStream(
				file)));
	}

	public static void writeTagToFile(File out, CompoundTag root)
			throws IOException, FileNotFoundException {
		NBTOutputStream outStream = createNBTOutputStream(out);
		outStream.writeTag(root);
		outStream.close();
	}

	public static NBTOutputStream createNBTOutputStream(File file)
			throws IOException, FileNotFoundException {
		return new NBTOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)));
	}
}
