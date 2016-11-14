import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.omg.CORBA.INITIALIZE;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
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
import java.util.Enumeration;

public class Designer extends JPanel implements ActionListener, MouseListener, MouseMotionListener{

	private static final long serialVersionUID = -1383411119408529184L;

	private final String PLOTTER_IP = "10.101.101.1";
	
	// Valg av farger til tegning. Grensesnittet utvides automatisk når tabellen utvides.
	private final Color[] FARGER = new Color[]{Color.BLACK, Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE};
	
	public KommandoListe kommandoer = new KommandoListe();
	public KommandoListe angre_kommandoer = new KommandoListe();
	
	Kommando kommando = null;
	public boolean printing = false;
	
	//X og Y-koordinater der musen ble trykt ned på tegneområdet
	int x, y;
	
	JButton btnNy, btnApne, btnLagre, btnPrint, btnAngre, btnGjenta;
	JToggleButton btnPrikk, btnLinje, btnFirkant, btnSirkel, btnOval, btnGrid;
	ButtonGroup grpPens;
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Designer designer = new Designer();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Designer() {
		
		JFrame frame = new JFrame();
		frame.setMinimumSize(new Dimension((int)(Plotter.A4_X*3), (int)(Plotter.A4_Y*2.5)));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Plotter-designer");
		frame.setVisible(true);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.LINE_AXIS));
		frame.add(toolBar, BorderLayout.NORTH);
		
		btnNy = new JButton(new ImageIcon("icons\\file.png"));
		btnNy.setActionCommand("ny");
		btnNy.setToolTipText("Ny fil");
		btnNy.addActionListener(this);
		toolBar.add(btnNy);
		
		btnApne = new JButton(new ImageIcon("icons\\directory.png"));
		btnApne.setActionCommand("apne");
		btnApne.setToolTipText("Åpne fil");
		btnApne.addActionListener(this);
		toolBar.add(btnApne);
		
		btnLagre = new JButton(new ImageIcon("icons\\floppy_35inch_blue.png"));
		btnLagre.setActionCommand("lagre");
		btnLagre.setToolTipText("Lagre fil");
		btnLagre.addActionListener(this);
		toolBar.add(btnLagre);
		
		btnPrint = new JButton(new ImageIcon("icons\\print.png"));
		btnPrint.setActionCommand("print");
		btnPrint.setToolTipText("Skriv ut");
		btnPrint.addActionListener(this);
		toolBar.add(btnPrint);
		
		btnAngre = new JButton(new ImageIcon("icons\\undo_blue.png"));
		toolBar.add(btnAngre);
		btnAngre.setActionCommand("angre");
		btnAngre.setToolTipText("Angre");
		btnAngre.addActionListener(this);
		btnAngre.setEnabled(false);
		
		btnGjenta = new JButton(new ImageIcon("icons\\redo_blue.png"));
		toolBar.add(btnGjenta);
		btnGjenta.setActionCommand("gjenta");
		btnGjenta.setToolTipText("Gjenta");
		btnGjenta.addActionListener(this);
		btnGjenta.setEnabled(false);
		
		btnGrid = new JToggleButton(new ImageIcon("icons\\grid_blue.png"));
		toolBar.add(btnGrid);
		btnGrid.setToolTipText("Vis rutenett");
		btnGrid.setSelected(true);
		btnGrid.addActionListener(this);
		
		toolBar.addSeparator();
		
		ButtonGroup grpShapes = new ButtonGroup();
		
		btnPrikk = new JToggleButton(new ImageIcon("icons\\dot_blue.png"));
		btnPrikk.setToolTipText("Tegn prikk");
		toolBar.add(btnPrikk);
		grpShapes.add(btnPrikk);
		btnPrikk.setSelected(true);
		
		btnLinje = new JToggleButton(new ImageIcon("icons\\line_blue.png"));
		btnLinje.setToolTipText("Tegn linje");
		toolBar.add(btnLinje);
		grpShapes.add(btnLinje);
		
		btnFirkant = new JToggleButton(new ImageIcon("icons\\rectangle_blue.png"));
		btnFirkant.setToolTipText("Tegn firkant");
		toolBar.add(btnFirkant);
		grpShapes.add(btnFirkant);
		
		btnSirkel = new JToggleButton(new ImageIcon("icons\\sirkel_blue.png"));
		btnSirkel.setToolTipText("Tegn sirkel");
		toolBar.add(btnSirkel);
		grpShapes.add(btnSirkel);
		
		
		btnOval = new JToggleButton(new ImageIcon("icons\\ellipse_blue.png"));
		btnOval.setToolTipText("Tegn oval");
		toolBar.add(btnOval);
		grpShapes.add(btnOval);
		
		toolBar.addSeparator();		
		
		grpPens = new ButtonGroup();
		
		for(int i = 0; i < FARGER.length; i++){
			JToggleButton btn = new JToggleButton();
			btn.setOpaque(true);
			btn.setBackground(FARGER[i]);
			btn.setActionCommand(""+ (i+1) );
			btn.setMaximumSize(new Dimension(31,31));
			toolBar.add(btn);
			grpPens.add(btn);
			
			//Trykk inn den første farge-knappen
			if(i == 0)
				btn.setSelected(true);
		}
		
		setBackground(Color.WHITE);
		setFocusable(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Z"),"angre");
		getActionMap().put("angre", new AngreAction(this));
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control Y"),"gjenta");
		getActionMap().put("gjenta", new GjentaAction(this));

		frame.add(this, BorderLayout.CENTER);
		
		for(Component component: toolBar.getComponents()){
			if(!(component instanceof Separator)){
				AbstractButton button = (AbstractButton)component;
				
				button.setMargin(new Insets(0,0,0,0));
			}

			//button.setOpaque(false);
			//button.setBorder(null);
			//button.setContentAreaFilled(false);
			//button.setBorderPainted(false);
			//button.setContentAreaFilled(false);
		}
		

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
			int retval = chooser.showSaveDialog(getTopLevelAncestor());
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
			int retval = chooser.showOpenDialog(getTopLevelAncestor());
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
		
		repaint();

	}

	// Finn ut hvilken penn som er valgt ut fra trykknappene (brukes som index mot FARGER[], og 
	// sendes også over til plotteren som en del av kommandodataene).
	int getCurPen(){
		
		Enumeration<AbstractButton> buttons = grpPens.getElements();
		AbstractButton button = null; 
		int curButton = 1;
	
		while(buttons.hasMoreElements()){
			button = buttons.nextElement();
			
			if(button.isSelected())
				return curButton;
			
			curButton++;
		}
		
		// Ingen av fargeknappene er valgt. Merkelig. Vi returnerer 1, som er vanlig svart.
		return 1;
	}
	
	@Override
	public void mousePressed(MouseEvent e){
		if(SwingUtilities.isLeftMouseButton(e)){
			x = skalerNedX(e.getX()) - Plotter.margVenstre;
			y = skalerNedY(e.getY()) - Plotter.margTopp;
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
			
			repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)){
			int x2 = skalerNedX(e.getX()) - Plotter.margVenstre;
			
			int y2 = skalerNedY(e.getY()) - Plotter.margTopp;
			
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
				
				radius = Math.min(radius, x - 0);
				radius = Math.min(radius, Plotter.A4_X - Plotter.margHoyre - Plotter.margVenstre - x);
				radius = Math.min(radius, y - 0);
				radius = Math.min(radius, Plotter.A4_Y-Plotter.margBunn - Plotter.margTopp -y);
				
				kommando.setArg(2, radius);
			}
			else if(btnOval.isSelected()){
			
				int bredde = diffX;
				int hoyde = diffY;
				
				bredde = Math.min(bredde, x - 0);
				bredde = Math.min(bredde, Plotter.A4_X - Plotter.margHoyre - Plotter.margVenstre - x);
				hoyde = Math.min(hoyde, y- 0);
				hoyde = Math.min(hoyde, Plotter.A4_Y-Plotter.margBunn - Plotter.margTopp-y);

				kommando.setArg(2, bredde);
				kommando.setArg(3, hoyde);
			}
			
		}
		repaint();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)){			
			kommandoer.add(kommando);
			kommando = null;
			btnAngre.setEnabled(true);
			
			angre_kommandoer.clear();
			
		}
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	// Oversetter fra Y-koordinat på A4-ark til y-koordinat på skjerm
	public int skalerOppX(int x){
		double skaleringX = (double)getWidth() / Plotter.A4_X;
		
		return (int)Math.round(x * skaleringX);
	}
	
	// Oversetter fra Y-koordinat på A4-ark til y-koordinat på skjerm
	public int skalerOppY(int y){
		double skaleringY = (double)getHeight() / Plotter.A4_Y;
		
		return (int)Math.round(y*skaleringY);
	}
	
	// Oversetter fra X-koordinat på skjerm til y-koordinat på A4-ark
	public int skalerNedX(int x){
		double skaleringX = (double)getWidth() / Plotter.A4_X;
		
		x = (int)Math.round(x/skaleringX);
		x = Math.max(Plotter.margVenstre, x);
		x = Math.min(Plotter.A4_X - Plotter.margHoyre, x);
		
		return x;
	}

	// Oversetter fra Y-koordinat på skjerm til y-koordinat på A4-ark
	public int skalerNedY(int y){
		double skaleringY = (double)getHeight() / Plotter.A4_Y;
		 
		y =  (int)Math.round(y/skaleringY);
		y = Math.max(Plotter.margTopp, y);
		y = Math.min(Plotter.A4_Y - Plotter.margBunn, y);
		
		return y;
	}
	

	public void angre(){
		if(!kommandoer.isEmpty()){
			angre_kommandoer.add(kommandoer.get(kommandoer.size()-1));
			kommandoer.remove(kommandoer.size()-1);
			repaint();
			
			btnGjenta.setEnabled(true);
			
			if(kommandoer.isEmpty())
				btnAngre.setEnabled(false);
		}
	}
	
	public void gjenta(){
		if(!angre_kommandoer.isEmpty()){
			kommandoer.add(angre_kommandoer.get(angre_kommandoer.size()-1));
			angre_kommandoer.remove(angre_kommandoer.size()-1);
			repaint();
			
			btnAngre.setEnabled(true);
			
			if(angre_kommandoer.isEmpty())
				btnGjenta.setEnabled(false);
		}
	}

	public void paintComponent(Graphics g){
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(Color.BLACK);
		
		super.paintComponent(g);
		
		
		g2d.setColor(new Color(240,240,240));
		
		if(btnGrid.isSelected()){
			for(int x = 0; x < Plotter.A4_X; x+=10){
				g2d.drawLine(skalerOppX(x), 0, skalerOppX(x), getHeight());
			}
			for(int y = 0; y < Plotter.A4_Y; y+=10){
				g2d.drawLine(0, skalerOppY(y), getWidth(), skalerOppY(y));
			}
		}

		for(Kommando kommando : kommandoer){
			utforKommando(kommando, g2d);
		}
		
		//Kommandoen som evt. tegnes i dette øyeblikk, mens brukeren holder nede musen.
		if(kommando != null){
			utforKommando(kommando, g2d);
		}
		
		g2d.setColor(Color.LIGHT_GRAY);
		float dash[] = {10.0f};
		g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0));
		g2d.drawRect(	skalerOppX(Plotter.margVenstre),
				skalerOppY(Plotter.margTopp), 
				this.getWidth() - skalerOppX(Plotter.margVenstre + Plotter.margHoyre),
				this.getHeight() - skalerOppY(Plotter.margTopp + Plotter.margBunn));
	}
	
	public void utforKommando(Kommando kommando, Graphics2D g2d){
		
		Kommando.KommandoType type = kommando.getType();
		int[] args = kommando.getArgs();
		
		g2d.setColor(FARGER[kommando.getPenn()-1]);
		g2d.setStroke(new BasicStroke(2));
		
		int x1 = 0;
		int y1 = 0;
		int x2 = 0;
		int y2 = 0;
		
		if(args.length >= 2){
			x1 = skalerOppX(args[0] + Plotter.margVenstre);
			y1 = skalerOppY(args[1] + Plotter.margTopp);
		}
		if(args.length >= 4){
			x2 = skalerOppX(args[2] + Plotter.margVenstre);
			y2 = skalerOppY(args[3] + Plotter.margTopp);
		}
		
		switch(type){
			case PRIKK:{
				g2d.fillOval(x1-5,y1-5,10,10);
				break;
			}
			
			case LINJE:{
				
				
				g2d.drawLine(x1, y1, x2, y2);
				break;
			}
			case FIRKANT:{
				int bredde = Math.abs(args[2]);
				int hoyde = Math.abs(args[3]);
				if(args[2] < 0){
					x1 -= bredde;
				}
				if(args[3] < 0){
					y1 -= hoyde;
				}
				g2d.drawRect(x1, y1, skalerOppX(bredde), skalerOppY(hoyde));
				break;
			}

			case OVAL:
				x1 = skalerOppX(args[0]-args[2]+Plotter.margVenstre);
				y1 = skalerOppY(args[1]-args[3]+Plotter.margTopp);
				int bredde = skalerOppX(args[2]*2);
				int hoyde = skalerOppY(args[3]*2);
				
				g2d.drawOval(x1, y1, bredde, hoyde);
				//TODO: tegnOval(args[0], args[1], args[2], args[3]);
				break;
			case SIRKEL:{
				x1 = skalerOppX(args[0]-args[2]+Plotter.margVenstre);
				y1 = skalerOppY(args[1]-args[2]+Plotter.margTopp);
				int breddeX = skalerOppX(2*args[2]);
				int breddeY = skalerOppY(2*args[2]);
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
