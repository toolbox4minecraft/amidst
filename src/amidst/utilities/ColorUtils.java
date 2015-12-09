package amidst.utilities;

import amidst.documentation.Immutable;

@Immutable
public enum ColorUtils {
	;

	public static int makeColor(int r, int g, int b) {
		int color = 0xFF000000;
		color |= 0xFF0000 & (r << 16);
		color |= 0xFF00 & (g << 8);
		color |= 0xFF & b;
		return color;
	}

	public static int mcColor(int color) {
		return 0xFF000000 | color;
	}

	public static int deselectColor(int color) {
		int r = (color & 0x00FF0000) >> 16;
		int g = (color & 0x0000FF00) >> 8;
		int b = (color & 0x000000FF);

		int average = (r + g + b);
		r = (r + average) / 30;
		g = (g + average) / 30;
		b = (b + average) / 30;
		return makeColor(r, g, b);
	}

	public static int lightenColor(int color, int brightness) {
		int r = (color & 0x00FF0000) >> 16;
		int g = (color & 0x0000FF00) >> 8;
		int b = (color & 0x000000FF);

		r += brightness;
		g += brightness;
		b += brightness;

		if (r > 0xFF) {
			r = 0xFF;
		}
		if (g > 0xFF) {
			g = 0xFF;
		}
		if (b > 0xFF) {
			b = 0xFF;
		}

		return makeColor(r, g, b);
	}

	public static int greyScale(int color) {
		int r = (color & 0x00FF0000) >> 16;
		int g = (color & 0x0000FF00) >> 8;
		int b = (color & 0x000000FF);
		int average = (r + g + b) / 3;
		return makeColor(average, average, average);
	}
}
