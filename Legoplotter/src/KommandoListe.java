import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

// Bruk KommandoListe.add(Kommando) for å legge til kommandoer i lista
public class KommandoListe extends ArrayList<Kommando>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6368457580328239864L;
	
	public KommandoListe(){
		super();
	}
	
	// Henter kommandoer fra nettverket.
	public static KommandoListe getKommandoListe(int portnummer) throws ClassNotFoundException, IOException{
		
		ServerSocket server = new ServerSocket(portnummer);
		
		Socket forbindelse = server.accept();
		
		ObjectInputStream leseren = new ObjectInputStream(forbindelse.getInputStream());
		
		return (KommandoListe) leseren.readObject();
	}
	
	// Send kommandoer over nettverket
	public void send(String ip, int portnummer) throws SocketTimeoutException, IOException{

		Socket forbindelse = new Socket();
		forbindelse.connect(new InetSocketAddress(ip, portnummer),5000);
		
		ObjectOutputStream skriveren = new ObjectOutputStream(forbindelse.getOutputStream());
		
		System.out.println("Antall kommandoer som sendes: " + this.size());
		
		skriveren.writeObject(this);
		
		forbindelse.close();
	}
	
	public void lagre(File fil) throws FileNotFoundException, IOException{
		ObjectOutputStream skriveren = new ObjectOutputStream(new FileOutputStream(fil+".plot"));
		
		skriveren.writeObject(this);
		
		skriveren.close();
	}
	
	public void apne(File fil) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream leseren = new ObjectInputStream(new FileInputStream(fil));
		
		KommandoListe k = (KommandoListe) leseren.readObject();
		
		this.clear();
		this.addAll(k);
		
		leseren.close();
		
		
	}
}
