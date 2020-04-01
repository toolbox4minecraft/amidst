package amidst.devtools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import amidst.documentation.NotThreadSafe;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIdException;
import amidst.settings.biomeprofile.BiomeProfile;
import amidst.settings.biomeprofile.BiomeProfileSelection;

@NotThreadSafe
public class GenerateBiomeColorImages {
	private static final int WIDTH = 100;
	private static final int HEIGHT = 30;

	private final Iterable<Biome> biomes;
	private final Path root;

	public GenerateBiomeColorImages(Iterable<Biome> biomes, Path root) {
		this.biomes = biomes;
		this.root = root;
	}

	public void run() throws IOException {
		try {
			StringBuilder b = new StringBuilder();
			b.append("Biome name | Biome id | Biome color\n");
			b.append("---------|-----------:|:----------:\n");
			BiomeProfileSelection colorProvider = new BiomeProfileSelection(BiomeProfile.getDefaultProfile());
			for (Biome biome : biomes) {
				int index = biome.getId();
				String name = biome.getName();
				Color color = colorProvider.getBiomeColor(biome.getId()).getColor();
				appendLine(b, index, name);
				createAndSaveImage(index, color);
			}
			System.out.println(b.toString());
		} catch (UnknownBiomeIdException e) {
			e.printStackTrace();
		}
	}

	private void appendLine(StringBuilder b, int index, String name) {
		b.append(name);
		b.append(" | ");
		b.append(index);
		b.append(" | ");
		appendImageReference(b, index, name);
		b.append("\n");
	}

	private void appendImageReference(StringBuilder b, int index, String name) {
		b.append("![" + name + "](biome-color-images/" + index + ".png)");
	}

	private void createAndSaveImage(int index, Color color) throws IOException {
		ImageIO.write(createImage(color), "png",
			new BufferedOutputStream(Files.newOutputStream(root.resolve(index + ".png"))));
	}

	private BufferedImage createImage(Color color) {
		BufferedImage result = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = result.createGraphics();
		g2d.setColor(color);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);
		g2d.dispose();
		return result;
	}
}
