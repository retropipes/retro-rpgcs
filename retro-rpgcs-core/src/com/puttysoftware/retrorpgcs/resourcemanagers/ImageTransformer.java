/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import java.awt.Color;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.retrorpgcs.maze.utilities.ImageColorConstants;

public class ImageTransformer {
    public static final int MAX_WINDOW_SIZE = 700;
    private static final Color TRANSPARENT = new Color(200, 100, 100);
    private static Color REPLACE = new Color(200, 100, 100, 0);

    public static BufferedImageIcon getCompositeImage(
            final BufferedImageIcon icon1, final BufferedImageIcon icon2,
            final int imageSize) {
        try {
            final var result = new BufferedImageIcon(icon2);
            if (icon1 != null && icon2 != null) {
                for (var x = 0; x < imageSize; x++) {
                    for (var y = 0; y < imageSize; y++) {
                        final var pixel = icon2.getRGB(x, y);
                        final var c = new Color(pixel);
                        if (c.equals(ImageTransformer.TRANSPARENT)) {
                            result.setRGB(x, y, icon1.getRGB(x, y));
                        }
                    }
                }
                return result;
            } else {
                return null;
            }
        } catch (final NullPointerException np) {
            return null;
        } catch (final IllegalArgumentException ia) {
            return null;
        }
    }

    public static int getGraphicSize() {
        return 64;
    }

    public static BufferedImageIcon getTransformedImage(
            final BufferedImageIcon icon, final int imageSize) {
        try {
            final var result = new BufferedImageIcon(icon);
            if (icon != null) {
                for (var x = 0; x < imageSize; x++) {
                    for (var y = 0; y < imageSize; y++) {
                        final var pixel = icon.getRGB(x, y);
                        final var c = new Color(pixel);
                        if (c.equals(ImageTransformer.TRANSPARENT)) {
                            result.setRGB(x, y,
                                    ImageTransformer.REPLACE.getRGB());
                        }
                    }
                }
                return result;
            } else {
                return null;
            }
        } catch (final NullPointerException np) {
            return null;
        } catch (final IllegalArgumentException ia) {
            return null;
        }
    }

    public static BufferedImageIcon getVirtualCompositeImage(
            final BufferedImageIcon icon1, final BufferedImageIcon icon2,
            final BufferedImageIcon icon3, final int imageSize) {
        try {
            final var icon4 = ImageTransformer
                    .getCompositeImage(icon1, icon2, imageSize);
            final var result = new BufferedImageIcon(icon3);
            if (icon3 != null && icon4 != null) {
                for (var x = 0; x < imageSize; x++) {
                    for (var y = 0; y < imageSize; y++) {
                        final var pixel = icon3.getRGB(x, y);
                        final var c = new Color(pixel);
                        if (c.equals(ImageTransformer.TRANSPARENT)) {
                            result.setRGB(x, y, icon4.getRGB(x, y));
                        }
                    }
                }
                return result;
            } else {
                return null;
            }
        } catch (final NullPointerException np) {
            return null;
        } catch (final IllegalArgumentException ia) {
            return null;
        }
    }

    public static String normalizeName(final String name) {
        final var sb = new StringBuilder(name);
        for (var x = 0; x < sb.length(); x++) {
            if (!Character.isLetter(sb.charAt(x))
                    && !Character.isDigit(sb.charAt(x))) {
                sb.setCharAt(x, '_');
            }
        }
        return sb.toString().toLowerCase();
    }

    static BufferedImageIcon templateTransformImage(
            final BufferedImageIcon input, final int transformColor,
            final int imageSize) {
        if (transformColor == ImageColorConstants.COLOR_NONE) {
            return input;
        } else {
            try {
                final var result = new BufferedImageIcon(input);
                for (var x = 0; x < imageSize; x++) {
                    for (var y = 0; y < imageSize; y++) {
                        final var pixel = input.getRGB(x, y);
                        final var c = new Color(pixel);
                        final var r = c.getRed();
                        final var g = c.getGreen();
                        final var b = c.getBlue();
                        if (r == g && r == b && g == b) {
                            final var tc = new Color(transformColor);
                            final var tr = (tc.getRed() + 1) / 256.0;
                            final var tg = (tc.getGreen() + 1) / 256.0;
                            final var tb = (tc.getBlue() + 1) / 256.0;
                            final var newR = (int) (r * tr);
                            final var newG = (int) (g * tg);
                            final var newB = (int) (b * tb);
                            final var nc = new Color(newR, newG, newB);
                            result.setRGB(x, y, nc.getRGB());
                        }
                    }
                }
                return result;
            } catch (final NullPointerException np) {
                return input;
            }
        }
    }
}
