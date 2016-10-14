
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class Plotter{
	private int A4_X = 210;
	private int A4_Y = 297;
	private int x; // kortsida - 0-210mm
	private int y; // langsida - 0-297mm

	private int hjulDiameter = 1;
	private double utveksling = 1;// girutveksling på 3:4 gir en double med verdi ¾

	private int margTopp = 0; // mm
	private int margHoyre = 0; // mm
	private int margBunn = 0; // mm
	private int margVenstre = 0; // mm

	private boolean pennNede = false; // Her kommer det en pennVelger

	
	
	private EV3LargeRegulatedMotor motorX;
	private EV3LargeRegulatedMotor motorY;
	private NXTRegulatedMotor motorZ;
	private EV3TouchSensor endestoppX;
	private EV3TouchSensor endestoppY;
	private float[] sample = new float[1];
	
	
	public Plotter(EV3LargeRegulatedMotor motorX, EV3LargeRegulatedMotor motorY, NXTRegulatedMotor motorZ, EV3TouchSensor endestoppX, EV3TouchSensor endestoppY,int hjulDiameter){
		if(hjulDiameter <= 0){
			throw new IllegalArgumentException("Diameteren paa hjulet kan ikke vaere mindre eller lik 0");
		}else{
		this.motorX = motorX;
		this.motorY = motorY;
		this.motorZ = motorZ;
		this.endestoppX = endestoppX;
		this.endestoppY = endestoppY;
		this.hjulDiameter = hjulDiameter;
		}	
		home();
	}
	
	public void settMarger(int margTopp, int margHoyre, int margBunn, int margVenstre){
		this.margTopp = margTopp;
		this.margBunn = margBunn;
		this.margHoyre = margHoyre;
		this.margVenstre = margVenstre;
	}
	
	public void settUtveksling(double utveksling){
		this.utveksling = utveksling;
	}
	//TODO: Lag metoden!
	public void velgPenn(int valgtPenn){// sendes til pennVelger.velgPenn
		
	}

	//TODO: Lag metoden!
	public void tegnPrikk(int x1, int y1){
		
	}
	//TODO: Lag metoden!
	public void tegnLinje(int x1, int y1, int x2, int y2){
		
	}
	//TODO: Lag metoden!
	public void tegnFirkant(int x1, int y1, int bredde, int hoyde){
		
	}
	//TODO: Lag metoden!
	public void tegnOval(int x1, int y2, int bredde, int hoyde){
		
	}
	//TODO: Lag metoden!
	public void tegnSirkel(int x1, int y2, int radius){
		
	}
	//TODO: Lag metoden!
	public void tegnBue(int x1, int y1, int x2, int y2, int h){
		
	}
	
	private void home(){
		boolean xHjemme = false;
		boolean yHjemme = false;
		motorX.backward();
		motorY.backward();
		while(!xHjemme && !yHjemme){
			if(endestoppXTryktNed()){
				motorX.stop();
				x = 0;
				xHjemme = true;
			}
			if(endestoppYTryktNed()){
				motorY.stop();
				y = 0;
				yHjemme = true;
			}
		}
	}
	//TODO: Lag metoden!
	private void move(int x1, int y1){
		motorX.forward();
		motorY.forward();
	}
	//TODO: Lag metoden!
	private void pennNed(){
		if(pennNede == false){
			
		}
		
	}
	//TODO: Lag metoden!
	private void pennOpp(){
		if(pennNede == true){
			
		}
	}
	
	private boolean endestoppXTryktNed(){
		sample[0] = endestoppX.sampleSize();
		if(sample[0]==1){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean endestoppYTryktNed(){
		sample[0] = endestoppY.sampleSize();
		if(sample[0]==1){
			return true;
		}else{
			return false;
		}
	}
	
	private int graderTilMillimeter(int grader){
		int millimeter = (int)Math.round((360/(Math.PI*hjulDiameter))*grader);
		return millimeter;
	}
	
	private int millimeterTilGrader(int millimeter){
		int grader = (int)Math.round((360/(Math.PI*hjulDiameter))*millimeter);
		return grader;
	}
	
}