package com.coralocean.sqldefiner.util;

import java.util.HashMap;
import java.util.Map;

public class Incrementer {
    public Map<String, Integer> counts;

    public Incrementer() {
        this.counts = new HashMap<>();
    }

    public int increment(String attribute) {
        this.counts.putIfAbsent(attribute, -1);
        this.counts.put(attribute, this.counts.get(attribute) + 1);
        return this.counts.get(attribute);
    }

    public void reset(String attribute) {
        this.counts.put(attribute, -1);
    }

    public void resetAll() {
        for (String attribute : counts.keySet()) {
            this.counts.put( attribute, -1 );
        }
    }


}
