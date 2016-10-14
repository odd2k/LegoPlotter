

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;

public class PlotterKlient{
	
	static Plotter plotter;
	
	public static void main (String[] args){
		NXTRegulatedMotor motorX = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor motorY = new NXTRegulatedMotor(MotorPort.B);
		EV3LargeRegulatedMotor motorZ = null;
		EV3TouchSensor endestoppX = new EV3TouchSensor(SensorPort.S1);
		EV3TouchSensor endestoppY = new EV3TouchSensor(SensorPort.S2);
		int hjulDiameter = 41;
		
		plotter = new Plotter(motorX, motorY, motorZ, endestoppX, endestoppY, hjulDiameter);
		
		Button.ENTER.waitForPressAndRelease();
	}
}