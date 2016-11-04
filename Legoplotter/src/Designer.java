import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Designer implements ActionListener, MouseListener, MouseMotionListener{

	private final String PLOTTER_IP = "10.101.101.1";
	
	private JFrame frame;
	Tegning tegning;
	public KommandoListe kommandoer = new KommandoListe();
	public KommandoListe angre_kommandoer = new KommandoListe();
	
	Kommando kommando = null;
	public boolean printing = false;
	
	//X og Y-koordinater der musen ble trykt ned på tegneområdet
	int x, y;
	
	JButton btnNy, btnApne, btnLagre, btnPrint, btnAngre, btnGjenta;
	JToggleButton btnPrikk, btnLinje, btnFirkant, btnSirkel, btnOval, btnGrid;
	JToggleButton btnPenn1, btnPenn2, btnPenn3, btnPenn4;
	ButtonGroup grpPens;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Designer window = new Designer();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Designer() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		//frame.setResizable(false);
		frame.setMinimumSize(new Dimension((int)(Plotter.A4_X*3), (int)(Plotter.A4_Y*2.5)));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Plotter-designer");
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		btnNy = new JButton("Ny");
		btnNy.setActionCommand("ny");
		btnNy.addActionListener(this);
		toolBar.add(btnNy);
		
		btnApne = new JButton("Åpne");
		btnApne.setActionCommand("apne");
		btnApne.addActionListener(this);
		toolBar.add(btnApne);
		
		btnLagre = new JButton("Lagre");
		btnLagre.setActionCommand("lagre");
		btnLagre.addActionListener(this);
		toolBar.add(btnLagre);
		
		btnPrint = new JButton("Print");
		btnPrint.setActionCommand("print");
		btnPrint.addActionListener(this);
		toolBar.add(btnPrint);
		
		ButtonGroup grpShapes = new ButtonGroup();
		
		btnPrikk = new JToggleButton("Prikk");
		toolBar.add(btnPrikk);
		grpShapes.add(btnPrikk);
		btnPrikk.setSelected(true);
		
		btnLinje = new JToggleButton("Linje");
		toolBar.add(btnLinje);
		grpShapes.add(btnLinje);
		
		btnFirkant = new JToggleButton("Firkant");
		toolBar.add(btnFirkant);
		grpShapes.add(btnFirkant);
		
		btnSirkel = new JToggleButton("Sirkel");
		toolBar.add(btnSirkel);
		grpShapes.add(btnSirkel);
		
		btnOval = new JToggleButton("Oval");
		toolBar.add(btnOval);
		grpShapes.add(btnOval);
		
		btnAngre = new JButton("Angre");
		toolBar.add(btnAngre);
		btnAngre.setActionCommand("angre");
		btnAngre.addActionListener(this);
		btnAngre.setEnabled(false);
		
		btnGjenta = new JButton("Gjenta");
		toolBar.add(btnGjenta);
		btnGjenta.setActionCommand("gjenta");
		btnGjenta.addActionListener(this);
		btnGjenta.setEnabled(false);
		
		btnGrid = new JToggleButton("Grid");
		toolBar.add(btnGrid);
		btnGrid.setSelected(true);
		btnGrid.addActionListener(this);
		
		grpPens = new ButtonGroup();
		
		btnPenn1 = new JToggleButton("1"); btnPenn1.setForeground(Color.BLACK);
		btnPenn2 = new JToggleButton("2"); btnPenn2.setForeground(Color.RED);
		btnPenn3 = new JToggleButton("3"); btnPenn3.setForeground(Color.BLUE);
		btnPenn4 = new JToggleButton("4"); btnPenn4.setForeground(Color.GREEN);
		
		toolBar.add(btnPenn1); toolBar.add(btnPenn2); toolBar.add(btnPenn3); toolBar.add(btnPenn4);
		grpPens.add(btnPenn1); grpPens.add(btnPenn2); grpPens.add(btnPenn3); grpPens.add(btnPenn4);
		btnPenn1.setSelected(true);
		
		tegning = new Tegning(this);
		tegning.setBackground(Color.WHITE);
		tegning.setFocusable(true);
		tegning.addMouseListener(this);
		tegning.addMouseMotionListener(this);
		
		tegning.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"),"angre");
		tegning.getActionMap().put("angre", new AngreAction(this));
		
		tegning.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y"),"gjenta");
		tegning.getActionMap().put("gjenta", new GjentaAction(this));
		
		
		frame.getContentPane().add(tegning, BorderLayout.CENTER);
		

	}
	
	public void actionPerformed(ActionEvent e){
		
		if(e.getActionCommand().equals("ny")){
			kommandoer.clear();
		}
		else if(e.getActionCommand().equals("print")){
	    	if(printing)
	    		return;
	    	
		    Thread printThread = new Thread() {
		        public void run() {
		        	printing = true;
		        	
		        	try {
						kommandoer.send(PLOTTER_IP, 1256);
					} 
		        	catch(SocketTimeoutException e){
	        		
	        		InetAddress ip = null;
	        		boolean reachable = false;
	        		
	        		try {
						ip = InetAddress.getByName(PLOTTER_IP);
						reachable = ip.isReachable(500);
						
					} catch (Exception e1) {}
	        		
	        			String errormsg = 	"Kunne ikke koble til plotter!\n" +
	        								"Plotteren svarer " + (reachable ? "" : "IKKE ") + "på ping.";
		        		
		        		JOptionPane.showMessageDialog(null, errormsg, "Tilkoblingsfeil", JOptionPane.ERROR_MESSAGE);
		        	}
		        	catch (IOException e) {
		        		JOptionPane.showMessageDialog(null, "Ukjent feil: " + e.getMessage());
					}
		        	printing = false;
		        }
		      };
		      printThread.start();
		}
		else if(e.getActionCommand().equals("angre")){
			angre();
		}
		else if(e.getActionCommand().equals("gjenta")){
			gjenta();
		}
		else if(e.getActionCommand().equals("lagre")){
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("Plotter-filer (.plot)", "plot"));
			int retval = chooser.showSaveDialog(frame);
			if(retval == JFileChooser.APPROVE_OPTION){
				File file = chooser.getSelectedFile();
				
				try {
					kommandoer.lagre(file);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else if(e.getActionCommand().equals("apne")){
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("Plotter-filer (.plot)", "plot"));
			int retval = chooser.showOpenDialog(frame);
			if(retval == JFileChooser.APPROVE_OPTION){
				File file = chooser.getSelectedFile();
				
				try {
					kommandoer.apne(file);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				angre_kommandoer.clear();
				
			}
		}
		
		tegning.repaint();

	}

	
	public int getCurPen(){
		if(btnPenn1.isSelected())
			return 1;
		else if(btnPenn2.isSelected())
			return 2;
		else if(btnPenn3.isSelected())
			return 3;
		else if(btnPenn4.isSelected())
			return 4;
		else{
			return 0;
		}

	}
	
	@Override
	public void mousePressed(MouseEvent e){
		if(SwingUtilities.isLeftMouseButton(e)){
			x = skalerNedX(e.getX()); x = boundX(x);
			y = skalerNedY(e.getY()); y = boundY(y);
			if(btnPrikk.isSelected())
				kommando = Kommando.tegnPrikk(x, y, getCurPen());
			else if(btnLinje.isSelected())
				kommando = Kommando.tegnLinje(x, y, x, y, getCurPen());
			else if(btnFirkant.isSelected())
				kommando = Kommando.tegnFirkant(x, y, 0, 0, getCurPen());
			else if(btnSirkel.isSelected())
				kommando = Kommando.tegnSirkel(x, y, 0, getCurPen());
			else if(btnOval.isSelected())
				kommando = Kommando.tegnOval(x, y, 0, 0, getCurPen());
			
			tegning.repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)){
			int x2 = skalerNedX(e.getX());
			x2 = boundX(x2);
			
			int y2 = skalerNedY(e.getY());
			y2 = boundY(y2);
			
			// Distanse mellom punkt der musen ble trykt ned, og punktet der musepekeren er nå.
			int diffX = Math.abs(x2 - x);
			int diffY = Math.abs(y2 - y);
			
			if(btnPrikk.isSelected()){
				//kommando.setArg(0, x2);
				//kommando.setArg(1, y2);
			}
			else if(btnLinje.isSelected()){
				kommando.setArg(2, x2);
				kommando.setArg(3, y2);
			}
			else if(btnFirkant.isSelected()){
				
				kommando.setArg(0, Math.min(x, x2));
				kommando.setArg(1, Math.min(y, y2));
				
				if(y2 < y)
					kommando.setArg(1, y2);
				else
					kommando.setArg(1, y);
				
				kommando.setArg(2, diffX);
				kommando.setArg(3, diffY);
			}
			else if(btnSirkel.isSelected()){

				int radius = (int) Math.sqrt(Math.pow(diffX, 2)+Math.pow(diffY, 2));
				
				radius = Math.min(radius, x - Plotter.margVenstre);
				radius = Math.min(radius, Plotter.A4_X - Plotter.margHoyre - x);
				radius = Math.min(radius, y-Plotter.margTopp);
				radius = Math.min(radius, Plotter.A4_Y-Plotter.margBunn-y);
				
				kommando.setArg(2, radius);
			}
			else if(btnOval.isSelected()){
			
				int bredde = diffX;
				int hoyde = diffY;
				
				bredde = Math.min(bredde, x - Plotter.margVenstre);
				bredde = Math.min(bredde, Plotter.A4_X - Plotter.margHoyre - x);
				hoyde = Math.min(hoyde, y-Plotter.margTopp);
				hoyde = Math.min(hoyde, Plotter.A4_Y-Plotter.margBunn-y);

				kommando.setArg(2, bredde);
				kommando.setArg(3, hoyde);
			}
			
		}
		tegning.repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)){			
			kommandoer.add(kommando);
			kommando = null;
			btnAngre.setEnabled(true);
		}
		tegning.repaint();
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

	private double skaleringX(){
		return (double)tegning.getWidth() / Plotter.A4_X;
	}

	private double skaleringY(){
		return (double)tegning.getHeight() / Plotter.A4_Y;
	}
	
	
	public int skalerOppX(int x){
		return (int)Math.round(x*skaleringX());
	}
	
	public int skalerNedX(int x){
		return (int)Math.round(x/skaleringX());
	}
	
	public int skalerOppY(int y){
		return (int)Math.round(y*skaleringY());
	}
	
	public int skalerNedY(int y){
		return (int)Math.round(y/skaleringY());
	}
	
	private int boundX(int x){
		x = Math.max(Plotter.margVenstre, x);
		x = Math.min(Plotter.A4_X - Plotter.margHoyre, x);
		
		return x;
	}
	
	private int boundY(int y){
		y = Math.max(Plotter.margTopp, y);
		y = Math.min(Plotter.A4_Y - Plotter.margBunn, y);
		
		return y;
	}

	public void angre(){
		if(!kommandoer.isEmpty()){
			angre_kommandoer.add(kommandoer.get(kommandoer.size()-1));
			kommandoer.remove(kommandoer.size()-1);
			tegning.repaint();
			
			btnGjenta.setEnabled(true);
			
			if(kommandoer.isEmpty())
				btnAngre.setEnabled(false);
		}
	}
	
	public void gjenta(){
		if(!angre_kommandoer.isEmpty()){
			kommandoer.add(angre_kommandoer.get(angre_kommandoer.size()-1));
			angre_kommandoer.remove(angre_kommandoer.size()-1);
			tegning.repaint();
			
			btnAngre.setEnabled(true);
			
			if(angre_kommandoer.isEmpty())
				btnGjenta.setEnabled(false);
		}
	}

}



class Tegning extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7019883647777777385L;
	private Designer designer;
	
	public void paintComponent(Graphics g){
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(Color.BLACK);
		
		super.paintComponent(g);
		
		
		g2d.setColor(new Color(240,240,240));
		
		if(designer.btnGrid.isSelected()){
			for(int x = 0; x < Plotter.A4_X; x+=10){
				g2d.drawLine(designer.skalerOppX(x), 0, designer.skalerOppX(x), getHeight());
			}
			for(int y = 0; y < Plotter.A4_Y; y+=10){
				g2d.drawLine(0, designer.skalerOppY(y), getWidth(), designer.skalerOppY(y));
			}
		}

		for(Kommando kommando : designer.kommandoer){
			utforKommando(kommando, g2d);
		}
		
		//Kommandoen som evt. tegnes i dette øyeblikk, mens brukeren holder nede musen.
		if(designer.kommando != null){
			utforKommando(designer.kommando, g2d);
		}
		
		g2d.setColor(Color.LIGHT_GRAY);
		float dash[] = {10.0f};
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0));
		g2d.drawRect(	designer.skalerOppX(Plotter.margVenstre),
				designer.skalerOppY(Plotter.margTopp), 
				this.getWidth() - designer.skalerOppX(Plotter.margVenstre + Plotter.margHoyre),
				this.getHeight() - designer.skalerOppY(Plotter.margTopp + Plotter.margBunn));
	}

	
	public Tegning(Designer designer){
		this.designer = designer;
	}

	
	public void utforKommando(Kommando kommando, Graphics2D g2d){
		
		Kommando.KommandoType type = kommando.getType();
		int[] args = kommando.getArgs();
		
		switch(kommando.getPenn()){
			case 1:
			default:
				g2d.setColor(designer.btnPenn1.getForeground());
				break;
			case 2:
				g2d.setColor(designer.btnPenn2.getForeground());
				break;
			case 3:
				g2d.setColor(designer.btnPenn3.getForeground());
				break;
			case 4:
				g2d.setColor(designer.btnPenn4.getForeground());
				break;
		}
		g2d.setStroke(new BasicStroke(2));
		
		switch(type){
			case PRIKK:{
				int x = designer.skalerOppX(args[0]);
				int y = designer.skalerOppY(args[1]);
				g2d.fillOval(x-5,y-5,10,10);
				break;
			}
			
			case LINJE:{
				g2d.drawLine(	designer.skalerOppX(args[0]),
						designer.skalerOppY(args[1]),
						designer.skalerOppX(args[2]),
						designer.skalerOppY(args[3]));
				break;
			}
			case FIRKANT:{
				int x1 = args[0];
				int y1 = args[1];
				int bredde = Math.abs(args[2]);
				int hoyde = Math.abs(args[3]);
				if(args[2] < 0){
					x1 -= bredde;
				}
				if(args[3] < 0){
					y1 -= hoyde;
				}
				g2d.drawRect(	designer.skalerOppX(x1),
								designer.skalerOppY(y1),
								designer.skalerOppX(bredde),
								designer.skalerOppY(hoyde));
				break;
			}

			case OVAL:
				int x = designer.skalerOppX(args[0]-args[2]);
				int y = designer.skalerOppY(args[1]-args[3]);
				int bredde = designer.skalerOppX(args[2]*2);
				int hoyde = designer.skalerOppY(args[3]*2);
				
				g2d.drawOval(x, y, bredde, hoyde);
				//TODO: tegnOval(args[0], args[1], args[2], args[3]);
				break;
			case SIRKEL:{
				int x1 = designer.skalerOppX(args[0]-args[2]);
				int y1 = designer.skalerOppY(args[1]-args[2]);
				int breddeX = designer.skalerOppX(2*args[2]);
				int breddeY = designer.skalerOppY(2*args[2]);
				g2d.drawOval(x1, y1, breddeX, breddeY);
				//TODO: tegnSirkel(args[0], args[1], args[2]);
				break;
			}

			case BUE:
				//TODO: tegnBue(args[0], args[1], args[2], args[3], args[4]);
				break;
			case BYTT_PENN:
			case PENN_NED:
			case PENN_OPP:
				break;
		}
	}
	

}

class AngreAction extends AbstractAction{
	
	private static final long serialVersionUID = 3499842459805898252L;
	Designer designer = null;
	
	public AngreAction(Designer designer){
		this.designer = designer;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		designer.angre();
	}
	
}

class GjentaAction extends AbstractAction{

	private static final long serialVersionUID = -41447889653539512L;
	Designer designer = null;
	
	public GjentaAction(Designer designer){
		this.designer = designer;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		designer.gjenta();

	}
}
