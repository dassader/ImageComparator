package com.home.comparator;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.min;

@Component
public class ImageComparator {

    public static final int MAX_DISTANCE_FROM_POINT_TO_AREA = 1000;
    public static final int MAX_PERCENT_OF_DIFFERENT = 10;
    public static final String FORMAT_OF_OUTPUT_IMAGE = "png";

    public List<Area> findAreasOfDifferents(InputStream firstImage, InputStream secondImage) throws IOException {
        BufferedImage firstBufferedImage = ImageIO.read(firstImage);
        BufferedImage secondBufferedImage = ImageIO.read(secondImage);

        return findAreas(firstBufferedImage, secondBufferedImage);
    }

    public InputStream findAreasOfDifferentAsImage(InputStream firstImage, InputStream secondImage) throws IOException {
        BufferedImage firstBufferedImage = ImageIO.read(firstImage);
        BufferedImage secondBufferedImage = ImageIO.read(secondImage);

        List<Area> areas = findAreas(firstBufferedImage, secondBufferedImage);

        return drawAreasToImage(firstBufferedImage, areas);
    }

    private InputStream drawAreasToImage(BufferedImage bufferedImage, List<Area> areas) throws IOException {
        Graphics2D graphics = bufferedImage.createGraphics();

        graphics.setColor(Color.RED);

        for (Area area : areas) {
            List<Map.Entry<com.home.comparator.Point, Point>> borders = area.getBorders();

            for (Map.Entry<Point, Point> border : borders) {
                graphics.drawLine(border.getKey().getX(), border.getKey().getY(), border.getValue().getX(), border.getValue().getY());
            }
        }

        graphics.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ImageIO.write(bufferedImage, FORMAT_OF_OUTPUT_IMAGE, byteArrayOutputStream);

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private List<Area> findAreas(BufferedImage firstBufferedImage, BufferedImage secondBufferedImage) {
        AreaHolder areaHolder = new AreaHolder();

        for (int x = 0; x < min(firstBufferedImage.getWidth(), secondBufferedImage.getWidth()); x++) {
            for (int y = 0; y < min(firstBufferedImage.getHeight(), secondBufferedImage.getHeight()); y++) {
                if (getPercentOfDifferent(firstBufferedImage.getRGB(x, y), secondBufferedImage.getRGB(x, y))
                        > MAX_PERCENT_OF_DIFFERENT) {
                    areaHolder.addPoint(x, y, MAX_DISTANCE_FROM_POINT_TO_AREA);
                }
            }
        }

        return areaHolder.getAreas();
    }

    private double getPercentOfDifferent(int firstRGB, int secondRGB) {
        int firstRed = (firstRGB >> 16) & 0xff;
        int firstGreen = (firstRGB >> 8) & 0xff;
        int firstBlue = (firstRGB) & 0xff;
        int secondRed = (secondRGB >> 16) & 0xff;
        int secondGreen = (secondRGB >> 8) & 0xff;
        int secondBlue = (secondRGB) & 0xff;

        int differentSum = abs(firstRed - secondRed)
                + abs(firstGreen - secondGreen)
                + abs(firstBlue - secondBlue);

        return (differentSum / 255.0) * 100.0;
    }
}
