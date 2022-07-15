package com.demo;

public class ElevatorFloorState implements Comparable<ElevatorFloorState> {
	private Integer floorNumber;
	private String state;
	private String startOrStop;

	public ElevatorFloorState(Integer floorNumber, String state) {
		super();
		this.floorNumber = floorNumber;
		this.state = state;
	}
	
	

	public ElevatorFloorState(Integer floorNumber, String state, String starOrStop) {
		super();
		this.floorNumber = floorNumber;
		this.state = state;
		this.startOrStop = starOrStop;
	}



	public Integer getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(Integer floorNumber) {
		this.floorNumber = floorNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStarOrStop() {
		return startOrStop;
	}

	public void setStarOrStop(String starOrStop) {
		this.startOrStop = starOrStop;
	}

	@Override
	public String toString() {
		return "ElevatorFloorState [floorNumber=" + floorNumber + ", state=" + state + "]";
	}

	public int compareTo(ElevatorFloorState o) {
		return this.floorNumber.compareTo(o.floorNumber);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((floorNumber == null) ? 0 : floorNumber.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElevatorFloorState other = (ElevatorFloorState) obj;
		if (floorNumber == null) {
			if (other.floorNumber != null)
				return false;
		} else if (!floorNumber.equals(other.floorNumber))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

}
