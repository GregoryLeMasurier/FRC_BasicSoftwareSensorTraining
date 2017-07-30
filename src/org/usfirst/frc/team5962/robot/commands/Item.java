package org.usfirst.frc.team5962.robot.commands;

import org.usfirst.frc.team5962.robot.Robot;
import org.usfirst.frc.team5962.robot.RobotMap;
import org.usfirst.frc.team5962.robot.subsystems.Autonomous;

public class Item {
	private double speed;
	private double turningValue;
	private Autonomous.sensorType sensorType;
	private double sensorValue = 0;
	private boolean complete = true;
	private long startSystemTime = -1;
	private double adjustedTurningValue;
	private boolean isLeft = true;

	public Item(double speed, int turningValue, Autonomous.sensorType sensorType, int sensorValue) {
		init(speed,turningValue,sensorType,sensorValue);
	}
	
	public Item(double speed, int turningValue, Autonomous.sensorType sensorType, int sensorValue, boolean isLeft) {
		init(speed,turningValue,sensorType,sensorValue);
		this.isLeft = isLeft;
	}
	
	private void init(double speed, int turningValue, Autonomous.sensorType sensorType, int sensorValue) {
		complete = false;
		this.speed = speed;
		this.sensorType = sensorType;
		this.sensorValue = sensorValue;
		this.turningValue = turningValue;
		this.adjustedTurningValue = turningValue;
		Robot.encoder.reset(); //reset encoders on every new command
	}

	public boolean isComplete() {

		return complete;
	}

	private double getGyroAngle() {
		double angle = Robot.gyro.getGyroAngle();
		return angle;
	}

	private double getRange() {
		double range = Robot.ultrasonic.getRange();
		return range;
	}

	private double getDistance() {
		double distance = Robot.encoder.getDistance();
		return distance;
	}

	public void execute() {
		if (complete) {
			RobotMap.myRobot.drive(0, 0);
			return;
		}
		switch (sensorType) {
		case time:
			time();
			break;
		case encoder:
			drive(getDistance());			
			break;
		case ultrasonic:
			drive(getRange());
			break;
		case gyro:
			if(isLeft) {
			gyroLeft();
			}
			else {
			gyroRight();
			}
			break;
		default:
			break;
		}
	}
	
	private void drive(double value) {
		if (value < sensorValue) {
			if (speed > 0 && turningValue == 0) {
				double angle = getGyroAngle();
				adjustedTurningValue = 0.03 * angle;
			}
			if (speed < 0 && turningValue == 0) {
				double angle = getGyroAngle();
				adjustedTurningValue = 0.03 * -angle;
			}
			RobotMap.myRobot.drive(-speed, adjustedTurningValue);
		} else {
			RobotMap.myRobot.drive(0, 0);
			complete = true;
		}		
	}

	private void gyroLeft() {
		if (getGyroAngle() > sensorValue) {
			RobotMap.myRobot.drive(-speed, turningValue);
		} else {
			RobotMap.myRobot.drive(0, 0);
			complete = true;
		}
	}
	private void gyroRight() {
		if (getGyroAngle() < sensorValue) {
			RobotMap.myRobot.drive(-speed, turningValue);
		} else {
			RobotMap.myRobot.drive(0, 0);
			complete = true;
		}
	}

	private void time() {
		if (startSystemTime == -1) {
			RobotMap.myRobot.drive(0, 0);
			startSystemTime = System.currentTimeMillis();
		}
		long currentTime = System.currentTimeMillis();
		if (currentTime < (startSystemTime + (sensorValue * 1000))) {
			RobotMap.myRobot.drive(-speed, adjustedTurningValue);
		} else {
			RobotMap.myRobot.drive(0, 0);
			complete = true;
		}
	}
}