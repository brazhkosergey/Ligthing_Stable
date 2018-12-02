import java.util.Date;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Test1 {
    public static void main(String[] args) {
        Deque<Integer> f = new ConcurrentLinkedDeque<>();
        for(int i=0;i<10;i++){
            f.addFirst(i);
        }


        int size = f.size();

        System.out.println("Size "+size);

        for(int i = 0;i<size;i++){
            System.out.println(f.pollLast());
        };

        System.out.println("Left " + f.size());
    }
}
