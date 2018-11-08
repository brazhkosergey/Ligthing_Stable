public class TestMethod {
    boolean inside = false;

    public TestMethod() {
        Thread thread = new Thread(() -> {
            while (true) {
                inside = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public  void print() {
        if (!inside) {
            inside = true;
            Test.setStart();
            System.out.println(Thread.currentThread().getName() + " inside ++++++++++++++++++++++++++++++++++++++++++++");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
