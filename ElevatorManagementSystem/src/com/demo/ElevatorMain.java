package com.demo;

import java.util.Random;
import java.util.Scanner;

/**
 * Design a lift management system for a building of 100 to 150 stories with 10 – 20 lifts in the building with each having a capacity 
 * between 10 – 15 people at a time. Each floor in the building houses 100 -150 people. Your system should be able to generate events like people using the floors to
 *  move in/out between various floors. You can emulate office start, office end, lunch and break timings to vary the load on the system. 
 *  The system should work on goals like less time to move between floor A to floor B, Utilize maximum capacity, intelligent floor routing etc. 
 *  The lifts should exhibit behaviors like stopping time, acceleration and de-acceleration in a realistic manner. 
 *  You could use a timer and use it accelerate a whole day of activity in a few minutes of the application run.
 * 
 *
 */
public class ElevatorMain {

	private static ElevatorController eleController;
	private static Thread eleControllerThread;

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		eleController = ElevatorController.getInstance();
		//initialize elevator
	
		Scanner input = new Scanner(System.in);
		System.out.println("Enter number of Elevator(1 to 15) ");
		int elevatorCount = input.nextInt();
		eleController.initializeElevators(elevatorCount);
		
		eleControllerThread = new Thread(eleController);
		eleControllerThread.start();

		int choice;

		input = new Scanner(System.in);
		System.out.println("Enter number of concurrent request for Elevator(0 to 22500) ");
		choice = input.nextInt();

		if (choice >= 1 && choice <= 22500) {
			for (int i = 0; i < choice; i++) {
				//it will take random requested floor and target floor
				int reqestFloor = getRandomNumber();
				int targetFloor = getRandomNumber();
				System.out.println("Request Floor : "+reqestFloor +" | Target Floor : "+targetFloor);

				ElevatorRequest elevatorRequest = new ElevatorRequest(reqestFloor, targetFloor);
				Elevator elevator = elevatorRequest.submitRequest();
			}

		}

	}

	public static int getRandomNumber() {
		Random r = new Random();
		int low = 1;
		int high = 150;
		return r.nextInt(high - low) + low;
	}
}
