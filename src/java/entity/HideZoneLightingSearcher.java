package entity;

import org.apache.log4j.Logger;
import ui.main.MainFrame;

import java.awt.image.BufferedImage;
import java.io.File;

public class HideZoneLightingSearcher {
    private static Logger log = Logger.getLogger("admin");




    public static void addHideZoneAreaName(File folderWithFiles) {




    }

//    private BufferedImage scanCountOfWhitePixelsPercent(BufferedImage bi) {
//        if (MainFrame.isProgramLightCatchEnable()) {
//            int countWhite = 0;
//            for (int y = 0; y < bi.getHeight(); y += 2) {
//                for (int x = 0; x < bi.getWidth(); x += 2) {
//                    if (setOfColorsRGBNumbers.contains(bi.getRGB(x, y))) {
//                        countWhite++;
//                    }
//                }
//            }
//            whiteDeque.addFirst(countWhite);
//            if (whiteDeque.size() > 10) {
//                int total = 0;
//                for (Integer integer : whiteDeque) {
//                    total += integer;
//                }
//                int average = total / whiteDeque.size();
//                if (countWhite != 0) {
//
//                    int differentWhitePixelsAverage = Math.abs(average - countWhite);
//                    if (differentWhitePixelsAverage != 0) {
//                        if (average != 0) {
//                            int diffPercent = differentWhitePixelsAverage * 100 / average;
//                            int abs = Math.abs(diffPercent);
//                            int percentDiffWhiteFromSetting = MainFrame.getPercentDiffWhite();
//                            if (percentWhiteDiff != percentDiffWhiteFromSetting) {
//                                percentWhiteDiff = percentDiffWhiteFromSetting;
//                            } else {
//                                if (abs > percentWhiteDiff * 50) {
//                                    VideoCreator.startCatchVideo(true);
//                                    whiteDeque.clear();
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    if (average != 0) {
//                        whiteDeque.clear();
//                    }
//                }
//                whiteDeque.pollLast();
//            }
//        }
//        return bi;
//    }
}
