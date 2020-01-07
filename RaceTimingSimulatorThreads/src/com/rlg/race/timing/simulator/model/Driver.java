package com.rlg.race.timing.simulator.model;
import java.util.Objects;
import java.util.SplittableRandom;

public class Driver {
	
	private String name			= "";
	private String number		= "";
	private int minConstancy	= 0;
	private int maxConstancy	= 0;
	private int ability			= 0;
	private boolean running		= false;
	private Team team			= null;
	
	public Driver(String name, String number, int minConstancy, int maxConstancy, int ability, Team team) {

		this.name			= name;
		this.number			= number;
		this.minConstancy	= minConstancy;
		this.maxConstancy	= maxConstancy;
		this.ability		= new SplittableRandom().nextInt(ability);
		this.team			= team;
	}

	public String getDriverName() {
		return name;
	}

	public String getNumber() {
		return number;
	}

	public int getMinConstancy() {
		return minConstancy;
	}

	public int getMaxConstancy() {
		return maxConstancy;
	}

	public int getAbility() {
		return ability;
	}

	public Team getTeam() {
		return team;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ability, maxConstancy, minConstancy, name, number, team);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Driver [name=" + name + ", number=" + number + ", team=" + team + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Driver)) {
			return false;
		}
		Driver other = (Driver) obj;
		return ability == other.ability && maxConstancy == other.maxConstancy && minConstancy == other.minConstancy
				&& Objects.equals(name, other.name) && Objects.equals(number, other.number)
				&& Objects.equals(team, other.team);
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
