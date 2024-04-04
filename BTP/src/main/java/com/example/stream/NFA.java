package com.example.stream;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class NFA implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<Integer> currentState;

    public NFA() {
        // Initialize the NFA with the start state
        currentState = new HashSet<>();
        currentState.add(0); // Assuming state 0 is the start state
    }

    // Transition function to simulate the movement of the NFA based on input events
    public void transition(String eventColor) {
        Set<Integer> nextState = new HashSet<>();
		nextState.add(0);

        // Simulate the transitions based on the current state and input event
        for (int state : currentState) {
            // Assuming state 0 transitions to state 1 on encountering a "red" event
            if (state == 0 && eventColor.equals("RED")) {
				// System.out.println("Transitioning from state 0 to state 1 based on color: " + eventColor);
                nextState.add(1);
            }
            // Add other transitions here if needed
        }

        currentState = nextState;
    }

    // Check if the NFA is in an accepting state
    public boolean isInAcceptingState() {
		// if(currentState.contains(1)){
		// 	System.out.println("Accepting state reached");
		// }
        return currentState.contains(1); // Assuming state 1 is the accepting state
    }
}
