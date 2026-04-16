package com.mycompany.schoolmanagementssytem_edp;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public final class AppAssets {

    private static final Path IMAGE_DIRECTORY = Paths.get(System.getProperty("user.dir"), "img");

    private static BufferedImage backgroundImage;
    private static BufferedImage mainCampusLogo;
    private static BufferedImage sarmientoLogo;

    private AppAssets() {
    }

    public static BufferedImage backgroundImage() {
        if (backgroundImage == null) {
            backgroundImage = readImage("background.jpg");
        }
        return backgroundImage;
    }

    public static ImageIcon mainCampusLogo(int width, int height) {
        return scaledIcon(readMainCampusLogo(), width, height);
    }

    public static ImageIcon sarmientoLogo(int width, int height) {
        return scaledIcon(readSarmientoLogo(), width, height);
    }

    public static void applyWindowIcon(JFrame frame) {
        BufferedImage icon = readMainCampusLogo();
        if (icon != null) {
            frame.setIconImage(icon);
        }
    }

    private static BufferedImage readMainCampusLogo() {
        if (mainCampusLogo == null) {
            mainCampusLogo = readImage("bulsu_logoMainCampus.png");
        }
        return mainCampusLogo;
    }

    private static BufferedImage readSarmientoLogo() {
        if (sarmientoLogo == null) {
            sarmientoLogo = readImage("Sarmiento.png");
        }
        return sarmientoLogo;
    }

    private static ImageIcon scaledIcon(BufferedImage image, int width, int height) {
        if (image == null) {
            return null;
        }

        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static BufferedImage readImage(String fileName) {
        Path imagePath = IMAGE_DIRECTORY.resolve(fileName);
        if (!Files.exists(imagePath)) {
            return null;
        }

        try {
            return ImageIO.read(imagePath.toFile());
        } catch (IOException exception) {
            return null;
        }
    }
}
