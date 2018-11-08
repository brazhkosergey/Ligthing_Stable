import entity.Storage.Storage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Test {
    static boolean start = false;

    public static void main(String[] args) {

        TestMethod testMethod = new TestMethod();

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                start = true;
                System.out.println("===================================================");
            }
        });
        thread.start();

        for (int i = 0; i < 10; i++) {
            Thread th = new Thread(() -> {
                while (true) {
                    if (start) {
                        testMethod.print();
                    } else {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            th.setName("Поток " + i + ": ");
            th.start();
        }
    }

    public static void setStart() {
        start = false;
    }
}
