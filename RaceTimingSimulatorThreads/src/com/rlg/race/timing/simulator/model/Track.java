package com.rlg.race.timing.simulator.model;

public class Track {
	
	private String name	= "";
	private int length	= 0;
	
	public Track(String name, int length) {

		this.name	= name;
		this.length	= length;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
}
