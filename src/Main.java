import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static final String SOURCE_FILE = "resources/many-flowers.jpg";
    public static final String DESTINATION_FILE = "./out/many-flowers.jpg";
    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        long startTime = System.currentTimeMillis();

        recolorMultiThreaded(originalImage, resultImage);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Tempo de execução: " + executionTime + " milissegundos");

        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);
    }
    public static void recolorSingleThreaded(BufferedImage originalImage,
                                             BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(),
                originalImage.getHeight());
    }

    public static void recolorMultiThreaded(BufferedImage originalImage,
                                             BufferedImage resultImage) {
        recolorImageFracionado(originalImage, resultImage, 8);
    }
    public static void recolorImageFracionado(BufferedImage originalImage, BufferedImage resultImage, int parts)
    {
        int width = originalImage.getWidth();
        int height = originalImage.getWidth() /parts;

        Thread[] threads = new Thread[8];

        for (int i =0; i<parts; i++){
            final int multipilcadorInicio = 1;
            int xInicio = 0;
            int yInicio = height*multipilcadorInicio;

            Thread thread = new Thread() {
                @Override
                public void run() {
                    recolorImage(originalImage, resultImage, xInicio,yInicio, width,height);
                }
            };
            thread.start();
            threads[multipilcadorInicio] = thread;
        }

        for (int i=0;i<parts; i++){
            threads[i].join();
        }
    }
    public static void recolorImage(BufferedImage originalImage, BufferedImage
            resultImage, int leftCorner, int topCorner,
                                    int width, int height) {
        for(int x = leftCorner ; x < leftCorner + width && x <
                originalImage.getWidth() ; x++) {
            for(int y = topCorner ; y < topCorner + height && y <
                    originalImage.getHeight() ; y++) {
                recolorPixel(originalImage, resultImage, x , y);
            }
        }
    }
    public static void recolorPixel(BufferedImage originalImage, BufferedImage
            resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);
        int newRed;
        int newGreen;
        int newBlue;

        if(isShadeOfGray(red, green, blue)) {
            newRed = Math.min(5, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }
    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y,
                image.getColorModel().getDataElements(rgb, null));
    }
    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(
                green - blue) < 30;
    }
    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;
        rgb |= 0xFF000000;
        return rgb;
    }
    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }
    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }
    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}