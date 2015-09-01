package amidst.minecraft.local;

import java.util.Map;
import java.util.regex.Matcher;

import amidst.byteclass.ByteClass;

public class ParameterStringParser {
	private Map<String, ByteClass> byteClassesByMinecraftClassName;
	private String minecraftName;
	private String byteName;
	private String[] parameterByteNames;

	public ParameterStringParser(
			Map<String, ByteClass> byteClassesByMinecraftClassName,
			String minecraftString, String minecraftName) {
		this.byteClassesByMinecraftClassName = byteClassesByMinecraftClassName;
		this.minecraftName = minecraftName;
		String byteString = replaceMinecraftClassNamesWithByteClassNames(minecraftString);
		this.byteName = byteString.substring(0, byteString.indexOf('('));
		String byteParameterString = byteString.substring(
				byteString.indexOf('(') + 1, byteString.indexOf(')'));
		if (byteParameterString.isEmpty()) {
			this.parameterByteNames = new String[0];
		} else {
			this.parameterByteNames = byteParameterString.split(",");
		}
	}

	public String getMinecraftName() {
		return minecraftName;
	}

	public String getByteName() {
		return byteName;
	}

	public String[] getParameterByteNames() {
		return parameterByteNames;
	}

	private String replaceMinecraftClassNamesWithByteClassNames(String inString) {
		return doReplaceMinecraftClassNamesWithByteClassNames(
				inString.replaceAll(" ", "")).replaceAll(",INVALID", "")
				.replaceAll("INVALID,", "").replaceAll("INVALID", "");
	}

	private String doReplaceMinecraftClassNamesWithByteClassNames(String result) {
		Matcher matcher = StatelessResources.INSTANCE.getClassNameRegex()
				.matcher(result);
		while (matcher.find()) {
			String match = result.substring(matcher.start(), matcher.end());
			result = replaceWithByteClassName(result, match);
			matcher = StatelessResources.INSTANCE.getClassNameRegex().matcher(
					result);
		}
		return result;
	}

	private String replaceWithByteClassName(String result, String match) {
		String minecraftClassName = match.substring(1);
		ByteClass byteClass = byteClassesByMinecraftClassName
				.get(minecraftClassName);
		if (byteClass != null) {
			result = result.replaceAll(match, byteClass.getByteClassName());
		} else {
			result = result.replaceAll(match, "INVALID");
		}
		return result;
	}
}
