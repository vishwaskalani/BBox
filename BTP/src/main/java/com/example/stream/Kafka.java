package com.example.stream;

import com.example.nfa.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.ArrayList;
import java.util.HashMap;


public class Kafka {

	// a variable to store the last frame id that was processed
	private int last_frame_id = 0;

	// a function to process the frame
	public void processFrame(ArrayList<Event<Integer, Integer, Integer, String, Float, Float, Float, Float>> frame_history,HashMap<String, NFA> pair_nfa) {
		// generate all the pair of events from frame history
		// and then process them
		ArrayList<PairBB> pairs = new ArrayList<>();
		for (int i = 0; i < frame_history.size(); i++) {
			for (int j = 0; j < frame_history.size(); j++) {

				if(i == j) continue;

				PairBB pair = new PairBB(frame_history.get(i), frame_history.get(j));
				pairs.add(pair);
				int id1 = pair.getobject1().getobj_id();
				int id2 = pair.getobject2().getobj_id();
				String key = id1 + " " + id2;
				// if the nfa corresponding to the pair is not present in the map then create a new nfa
				// else transition in the existing nfa
				if (!pair_nfa.containsKey(key)) {
					NFA nfa = new NFA();
					nfa.hardcodebuild();
					pair_nfa.put(key, nfa);
					nfa.transition(pair);
					if (nfa.isInAcceptingState()) {
						System.out.println("Accepting state reached for objects with id: " + pair.getobject1().getobj_id() + " and " + pair.getobject2().getobj_id());
					}
				}
				else{
					System.out.println("Transitioning in the existing nfa");
					NFA nfa = pair_nfa.get(key);
					nfa.transition(pair);
					if (nfa.isInAcceptingState()) {
						System.out.println("Accepting state reached for objects with id: " + pair.getobject1().getobj_id() + " and " + pair.getobject2().getobj_id());
					}
				}
				//print the size of the pair_nfa map
				// System.out.println("Size of pair_nfa map: " + pair_nfa.size());
				// print the object ids of the pair
				// System.out.println("Pair of objects with id: " + pair.getobject1().getobj_id() + " and " + pair.getobject2().getobj_id());
			}
		}

	}

    public void readStream() {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        String topic = "events"; // specify the topic here
        consumer.subscribe(Collections.singletonList(topic));

		// this will store all the events in the frame
		ArrayList<Event<Integer, Integer, Integer, String, Float, Float, Float, Float>> frame_history = new ArrayList<>();

		// map of pair of object ids and nfas
		HashMap<String, NFA> pair_nfa = new HashMap<>();

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    value = value.substring(1, value.length() - 1);
                    String[] values = value.split(", ");
                    int frame_id = Integer.parseInt(values[0]);
                    int obj_id = Integer.parseInt(values[1]);
                    int obj_class = Integer.parseInt(values[2]);
                    String color = values[3];

                    float xmin = Float.parseFloat(values[4]);
                    float ymin = Float.parseFloat(values[5]);
                    float xmax = Float.parseFloat(values[6]);
                    float ymax = Float.parseFloat(values[7]);

					// this will store the current event
                    Event<Integer, Integer, Integer, String, Float, Float, Float, Float> curr_event =
                            new Event<>(frame_id, obj_id, obj_class, color, xmin, ymin, xmax, ymax);

					if (frame_history.size() == 0) {
						frame_history.add(curr_event);
						last_frame_id = frame_id;
					} 
					else {
						if (frame_id == last_frame_id) {
							frame_history.add(curr_event);
						} 
						else {
							// process the frame
							processFrame(frame_history, pair_nfa);
							frame_history.clear();
							frame_history.add(curr_event);
							last_frame_id = frame_id;
						}
                	}
            	}
        	} 
		}
		finally {consumer.close();}
	}
}

