
import java.util.ArrayList;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.sensor.EV3TouchSensor;

public class Plotter{
	public static int A4_X = 210; // I millimeter
	public static int A4_Y = 297;	// I millimeter
	
	public static final String PLOTTER_IP = "10.101.101.1";
	public static final int PORTNR = 1256;
	
	private int x; // kortsida - 0-210mm
	private int y; // langsida - 0-297mm

	private double hjulDiameter;
	private double utvekslingX, utvekslingY;
	
	private int makshastighet =35; // millimeter per sekund!
	
	//OBS: Lekeverdier for å teste designer.
	public static final int margTopp = 63; // mm
	public static final int margHoyre = 0; // mm
	public static final int margBunn = 73; // mm
	public static final int margVenstre = 30; // mm

	//private boolean pennNede = false; // Her kommer det en pennVelger

	private NXTRegulatedMotor motorY;
	private NXTRegulatedMotor motorX;
	private NXTRegulatedMotor motorX2;
	private NXTTouchSensor endestoppX, endestoppX2;
	private NXTTouchSensor endestoppY;

	// Brukes for å mellomlagre innleste verdier fra touch-sensorer
	private float[] sample = new float[1];
	
	private PennVelger penn;
	
	public Plotter(NXTRegulatedMotor motorY, NXTRegulatedMotor motorX, NXTRegulatedMotor motorX2, EV3LargeRegulatedMotor motorZ, 
			NXTTouchSensor endestoppX, NXTTouchSensor endestoppX2, NXTTouchSensor endestoppY, double hjulDiameter, double utvekslingX, double utvekslingY){
		if(hjulDiameter <= 0)
			throw new IllegalArgumentException("Diameteren paa hjulet kan ikke vaere mindre eller lik 0");

		this.motorY = motorY;
		this.motorX = motorX;
		this.motorX2 = motorX2;
		
		System.out.println(motorX.getAcceleration());
		
		this.endestoppX = endestoppX;
		this.endestoppX2 = endestoppX2;
		this.endestoppY = endestoppY;
		
		this.hjulDiameter = hjulDiameter;
		
		this.utvekslingX = utvekslingX;
		this.utvekslingY = utvekslingY;
		
		this.penn = new PennVelger(motorZ);
		
		setSpeedX(10);
		setSpeedY(10);
		
		home();
		
		setSpeedX(makshastighet);
		setSpeedY(makshastighet);
	}
	
	// Millimeter per sekund på begge aksene
	public void settMakshastighet(int makshastighet){
		this.makshastighet = makshastighet;
	}

	//Velg en gitt penn med pennevelgeren
	public void velgPenn(int nr){
		penn.velgPenn(nr);
	}
	
	public void tegnPrikk(int x1, int y1){
		sjekkMarg(x1,y1,x1,y1);
		move(x1,y1);
		penn.ned();
		penn.opp();
	}
	
	public void tegnLinje(int x1, int y1, int x2, int y2){
		sjekkMarg(x1,y1,x2,y2);
		move(x1,y1);
		penn.ned();
		move(x2,y2);
		penn.opp();
	}
	
	public void tegnFirkant(int x1, int y1, int bredde, int hoyde){
		sjekkMarg(x1,y1,x1+bredde,y1+hoyde);
		move(x1,y1);
		penn.ned();
		move(x1,y1 + hoyde);
		move(x1 + bredde,y1 + hoyde);
		move(x1 + bredde,y1);
		move(x1,y1);
		penn.opp();
	}
	
	//TODO: Forbedre metoden
	public void tegnOval(int x1, int y1, int bredde, int hoyde){
		sjekkMarg(x1,y1,x1 + bredde,y1 + hoyde);
		int radiusX = bredde/2;
		int radiusY = hoyde/2;
		
		int senterX = x1 + radiusX;
		int senterY = y1 + radiusY;
		
		
		
		final float SEGMENT_LENGDE = 10; // millimeter... Segmentene blir noe kortere enn dette, men det fungerer.
		float omkrets = (float)	( 2 * Math.PI * Math.sqrt	(
																(Math.pow(radiusX, 2) + Math.pow(radiusY, 2) ) / 2 
															)
								);
		float theta = 360 * (SEGMENT_LENGDE / omkrets);
		
		move(senterX + radiusX, senterY);
		penn.ned();
		
		for(float deg = 0; deg<=360; deg+=theta){
			double rad = Math.toRadians(deg);
			int x = (int) Math.round(senterX + radiusX * Math.cos(rad));
			int y = (int) Math.round(senterY + radiusY * Math.sin(rad));
			move(x, y);
		}
		penn.opp();
	}
	
	//TODO: Finn en annen løsning
	public void tegnSirkel(int x1, int y1, int radius){
		sjekkMarg(x1-radius,y1-radius,x1+radius,y1+radius);
		final float SEGMENT_LENGDE = 10; // millimeter... Segmentene blir noe kortere enn dette, men det fungerer.
		float omkrets = 2 * radius * (float)Math.PI;
		float theta = 360 * (SEGMENT_LENGDE / omkrets);
		
		move(x1 + radius,y1);
		penn.ned();
		
		for(float deg = 0; deg<=360; deg+=theta){
			
			double rad = Math.toRadians(deg);
			int x = (int) Math.round(x1 + radius * Math.cos(rad));
			int y = (int) Math.round(y1 + radius * Math.sin(rad));
			move(x, y);
		}
		penn.opp();
	}

	
	
	//TODO: Test metoden! TODO: Kommenter bedre
	public void tegnBue(int x1, int y1, int x2, int y2, int h){
		//sjekkMarg(x1,y1,x2,y2);
		//move(x1,y1);
		double radius = ((4*Math.pow(h, 2))+(Math.pow((x2-x1),2)+Math.pow((y2-y1),2)))/(8*h);
		//double bue = 2*(Math.sin(1-(h/radius)));
		/* vektor en = (x2-x1,y2-y1)
		 * vektor to = (y2-y1,-(x2-x1))
		 * midtpunkt t = |vektor en|/2 + punkt(x1,y1);
		 * senter i sirkel(s) = t + k * vektor to;
		 */
		int tx = Math.round((x1+x2)/2);// h= 20 -> h^2 = 22
		int ty = Math.round((y1+y2)/2);
		double k = (h-radius)/(Math.sqrt(Math.pow((x2-x1),2)+(Math.pow((y2-y1),2))));
		int xs;// X senter
		int ys;// Y senter
		//move(59,61);
		//penn.ned();
		xs = (int)Math.round((k*(-y2+y1)) + tx);
		ys = (int)Math.round((k*(x2-x1)) + ty);
		//System.out.println("Radius: " + radius + ";");
		//System.out.println("SenterX: " + xs + ", SenterY: " + ys +";");
		ArrayList<Integer> kordinatX = new ArrayList<Integer>();
		ArrayList<Integer> kordinatY = new ArrayList<Integer>();
		int xMax = 0;
		int xMin = 0;
		int yMax = 0;
		int yMin = 0;
		for(int deg = 0; deg<=360; deg++){
			double rad = Math.toRadians(deg);
			int index = 0;
			int x = (int) Math.round(xs + radius * Math.cos(rad));
			int y = (int) Math.round(ys + radius * Math.sin(rad));
			//System.out.println("X = " + x + ", Y = " + y);
			//if(h > 0 &&(x >= x1 || x >= x2 && y >= y1 || y>= y2)||(h < 0 && x <= x1 || x <= x2 && y <= y1 || y <= y2)){
			if((h>0 && (x >= xs && (y >= y2 || y <= y1)) || (x <= xs && (y >= y1 || y <= y2))) ||
					(h < 0 && (x>=xs && (y<= y2 || y>= y1)) || (x<=xs && (y <= y1 || y>= y2)))){
				kordinatX.add(index,x);
				kordinatY.add(index,y);
				index++;
				
				//Sjekk maks og min x og y
				if(x > xMax){xMax = x;}
				if(x < xMin){xMin = x;}
				if(y > yMax){yMax = x;}
				if(y < yMin){yMin = x;}
				/*
				System.out.println("Move(" + x + ", " + y + ")");
				move(x,y);
				penn.ned();
				*/
			} else {
				index = 0;
				//System.out.println("Can't make it!");
			}
		}
		//test
		sjekkMarg(xMin,yMin,xMax,yMax);
		move(kordinatX.get(0),kordinatY.get(0));
		penn.ned();
		for(int i = 1;i<kordinatX.size();i++){
			move(kordinatX.get(i),kordinatY.get(i));
			x = kordinatX.get(i);
			y = kordinatY.get(i);
		}
		penn.opp();
		//System.out.println(bue);
	}
	//TODO: Test metoden!
	public void tegnBue2(int x1, int y1, int x2, int y2, int x3, int y3) throws InterruptedException{
		/* t= tid = delta t / total t;
		 * hastighet = 2*P(t,y-x,z-y);
		 */
		double t = 0;
		move(x1,y1);
		x = x1;
		y = y1;
		penn.ned();
		Thread.sleep(200);
		boolean xForward = true;
		boolean yForward = true;
		while(t<1){
			int x = (int)Math.round(B(t,x1,x2,x3));
			int y = (int)Math.round(B(t,y1,y2,y3));
			if(y<this.y && yForward){
				motorY.backward();
				yForward = false;
			}else if(y>this.y && !yForward){
				motorY.forward();
				yForward = true;
			}
			if(x<this.x && xForward){
				motorX.backward();
				motorX2.backward();
				xForward = false;
			}else if(x>this.x && !xForward){
				motorX.forward();
				motorX2.forward();
				xForward = true;
			}
			this.y = y;
			this.x = x;
			
			motorX.setSpeed(Math.abs((int)Math.round(2*P(t,x2-x1,x3-x2))));
			motorX2.setSpeed(Math.abs((int)Math.round(2*P(t,x2-x1,x3-x2))));
			motorY.setSpeed(Math.abs((int)Math.round(2*P(t,y2-y1,y3-y2))));
			
			Thread.sleep(500);
			t+=0.05;
		}
		motorX.stop();
		motorX2.stop();
		motorY.stop();
	}
	
	private double P(double t, double x, double y){
		return (1-t) * x + t * y;
	}
	
	private double B(double t, double x, double y, double z){
		return (1-t) * P(t,x,y) + t * P(t,y,z);
	}
	
	private void home(){
		boolean x1Hjemme = endestoppXTryktNed();
		boolean x2Hjemme = endestoppX2TryktNed();
		boolean yHjemme = endestoppYTryktNed();
		
		if(!yHjemme)
			motorY.backward();// bakover er bakover.
		
		if(!x1Hjemme)
			motorX.forward();// bakover er bakover.
		
		if(!x2Hjemme)
		motorX2.forward();
		
		while(!x1Hjemme || !x2Hjemme || !yHjemme){
			if(endestoppXTryktNed()){
				motorX.stop();
				x1Hjemme = true;
			}
			if(endestoppX2TryktNed()){
				motorX2.stop();
				x2Hjemme = true;
			}
			
			if(endestoppYTryktNed()){
				motorY.stop();
				yHjemme = true;
			}
		}
		
		x = 0;
		y = 0;
	}
	
	// Returnerer bredde på det området på arket som ligger innenfor margene
	public static int getBredde(){
		return A4_X - (margVenstre + margHoyre);
	}
	
	// Returnerer høyde på det området på arket som ligger innenfor margene
	public static int getHoyde(){
		return A4_Y - (margTopp + margBunn);
	}
	
	//Beveger pennen i rett linje til punktet (x1,y1)
	private void move(int x1, int y1){// Flytt til kordinat.
		
		int lengdeX = x1 - x;
		int lengdeY = y1 - y;
		
		// Om bevegelse i X-retning er 3 ganger større enn i y-retning, skal
		// motor Y bevege seg 3 ganger så langsomt som X.
		// Om den ene bevegelsen er 0, er ikke dette så nøye.
		
		if(Math.abs(lengdeX) > Math.abs(lengdeY) && lengdeX != 0){
			setSpeedX(makshastighet);
			setSpeedY(makshastighet * (double)lengdeY/lengdeX);
		}
		else if(Math.abs(lengdeY) > Math.abs(lengdeX) && lengdeY != 0){
			setSpeedX(makshastighet * (double)lengdeX/lengdeY);
			setSpeedY(makshastighet);
		}
		else{
			setSpeedX(makshastighet);
			setSpeedY(makshastighet);
		}
		
		moveX(lengdeX);
		moveY(lengdeY);
		
		motorX.resetTachoCount(); motorX2.resetTachoCount(); motorY.resetTachoCount();
		
		while(motorX.isMoving() || motorY.isMoving() || motorX2.isMoving()){}
		
		x = x1;
		y = y1;
		
	}

	// Hjelpemetode. Kaster unntak om ett av punktene er utfor margene.
	private void sjekkMarg(int x1, int y1, int x2, int y2){
		int bredde = getBredde();
		int hoyde = getHoyde();
		if(x1 != x2 && y1 != y2){
			if(sjekk(x1,y1) && sjekk(x2,y2)){
			throw new IllegalArgumentException("X og Y-verdier utenfor område! x1: " + x1 + " x2: " + x2 + " bredde: " + bredde + " y1: " + y1 + " y2: " + y2 + "hoyde: " + hoyde);
			}
		}
		else if(sjekk(x1,y1)){
			throw new IllegalArgumentException("X og Y-verdier utenfor område! x1: " + x1 + " bredde: " + bredde + " y1: " + y1 + "hoyde: " + hoyde);
		}
		else if(sjekk(x2,y2)){
			throw new IllegalArgumentException("X og Y-verdier utenfor område! x2: " + x2 + " bredde: " + bredde + " y2: " + y2 + "hoyde: " + hoyde);
		}
		else if(sjekkX(x1)){
			throw new IllegalArgumentException("X-verdi utenfor område! x1: " + x1 + " bredde: " + bredde);
		}
		else if(sjekkX(x2)){
			throw new IllegalArgumentException("X-verdi utenfor område! x2: " + x2 + " bredde: " + bredde);
		}
		else if(sjekkY(y1)){
			throw new IllegalArgumentException("Y-verdi utenfor område! y1: " + y1 + " høyde: " + hoyde);
		}
		else if(sjekkY(y2)){
			throw new IllegalArgumentException("Y-verdi utenfor område! y2: " + y2 + " høyde: " + hoyde);
		}
	}
	
	// Hjelpemetode. Kaster unntak om punktet er utfor margene.
	public static boolean sjekk(int x1, int y1){
		return sjekkX(x1) && sjekkY(y1);
	}
	
	// Hjelpemetode. Kaster unntak om koordinatet er utfor margene.
	private static boolean sjekkX(int x1){
		return (x1 < 0 || x1 > A4_X - (margVenstre + margHoyre));
	}
	
	// Hjelpemetode. Kaster unntak om koordinatet er utfor margene.
	private static boolean sjekkY(int y1){
		return(y1 < 0 || y1 > A4_Y - (margTopp + margBunn));
	}
	
	// True hvis endestoppbryter 1 for akse X er trykt ned.
	private boolean endestoppXTryktNed(){
		endestoppX.fetchSample(sample, 0);
		return (sample[0]==1);
	}
	
	// True hvis endestoppbryter 2 for akse X er trykt ned.
	private boolean endestoppX2TryktNed(){
		endestoppX2.fetchSample(sample, 0);
		return (sample[0]==1);
	}
	
	// True hvis endestoppbryter for akse Y er trykt ned.
	private boolean endestoppYTryktNed(){
		endestoppY.fetchSample(sample, 0);
		return (sample[0]==1);
	}

	//Konverterer fra omdreining på motor i grader til forflytning på arket i millimeter.
	private int graderTilMillimeter(int grader, double utveksling){
		int millimeter = (int)Math.round((Math.PI * hjulDiameter * grader * utveksling) / (360));
		return millimeter;
	}
	
	//Konverterer fra forflytning på arket i millimeter til omdreining på motor i grader.
	private int millimeterTilGrader(int millimeter, double utveksling){
		int grader = (int)Math.round((360 * millimeter) / (Math.PI * hjulDiameter * utveksling));
		return grader;
	}
	
	//Setter hastighet på x-aksen, i millimeter per sekund
	public void setSpeedX(double mmps){
		motorX.setSpeed(millimeterTilGrader((int)Math.round(mmps), utvekslingX));
		motorX2.setSpeed(millimeterTilGrader((int)Math.round(mmps), utvekslingX));
	}
	
	//Setter hastighet på y-aksen, i millimeter per sekund
	public void setSpeedY(double mmps){
		motorY.setSpeed(millimeterTilGrader((int)Math.round(mmps), utvekslingY));
	}
	
	//Beveger akse Y gitt antall millimeter. OBS: metoden returnerer umiddelbart uten å vente på fullført bevegelse.
	public void moveY(int mm){
		int grader = millimeterTilGrader(mm, utvekslingY);
		motorY.rotate(grader, true);
	}
	
	//Beveger akse X gitt antall millimeter. OBS: metoden returnerer umiddelbart uten å vente på fullført bevegelse.
	public void moveX(int mm){
		motorX.rotate(-millimeterTilGrader(mm, utvekslingX), true);
		motorX2.rotate(-millimeterTilGrader(mm, utvekslingX), true);
	}
	
	// Brukes for å motta kommandoer over nettverket.
	// TODO: Implementer pennvelger-funksjonalitet
	public void utforKommando(Kommando kommando){
		
		Kommando.KommandoType type = kommando.getType();
		int[] args = kommando.getArgs();
		penn.velgPenn(kommando.getPenn());
		
		System.out.println("Utforer kommando " + kommando);
		
		switch(type){
			case PRIKK:
				tegnPrikk(args[0], args[1]);
				break;
			case LINJE:
				tegnLinje(args[0], args[1], args[2], args[3]);
				break;
			case FIRKANT:
				tegnFirkant(args[0], args[1], args[2], args[3]);
				break;
			case OVAL:
				tegnOval(args[0], args[1], args[2], args[3]);
				break;
			case SIRKEL:
				tegnSirkel(args[0], args[1], args[2]);
				break;
			case BUE:
				tegnBue(args[0], args[1], args[2], args[3], args[4]);
				break;
			case BYTT_PENN:
				penn.velgPenn(args[0]);
				break;
			case PENN_NED:
				penn.ned();
				break;
			case PENN_OPP:
				penn.opp();
				break;
		}
	}
	
	// Utfører flere kommandoer
	public void utforKommandoer(KommandoListe kommandoer){
		System.out.println("Utforer " + kommandoer.size() + " kommandoer");
		for(Kommando kommando : kommandoer){
			utforKommando(kommando);
		}
	}
}