package amidst.mojangapi.file.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;

import amidst.documentation.Immutable;

@Immutable
public enum NBTUtils {
	;

	public static CompoundTag readTagFromFile(File file) throws IOException {
		NBTInputStream stream = createNBTInputStream(file);
		CompoundTag result = (CompoundTag) stream.readTag();
		stream.close();
		return result;
	}

	public static NBTInputStream createNBTInputStream(File file)
			throws IOException {
		return new NBTInputStream(new BufferedInputStream(new FileInputStream(
				file)));
	}

	public static void writeTagToFile(File out, CompoundTag root)
			throws IOException {
		NBTOutputStream outStream = createNBTOutputStream(out);
		outStream.writeTag(root);
		outStream.close();
	}

	public static NBTOutputStream createNBTOutputStream(File file)
			throws IOException {
		return new NBTOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)));
	}
}
