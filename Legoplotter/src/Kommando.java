import java.io.Serializable;

public class Kommando implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1461160317801348936L;

	public enum KommandoType{
		PRIKK(1),
		LINJE(2),
		FIRKANT(3),
		OVAL(4),
		SIRKEL(5),
		BUE(6),
		BYTT_PENN(7),
		PENN_NED(8),
		PENN_OPP(9);
		private int value;
		private KommandoType(int value){this.value = value;}
	}
	
	private KommandoType type; // Spesifiserer hvilken type kommando dette er
	private int[] args; // Argumentene til kommandoer. Noen kommandoer har flere argumenter enn andre.
	private int penn = 0; // Hvilken penn skal være aktiv mens kommandoen utføres?
	
	public Kommando(KommandoType type, int[] args, int penn){
		this.type = type;
		
		this.args = new int[args.length];
		
		for(int i = 0; i < args.length; i++){
			this.args[i] = args[i];
		}
		
		this.penn = penn;
	}
	
	// Funksjonene under genererer kommando-objekter tilhørende ulike figurer.
	
	public static Kommando tegnPrikk(int x1, int y1, int penn){
		int[] args = new int[2];
		args[0] = x1;
		args[1] = y1;
		
		return new Kommando(KommandoType.PRIKK, args, penn);
	}
	
	public static Kommando tegnLinje(int x1, int y1, int x2, int y2, int penn){
		int[] args = new int[4];
		args[0] = x1;
		args[1] = y1;
		args[2] = x2;
		args[3] = y2;
		
		return new Kommando(KommandoType.LINJE, args, penn);
	}
	
	public static Kommando tegnFirkant(int x1, int y1, int bredde, int hoyde, int penn){
		int[] args = new int[4];
		args[0] = x1;
		args[1] = y1;
		args[2] = bredde;
		args[3] = hoyde;
		
		return new Kommando(KommandoType.FIRKANT, args, penn);
	}
	
	public static Kommando tegnOval(int x1, int y1, int bredde, int hoyde, int penn){
		int[] args = new int[4];
		args[0] = x1;
		args[1] = y1;
		args[2] = bredde;
		args[3] = hoyde;
		
		return new Kommando(KommandoType.OVAL, args, penn);
	}
	
	public static Kommando tegnSirkel(int x1, int y1, int radius, int penn){
		int[] args = new int[3];
		args[0] = x1;
		args[1] = y1;
		args[2] = radius;
		
		return new Kommando(KommandoType.SIRKEL, args, penn);
	}
	
	public static Kommando tegnBue(int x1, int y1, int x2, int y2, int h, int penn){
		int[] args = new int[5];
		args[0] = x1;
		args[1] = y1;
		args[2] = x2;
		args[3] = y2;
		args[4] = h;
		
		return new Kommando(KommandoType.BUE, args, penn);
	}
	
	public static Kommando byttPenn(int penn){
		int[] args = new int[1];
		args[0] = penn;
		
		return new Kommando(KommandoType.BYTT_PENN, args, penn);
	}
	
	public static Kommando pennNed(){
		return new Kommando(KommandoType.PENN_NED, null, 0);
	}
	
	public static Kommando pennOpp(){
		return new Kommando(KommandoType.PENN_OPP, null, 0);
	}

	// Hent aktiv kommandotype
	public KommandoType getType(){
		return type;
	}
	
	//Hent aktiv penn
	public int getPenn(){
		return penn;
	}
	
	//Hent argumenter
	public int[] getArgs(){
		return args;
	}
	
	// Hent gitt argument
	public int getArg(int index){
		return args[index];
	}
	
	// Sett gitt argument
	public void setArg(int index, int val){
		args[index] = val;
	}
	
	public String toString(){
		String toString = "" + type + " ";
		for(int i = 0; i < args.length; i++){
			toString += args[i] + " ";
		}
		return toString;
	}
	
}
