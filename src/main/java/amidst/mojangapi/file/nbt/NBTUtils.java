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
import org.jnbt.Tag;

import amidst.documentation.Immutable;

@Immutable
public enum NBTUtils {
	;

	public static CompoundTag readTagFromFile(File file) throws IOException {
		try (NBTInputStream stream = createNBTInputStream(file)) {
			return (CompoundTag) stream.readTag();
		}
	}

	public static NBTInputStream createNBTInputStream(File file) throws IOException {
		return new NBTInputStream(new BufferedInputStream(new FileInputStream(file)));
	}

	public static void writeTagToFile(File out, CompoundTag root) throws IOException {
		try (NBTOutputStream outStream = createNBTOutputStream(out)) {
			outStream.writeTag(root);
		}
	}

	public static NBTOutputStream createNBTOutputStream(File file) throws IOException {
		return new NBTOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
	}

	public static long getLongValue(Tag tag) {
		Object value = tag.getValue();
		if (value instanceof Number) {
			return ((Number) value).longValue();
		} else {
			throw new IllegalArgumentException("cannot read long value from the class '" + tag.getClass().getName()
					+ "'");
		}
	}
}
