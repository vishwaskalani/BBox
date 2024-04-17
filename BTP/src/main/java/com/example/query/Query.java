package com.example.query;

import java.util.Vector;

public class Query {

    public static Vector<Component> components = new Vector<>();

	// query type 1
    public static void build1() {
		components.clear();
        Component comp1 = new Component();
        comp1.power=50;
        comp1.type="num";
		comp1.isPair_related = true;
		comp1.cid1 = 67;
		comp1.cid2 = 0;
		comp1.operationType = Component.OperationType.INSIDE;
        components.add(comp1);
    }

	// query type 2
	public static void build2(){
		components.clear();
		Component comp1 = new Component();
		comp1.power=1;
		comp1.type="num";
		comp1.isPair_related = true;
		comp1.cid1 = 67;
		comp1.cid2 = 0;
		comp1.operationType = Component.OperationType.INSIDE;
		components.add(comp1);
		Component comp2 = new Component();
		comp2.power=10;
		comp2.type="num";
		comp2.isPair_related = true;
		comp2.cid1 = 67;
		comp2.cid2 = 0;
		comp2.operationType = Component.OperationType.DISJOINT;
		components.add(comp2);
	}
}
