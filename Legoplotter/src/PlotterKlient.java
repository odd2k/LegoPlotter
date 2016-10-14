

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;

public class PlotterKlient{
	public static void main (String[] args){
		EV3LargeRegulatedMotor motorX = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor motorY = new EV3LargeRegulatedMotor(MotorPort.B);
		NXTRegulatedMotor motorZ = null;
		EV3TouchSensor endestoppX = new EV3TouchSensor(SensorPort.S1);
		EV3TouchSensor endestoppY = new EV3TouchSensor(SensorPort.S2);
		int hjulDiameter = 41;
		
		Plotter p = new Plotter(motorX, motorY, motorZ, endestoppX, endestoppY, hjulDiameter);
		
		Button.ENTER.waitForPressAndRelease();
	}
}