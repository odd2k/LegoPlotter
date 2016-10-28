import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;

public class PennVelger {
	private int antPenner;
	private int makshastighet = 150;
	private boolean pennNede = false;
	
	private EV3LargeRegulatedMotor motorZ;
	private EV3TouchSensor endestoppZ;
	private float[] sample = new float[1];
	
	
	public PennVelger(EV3LargeRegulatedMotor motorZ, EV3TouchSensor endestoppZ, int antPenner){
		this.motorZ = motorZ;
		this.endestoppZ = endestoppZ;
		this.antPenner = antPenner;
		
		motorZ.setSpeed(makshastighet);
	}
	
	public PennVelger(EV3LargeRegulatedMotor motorZ, EV3TouchSensor endestoppZ){
		this.motorZ = motorZ;
		this.endestoppZ = endestoppZ;
		this.antPenner = 3;
	}
	
	//TODO: Lag metoden!
	public void velgPenn(int nr){
		
	}
	
	//TODO: Test metoden
	public void ned(){
		if(!pennNede){
			motorZ.backward();
			while(!pennNede){
				if(endestoppZTryktNed()){
					motorZ.stop();
					pennNede = true;
				}
			}
		}
		
	}
	//TODO: Test metoden
	public void opp(){
		if(pennNede == true){
			motorZ.backward();
			while(pennNede){
				if(!endestoppZTryktNed()){
					motorZ.stop();
					pennNede = false;
				}
			}
		}
	}
	private boolean endestoppZTryktNed(){
		endestoppZ.fetchSample(sample, 0);
		return (sample[0]==1);
	}
}
