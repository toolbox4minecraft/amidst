package amidst.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import amidst.documentation.Immutable;

@Immutable
public enum MessageFormatter {
	;

	public static String format(Throwable e, String message) {
		return format(message) + "\n\n" + format(e);
	}

	public static String format(Throwable e, String message, Object part1, Object[] parts) {
		return format(message, part1, parts) + "\n\n" + format(e);
	}

	public static String format(Object message) {
		return String.valueOf(message);
	}

	public static String format(Throwable e) {
		if (e != null) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		} else {
			return "null";
		}
	}

	public static String format(String message, Object part1, Object... parts) {
		Objects.requireNonNull(message, "the message cannot be null");

		int expectedNumberOfMessageParts = 2 + parts.length;
		String[] messageParts = message.split("\\{\\}", expectedNumberOfMessageParts);
		if (messageParts.length == 0) {
			return "";
		} else if (messageParts.length == 1) {
			return messageParts[0];
		} else {
			StringBuilder b = new StringBuilder();
			b.append(messageParts[0]);
			b.append(format(part1));
			b.append(messageParts[1]);
			for (int i = 2; i < messageParts.length; i++) {
				b.append(format(parts[i - 2]));
				b.append(messageParts[i]);
			}
			return b.toString();
		}
	}
}
