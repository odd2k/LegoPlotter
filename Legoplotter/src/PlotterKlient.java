

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
		double hjulDiameter = 39.46;// I millimeter
		
		plotter = new Plotter(motorX, motorY, motorZ, endestoppX, endestoppY, endestoppZ, hjulDiameter);

		int x = 0;
		
		while(true){
			System.out.println("Trykk ENTER for å bevege penn");
			Button.ENTER.waitForPressAndRelease();
			plotter.move(0, x);
			plotter.move(150, x);
			
			x += 10;
			
			System.out.println("Trykk ENTER for å bevege penn");
			Button.ENTER.waitForPressAndRelease();
			plotter.move(150, x);
			plotter.move(0, x);
			
			x += 10;
		}
	}
}