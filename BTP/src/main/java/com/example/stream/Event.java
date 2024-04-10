package com.example.stream;
import java.util.Objects;

public class Event<A, B, C, D,E extends Comparable<E>,F extends Comparable<F>,G extends Comparable<G>,H extends Comparable<H>> {
    private final A frame_id;
    private final B obj_id;
    private final C obj_class;
    private final D color;
    private final E xmin;
    private final F ymin;
    private final G xmax;
    private final H ymax;

    public Event(A frame_id, B obj_id, C obj_class, D color, E xmin, F ymin, G xmax, H ymax) {
        this.frame_id = frame_id;
        this.obj_id = obj_id;
        this.obj_class = obj_class;
        this.color = color;
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    public A getframe_id() {
        return frame_id;
    }

    public B getobj_id() {
        return obj_id;
    }

    public C getobj_class() {
        return obj_class;
    }

    public D getcolor() {
        return color;
    }

    public E getxmin() {
        return xmin;
    }

    public F getymin() {
        return ymin;
    }

    public G getxmax() {
        return xmax;
    }

    public H getymax() {
        return ymax;
    }

    @Override
    public String toString() {
        return "(" + frame_id + ", " + obj_id + ", " + obj_class + ", " +
               color + ", " + xmin + ", " + ymin + ", " + xmax + ", " + ymax + ")";
    }
}
