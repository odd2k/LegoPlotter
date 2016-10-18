
import lejos.hardware.motor.EV3LargeRegulatedMotor;
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
	private float makshastighet;

	private int margTopp = 0; // mm
	private int margHoyre = 0; // mm
	private int margBunn = 0; // mm
	private int margVenstre = 0; // mm

	private boolean pennNede = false; // Her kommer det en pennVelger

	private NXTRegulatedMotor motorX;
	private NXTRegulatedMotor motorY;
	private EV3LargeRegulatedMotor motorZ;
	private EV3TouchSensor endestoppX;
	private EV3TouchSensor endestoppY;
	private float[] sample = new float[1];
	
	
	public Plotter(NXTRegulatedMotor motorX, NXTRegulatedMotor motorY, EV3LargeRegulatedMotor motorZ, EV3TouchSensor endestoppX, EV3TouchSensor endestoppY,int hjulDiameter){
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

	
	public void tegnPrikk(int x1, int y1){
		move(x1,y1);
		pennNed();
		pennOpp();
	}
	
	public void tegnLinje(int x1, int y1, int x2, int y2){
		move(x1,y1);
		pennNed();
		move(x2-x1,y2-y1);
		pennOpp();
	}
	
	public void tegnFirkant(int x1, int y1, int bredde, int hoyde){
		move(x1,y1);
		pennNed();
		move(0,hoyde);
		move(bredde,0);
		move(0,-hoyde);
		move(-bredde,0);
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
	//TODO: Fiks metoden.
	private void home(){
		boolean xHjemme = false;
		boolean yHjemme = false;
		motorX.backward();
		motorY.backward();
		while(!xHjemme && !yHjemme){
			xHjemme = endestoppXTryktNed();
			if(xHjemme){
				motorX.stop();
				x = 0;
			}
			yHjemme = endestoppYTryktNed();
			if(yHjemme){
				motorY.stop();
				y = 0;
			}
		}
	}
	//TODO: Gjør metoden avansert!
	private void move(int x1, int y1){
		motorX.rotate(millimeterTilGrader(x1), true);
		motorY.rotate(millimeterTilGrader(y1), true);
		x += x1;
		y += y1;
	}
	//TODO: Sjekk konstanten
	private void pennNed(){
		if(pennNede == false){
			motorZ.rotate(180);
			pennNede = true;
		}
		
	}
	//TODO: Sjekk konstanten
	private void pennOpp(){
		if(pennNede == true){
			motorZ.rotate(-180);
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
	
	private int graderTilMillimeter(int grader){
		int millimeter = (int)Math.round(((Math.PI*hjulDiameter)/360)*grader);
		return millimeter;
	}
	
	private int millimeterTilGrader(int millimeter){
		int grader = (int)Math.round((360/(Math.PI*hjulDiameter))*millimeter);
		return grader;
	}
	
}