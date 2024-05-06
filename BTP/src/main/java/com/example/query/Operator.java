package com.example.query;

import com.example.stream.Event;

public class Operator {

	//checks if obj1 is inside obj2
    public static boolean inside(Event obj1, Event obj2) {
        return obj1.getxmin().compareTo(obj2.getxmin()) > 0 && obj1.getxmax().compareTo(obj2.getxmax()) < 0 && obj1.getymin().compareTo(obj2.getymin()) > 0 && obj1.getymax().compareTo(obj2.getymax()) < 0;
    };

	//checks if obj1 is completely disjoint from obj2 without any overlap
	public static boolean disjoint(Event obj1, Event obj2) {
		return obj1.getxmax().compareTo(obj2.getxmin()) < 0 || obj1.getxmin().compareTo(obj2.getxmax()) > 0 || obj1.getymax().compareTo(obj2.getymin()) < 0 || obj1.getymin().compareTo(obj2.getymax()) > 0;
	};

	public static boolean behind(Event obj1, Event obj2) {
		return obj1.getxmax().compareTo(obj2.getxmin()) < 0;
	};

	public static boolean infront(Event obj1, Event obj2) {
		return obj1.getxmin().compareTo(obj2.getxmax()) > 0;
	};

	public static boolean intersect(Event obj1, Event obj2) {
		return !disjoint(obj1, obj2);
	};

}
