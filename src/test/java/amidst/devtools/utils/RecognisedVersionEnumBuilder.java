package amidst.devtools.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;

/**
 * First, add all known, then add all launcher version ids.
 */
@NotThreadSafe
public class RecognisedVersionEnumBuilder {
	public static RecognisedVersionEnumBuilder createPopulated() {
		RecognisedVersionEnumBuilder result = new RecognisedVersionEnumBuilder();
		for (RecognisedVersion recognisedVersion : RecognisedVersion.values()) {
			result.addKnown(recognisedVersion);
		}
		return result;
	}

	private final Map<String, RecognisedVersionEnumEntryBuilder> builders = new LinkedHashMap<>();
	private int maxNameLength;
	private int maxDeclarationLength;

	public void addKnown(RecognisedVersion recognisedVersion) {
		if (recognisedVersion.isKnown()) {
			builders.put(
					recognisedVersion.getMagicString(),
					RecognisedVersionEnumEntryBuilder.known(recognisedVersion));
		}
	}

	public void addLauncherVersionId(String versionId, String magicString) {
		RecognisedVersionEnumEntryBuilder builder = builders.get(magicString);
		if (builder != null) {
			builder.addMatch(versionId);
		} else {
			builders.put(magicString, RecognisedVersionEnumEntryBuilder.unknown(versionId, magicString));
		}
	}

	public void calculateMaxLength() {
		maxNameLength = getMaxNameLength();
		maxDeclarationLength = renderDeclarationsAndGetMaxLength(maxNameLength);
	}

	private int getMaxNameLength() {
		int result = 0;
		for (RecognisedVersionEnumEntryBuilder builder : builders.values()) {
			result = Math.max(result, builder.getName().length());
		}
		return result;
	}

	private int renderDeclarationsAndGetMaxLength(int maxNameLength) {
		int result = 0;
		for (RecognisedVersionEnumEntryBuilder builder : builders.values()) {
			result = Math.max(result, builder.renderDeclarationAndGetLength(maxNameLength));
		}
		return result;
	}

	public Iterable<String> renderNew() {
		List<String> result = new LinkedList<>();
		render(result, b -> !b.isKnown(), b -> b.renderLine(maxNameLength, maxDeclarationLength));
		return result;
	}

	public Iterable<String> renderRenamed() {
		List<String> result = new LinkedList<>();
		render(result, b -> b.isRenamed(), b -> b.renderLine(maxNameLength, maxDeclarationLength));
		return result;
	}

	public Iterable<String> renderComplete() {
		List<String> result = new LinkedList<>();
		render(result, b -> !b.isKnown(), b -> b.renderLine(maxNameLength, maxDeclarationLength));
		render(result, b -> b.isKnown(), b -> b.renderLine(maxNameLength, maxDeclarationLength));
		return result;
	}

	private void render(
			List<String> result,
			Function<RecognisedVersionEnumEntryBuilder, Boolean> condition,
			Function<RecognisedVersionEnumEntryBuilder, String> renderer) {
		for (RecognisedVersionEnumEntryBuilder builder : builders.values()) {
			if (condition.apply(builder)) {
				result.add(renderer.apply(builder));
			}
		}
	}
}
