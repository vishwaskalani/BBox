package com.example.query;
import com.example.stream.PairBB;
public class Component {

    public Integer power;
    public String type;
	public boolean isPair_related;
	public Integer cid1;
	public Integer cid2;

    public boolean pred(PairBB element) {
        if (element.getobject1().getobj_class()==cid1 && element.getobject2().getobj_class()==cid2) {
            return Operator.inside(element.getobject1(),element.getobject2());
        }
        else {
            return false;
        }
    };

}
