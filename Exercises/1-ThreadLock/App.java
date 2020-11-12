public class App {

    public static void main(String[] args) throws Exception {
        Counter c = new Counter();

        int maxCount = 1000000000;
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                System.out.println(String.format("ThreadID: %d", Thread.currentThread().getId()));
                for (int i = 0; i < maxCount; i++) {
                    c.increment();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                System.out.println(String.format("ThreadID: %d", Thread.currentThread().getId()));
                for (int i = 0; i < maxCount; i++) {
                    c.decrement();
                }
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            System.out.println(String.format("Counter: %d", c.GetCounter()));
            if (c.GetCounter() == 0)
                System.out.println("Success!!!");
            else
                System.out.println("Failure!!!");
        }
    }
}