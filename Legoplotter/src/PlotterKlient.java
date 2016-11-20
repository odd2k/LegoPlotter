import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTTouchSensor;

public class PlotterKlient{
	
	static Plotter plotter;
	
	public static void main (String[] args) throws Exception{
		NXTRegulatedMotor motorY = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor motorX = new NXTRegulatedMotor(MotorPort.B);
		NXTRegulatedMotor motorX2 = new NXTRegulatedMotor(MotorPort.C);
		EV3LargeRegulatedMotor motorZ = new EV3LargeRegulatedMotor(MotorPort.D);
		NXTTouchSensor endestoppX = new NXTTouchSensor(SensorPort.S2);
		NXTTouchSensor endestoppX2 = new NXTTouchSensor(SensorPort.S3);
		NXTTouchSensor endestoppY = new NXTTouchSensor(SensorPort.S1);
		double hjulDiameter = 39.46;// I millimeter
		
		double utvekslingX = 1.0f/2.0f;
		double utvekslingY = 24.0f/40.0f;
		
		plotter = new Plotter(motorY, motorX, motorX2, motorZ, endestoppX, endestoppX2, endestoppY, hjulDiameter, utvekslingX, utvekslingY);

		while(true){
			System.out.println("Venter pa klient...");
			
			plotter.utforKommandoer(KommandoListe.getKommandoListe(Plotter.PORTNR));
		}
	}
}