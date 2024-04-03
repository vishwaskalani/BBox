package com.example.query;

public class TestParser {
	public static void main(String[] args) {
		QueryParser parser = new QueryParser();
		System.out.println(parser.parseQuery("SELECT * FROM table"));
	}
	
}
