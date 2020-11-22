package locks;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class Bakery {
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
        private static Bakery bakery = new Bakery(ThreadLock.threadCount);

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
            bakery.lock(this.id);
            c.increment();
            try {
                sleep((int) (Math.random() * 2));
            } catch (InterruptedException e) {
            }
            bakery.unlock(this.id);
        }

        private void decOperation() {
            System.out.println(String.format("Thread %d is entering critical section", this.id));
            bakery.lock(this.id);
            c.decrement();
            try {
                sleep((int) (Math.random() * 2));
            } catch (InterruptedException e) {
            }
            bakery.unlock(this.id);
        }

        @Override
        public void run() {
            for (int i = 0; i < ThreadLock.maxCount; i++) {
                incOperation();
                decOperation();
            }
        }
    }

    private AtomicIntegerArray ticket;
    private AtomicIntegerArray entering;
    private int threadCount;

    public Bakery(int threadCount) {
        this.threadCount = threadCount;
        this.ticket = new AtomicIntegerArray(threadCount);
        this.entering = new AtomicIntegerArray(threadCount);
    }

    public void lock(int pid) {
        entering.set(pid, 1);
        int max = 0;
        for (int i = 0; i < this.threadCount; i++) {
            int current = ticket.get(i);
            if (current > max) {
                max = current;
            }
        }
        ticket.set(pid, 1 + max);
        entering.set(pid, 0);
        for (int i = 0; i < ticket.length(); ++i) {
            if (i != pid) {
                while (entering.get(i) == 1) {
                    Thread.yield();
                }
                while (ticket.get(i) != 0
                        && (ticket.get(i) < ticket.get(pid) || (ticket.get(i) == ticket.get(pid) && i < pid))) {
                    Thread.yield();
                }
            }
        }
    }

    public void unlock(int pid) {
        ticket.set(pid, 0);
    }
}
