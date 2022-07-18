package com.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * This class act as Elevator Control System keeps the track of all the elevator available in the hotel building
 * and send the elevator on requested floor
 */

public class ElevatorController implements Runnable {
	
	private boolean controllerState;
	
	private static List<Elevator> elevatorList = new ArrayList<Elevator>();
	
	private static Map<Integer, Elevator> upMovingElevatorMap = new HashMap<Integer, Elevator>();
	
	private static Map<Integer, Elevator> downMovingElevatorMap = new HashMap<Integer, Elevator>();
	
	private static final ElevatorController instance = new ElevatorController();
	
	private ElevatorController() {
		if(instance != null) {
			throw new IllegalStateException("Already instantiated");
		}
		controllerState = false;
		//initializeElevators(15);
	}
	
	  public static ElevatorController getInstance(){
	        return instance;
	    }
	  
	/**
	 * To initialize 15 elevators
	 */
	public void initializeElevators(int numberOfEle) {
		for (int i = 1; i <= numberOfEle; i++) {
			Elevator elevator = new Elevator(i);
			elevator.setElevatorCapacity(15);
			Thread thread = new Thread(elevator);
			thread.start();
			elevatorList.add(elevator);
		}
		
	}
	
	/**
	 * To find the elevator to serve the request
	 * @param elevatorRequest Elevator
	 * @return Elevator
	 */
	public synchronized Elevator selectElevator(ElevatorRequest elevatorRequest) {
		ElevatorState state = getRequestedElevatorDirection(elevatorRequest);
		Elevator selectedElevator = getNearestElevator(elevatorRequest, state);
		
		//increase the number of person in elevator
		selectedElevator.setNumberOfPersons(selectedElevator.getNumberOfPersons() + 1);
		
		//add request in elevator floor stops set to stop on requested floor
		ElevatorRequest request1 = new ElevatorRequest(selectedElevator.getCurrentFloor().getFloorNumber(), elevatorRequest.getRequestFloor());
		ElevatorState direction1 = getRequestedElevatorDirection(request1);
		updateFloorStopMap(selectedElevator.getCurrentFloor().getFloorNumber(), elevatorRequest.getRequestFloor(), direction1, selectedElevator, ElevatorConstants.START);
		
		ElevatorRequest request2 = new ElevatorRequest(elevatorRequest.getRequestFloor(), elevatorRequest.getTargetFloor());
		ElevatorState direction2 = getRequestedElevatorDirection(request2);
		updateFloorStopMap(elevatorRequest.getRequestFloor(), elevatorRequest.getTargetFloor(), direction2, selectedElevator, ElevatorConstants.STOP);
		try {
			this.notifyAll();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return selectedElevator;
		
	}


	

	private void updateFloorStopMap(int requestFloor, int targetFloor,
			ElevatorState direction1, Elevator selectedElevator, String startOrStop) {
		NavigableSet<ElevatorFloorState> floorStops = selectedElevator.getFloorStopMap().get(direction1);
		if (null == floorStops || floorStops.isEmpty()) {
			floorStops = new ConcurrentSkipListSet<ElevatorFloorState>();
		}
		floorStops.add(new ElevatorFloorState(requestFloor, ElevatorConstants.HALT));
		floorStops.add(new ElevatorFloorState(targetFloor, ElevatorConstants.HALT, startOrStop));
		selectedElevator.getFloorStopMap().put(direction1, floorStops);
	}

	private Elevator getNearestElevator(ElevatorRequest elevatorRequest, ElevatorState state) {
		Elevator selectedElevator = null;
		Integer minDistance = Integer.MIN_VALUE;
		int elevatorId = 1;
		TreeMap<Integer, Integer> sortedMap = new TreeMap<Integer, Integer>();
		if (state.equals(ElevatorState.UP)) {

			// find the nearest Elevator
			for (Map.Entry<Integer, Elevator> entry : upMovingElevatorMap.entrySet()) {
				Elevator elevator = entry.getValue();
				int currentFloor = elevator.getCurrentFloor().getFloorNumber();
				int distance = elevatorRequest.getRequestFloor() - currentFloor;
				if (elevator.getNumberOfPersons() < elevator.getElevatorCapacity()) {
					if (distance < 0) {
						if (minDistance.intValue() != Integer.MIN_VALUE
								&& distance > Math.abs(minDistance.intValue())) {
							minDistance = distance;
							elevatorId = elevator.getId();
						}
						// Elevator has already crossed the requested floor
						continue;
					} else {
						if (elevator.getNumberOfPersons() < elevator.getElevatorCapacity()) {
							sortedMap.put(distance, elevator.getId());
						}
					}
				}

			}
			// get the nearest elevator
			selectedElevator = upMovingElevatorMap.get(sortedMap.isEmpty() ? null : sortedMap.firstEntry().getValue());
			if ((null == selectedElevator) || (minDistance.intValue() != Integer.MIN_VALUE
					&& selectedElevator.getCurrentFloor().getFloorNumber() > Math.abs(minDistance.intValue()))) {
				selectedElevator = getElevatorList().get(elevatorId - 1);
			}
		} else if (state.equals(ElevatorState.DOWN)) {
			// find the nearest Elevator
			for (Map.Entry<Integer, Elevator> entry : downMovingElevatorMap.entrySet()) {
				Elevator elevator = entry.getValue();
				int currentFloor = elevator.getCurrentFloor().getFloorNumber();
				int distance = currentFloor - elevatorRequest.getRequestFloor();
				if (elevator.getNumberOfPersons() < elevator.getElevatorCapacity()) {
					if (distance < 0) {
						// Elevator has already crossed the requested floor
						if (minDistance.intValue() != Integer.MIN_VALUE
								&& distance > Math.abs(minDistance.intValue())) {
							minDistance = distance;
							elevatorId = elevator.getId();
						}
						continue;
					} else {
						sortedMap.put(distance, elevator.getId());
					}
				}
			}
			// get the nearest elevator
			selectedElevator = downMovingElevatorMap
					.get(sortedMap.isEmpty() ? null : sortedMap.firstEntry().getValue());
			if ((null == selectedElevator) || (minDistance.intValue() != Integer.MIN_VALUE
					&& selectedElevator.getCurrentFloor().getFloorNumber() > Math.abs(minDistance.intValue()))) {
				selectedElevator = getElevatorList().get(elevatorId - 1);
			}
		}

		return selectedElevator;
	}

	private ElevatorState getRequestedElevatorDirection(ElevatorRequest eleRequest) {
		
		ElevatorState state =  ElevatorState.UP;
		int currentFloor = eleRequest.getRequestFloor();
		int nextFloor = eleRequest.getTargetFloor();
		//considered currentFloor and nextFloor input wont be equal
		if(currentFloor - nextFloor > 0) {
			state = ElevatorState.DOWN;
		} else if(currentFloor - nextFloor < 0){
			state = ElevatorState.UP;
		}
		return state;
	}

	
	public static synchronized void updateElevatorList(Elevator elevator){
		 if(elevator.getState().equals(ElevatorState.UP)){
	            upMovingElevatorMap.put(elevator.getId(), elevator);
	            downMovingElevatorMap.remove(elevator.getId());
	        } else if(elevator.getState().equals(ElevatorState.DOWN)){
	        	downMovingElevatorMap.put(elevator.getId(), elevator);
	            upMovingElevatorMap.remove(elevator.getId());
	        } else if (elevator.getState().equals(ElevatorState.STATIONARY)){
	            upMovingElevatorMap.put(elevator.getId(), elevator);
	            downMovingElevatorMap.put(elevator.getId(),elevator);
	        } else if (elevator.getState().equals(ElevatorState.MAINTAINANCE)){
	            upMovingElevatorMap.remove(elevator.getId());
	            downMovingElevatorMap.remove(elevator.getId());
	        }
	}
	
	public void run() {
		while (true) {
			if(controllerState) {
				break;
			}
		}
	}

	public static synchronized List<Elevator> getElevatorList() {
		return elevatorList;
	}

	public boolean isControllerState() {
		return controllerState;
	}

	public void setControllerState(boolean controllerState) {
		this.controllerState = controllerState;
	}
	
	
	
}
