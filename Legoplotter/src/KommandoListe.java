import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// Bruk KommandoListe.add(Kommando) for å legge til kommandoer i lista
public class KommandoListe extends ArrayList<Kommando>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6368457580328239864L;
	private ArrayList<Kommando> kommandoer = new ArrayList<Kommando>();
	
	// Henter kommandoer fra nettverket.
	public KommandoListe(int portnummer) throws ClassNotFoundException, IOException{
		
		ServerSocket server = new ServerSocket(portnummer);
		
		Socket forbindelse = server.accept();
		
		ObjectInputStream leseren = new ObjectInputStream(forbindelse.getInputStream());
		
		Kommando k;
		
		while( (k = (Kommando) leseren.readObject()) != null ){
			kommandoer.add(k);
		}
		
		forbindelse.close();
		server.close();
	}
	
	// Send kommandoer over nettverket
	public void send(String ip, int portnummer) throws IOException{

		Socket forbindelse = new Socket(ip, portnummer);
		
		ObjectOutputStream skriveren = new ObjectOutputStream(forbindelse.getOutputStream());
		
		for(Kommando k : this){
			skriveren.writeObject(k);
		}
		
		forbindelse.close();
	}
}
