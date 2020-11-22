package locks;

public class Peterson implements Runnable {
    public static class Counter {
        private int counter;

        Counter() {
            this.counter = 0;
        }

        public void increment() {
            this.counter++;
        }

        public void decrement() {
            this.counter--;
        }

        public int getCounter() {
            return this.counter;
        }
    }

    private static int maxCount = 1000;
    private static boolean[] in = { false, false };
    private static volatile int turn = -1;
    private static Counter c = new Counter();

    public static void main(String[] args) {
        Thread t1 = new Thread(new Peterson(0), "Thread - 0");
        Thread t2 = new Thread(new Peterson(1), "Thread - 1");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(String.format("Counter: %d", c.getCounter()));
            if (c.getCounter() == 0)
                System.out.println("Success!!!");
            else
                System.out.println("Failure!!!");
        }
    }

    private final int id;

    public Peterson(int i) {
        this.id = i;
    }

    private int other() {
        return this.id == 0 ? 1 : 0;
    }

    @Override
    public void run() {
        for (int i = 0; i < Peterson.maxCount; i++) {
            Peterson.in[this.id] = true;
            Peterson.turn = other();
            while (Peterson.in[other()] && Peterson.turn == other()) {
            }

            System.out.println(String.format("Thread %d is entering critical section", this.id));

            if (this.id == 0)
                c.increment();

            else
                c.decrement();

            try {
                Thread.sleep((int) (Math.random() * 2));
            } catch (InterruptedException e) {
            }

            Peterson.in[this.id] = false;
        }
    }
}
