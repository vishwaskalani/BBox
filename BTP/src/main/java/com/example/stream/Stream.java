// this file will help us read stream of events from a kafka topic
// and then write them to a file
// we will use the kafka-clients library to read from kafka
// and the java.io library to write to a file

package com.example.stream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Stream {

    public static void main(String[] args) {
        // Kafka consumer properties
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Change to your Kafka bootstrap server address
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // Create Kafka consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // Subscribe to a Kafka topic
        consumer.subscribe(Collections.singletonList("your_topic_name")); // Change to the topic you want to subscribe to

        // Output file path
        String filePath = "output.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Continuously poll for new messages
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    // Write the message to the output file
                    writer.write(record.value());
                    writer.newLine();
                }
                // Flush the buffer to ensure all messages are written to the file
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the Kafka consumer when done
            consumer.close();
        }
    }
}

