// this file will build nfa based on the components of the query
package com.example.nfa;

import java.util.Vector;
import java.util.HashSet;
import java.util.Set;
import com.example.query.Component;
import com.example.query.Operator;
import com.example.query.Query;
import com.example.stream.PairBB;
import com.example.stream.Event;

// this class will represent the NFA
public class NFA {

	public State startState;
	public State currentState;

	// this will build the nfa based on the components of the query
	public void build(Query q){
		// get the components of the query
		Vector<Component> components = q.components;
		startState = new State();
		startState.isStartState = true;
		Component firstComponent = components.get(0);
		if(firstComponent.type.equals("num")){
			State temp = startState;
			// get the power
			int power = firstComponent.power;
			// now we need to create the states
			for(int i=0; i<power; i++){
				if(i==power-1){
					startState.isAcceptingState = true;
					startState.hasnextState = false;
				}
				else{
					State newState = new State();
					startState.transition_to_next = firstComponent;
					startState.nextState = newState;
					startState.hasnextState = true;
					startState = newState;

				}
			}
			startState = temp;
			currentState = startState;
		}
	}

	public void hardcodebuild(){
		Query q = new Query();
		q.build1();
		build(q);
	}

	// this is the transition function
	public void transition(PairBB element){
		if(currentState.hasnextState){
			if(currentState.transition_to_next.pred(element)){
				currentState = currentState.nextState;
			}
			else{
				currentState = startState;
			}
		}
		// else if(currentState.haselfLoop){
		// 	if(currentState.transition_to_self.pred(c1,c2,element)){
		// 		currentState = currentState;
		// 	}
		// }
		else{
			currentState = startState;
		}
	}

	public boolean isInAcceptingState(){
		return currentState.isAcceptingState;
	}
	
}
