
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;

public class PennVelger {
	private final int antPenner = 3; //Antall penner som kan byttes mellom
	private final int makshastighet = 150;
	private boolean pennNede = false; // Er pennen nede?
	private boolean skiftePenn = false; // Er girsystemet i skift penn-modus?
	private int slack = 45; //Ekstra rotasjon i girsystemet pga. slakk
	private int pennNr = 1; // Valgt penn
	
	private EV3LargeRegulatedMotor motorZ;
	
	public PennVelger(EV3LargeRegulatedMotor motorZ){
		this.motorZ = motorZ;
	}
	
	//Bytter penn på pennvelgeren
	//TODO: Lag metoden!
	public void velgPenn(int nr){
		if(nr<1 || nr> antPenner){
			throw new IllegalArgumentException("Ugyldig penn nr!");
		}
		if(!pennNede && !skiftePenn && nr != pennNr){
			motorZ.rotate(slack);
			skiftePenn = true;
		}else if(!pennNede && skiftePenn && nr != pennNr){
			if(nr == pennNr+1 || nr == pennNr -2){
				motorZ.rotate(200);
			}else if(nr == pennNr +2 || nr == pennNr -1){
				motorZ.rotate(400);
			}
			pennNr = nr;
		}
	}
	
	//Flytter pennen ned
	//TODO: Test metoden
	public void ned(){
		if(!pennNede && skiftePenn){
			motorZ.rotate(-slack);
			skiftePenn = false;
		}
		if(!pennNede){
			motorZ.rotate(-375);
			pennNede = true;
		}
		
	}
	
	//Flytter pennen opp
	//TODO: Test metoden
	public void opp(){
		if(pennNede){
			motorZ.rotate(-375);
			pennNede = false;
		}
	}
}
