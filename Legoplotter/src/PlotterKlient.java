

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
		NXTRegulatedMotor motorY2 = new NXTRegulatedMotor(MotorPort.C);
		EV3LargeRegulatedMotor motorZ = null;
		NXTTouchSensor endestoppX = new NXTTouchSensor(SensorPort.S2);
		NXTTouchSensor endestoppY = new NXTTouchSensor(SensorPort.S1);
		EV3TouchSensor endestoppZ = null;
		double hjulDiameter = 39.46;// I millimeter
		
		plotter = new Plotter(motorX, motorY, motorY2, motorZ, endestoppX, endestoppY, endestoppZ, hjulDiameter);

		int x = 0;
		
		while(true){
			System.out.println("Trykk ENTER for å bevege penn");
			Button.ENTER.waitForPressAndRelease();
			plotter.move(x, 0);
			plotter.move(x, 200);
			
			x += 10;
			
			System.out.println("Trykk ENTER for å bevege penn");
			Button.ENTER.waitForPressAndRelease();
			plotter.move(x, 200);
			plotter.move(x, 0);
			
			x += 10;
		}
	}
}