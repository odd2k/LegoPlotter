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
	
	private KommandoType type;
	private int[] args;
	
	public Kommando(KommandoType type, int[] args){
		this.type = type;
		
		for(int i = 0; i < args.length; i++){
			this.args[i] = args[i];
		}
	}
	
	public static Kommando tegnPrikk(int x1, int y1){
		int[] args = new int[2];
		args[0] = x1;
		args[1] = y1;
		
		return new Kommando(KommandoType.PRIKK, args);
	}
	
	public static Kommando tegnLinje(int x1, int y1, int x2, int y2){
		int[] args = new int[4];
		args[0] = x1;
		args[1] = y1;
		args[2] = x2;
		args[3] = y2;
		
		return new Kommando(KommandoType.LINJE, args);
	}
	
	public static Kommando tegnFirkant(int x1, int y1, int bredde, int hoyde){
		int[] args = new int[4];
		args[0] = x1;
		args[1] = y1;
		args[2] = bredde;
		args[3] = hoyde;
		
		return new Kommando(KommandoType.FIRKANT, args);
	}
	
	public static Kommando tegnOval(int x1, int y1, int bredde, int hoyde){
		int[] args = new int[4];
		args[0] = x1;
		args[1] = y1;
		args[2] = bredde;
		args[3] = hoyde;
		
		return new Kommando(KommandoType.OVAL, args);
	}
	
	public static Kommando tegnSirkel(int x1, int y1, int radius){
		int[] args = new int[3];
		args[0] = x1;
		args[1] = y1;
		args[2] = radius;
		
		return new Kommando(KommandoType.SIRKEL, args);
	}
	
	public static Kommando tegnBue(int x1, int y1, int x2, int y2, int h){
		int[] args = new int[5];
		args[0] = x1;
		args[1] = y1;
		args[2] = x2;
		args[3] = y2;
		args[4] = h;
		
		return new Kommando(KommandoType.BUE, args);
	}
	
	public static Kommando byttPenn(int penn){
		int[] args = new int[1];
		args[0] = penn;
		
		return new Kommando(KommandoType.BYTT_PENN, args);
	}
	
	public static Kommando pennNed(){
		return new Kommando(KommandoType.PENN_NED, null);
	}
	
	public static Kommando pennOpp(){
		return new Kommando(KommandoType.PENN_OPP, null);
	}

	public KommandoType getType(){
		return type;
	}
	
	public int[] getArgs(){
		return args;
	}
	
}
