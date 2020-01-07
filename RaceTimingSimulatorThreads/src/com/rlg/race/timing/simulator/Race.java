package com.rlg.race.timing.simulator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.rlg.race.timing.simulator.dao.EventControlDao;
import com.rlg.race.timing.simulator.model.Driver;
import com.rlg.race.timing.simulator.model.Event;
import com.rlg.race.timing.simulator.model.Team;
import com.rlg.race.timing.simulator.model.Track;
import com.rlg.race.timing.simulator.thread.Runner;

public class Race {
	
	public static void main(String[] args) throws Exception {

		Team mercedes	= new Team("Mercedes    ", 100, 1, 3);
		Team ferrari	= new Team("Ferrari     ", 99, 2, 4);
		Team redBull	= new Team("Red Bull    ", 99, 2, 6);
		Team mcLaren	= new Team("McLaren     ", 98, 3, 6);
		Team toroRosso	= new Team("Toro Rosso  ", 98, 3, 6);
		Team renault	= new Team("Renault     ", 98, 3, 6);
		Team alfaRomeo	= new Team("Alfa Romeo  ", 97, 3, 7);
		Team racingPoint= new Team("Racing Point", 97, 3, 7);
		Team hass		= new Team("Hass        ", 97, 3, 7);
		Team wiliams	= new Team("Williams    ", 96, 4, 8);

		List<Driver> drivers = Arrays.asList(
				new Driver("Hamilton  ", "44", 0, 3, 3, mercedes),
				new Driver("Bottas    ", "77", 1, 4, 3, mercedes),
				new Driver("Vettel    ", "5", 1, 3, 2, ferrari),
				new Driver("Leclerc   ", "16", 1, 4, 3, ferrari),
				new Driver("Verstappen", "33", 0, 3, 2, redBull),
				new Driver("Gasly     ", "10", 2, 4, 4, redBull),
				new Driver("Sainz     ", "55", 1, 4, 2, mcLaren),
				new Driver("Norris    ", "4", 2, 4, 3, mcLaren),
				new Driver("Ricciardo ", "3", 1, 3, 3, renault),
				new Driver("Hulkenberg", "27", 1, 3, 3, renault),
				new Driver("Raikonnen ", "7", 0, 3, 2, alfaRomeo),
				new Driver("Giovinazzi", "99", 1, 4, 2, alfaRomeo),
				new Driver("Kvyat     ", "26", 1, 4, 2, toroRosso),
				new Driver("Albon     ", "23", 2, 4, 2, toroRosso),
				new Driver("Stroll    ", "18", 2, 4, 3, racingPoint),
				new Driver("Perez     ", "11", 0, 3, 2, racingPoint),
				new Driver("Magnussen ", "20", 1, 4, 3, hass),
				new Driver("Grosjean  ", "8", 1, 5, 2, hass),
				new Driver("Russel    ", "63", 1, 4, 3, wiliams),
				new Driver("Kubica    ", "88", 3, 5, 2, wiliams)
				);

		Track track = new Track("Interlagos", 1000);
		
		startQualifying(drivers, track);
		
		startRace(track);

	}
	
	public static void startQualifying(List<Driver> drivers, Track track) throws InterruptedException {

		Event qualiEvent = new Event("Qualifying", 6, track, getQualifyingLapsDao());
		
		System.out.println("**** Starting " + qualiEvent.getName() + " event...****");

		//Starting driver threads
		drivers.forEach(driver -> new Runner(driver, qualiEvent).start());

		Thread.sleep(1000);

		printGrid(qualiEvent, drivers);
	}

	public static void printGrid(Event event, List<Driver> drivers) throws InterruptedException {
		
		long driversNotRunning = 0;
		
		while(driversNotRunning < drivers.size())
		{
			//Cloning drivers and their laps
			Map<Driver, List<Long>> cloneQualiLaps = new ConcurrentHashMap<>(getQualifyingLapsDao()).entrySet()
																									.stream()
																									.collect(Collectors.toMap(entry -> entry.getKey(), entry -> new ArrayList<Long>(entry.getValue())));
			
			List<Entry<Driver, List<Long>>> orderedList = cloneQualiLaps.entrySet()
																		.stream()
																		.sorted(Comparator.comparingLong(entry -> entry.getValue()
																														.stream()
																														.min(Comparator.comparingLong(lap -> lap.longValue()))
																														.orElse(0L)))
																		.sorted(Comparator.comparingInt(entry2 -> ((Entry<Driver, List<Long>>) entry2).getValue().size())
																							.reversed())
																		.collect(Collectors.toList());
			
			Entry<Driver, List<Long>> leadDriver	= orderedList.stream()
																	.findFirst()
																	.get();

			int qtdLapsLeader						= leadDriver.getValue().size();

			long bestLapLeader						= leadDriver.getValue()
																	.stream()
																	.collect(Collectors.minBy(Comparator.comparingLong(lap -> lap.longValue())))
																	.orElse(0L);

			System.out.println("****" + event.getName() + "****");
			System.out.println("Laps: " + qtdLapsLeader + " / " + event.getLaps());
			System.out.println("Pos\tDriver     - Best Lap  - Diff    - Laps");
			
			for (int i = 0 ; i < orderedList.size() ; i++) {

				Entry<Driver, List<Long>> driver= orderedList.get(i);

				List<Long> driverLaps	= driver.getValue();
				
				String lapsFormatted	= driverLaps.stream()
												.map(lap -> formatDate(lap))
												.collect(Collectors.joining(", ", "[", "]"));

				long bestLap			= driverLaps.stream()
												.collect(Collectors.minBy(Comparator.comparingLong(lap -> lap.longValue())))
												.orElse(0L);

				System.out.println((i+1) + ".\t" + 
									driver.getKey().getDriverName() + " - " + 
									(bestLap <= 0 ? "" : formatDate(bestLap)) + " - " +
									(bestLap - bestLapLeader <= 0 ? "       " : "+" + formatDate(bestLap - bestLapLeader, "ss.SSS")) + " - " + 
									lapsFormatted);		
			}
			
			driversNotRunning						 = orderedList.stream()
																	.filter(entry -> !entry.getKey().isRunning())
																	.count();

			System.out.println("------------------");
			Thread.sleep(1000);

		}
	}
	
	public static void startRace(Track track) throws InterruptedException {

		//Getting qualifying results
		List<Driver> orderedQuali = getQualifyingLapsDao().entrySet()
															.stream()
															.sorted(Comparator.comparingLong(entry -> ((Map.Entry<Driver, List<Long>>) entry).getValue()
																																			.stream()
																																			.min(Comparator.comparingLong(lap -> lap.longValue()))
																																			.orElse(0L)))
															.map(entry -> entry.getKey())
															.collect(Collectors.toList());

		Event raceEvent = new Event("Race", 20, track, getRaceLapsDao());
		
		System.out.println("**** Starting " + raceEvent.getName() + " event...****");
		
		Thread.sleep(5000);
		
		//Starting driver threads in qualifying positions order
		for (int i = 0; i < orderedQuali.size(); i++) {
		
			new Runner(orderedQuali.get(i), raceEvent, i+1).start();
		}
		
		printRace(raceEvent, orderedQuali);
	}
	
	public static void printRace(Event event, List<Driver> driversQuali) throws InterruptedException {
		
		long driversNotRunning = 0;
		
		while(driversNotRunning < driversQuali.size())
		{
			//Cloning drivers and their laps
			Map<Driver, List<Long>> cloneRaceLaps = new ConcurrentHashMap<>(getRaceLapsDao()).entrySet()
																							.stream()
																							.collect(Collectors.toMap(entry -> entry.getKey(), entry -> new ArrayList<Long>(entry.getValue())));

			List<Entry<Driver, List<Long>>> orderedList = cloneRaceLaps.entrySet()
																		.stream()
																		.sorted(Comparator.comparingLong(driver -> ((Entry<Driver, List<Long>>) driver).getValue()
																																						.stream()
																																						.mapToLong(lap -> lap.longValue())
																																						.sum()))
																		.sorted(Comparator.comparingInt(driver2 -> ((Entry<Driver, List<Long>>) driver2).getValue().size())
																							.reversed())
																		.collect(Collectors.toList());
			
			String fastestLap						= orderedList.stream()
																.map(entry -> entry.getValue())
																.flatMap(Collection::stream)
																.collect(Collectors.minBy(Comparator.comparingLong(lap -> lap.longValue())))
																.map(lap -> formatDate(lap.longValue()))
																.orElse("");
											
			Entry<Driver, List<Long>> leadDriver	= orderedList.stream()
																.findFirst()
																.get();

			long sumLapsLeader						= leadDriver.getValue()
																.stream()
																.collect(Collectors.summingLong(lap -> lap.longValue()));
			
			int qtdLapsLeader						= leadDriver.getValue().size();

			System.out.println("****" + event.getName() + "****");
			System.out.println("Laps: " + qtdLapsLeader + " / " + event.getLaps());
			System.out.println("Pos\tDriver     - Race time - Leader     - Gap        - Last Lap  - Diff       - Best Lap    - G/L - Laps");
			
			for (int i = 0 ; i < orderedList.size() ; i++) {

				Entry<Driver, List<Long>> driver= orderedList.get(i);

				List<Long> driverLaps			= driver.getValue()
														.stream()
														.collect(Collectors.toList());
				
				long lastLap					= driverLaps.size() > 0 ? driverLaps.get(driverLaps.size() - 1) : 0;
				
				String bestLap					= driverLaps.stream()
															.collect(Collectors.minBy(Comparator.comparingLong(lap -> lap.longValue())))
															.map(lap -> formatDate(lap.longValue()))
															.orElse("");
				
				String lapsFormatted			= driverLaps.stream()
															.map(lap -> {
																			String aux = formatDate(lap);
																			return aux + " " + (aux.equals(bestLap) ? "x": " ") + (aux.equals(fastestLap) ? "*" : " ");
																		}
																	)
															.collect(Collectors.joining(", ", "[", "]"));
				
				long sumLaps					= driverLaps.stream()
															.collect(Collectors.summingLong(lap -> lap.longValue()));

				long qtdLaps					= driverLaps.size();
				
				long differenceLeader			= sumLaps - sumLapsLeader;

				long sumLapsPreviousDriver		= 0;
				
				long lastLapPreviousDriver		= 0;
				
				if(i > 0) {
					
					List<Long> lapsPreviousDriver	= orderedList.get(i-1)
																.getValue();
					
					lastLapPreviousDriver			= lapsPreviousDriver.size() > 0 ? lapsPreviousDriver.get(lapsPreviousDriver.size() - 1) : 0;
					
					sumLapsPreviousDriver			= lapsPreviousDriver.stream()
																		.collect(Collectors.summingLong(lap -> lap.longValue()));
					
				}

				System.out.println(	(i+1) + ".\t" + 
									driver.getKey().getDriverName() + " - " + 
									(sumLaps > 0 ? formatDate(sumLaps) : "") + " - " + 
									(differenceLeader > 0 ? "+" + ((qtdLapsLeader - qtdLaps)  > 0 ? (qtdLapsLeader - qtdLaps) + " Laps   " : formatDate(differenceLeader)) : "          ") + " - " + 
									(sumLapsPreviousDriver > 0 && sumLaps > 0 && differenceLeader > 0 ? (sumLaps - sumLapsPreviousDriver > 0 ? "+" + formatDate(sumLaps - sumLapsPreviousDriver) : "+" + formatDate(sumLapsPreviousDriver - sumLaps)) : "          ") + " - " +
									(lastLap > 0 ? formatDate(lastLap) : " ") + " - " +
									(lastLapPreviousDriver > 0 && lastLap > 0 ? (lastLap - lastLapPreviousDriver) > 0 ? "+" + formatDate(lastLap - lastLapPreviousDriver) : "-" + formatDate(lastLapPreviousDriver - lastLap) : "          ") + " - " +
									bestLap + (!fastestLap.isEmpty() && fastestLap.equals(bestLap) ? " *" : "  ") + " - " +
									(sumLaps > 0 && (i - driversQuali.indexOf(driver.getKey()) >= 0) ? "+" : "") + (sumLaps > 0 ? i - driversQuali.indexOf(driver.getKey()) + " " : "   ") + " - " +
									lapsFormatted);
			}
			
			driversNotRunning						 = orderedList.stream()
																.filter(entry -> !entry.getKey().isRunning())
																.count();


			System.out.println("------------------");
			Thread.sleep(1000);
		}
	}
	
	public static String formatDate(Long time) {
		
		return formatDate(time, "mm:ss.SSS");
	}
	
	public static String formatDate(Long time, String pattern) {
		
		return new SimpleDateFormat(pattern).format(time);
	}
	
	public static ConcurrentMap<Driver, List<Long>> getQualifyingLapsDao() {
		
		return EventControlDao.getQualifyingLapsData();
	}

	public static ConcurrentMap<Driver, List<Long>> getRaceLapsDao() {
		
		return EventControlDao.getRaceLapsData();
	}
}
