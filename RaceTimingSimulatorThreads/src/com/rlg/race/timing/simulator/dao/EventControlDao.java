package com.rlg.race.timing.simulator.dao;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.rlg.race.timing.simulator.model.Driver;

public class EventControlDao {

	private static ConcurrentMap<Driver, List<Long>> raceLaps	= new ConcurrentHashMap<Driver, List<Long>>();
	private static ConcurrentMap<Driver, List<Long>> qualiLaps 	= new ConcurrentHashMap<Driver, List<Long>>();
	
	public static ConcurrentMap<Driver, List<Long>> getRaceLapsData() {
		return raceLaps;
	}

	public static ConcurrentMap<Driver, List<Long>> getQualifyingLapsData() {
		return qualiLaps;
	}
}
