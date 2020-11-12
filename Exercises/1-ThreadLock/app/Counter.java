package app;

public class Counter {

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

    public int GetCounter() {
        return this.counter;
    }
}