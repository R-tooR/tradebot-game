package test;

import java.util.HashMap;
import java.util.Map;

public class FibonacciMemoized {

    private Map<Integer, Integer> cache = new HashMap<>();

    public FibonacciMemoized() {
        cache.put(0, 0);
        cache.put(1, 1);
    }
    public int get(int i) {
        if(i < 0) return -1;
        if (cache.containsKey(i)) {
            return cache.get(i);
        } else {
            cache.put(i, get(i-1) + get(i-2));
        }
        return cache.get(i);
    }
}
