package com.example.query;

import com.example.stream.Event;

public class Operator {

    public static boolean inside(Event obj1, Event obj2) {
        return obj1.getxmin().compareTo(obj2.getxmin()) > 0 && obj1.getxmax().compareTo(obj2.getxmax()) < 0 && obj1.getymin().compareTo(obj2.getymin()) > 0 && obj1.getymax().compareTo(obj2.getymax()) < 0;
    };

}
