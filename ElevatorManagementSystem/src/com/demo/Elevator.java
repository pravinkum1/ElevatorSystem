package com.demo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;

public class Elevator implements Runnable{
	
	private int id;
	
	private ElevatorFloorState currentFloor;
	
	private Map<ElevatorState, NavigableSet<ElevatorFloorState>> floorStopsMap;
	
	private NavigableSet<ElevatorFloorState> floorStops;
	
	private boolean operating;
	
	private ElevatorState state;
	
	private int numberOfPersons;
	
	private int elevatorCapacity;
	
	
	public Elevator(int elevatorId ) {
		id = elevatorId;
		setOperating(true);
	}
	
	public void run() {
		while (true) {
			if (isOperating()) {
				synchronized (ElevatorController.getInstance()) {
					move();
					try {

						ElevatorController.getInstance().wait();
					} catch (InterruptedException e) {
						System.out.println(e.getMessage());

					}
				}
			} else {
				break;
			}

		}
	}
	
	private void move() {
		Iterator<ElevatorState> iter = floorStopsMap.keySet().iterator();
		while (iter.hasNext()) {
			state = iter.next();
			floorStops = floorStopsMap.get(state);
			ElevatorFloorState current = null;
			ElevatorFloorState next = null;
			iter.remove();
			long startTime = System.nanoTime();
			while (!floorStops.isEmpty()) {
				if (state.equals(ElevatorState.UP)) {
					current = floorStops.pollFirst();
					next = floorStops.higher(current);
				} else if (state.equals(ElevatorState.DOWN)) {
					current = floorStops.pollLast();
					next = floorStops.lower(current);
				}
				setCurrentFloor(current);
				if (next != null) {
					getInterMediateFloor(current, next);
				} else {
					this.state = ElevatorState.STATIONARY;
					ElevatorController.updateElevatorList(this);
				}

				// if elevator stops minus the number of person
				if (current.getStarOrStop() != null && current.getStarOrStop().equals(ElevatorConstants.STOP)) {
					numberOfPersons--;
				}
				System.out.println("Elevator ID " + this.id + " | Current floor - " + getCurrentFloor().toString()
						+ " | next move - " + getState() + " | number Of Persons " + numberOfPersons);
				requestServerTime(startTime);

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	private void requestServerTime(long startTime) {
		if( getCurrentFloor().getState().equals(ElevatorConstants.HALT)) {
			long endTime   = System.nanoTime();
			long totalTime = endTime - startTime;
			System.out.println("Time taken to serve request in nano secs :"+ totalTime );
			startTime = System.nanoTime();
		}
	}

	private void getInterMediateFloor(ElevatorFloorState current, ElevatorFloorState next) {
		int initial = current.getFloorNumber();
		int target = next.getFloorNumber();
        if(initial==target){
            return;
        }

        if(Math.abs(initial-target) == 1){
            return;
        }

        int n = 1;
        if(target-initial<0){
            // This means with are moving DOWN
            n = -1;
        }

        while(initial!=target){
            initial += n;
            ElevatorFloorState temp = new ElevatorFloorState(initial, ElevatorConstants.PASS);
            if(!floorStops.contains(temp)) {
            	floorStops.add(temp);
            }
        }
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public ElevatorFloorState getCurrentFloor() {
		return currentFloor;
	}
	public void setCurrentFloor(ElevatorFloorState currentFloor) {
		this.currentFloor = currentFloor;
	}

	public Map<ElevatorState, NavigableSet<ElevatorFloorState>> getFloorStopMap() {
		return floorStopsMap;
	}
	
	public void setFloorStopMap(Map<ElevatorState, NavigableSet<ElevatorFloorState>> floorStopMap) {
		this.floorStopsMap = floorStopMap;
	}

	public boolean isOperating() {
		return operating;
	}

	public void setOperating(boolean operating) {
		this.operating = operating;
		if(!operating) {
			state = ElevatorState.MAINTAINANCE;
			floorStops.clear();
		} else {
			state = ElevatorState.STATIONARY;
			if(null == floorStopsMap || floorStopsMap.isEmpty()) {
				floorStopsMap = new HashMap<ElevatorState, NavigableSet<ElevatorFloorState>>();
			}
		}
		ElevatorController.updateElevatorList(this);
		setCurrentFloor(new ElevatorFloorState(0, ElevatorConstants.HALT));
	}

	public ElevatorState getState() {
		return state;
	}

	public void setState(ElevatorState state) {
		this.state = state;
	}

	public int getNumberOfPersons() {
		return numberOfPersons;
	}

	public void setNumberOfPersons(int numberOfPersons) {
		this.numberOfPersons = numberOfPersons;
	}

	public int getElevatorCapacity() {
		return elevatorCapacity;
	}

	public void setElevatorCapacity(int elevatorCapacity) {
		this.elevatorCapacity = elevatorCapacity;
	}
	
}
