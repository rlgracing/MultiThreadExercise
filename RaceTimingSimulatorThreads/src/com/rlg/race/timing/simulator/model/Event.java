package com.rlg.race.timing.simulator.model;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class Event {

	private String	name	= "";
	private int		laps	= 0;
	private boolean	finished= false;
	private Track	track	= null;
	private ConcurrentMap<Driver, List<Long>> lapTimes = null;
	
	public Event(String name, int laps, Track track, ConcurrentMap<Driver, List<Long>> lapTimes) {
		
		this.name		= name;
		this.laps		= laps;
		this.track		= track;
		this.lapTimes	= lapTimes;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the laps
	 */
	public int getLaps() {
		return laps;
	}

	/**
	 * @return the track
	 */
	public Track getTrack() {
		return track;
	}

	public ConcurrentMap<Driver, List<Long>> getLapTimes() {
		return lapTimes;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
}
