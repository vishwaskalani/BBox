package com.example.query;

import java.util.Vector;

public class Query {

    public static Vector<Component> components = new Vector<>();

	// query type 1
    public static void build1() {
        Component comp1 = new Component();
        comp1.power=50;
        comp1.type="num";
		comp1.isPair_related = true;
		comp1.cid1 = 67;
		comp1.cid2 = 0;
        components.add(comp1);
    }
}
