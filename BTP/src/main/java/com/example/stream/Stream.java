// this file will help us read stream of events from a kafka topic
// and then write them to a file
// we will use the kafka-clients library to read from kafka
// and the java.io library to write to a file

package com.example.stream;

import com.example.nfa.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.regex.Matcher;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.util.Collector;
import org.apache.flink.core.fs.FileSystem;

import java.util.Properties;

public class Stream {

    public void readStream() throws Exception {

		NFA nfa = new NFA();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", "localhost:9092");
        kafkaProps.setProperty("group.id", "flink-group");

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("events")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

		DataStream<String> kafkaStream = env.fromSource(kafkaSource,WatermarkStrategy.noWatermarks(), "Kafka Source");

		DataStream<Event<Integer, Integer, Integer, String, Float, Float, Float, Float>> eventStream = kafkaStream.map(new MapFunction<String, Event<Integer, Integer, Integer, String, Float, Float, Float, Float>>() {
        	@Override
        	public Event<Integer, Integer, Integer, String, Float, Float, Float, Float> map(String value) {
            	value = value.substring(1, value.length() - 1);
            	String[] values = value.split(", ");
            	int frame_id = Integer.parseInt(values[0]);
            	int obj_id = Integer.parseInt(values[1]);
            	int obj_class = Integer.parseInt(values[2]);
            	String color = values[3];

				// System.out.println("Transitioning NFA based on color: " + color);
				
				// Transition the NFA based on the color of the event
				nfa.transition(color);

				// if the NFA is in an accepting state, then print "Detection of red object!"
				if(nfa.isInAcceptingState()){
					System.out.println("Detection of red object!");
				}


            	float xmin = Float.parseFloat(values[4]);
            	float ymin = Float.parseFloat(values[5]);
            	float xmax = Float.parseFloat(values[6]);
            	float ymax = Float.parseFloat(values[7]);
            	return new Event<>(frame_id, obj_id, obj_class, color, xmin, ymin, xmax, ymax);
        	}
    	});

		// eventStream.print();

        // Filter the stream based on the NFA's accepting state
        // DataStream<Event<Integer, Integer, Integer, String, Float, Float, Float, Float>> filteredStream = eventStream.filter(event -> nfa.isInAcceptingState());

        // Print the filtered stream
        // filteredStream.print();

        env.execute("Kafka Consumer");
    }
}


