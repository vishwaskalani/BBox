package com.example.stream;

// this file will test the function written in Kafka.java
public class TestKafka {
	public static void main(String[] args) {
		Kafka kafka = new Kafka();
		try {
			kafka.readStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
