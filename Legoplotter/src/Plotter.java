
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class Plotter{
	private int A4_X = 210; // I millimeter
	private int A4_Y = 297;	// I millimeter
	private int x; // kortsida - 0-210mm
	private int y; // langsida - 0-297mm

	private int hjulDiameter;
	private double utveksling = 1;// girutveksling på 3:4 gir en double med verdi ¾
	private int makshastighet = 50;

	private int margTopp = 0; // mm
	private int margHoyre = 0; // mm
	private int margBunn = 0; // mm
	private int margVenstre = 0; // mm

	private boolean pennNede = false; // Her kommer det en pennVelger

	private NXTRegulatedMotor motorX;
	private NXTRegulatedMotor motorY;
	private EV3LargeRegulatedMotor motorZ;
	private NXTTouchSensor endestoppX;
	private NXTTouchSensor endestoppY;
	private EV3TouchSensor endestoppZ;
	private float[] sample = new float[1];
	
	
	public Plotter(NXTRegulatedMotor motorX, NXTRegulatedMotor motorY, EV3LargeRegulatedMotor motorZ, NXTTouchSensor endestoppX, NXTTouchSensor endestoppY, EV3TouchSensor endestoppZ, int hjulDiameter){
		if(hjulDiameter <= 0){
			throw new IllegalArgumentException("Diameteren paa hjulet kan ikke vaere mindre eller lik 0");
		}else{
		this.motorX = motorX;
		this.motorY = motorY;
		this.motorZ = motorZ;
		this.endestoppX = endestoppX;
		this.endestoppY = endestoppY;
		this.endestoppZ = endestoppZ;
		this.hjulDiameter = hjulDiameter;
		motorX.setSpeed(makshastighet);
		motorY.setSpeed(makshastighet);
		//motorZ.setSpeed(makshastighet);
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
	
	public void settMakshastighet(int makshastighet){
		this.makshastighet = makshastighet;
	}
	//TODO: Lag metoden!
	public void velgPenn(int valgtPenn){// sendes til pennVelger.velgPenn
		
	}

	
	public void tegnPrikk(int x1, int y1){
		move(x1,y1);
		pennNed();
		pennOpp();
	}
	
	public void tegnLinje(int x1, int y1, int x2, int y2){
		move(x1,y1);
		pennNed();
		move(x2,y2);
		pennOpp();
	}
	
	public void tegnFirkant(int x1, int y1, int bredde, int hoyde){
		move(x1,y1);
		pennNed();
		move(x1,y1 + hoyde);
		move(x1 + bredde,y1 + hoyde);
		move(x1 + bredde,y1);
		move(x1,y1);
		pennOpp();
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
		motorX.forward();// Framover er bakover.
		motorY.forward();// Framover er bakover.
		while(!xHjemme || !yHjemme){
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
	//TODO: Gjør metoden avansert!
	private void move(int x1, int y1){// Flytt til kordinat.
		/*
		if(x + x1 > A4_X - (margVenstre + margHoyre)|| y + y1 > A4_Y - (margTopp + margBunn)){
			throw new IllegalArgumentException("For stor verdi!");
		}else{
			motorX.rotate(millimeterTilGrader(x1), true);
			motorY.rotate(millimeterTilGrader(y1), true);
			x += x1;
			y += y1;
		}
		*/
		if(x1 - x> A4_X - (margVenstre + margHoyre)|| x1 - x < 0 || y1 - y > A4_Y - (margTopp + margBunn)|| y1 - y < 0){
			if(x1 - x > A4_X - (margVenstre + margHoyre)|| x1 - x < 0){
				throw new IllegalArgumentException("For stor X verdi!");
			}
			if(y1 - y > A4_X - (margTopp + margBunn)|| y1 - y < 0){
				throw new IllegalArgumentException("For stor Y verdi!");
			}
		}else{
			motorX.rotate(millimeterTilGrader(-(x1-x)), true);// Bakover er framover.
			motorY.rotate(millimeterTilGrader(-(y1-y)), true);// Bakover er framover.
			x = x1;
			y = y1;
		}
	}
	//TODO: Sjekk konstanten
	private void pennNed(){
		while(!pennNede){
			motorZ.forward();
			if(endestoppZTryktNed()){
				motorZ.stop();
				pennNede = true;
			}
		}
		
	}
	//TODO: Sjekk konstanten
	private void pennOpp(){
		if(pennNede == true){
			motorZ.rotate(180);
			pennNede = false;
		}
	}
	
	private boolean endestoppXTryktNed(){
		endestoppX.fetchSample(sample, 0);
		return (sample[0]==1);
	}
	
	private boolean endestoppYTryktNed(){
		endestoppY.fetchSample(sample, 0);
		return (sample[0]==1);
	}
	
	private boolean endestoppZTryktNed(){
		endestoppZ.fetchSample(sample, 0);
		return (sample[0]==1);
	}
	
	private int graderTilMillimeter(int grader){
		int millimeter = (int)Math.round(((Math.PI*hjulDiameter)/360)*grader);
		return millimeter;
	}
	
	private int millimeterTilGrader(int millimeter){
		int grader = (int)Math.round((360/(Math.PI*hjulDiameter))*millimeter);
		return grader;
	}
	
}