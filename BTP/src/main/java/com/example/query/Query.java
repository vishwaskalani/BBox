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

	// query type 3 with plus and num
	public static void build3(){
		components.clear();
		Component comp1 = new Component();
		comp1.type="plus";
		comp1.isPair_related = true;
		comp1.cid1 = 2;
		comp1.cid2 = 2;
		comp1.operationType = Component.OperationType.BEHIND;
		components.add(comp1);
		Component comp2 = new Component();
		comp2.type="plus";
		comp2.isPair_related = true;
		comp2.cid1 = 2;
		comp2.cid2 = 2;
		comp2.operationType = Component.OperationType.INTERSECT;
		components.add(comp2);
		Component comp3 = new Component();
		comp3.type="plus";
		comp3.isPair_related = true;
		comp3.cid1 = 2;
		comp3.cid2 = 2;
		comp3.operationType = Component.OperationType.INFRONT;
		components.add(comp3);
	}
}
