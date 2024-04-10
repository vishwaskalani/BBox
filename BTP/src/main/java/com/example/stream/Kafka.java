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


public class Kafka {

	// a variable to store the last frame id that was processed
	private int last_frame_id = 0;

	// a function to process the frame
	public void processFrame(ArrayList<Event<Integer, Integer, Integer, String, Float, Float, Float, Float>> frame_history, NFA nfa) {
		// generate all the pair of events from frame history
		// and then process them
		ArrayList<PairBB> pairs = new ArrayList<>();
		for (int i = 0; i < frame_history.size(); i++) {
			for (int j = i + 1; j < frame_history.size(); j++) {
				PairBB pair = new PairBB(frame_history.get(i), frame_history.get(j));
				pairs.add(pair);
				// print the object ids of the pair
				// System.out.println("Pair of objects with id: " + pair.getobject1().getobj_id() + " and " + pair.getobject2().getobj_id());
				// nfa.transition(pair);
				// if (nfa.isInAcceptingState()) {
				// 	System.out.println("Accepting state reached for objects with id: " + pair.getobject1().getobj_id() + " and " + pair.getobject2().getobj_id());
				// }
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

		NFA nfa = new NFA();

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
							processFrame(frame_history, nfa);
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

