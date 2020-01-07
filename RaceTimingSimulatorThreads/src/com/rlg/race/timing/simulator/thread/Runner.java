package com.rlg.race.timing.simulator.thread;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.rlg.race.timing.simulator.model.Driver;
import com.rlg.race.timing.simulator.model.Event;

public class Runner extends Thread {
	
	private	Driver driver	= null;
	private Event event		= null;
	private	int position	= 1;
	
	public Runner(Driver driver, Event event) {
		this.setName(driver.getDriverName());

		this.driver	= driver;
		this.event	= event;
	}

	public Runner(Driver driver, Event event, int position) {
		this.setName(driver.getDriverName());

		this.driver		= driver;
		this.event		= event;
		this.position	= position;
	}

	public void run() {
		
		getDriver().setRunning(true);

		final BlockingQueue<Long> SLEEPER = new ArrayBlockingQueue<Long>(1);
	    
	    List<Long> lapTimes = new ArrayList<Long>();
		
		getEvent().getLapTimes().put(getDriver(), lapTimes);
		
		long result				= getPosition() > 1 ? ((getPosition() - 1) * 2) + new SplittableRandom().nextInt(0, 3) : 0;

		for(int i = 0 ; i < getEvent().getLaps() && !getEvent().isFinished(); i++) {
			
			int powerSecs100Meters	= (100 - getDriver().getTeam().getPower()) + 100 - i;
			int teamConstancy		= getDriver().getTeam().getConstancy();
			int driverConstancy		= new SplittableRandom().nextInt(getDriver().getMinConstancy(), getDriver().getMaxConstancy()+1);
			int driverAbility		= getDriver().getAbility();
			int tireWear			= new SplittableRandom().nextInt(i, i+2);
						
			result += powerSecs100Meters;
			result += teamConstancy;
			result += driverConstancy;
			result += driverAbility;
			result += tireWear;
			result = (result * 100 * getEvent().getTrack().getLength()) + new SplittableRandom().nextInt(50000, 100001);

            try {
				SLEEPER.poll(result, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			lapTimes.add(new Long(result/1000));

			result = 0;
		}
		
		getDriver().setRunning(false);
		getEvent().setFinished(true);
		
    }

	/**
	 * @return the driver
	 */
	public Driver getDriver() {
		return this.driver;
	}

	/**
	 * @return the event
	 */
	public Event getEvent() {
		return this.event;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return this.position;
	}

}
