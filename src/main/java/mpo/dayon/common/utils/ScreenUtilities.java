package mpo.dayon.common.utils;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import mpo.dayon.common.capture.Gray8Bits;

public abstract class ScreenUtilities {
	private static final Robot robot;

	private static final Rectangle SCREEN;

	private static final int[] rgb;

	private static final byte[] gray;

	static {
		try {
			final Toolkit toolkit = Toolkit.getDefaultToolkit();
			SCREEN = new Rectangle(0, 0, toolkit.getScreenSize().width, toolkit.getScreenSize().height);
			rgb = new int[SCREEN.height * SCREEN.width];
			gray = new byte[rgb.length];
			robot = new Robot();
		} catch (AWTException ex) {
			throw new RuntimeException("Could not initialize the AWT robot!", ex);
		}
	}

	public static byte[] captureGray(Gray8Bits quantization) {
		return rgbToGray8(quantization, captureRGB(SCREEN));
	}

	private static int[] captureRGB(Rectangle bounds) {
		BufferedImage image = robot.createScreenCapture(bounds);
		int yPos = bounds.y;
		int i = 0;
		while (yPos < bounds.height) {
			for (int xPos = bounds.x; xPos < bounds.width; xPos++) {
				rgb[i++] = image.getRGB(xPos, yPos);
			}
			yPos++;
		}
		return rgb;
	}

	public static byte[] captureGray(Rectangle bounds, Gray8Bits quantization) {
		return rgbToGray8(quantization, captureRGB(bounds));
	}

	private static byte[] rgbToGray8(Gray8Bits quantization, int[] rgb) {
		return doRgbToGray8(quantization, rgb);
	}

	private static byte[] doRgbToGray8(Gray8Bits quantization, int[] rgb) {
		final byte[] xLevels = grays[quantization.ordinal()];

		int prev_rgb = -1;
		byte prev_gray = -1;

		for (int idx = 0; idx < rgb.length; idx++) {
			final int pixel = rgb[idx];

			if (pixel == prev_rgb) {
				gray[idx] = prev_gray;
				continue;
			}

			final int red = (pixel & 0x00FF0000) >> 16;
			final int green_blue = pixel & 0x0000FFFF;

			final int level = (red_levels[red] + green_blue_levels[green_blue]) >> 7;

			gray[idx] = xLevels[level];

			prev_rgb = pixel;
			prev_gray = gray[idx];
		}
		return gray;
	}

	private static final short[] red_levels;

	private static final short[] green_blue_levels;

	/**
	 * Cache the conversion from red/green/blue into gray levels.
	 */
	static {
		red_levels = new short[256];

		for (int red = 0; red < 256; red++) {
			red_levels[red] = (short) (128.0 * 0.212671 * red);
		}

		green_blue_levels = new short[256 * 256];

		for (int green = 0; green < 256; green++) {
			for (int blue = 0; blue < 256; blue++) {
				green_blue_levels[(green << 8) + blue] = (short) ((128.0 * 0.715160 * green) + (128.0 * 0.072169 * blue));
			}
		}
	}

	private static final byte[][] grays;

	/**
	 * Cache the quantization of all the gray levels (256).
	 */
	static {
		final Gray8Bits[] quantizations = Gray8Bits.values();

		grays = new byte[quantizations.length][];

		for (final Gray8Bits quantization : quantizations) {
			grays[quantization.ordinal()] = new byte[256];

			final int factor = 256 / quantization.getLevels();

			for (int idx = 0; idx < 256; idx++) {
				// DOWN (0, 32, 64, 96, ...)
				// levels[quantization.ordinal()][idx] = (byte) (idx / factor *
				// factor);

				// UP (31, 63,, 95, ...)
				grays[quantization.ordinal()][idx] = (byte) (((1 + (idx / factor)) * factor) - 1);
			}
		}
	}

}
