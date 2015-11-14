package amidst.minecraft.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

public class NBTUtils {
	public CompoundTag readTagFromFile(File file) throws IOException,
			FileNotFoundException {
		NBTInputStream stream = createNBTInputStream(file);
		CompoundTag result = (CompoundTag) stream.readTag();
		stream.close();
		return result;
	}

	public NBTInputStream createNBTInputStream(File file) throws IOException,
			FileNotFoundException {
		return new NBTInputStream(new BufferedInputStream(new FileInputStream(
				file)));
	}

	public void writeTagToFile(File out, CompoundTag root) throws IOException,
			FileNotFoundException {
		NBTOutputStream outStream = createNBTOutputStream(out);
		outStream.writeTag(root);
		outStream.close();
	}

	public NBTOutputStream createNBTOutputStream(File file) throws IOException,
			FileNotFoundException {
		return new NBTOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)));
	}

	public Object getValue(String key, CompoundTag rootTag) {
		return rootTag.getValue().get(key).getValue();
	}

	public List<Tag> getValueAsList(String key, CompoundTag rootTag) {
		ListTag listTag = (ListTag) rootTag.getValue().get(key);
		return listTag.getValue();
	}

	public boolean isValueExisting(String key, CompoundTag rootTag) {
		return rootTag.getValue().get(key) != null;
	}
}
