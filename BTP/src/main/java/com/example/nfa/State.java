// this class is used to represent a state in the NFA

package com.example.nfa;

import java.util.HashSet;
import java.util.Set;
import com.example.query.Component;
import com.example.query.Operator;
import com.example.query.Query;

// the states will either be a start state, accepting state, have a self loop or have a next state
public class State {

	boolean isStartState;
	boolean isAcceptingState;
	boolean haselfLoop;
	boolean hasnextState;

	State nextState;
	State startState;

	// transition function to go to next state (represented by component whose pred we are using)
	Component transition_to_next;
	Component transition_to_self;
	


	
}
