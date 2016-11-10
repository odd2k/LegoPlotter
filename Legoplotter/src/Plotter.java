
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.sensor.EV3TouchSensor;

public class Plotter{
	public static int A4_X = 210; // I millimeter
	public static int A4_Y = 297;	// I millimeter
	
	private int x; // kortsida - 0-210mm
	private int y; // langsida - 0-297mm

	private double hjulDiameter;
	private double utvekslingX, utvekslingY;
	
	private int makshastighet = 20; // millimeter per sekund!
	
	//OBS: Lekeverdier for å teste designer.
	public static final int margTopp = 10; // mm
	public static final int margHoyre = 20; // mm
	public static final int margBunn = 30; // mm
	public static final int margVenstre = 10; // mm

	//private boolean pennNede = false; // Her kommer det en pennVelger

	private NXTRegulatedMotor motorX;
	private NXTRegulatedMotor motorY;
	private NXTRegulatedMotor motorY2;
	//private EV3LargeRegulatedMotor motorZ;
	private NXTTouchSensor endestoppX;
	private NXTTouchSensor endestoppY;
	//private EV3TouchSensor endestoppZ;
	private float[] sample = new float[1];
	
	private PennVelger penn;
	
	public Plotter(NXTRegulatedMotor motorX, NXTRegulatedMotor motorY, NXTRegulatedMotor motorY2, EV3LargeRegulatedMotor motorZ, 
			NXTTouchSensor endestoppX, NXTTouchSensor endestoppY, EV3TouchSensor endestoppZ, double hjulDiameter, double utvekslingX, double utvekslingY){
		if(hjulDiameter <= 0){
			throw new IllegalArgumentException("Diameteren paa hjulet kan ikke vaere mindre eller lik 0");
		}else{
		this.motorX = motorX;
		this.motorY = motorY;
		this.motorY2 = motorY2;
		//this.motorZ = motorZ;
		this.endestoppX = endestoppX;
		this.endestoppY = endestoppY;
		//this.endestoppZ = endestoppZ;
		this.hjulDiameter = hjulDiameter;
		
		this.utvekslingX = utvekslingX;
		this.utvekslingY = utvekslingY;
		
		setSpeedX(makshastighet);
		setSpeedY(makshastighet);
	
		this.penn = new PennVelger(motorZ, endestoppZ);
		}
		
		home();
	}
	
	public void settMakshastighet(int makshastighet){
		this.makshastighet = makshastighet;
	}
	/*
	//TODO: Lag metoden!
	public void velgPenn(int valgtPenn){// sendes til pennVelger.velgPenn
		
	}

	*/
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
		sjekkMarg(x1-bredde,y1-hoyde,x1+bredde,y1+hoyde);
		int radiusX = bredde/2;
		int radiusY = hoyde/2;
		for(int deg = 0; deg<=360; deg++){
			double rad = Math.toRadians(deg);
			int x = (int) Math.round(x1 + radiusX * Math.cos(rad));
			int y = (int) Math.round(y1 + radiusY * Math.sin(rad));
			move(x, y);
		}
	}
	
	//TODO: Finn en annen løsning
	public void tegnSirkel(int x1, int y1, int radius){
		sjekkMarg(x1-radius,y1-radius,x1+radius,y1+radius);
		for(int deg = 0; deg<=360; deg++){
			double rad = Math.toRadians(deg);
			int x = (int) Math.round(x1 + radius * Math.cos(rad));
			int y = (int) Math.round(y1 + radius * Math.sin(rad));
			move(x, y);
		}
	}

	
	
	//TODO: Lag metoden!
	public void tegnBue(int x1, int y1, int x2, int y2, int h){
		sjekkMarg(x1,y1,x2,y2);
		//move(x1,y1);
		double radius = Math.abs((((h^3)/2d)+(((double)h*Math.sqrt(((x2-x1)^2)+((y2-y1)^2)))/4)));
		double bue = 2*(Math.sin(1-(h/radius)));
		/* vektor en = (x2-x1,y2-y1)
		 * vektor to = (y2-y1,-(x2-x1))
		 * midtpunkt t = |vektor en|/2+x1;
		 * senter i sirkel(s) = t + (radius-h) * vektor to;
		 */
		int tx = Math.round((x1+x2)/2);
		int ty = Math.round((y1+y2)/2);
		double k = radius/(Math.abs((x2-x1)^2)+((y2-y1)^2));
		int xs = (int)Math.round(k*(y2-y1));// X senter
		int ys = (int)Math.round(k*(-(x2-x1)));// Y senter
		for(int deg = 0; deg<=360; deg++){
			double rad = Math.toRadians(deg);
			int x = (int) Math.round(xs + radius * Math.cos(rad));//denne delen stemmer ikke
			int y = (int) Math.round(ys + radius * Math.sin(rad));//nøkkelord: vektorregning!
			if((h > 0 && x >= x1 || x >= x2 && y >= y1 || y>= y2)||(h < 0 && x <= x1 || x <= x2 && y <= y1 || y <= y2)){
				move(x, y);
			} else {
				System.out.println("Can't make it!");
			}
		}
		System.out.println(bue);
	}
	
	private void home(){
		boolean xHjemme = false;
		boolean yHjemme = false;
		motorX.backward();// bakover er bakover.
		motorY.backward();// bakover er bakover.
		motorY2.backward(); //bakover er bakover :)
		while(!xHjemme || !yHjemme){
			if(endestoppXTryktNed()){
				motorX.stop();
				x = margVenstre;
				xHjemme = true;
			}
			if(endestoppYTryktNed()){
				motorY.stop();
				motorY2.stop();
				y = margTopp;
				yHjemme = true;
			}
		}
		
	}
	
	// Returnerer bredde på det området på arket som ligger innenfor margene
	public static int getBredde(){
		return A4_X - (margVenstre + margHoyre);
	}
	
	// Returnerer høyde på det området på arket som ligger innenfor margene
	public static int getHoyde(){
		return A4_Y - (margTopp + margBunn);
	}
	
	//TODO: Gjør metoden avansert!
	//TODO: Bytt til private når vi er ferdig å teste denne?
	public void move(int x1, int y1){// Flytt til kordinat.
		
		int lengdeX = Math.abs(x1 - x);
		int lengdeY = Math.abs(y1 - y);
		
		// Om bevegelse i X-retning er 3 ganger større enn i y-retning, skal
		// motor Y bevege seg 3 ganger så langsomt som X.
		// Om den ene bevegelsen er 0, er ikke dette så nøye.
		
		if(lengdeX > lengdeY && lengdeX > 0){
			setSpeedX(makshastighet);
			setSpeedY(makshastighet * (double)lengdeY/lengdeX);
		}
		else if(lengdeY > lengdeX && lengdeY > 0){
			setSpeedX(makshastighet * (double)lengdeX/lengdeY);
			setSpeedY(makshastighet);
		}
		else{
			setSpeedX(makshastighet);
			setSpeedY(makshastighet);
		}
		
		motorX.resetTachoCount(); motorY.resetTachoCount(); motorY2.resetTachoCount();
		
		moveX(lengdeX);
		moveY(lengdeY);
		
		// Ikke hopp ut av metoden før motorene har sluttet å bevege seg, og pennen er over (x1,y1)
		while(motorX.getTachoCount() < millimeterTilGrader(lengdeX, utvekslingX) && 
				motorY.getTachoCount() < millimeterTilGrader(lengdeY, utvekslingY)){}
		
		x = x1;
		y = y1;
		
		System.out.println("x: " + x + "   y: " + y);
	}

	/*
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
	*/
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
	public static boolean sjekk(int x1, int y1){
		return sjekkX(x1) && sjekkY(y1);
	}
	
	private static boolean sjekkX(int x1){
		return (x1 < 0 || x1 > A4_X - (margVenstre + margHoyre));
	}
	
	private static boolean sjekkY(int y1){
		return(y1 < 0 || y1 > A4_Y - (margTopp + margBunn));
	}
		
	private boolean endestoppXTryktNed(){
		endestoppX.fetchSample(sample, 0);
		return (sample[0]==1);
	}
	
	private boolean endestoppYTryktNed(){
		endestoppY.fetchSample(sample, 0);
		return (sample[0]==1);
	}
	/*
	private boolean endestoppZTryktNed(){
		endestoppZ.fetchSample(sample, 0);
		return (sample[0]==1);
	}
	*/
	private int graderTilMillimeter(int grader, double utveksling){
		int millimeter = (int)Math.round((Math.PI * hjulDiameter * grader * utveksling) / (360));
		return millimeter;
	}
	
	private int millimeterTilGrader(int millimeter, double utveksling){
		int grader = (int)Math.round((360 * millimeter) / (Math.PI * hjulDiameter * utveksling));
		return grader;
	}
	
	//Tar inn millimeter per sekund
	private void setSpeedX(double mmps){
		motorX.setSpeed(millimeterTilGrader((int)Math.round(mmps), utvekslingX));
	}
	
	//Tar inn millimeter per sekund
	private void setSpeedY(double mmps){
		motorY.setSpeed(millimeterTilGrader((int)Math.round(mmps), utvekslingX));
		motorY2.setSpeed(millimeterTilGrader((int)Math.round(mmps), utvekslingX));
	}
	
	private void moveX(int mm){
		motorX.rotate(millimeterTilGrader(mm, utvekslingX), true);
	}
	
	private void moveY(int mm){
		motorY.rotate(millimeterTilGrader(mm, utvekslingY), true);
		motorY2.rotate(millimeterTilGrader(mm, utvekslingY), true);
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