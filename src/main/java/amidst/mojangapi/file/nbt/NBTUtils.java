package amidst.mojangapi.file.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.NumberTag;
import net.querz.nbt.tag.Tag;

import amidst.documentation.Immutable;

@Immutable
public enum NBTUtils {
	;

	public static CompoundTag readTagFromFile(Path file) throws IOException {
		try(NBTInputStream inStream = createNBTInputStream(file)) {
			NamedTag namedRoot = inStream.readTag(Tag.DEFAULT_MAX_DEPTH);
			if (!namedRoot.getName().isEmpty()) {
				throw new IOException("Unexpected NBT root name '" + namedRoot.getName() + "' in " + file);
			}
			Tag<?> root = namedRoot.getTag();
			if (root instanceof CompoundTag) {
				return (CompoundTag) root;
			} else {
				throw new IOException("Invalid NBT tag id " + root.getID() + " in " + file);
			}
		}
	}

	public static NBTInputStream createNBTInputStream(Path file) throws IOException {
		return new NBTInputStream(new GZIPInputStream(new BufferedInputStream(Files.newInputStream(file))));
	}

	public static void writeTagToFile(Path out, CompoundTag root) throws IOException {
		try (NBTOutputStream outStream = createNBTOutputStream(out)) {
			outStream.writeTag(new NamedTag("", root), Tag.DEFAULT_MAX_DEPTH);
		}
	}

	public static NBTOutputStream createNBTOutputStream(Path file) throws IOException {
		return new NBTOutputStream(new GZIPOutputStream(new BufferedOutputStream(Files.newOutputStream(file))));
	}

	public static long getLongValue(Tag<?> tag) {
		if (tag instanceof NumberTag<?>) {
			return ((NumberTag<?>) tag).asLong();
		} else {
			throw new IllegalArgumentException(
					"cannot read long value from the class '" + tag.getClass().getName() + "'");
		}
	}

	public static Tag<?> getNestedTag(Tag<?> tag, String... path) {
		Tag<?> currentTag = tag;
		for (String key: path) {
			if (currentTag == null || !(currentTag instanceof CompoundTag)) {
				return null;
			}

			currentTag = ((CompoundTag) currentTag).get(key);
		}
		return currentTag;
	}

	public static CompoundTag shallowCopy(CompoundTag tag) {
		CompoundTag result = new CompoundTag();
		for (Entry<String, Tag<?>> entry: tag.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
