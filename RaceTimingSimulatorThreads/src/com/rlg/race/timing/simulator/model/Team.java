package com.rlg.race.timing.simulator.model;
import java.util.Objects;
import java.util.SplittableRandom;

public class Team {
	
	private String name			= "";
	private int power			= 0;
	private int minConstancy	= 0;
	private int maxConstancy	= 0;
	private int constancy		= 0;
	
	public Team(String name, int power, int minConstancy, int maxConstancy) {
		this.name			= name;
		this.power			= power;
		this.minConstancy	= minConstancy;
		this.maxConstancy	= maxConstancy;
		this.constancy		= new SplittableRandom().nextInt(minConstancy, maxConstancy+1);
	}

	public int getPower() {
		return power;
	}

	public int getConstancy() {
		return constancy;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(constancy, maxConstancy, minConstancy, name, power);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Team)) {
			return false;
		}
		Team other = (Team) obj;
		return constancy == other.constancy && maxConstancy == other.maxConstancy && minConstancy == other.minConstancy
				&& Objects.equals(name, other.name) && power == other.power;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Team [name=" + name + "]";
	}

//	public void run() {
//		long result				= 0;
//		int meters				= 0;
//		int powerSecs100Meters	= ((100-this.power)+100);
//		int constancy			= new Random().nextInt(this.constancy);
//		
//		System.out.println(this.getName() + ": " + constancy);
//		
//		result += powerSecs100Meters * 100000;
//		result += constancy * 100;
//
//		long start = System.currentTimeMillis();
//		while(true) {
//			
//			Waiter.forceWait(result + (new Random().nextInt(this.constancy) * 10));
//			
//			meters++;
//			
//			if(this.trackLength == meters) {
//				break;
//			}
//		}
//		long end = System.currentTimeMillis();
//		
//		System.out.println(this.getName() + ": " + new SimpleDateFormat("mm:ss.SSS").format(new Date(end - start)));
//    }
}
