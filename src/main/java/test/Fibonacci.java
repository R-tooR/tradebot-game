package test;

public class Fibonacci {
    public int get(int i) {
        if(i < 0) return -1;
        if (i == 1 || i == 0) return 1;
        return get(i-1) + get(i-2);
    }
}
