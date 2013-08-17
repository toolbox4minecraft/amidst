package amidst.bytedata;

import com.skiphs.AMIDST.Log;
import com.skiphs.AMIDST.minecraft.Minecraft;

public class CCRequireInterface extends CCRequireSimple {
	public CCRequireInterface(ClassChecker checker) {
		super(checker);
	}

	public boolean canPass(Minecraft mc, ByteClass bClass) {
		return bClass.isInterface();
	}
}
