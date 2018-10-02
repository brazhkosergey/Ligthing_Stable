import entity.HideZoneLightingSearcher;

import java.io.File;

public class Test {

    public static void main(String[] args) {

        int degree= 60;
        double radDegree = (double) degree/180*Math.PI;

        System.out.println("Degree - "+ radDegree);
        double tan = Math.tan(radDegree);
        System.out.println("Tangens - "+tan);
        System.out.println("Degree - "+Math.atan(tan));
        System.out.println(Math.toDegrees(Math.PI));
    }
}
