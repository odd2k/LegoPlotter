

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class PlotterKlient{
	
	static Plotter plotter;
	
	public static void main (String[] args){
		NXTRegulatedMotor motorX = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor motorY = new NXTRegulatedMotor(MotorPort.B);
		EV3LargeRegulatedMotor motorZ = null;
		NXTTouchSensor endestoppX = new NXTTouchSensor(SensorPort.S2);
		NXTTouchSensor endestoppY = new NXTTouchSensor(SensorPort.S1);
		EV3TouchSensor endestoppZ = null;
		int hjulDiameter = 41;// I millimeter
		
		plotter = new Plotter(motorX, motorY, motorZ, endestoppX, endestoppY, endestoppZ, hjulDiameter);
		plotter.tegnLinje(15, 30, 45, 30);
		plotter.tegnLinje(45, 30, 15, 60);
		
		Button.ENTER.waitForPressAndRelease();
	}
}