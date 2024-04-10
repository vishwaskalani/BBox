package com.example.query;

public class Component {

    Integer power;
    String type;

    public boolean pred(int c1, int c2, PairBB element) {
        if (element.getobject1().getobj_class()==c1 && element.getobject2().getobj_class()==c2) {
            return Operator.inside(element.getobject1(),element.getobject2());
        }
        else {
            return false;
        }
    };

}
