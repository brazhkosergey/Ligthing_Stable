import entity.Storage.Storage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Test {

    public static void main(String[] args) {

        for (int rowNumber = 0; rowNumber < 2; rowNumber++) {
            String rowPosition;
            switch (rowNumber) {
                case 0:
                    rowPosition = BorderLayout.NORTH;
                    break;
                case 1:
                    rowPosition = BorderLayout.SOUTH;
                    break;
            }
            for (int columnNumber = 0; columnNumber < 2; columnNumber++) {
                System.out.println(2 * rowNumber + columnNumber + 1);

            }
        }
    }
}
