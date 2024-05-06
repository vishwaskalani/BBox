package com.example.query;
import com.example.stream.PairBB;
public class Component {

    public Integer power;
    public String type;
	public boolean isPair_related;
	public Integer cid1;
	public Integer cid2;
	public enum OperationType{
		INSIDE, DISJOINT, BEHIND, INFRONT, INTERSECT
	};
	public OperationType operationType;

    public boolean pred(PairBB element) {
        if (element.getobject1().getobj_class()==cid1 && element.getobject2().getobj_class()==cid2) {
			switch (operationType) {
				case INSIDE:
					return Operator.inside(element.getobject1(),element.getobject2());
				case DISJOINT:
					return Operator.disjoint(element.getobject1(),element.getobject2());
				case BEHIND:
					return Operator.behind(element.getobject1(),element.getobject2());
				case INFRONT:
					return Operator.infront(element.getobject1(),element.getobject2());
				case INTERSECT:
					return Operator.intersect(element.getobject1(),element.getobject2());
				default:
					return false;
			}
        }
        else {
            return false;
        }
    };

}
