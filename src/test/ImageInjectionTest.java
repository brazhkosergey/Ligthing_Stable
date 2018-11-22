import ui.video.VideoPlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;

public class ImageInjectionTest {


    public static void main(String[] args) {
        ImageInjectionTest imageInjectionTest = new ImageInjectionTest();

        File file = new File("C:\\test\\bytes\\1542908828502{NO DATA}\\1(6)[26]");
        File imageF = new File("C:\\test\\1542903541998-1.jpg");
        try {
            BufferedImage image = ImageIO.read(imageF);
            imageInjectionTest.injectImage(file, image);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void injectImage(File folder, BufferedImage image) {
        if (folder != null) {
            String name = folder.getName();
            int frameToReplace = 0;
            int first = name.indexOf("[");
            int second = name.indexOf("]");
            String substring = name.substring(first + 1, second);
            String[] eventsSplit = substring.split(",");
            String aSplit = eventsSplit[0];
            boolean contains = aSplit.contains("(");
            if (contains) {
                String s = aSplit.substring(1, aSplit.length() - 1);
                try {
                    frameToReplace = Integer.parseInt(s);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    frameToReplace = Integer.parseInt(aSplit);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            int x = 0;
            int t;
            int totalCountFrames = 0;
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {

                    String fileName = file.getName();

                    String[] fileNameSplit = fileName.split("\\.");
                    String[] lastSplit = fileNameSplit[0].split("-");
                    String countFramesString = lastSplit[1];
                    int countFrames = Integer.parseInt(countFramesString);
                    totalCountFrames += countFrames;
                    if (totalCountFrames >= frameToReplace) {
                        totalCountFrames = totalCountFrames - countFrames;
                        File testFile = new File(file.getAbsolutePath() + "test");
                        try {

                            FileInputStream fileInputStream = new FileInputStream(file);
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

                            ByteArrayOutputStream temporaryStream = new ByteArrayOutputStream(65535);
                            FileOutputStream fileOutputStream = new FileOutputStream(testFile);


                            testFile.createNewFile();
                            do {
                                t = x;
                                try {
                                    x = bufferedInputStream.read();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                temporaryStream.write(x);
                                if (x == 216 && t == 255) {// начало изображения
                                    temporaryStream.reset();
                                    temporaryStream.write(t);
                                    temporaryStream.write(x);
                                } else if (x == 217 && t == 255) {//конец изображения
                                    byte[] imageBytes = temporaryStream.toByteArray();
                                    totalCountFrames++;
                                    if (totalCountFrames == frameToReplace) {
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        ImageIO.write(image, "jpg", baos);
                                        baos.flush();
                                        imageBytes = baos.toByteArray();
                                        baos.close();
                                    }
                                    fileOutputStream.write(imageBytes);
                                }
                            } while (x > -1);
                            fileOutputStream.flush();
                            fileOutputStream.close();

                            fileInputStream.close();
                            bufferedInputStream.close();
                            temporaryStream.close();
                            file.delete();
                            testFile.renameTo(file);
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        }
    }
}
