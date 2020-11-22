package locks;

import java.util.concurrent.atomic.AtomicInteger;

public class Filter {
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

    public static class ThreadLock extends Thread {
        private static int maxCount = 1000;
        private static int idCount = 0;
        private static int threadCount = 3;
        private static Counter c = new Counter();
        private static Filter filter = new Filter(ThreadLock.threadCount);

        public static void main(String[] args) {

            ThreadLock[] threads = new ThreadLock[ThreadLock.threadCount];
            for (int i = 0; i < ThreadLock.threadCount; i++) {
                threads[i] = new ThreadLock();
                threads[i].start();
            }

            try {
                for (ThreadLock thread : threads) {
                    thread.join();
                }
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

        private int id;

        ThreadLock() {
            this.id = ThreadLock.idCount++;
        }

        private void incOperation() {
            System.out.println(String.format("Thread %d is entering critical section", this.id));
            filter.lock(this.id);
            c.increment();
            try {
                sleep((int) (Math.random() * 2));
            } catch (InterruptedException e) {
            }
            filter.unlock(this.id);
        }

        private void decOperation() {
            System.out.println(String.format("Thread %d is entering critical section", this.id));
            filter.lock(this.id);
            c.decrement();
            try {
                sleep((int) (Math.random() * 2));
            } catch (InterruptedException e) {
            }
            filter.unlock(this.id);
        }

        @Override
        public void run() {
            for (int i = 0; i < ThreadLock.maxCount; i++) {
                incOperation();
                decOperation();
            }
        }
    }

    private AtomicInteger[] level;
    private AtomicInteger[] victim;

    private int threadCount;

    public Filter(int n) {
        this.threadCount = n;
        level = new AtomicInteger[n];
        victim = new AtomicInteger[n];
        for (int i = 0; i < n; i++) {
            level[i] = new AtomicInteger();
            victim[i] = new AtomicInteger();
        }
    }

    public void lock(int me) {
        for (int i = 1; i < this.threadCount; i++) {
            level[me].set(i);
            victim[i].set(me);
            for (int k = 0; k < this.threadCount; k++) {
                while ((k != me) && (level[k].get() >= i && victim[i].get() == me)) {
                }
            }
        }
    }

    public void unlock(int me) {
        level[me].set(0);
    }
}
