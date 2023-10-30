package edu.neu.csye6225;

import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;

public class Foo {
    private static final StatsDClient statsd = new NonBlockingStatsDClient("my.prefix", "statsd-host", 8125);

    public static void main(String[] args) {
        statsd.incrementCounter("bar");
        statsd.recordGaugeValue("baz", 100);
        statsd.recordExecutionTime("bag", 25);
        statsd.recordSetEvent("qux", "one");
    }
}