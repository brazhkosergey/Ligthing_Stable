import entity.HideZoneLightingSearcher;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Test {

    public static void main(String[] args) {
        Random random = new Random();
        Set<String> set = new HashSet<>();
        for(int i =0;i<5;i++){
            int l = random.nextInt(5);
            System.out.println(l);
            set.add(String.valueOf(l));
            set.add(null);
        }
        System.out.println("====================");
        for(String s:set){
            System.out.println(s);
        }
        System.out.println("====================");
        System.out.println(set.size());
    }
}
