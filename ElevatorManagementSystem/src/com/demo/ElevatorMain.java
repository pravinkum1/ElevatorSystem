package com.demo;

import java.util.Random;
import java.util.Scanner;

/**
 * Design a lift management system for Hotel of 30 to 50  stories with 10 – 15 lifts in the Hotel with each having a capacity 
 * between 10 – 15 people at a time. Each floor in the hotel rooms 50 -100 people.
 *  The system should work on goals like less time to move between floor A to floor B,
 *  Utilize maximum capacity, intelligent floor routing etc. 
 *  The lifts should exhibit behaviors like stopping time, acceleration and de-acceleration in a realistic manner. 
 *  You could use a timer and use it accelerate a whole day of activity in a few minutes of the application run.
 * 
 *
 */
public class ElevatorMain {

	private static ElevatorController eleController;
	private static Thread eleControllerThread;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException {
		
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
		System.out.println("Enter number of concurrent request for Elevator(0 to 2000) ");
		choice = input.nextInt();

		if (choice >= 1 && choice <= 2000) {
			for (int i = 0; i < choice; i++) {
				//it will take random requested floor and target floor
				int reqestFloor = getRandomNumber();
				int targetFloor = getRandomNumber();
				System.out.println("Request Floor : "+reqestFloor +" | Target Floor : "+targetFloor);

				ElevatorRequest elevatorRequest = new ElevatorRequest(reqestFloor, targetFloor);
				Elevator elevator = elevatorRequest.submitRequest();
				Thread.sleep(20000);
			}

		}

	}

	public static int getRandomNumber() {
		Random r = new Random();
		int low = 1;
		int high = 50;
		return r.nextInt(high - low) + low;
	}
}
