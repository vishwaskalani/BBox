package com.example.stream;

public class TestStream {
	public static void main(String[] args) {
		Stream stream = new Stream();
		try {
			stream.readStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
