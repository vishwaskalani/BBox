package com.example.query;

import java.util.Vector;

public class Query {

    public static Vector<Component> components = new Vector<>();

    public static void build() {
        Component comp1 = new Component();
        comp1.power=10;
        comp1.type="num";
        components.add(comp1);
    }
}
