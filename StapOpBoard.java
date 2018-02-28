package stapopspel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class StapOpBoard implements MouseListener {

	private JFrame frame = new JFrame("Stap Op");
	private JTextArea[] textComp = new JTextArea[2];
	private JTextArea textMsg = new JTextArea();
	private JTextArea textSelf = new JTextArea();
    
	private JLabel[] kaartHand = new JLabel[6];
	private JLabel kaartStatus = new JLabel();
	private JLabel kaartWind = new JLabel();
	private JLabel kaartWeg = new JLabel();
	
    private JPanel[] panel_kaart = new JPanel[6];
    private JPanel[] panel_comp = new JPanel[2];
    private JPanel panel_self = new JPanel(new GridBagLayout());
    private JPanel panel_weg = new JPanel();
    private JPanel panel_status = new JPanel();
    private JPanel panel_wind = new JPanel();
    private JScrollPane panel_msg = new JScrollPane(textMsg);
    
    private ImageIcon[] scaledCard = new ImageIcon[13];
    private ImageIcon[] scaledCardHor = new ImageIcon[13];
    
    private int chosenCard = 0;
    private char chosenUse = 'x';
    private String phase = "ChooseCard";
    private boolean playAgain = false;
    private boolean nextGame = false;
        
    
	public StapOpBoard() {
// Constructor to create and set up the main window.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        panel_msg.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        Color myRed = new Color(230,18,14);
        
        for (int count = 0; count <= 5; count++) {
        	panel_kaart[count] = new JPanel();
        	panel_kaart[count].setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        	panel_kaart[count].setBackground(Color.gray);
        	panel_kaart[count].addMouseListener(this);
        }
        
        panel_weg.addMouseListener(this);
        panel_self.addMouseListener(this);
        panel_status.addMouseListener(this);
        panel_wind.addMouseListener(this);
        
        
        panel_status.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        panel_wind.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        panel_weg.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        panel_self.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        panel_msg.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        
        panel_status.setBackground(Color.gray);
        panel_wind.setBackground(Color.gray);
        panel_weg.setBackground(Color.gray);
        panel_self.setBackground(Color.gray);
        panel_msg.setBackground(Color.gray);
        
        JPanel panel_hand = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.weightx = 0.5;
        for (int count = 0; count <= 5; count++) {
            c.gridx = count;
            panel_hand.add(panel_kaart[count], c);
        }
        
        JPanel panel_up = new JPanel(new GridBagLayout());
        c.gridx = 0;
        panel_up.add(panel_status, c);
        c.gridx = 1;
        panel_up.add(panel_wind, c);
        c.gridx = 2;
        panel_up.add(panel_self, c);
        
        for (int comp = 1; comp <= 2; comp++) {
        	panel_comp[comp-1] = new JPanel(new GridBagLayout());
        	panel_comp[comp-1].setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        	panel_comp[comp-1].setBackground(Color.gray);
        	panel_comp[comp-1].addMouseListener(this);
        	c.gridx += 1;
        	panel_up.add(panel_comp[comp-1], c);
        }
    
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        panel_up.add(panel_weg, c);        
        c.gridx = 2; c.gridy = 1; c.gridwidth = 3;
        panel_up.add(panel_msg, c);
        
        c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
        JLabel compA = new JLabel("Computer A", SwingConstants.CENTER);
        compA.setFont(new Font("Arial", Font.BOLD, 20));
        panel_comp[0].add(compA, c);
        JLabel compB = new JLabel("Computer B", SwingConstants.CENTER);
        compB.setFont(new Font("Arial", Font.BOLD, 20));
        panel_comp[1].add(compB, c);
        JLabel self = new JLabel("Uzelf", SwingConstants.CENTER);
        self.setFont(new Font("Arial", Font.BOLD, 20));
        panel_self.add(self, c);
                
// add text areas in panels
        textMsg.setFont(new Font("Serif", Font.BOLD, 16));
    	textMsg.setLineWrap(false);
    	textMsg.setEditable(false);
    	textMsg.setText("");
    	
    	c.gridy = 1; c.fill = GridBagConstraints.HORIZONTAL;
		textSelf.setFont(new Font("Serif", Font.BOLD, 16));
		textSelf.setLineWrap(false);
		textSelf.setEditable(false);
		textSelf.addMouseListener(this);
		panel_self.add(textSelf, c);
    	
    	for (int comp = 0; comp <= 1; comp++) {
    		textComp[comp] = new JTextArea();
    		textComp[comp].setFont(new Font("Serif", Font.BOLD, 16));
    		textComp[comp].setLineWrap(false);
    		textComp[comp].setEditable(false);
    		textComp[comp].addMouseListener(this);
    		panel_comp[comp].add(textComp[comp], c);
    	}
    	
// scale cards
        for (int card = 0; card <= 12; card++) {
			String kaartString = Integer.toString(card);
			scaledCard[card] = new ImageIcon("src\\images\\kaart" + kaartString + "xs.png");
		}    	
        for (int card = 0; card <= 12; card++) {
			String kaartString = Integer.toString(card);
			scaledCardHor[card] = new ImageIcon("src\\images\\kaart" + kaartString + "xhs.png");
		}
    	
// add dummy cards
		for (int count = 0; count <= 5; count++) {
			kaartHand[count] = new JLabel();
			kaartHand[count].setIcon(scaledCard[0]);
			panel_kaart[count].add(kaartHand[count]);
		}
		kaartStatus.setIcon(scaledCard[0]);
		kaartWind.setIcon(scaledCard[0]);
		kaartWeg.setIcon(scaledCardHor[0]);
    	panel_status.add(kaartStatus);
    	panel_wind.add(kaartWind);
    	panel_weg.add(kaartWeg);
        
        JSplitPane panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel_up, panel_hand);
        
        frame.add(panel);
		
        frame.pack();
        frame.setResizable(false);
	}
	
	
	public void drawBoard(GameState GS) {
// Function to update the components in the main window.		
		
		Color myRed = new Color(230,18,14);
		
// set background and border of panel with mouse listener back to normal
		for (int count = 0; count <= 5; count++) {
			panel_kaart[count].setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        	panel_kaart[count].setBackground(Color.gray);
		}
		for (int comp = 1; comp <= 2; comp++) {
        	panel_comp[comp-1].setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        	panel_comp[comp-1].setBackground(Color.gray);
		}
		panel_self.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
		panel_self.setBackground(Color.gray);
		panel_status.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
		panel_status.setBackground(Color.gray);
		panel_wind.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
		panel_wind.setBackground(Color.gray);
		panel_weg.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
		panel_weg.setBackground(Color.gray);
	
// cards in user's hand		
		for (int count = 0; count < GS.hand.get(0).size(); count++) {
			int numCard = GS.hand.get(0).get(count).getKaartnummer();
			kaartHand[count].setIcon(scaledCard[numCard]);
		}
		if (GS.hand.get(0).size()==5) {
			kaartHand[5].setIcon(scaledCard[0]);
		}
        
// status and wind cards of user		
        kaartStatus.setIcon(scaledCard[GS.status[0].getKaartnummer()]);
        kaartWind.setIcon(scaledCard[GS.wind[0].getKaartnummer()]);

// top card of discarded deck
        kaartWeg.setIcon(scaledCardHor[GS.weg.getKaartnummer()]);
        
// cycled distances of user
        String textPrint = "--------------------------------------------\n" +    
        		" Bossen:  " + GS.numKMcards[0][0]*5 + " van 40 KM \n" +
				" Hei:        " + GS.numKMcards[0][1]*6 + " van 24 KM \n" +
				" Zee:        " + GS.numKMcards[0][2]*8 + " van 16 KM \n" +
				" Plassen:  " + GS.numKMcards[0][3]*10 + " van 20 KM \n" +
				" Totaal:    " + GS.gameTotalKM(0) + " KM \n \n \n" ;
        textSelf.setText(textPrint);
        
// cycled distances of computer players
        for (int comp = 1; comp <= 2; comp++) {
        	textPrint =  "-------------------------------------------\n" +   
        				 " Bossen:  " + GS.numKMcards[comp][0]*5 + " van 40 KM \n" +
        				 " Hei:        " + GS.numKMcards[comp][1]*6 + " van 24 KM \n" +
        				 " Zee:        " + GS.numKMcards[comp][2]*8 + " van 16 KM \n" +
        				 " Plassen:  " + GS.numKMcards[comp][3]*10 + " van 20 KM \n" +
        				 " Totaal:    " + GS.gameTotalKM(comp) + " KM \n \n" +
        				 " Status: " + GS.status[comp].getKaartnaam() + "\n" +
        				 " Wind:  " + GS.wind[comp].getKaartnaam();
        	textComp[comp-1].setText(textPrint);
        }
        
        frame.setVisible(true);

	}
	
	
	public void standings(int[][] totalKM) {
// Function to print standings after a completed game (etappe).		
		String textPrint[] = {"-------------------------------------------\n", 
				"-------------------------------------------\n", 
				"------------------------------------------\n"};
		for (int player = 0; player <= 2; player++) {
			int sumPlayer = 0;
			for (int count = 0; count <= 4; count++) {
				textPrint[player] += " Etappe " + (int)(count+1) + ":     " + totalKM[player][count] + " KM \n";
				sumPlayer += totalKM[player][count];
			}
			textPrint[player] += "\n" + " Totaal:         " + sumPlayer + " KM \n";
		}
		textSelf.setText(textPrint[0]);
		textComp[0].setText(textPrint[1]);
		textComp[1].setText(textPrint[2]);
	}
	
	
	public void printWinner(int[][] totalKM) {
// Function to print the winner of five games (etappes).		
		int[] sumPlayer = {0, 0, 0};
		for (int player = 0; player <= 2; player++) {
			for (int count = 0; count <= 4; count++) {
				sumPlayer[player] += totalKM[player][count];
			}
		}
		String winPlayer = "U";
		int winPlayernum = 0;
		if (sumPlayer[1]>sumPlayer[0] && sumPlayer[1]>sumPlayer[2]) {
			winPlayer = "COMPUTER A";
			winPlayernum = 1;
			
		}
		if (sumPlayer[2]>sumPlayer[0] && sumPlayer[2]>sumPlayer[1]) {
			winPlayer = "COMPUTER B";
			winPlayernum = 2;
		}
		try {Thread.sleep(1000);} catch (Exception e) {System.out.println(e);}
		textMsg.append(" " + winPlayer + " HEEFT STAP OP GEWONNEN !!\n");
		panel_msg.getVerticalScrollBar().setValue(panel_msg.getVerticalScrollBar().getMaximum());
		textMsg.append(" Nog een keer Stap Op ? Klik op Uzelf.\n");
		panel_msg.getVerticalScrollBar().setValue(panel_msg.getVerticalScrollBar().getMaximum());
		knipperPanels(winPlayernum, 10, winPlayernum, 0, true);
	}
	
	
	public void displayMsg(String textToDisplay) {
// Function to print text on a game event.		
		textMsg.append(" " + textToDisplay + "\n");
		panel_msg.getVerticalScrollBar().setValue(panel_msg.getVerticalScrollBar().getMaximum());
	}
	
	
	public void knipperPanels(int fromPlayer, int card, int toPlayer, int numCard, boolean longer) {
// Function to generate blinking effects for playing a card or to announce a winner.		
		int endNum = 3;
		if (longer==true) {
			endNum = 10;
		}
		Color[] kleur = new Color[4];
		kleur[1] = Color.blue;
		kleur[2] = Color.yellow;
		kleur[3] = Color.red;
		for (int knip = 0; knip <= endNum; knip++) {
			try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
			if (fromPlayer==0) {
				for (int count = 0; count <= 5; count++) {
					if (card==count) {
						panel_kaart[count].setBackground(kleur[knip%2+1]);
					}
				}
				if (card>5) {
					panel_self.setBackground(kleur[knip%2+1]);
				}
			}
			else {
				panel_comp[fromPlayer-1].setBackground(kleur[knip%2+1]);
			}
			if (toPlayer>0) {
				panel_comp[toPlayer-1].setBackground(kleur[knip%2 + 2]);
			}
			else if (toPlayer==-1) {
				panel_comp[0].setBackground(Color.gray);
				panel_comp[1].setBackground(Color.gray);
				panel_self.setBackground(Color.gray);
				panel_status.setBackground(Color.gray);
				panel_wind.setBackground(Color.gray);
				panel_weg.setBackground(kleur[knip%2 + 2]);
			}
			else if (toPlayer==-2) {
				panel_weg.setBackground(kleur[knip%2 + 2]);
			}
			else {
				if (numCard<=4) {
					panel_self.setBackground(kleur[knip%2 + 2]);
				}
				else if (numCard==9 || numCard==10) {
					panel_self.setBackground(Color.gray);
					panel_status.setBackground(Color.gray);
					panel_wind.setBackground(kleur[knip%2 + 2]);
				}
				else {
					panel_self.setBackground(Color.gray);
					panel_wind.setBackground(Color.gray);
					panel_status.setBackground(kleur[knip%2 + 2]);
				}				
			}
			try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
		}
	}
	
	
// Next are auxiliary functions and some getters and setters.	
	public void setBackgroundGray() {
		panel_self.setBackground(Color.gray);
		panel_comp[0].setBackground(Color.gray);
		panel_comp[1].setBackground(Color.gray);
	}
	
	
	public int getChosenCard() {
		return chosenCard;
	}
	
	public void resetChosenCard() {
		chosenCard = 0;
	}
	
	public char getChosenUse() {
		return chosenUse;
	}
	
	public void resetChosenUse() {
		chosenUse = 'x';
	}
	
	public String getGamePhase() {
		return phase;
	}
	
	public void setGamePhase(String thePhase) {
		phase = thePhase;
	}
	
	public boolean getPlayAgain() {
		return playAgain;
	}
	
	public void setPlayAgain(boolean thePA) {
		playAgain = thePA;
	}

	public boolean getNextGame() {
		return nextGame;
	}
	
	public void setNextGame(boolean theGame) {
		nextGame = theGame;
	}
	
	public void resetMsg() {
    	textMsg.setText("");
	}

	
// Next are the mouse event handlers.	
	public void mousePressed(MouseEvent e) {
		if (e.getComponent() instanceof JPanel) {
			JPanel temp = (JPanel) e.getComponent();
			for (int count = 0; count <= 5; count++) {
				if (temp==panel_kaart[count] && phase=="ChooseCard" && chosenCard==0) {
					chosenCard = count+1;
					temp.setBackground(Color.yellow);
					break;
				}
			}
			if (temp==panel_self && phase=="EndOfGame" && nextGame==false) {
				temp.setBackground(Color.yellow);
				nextGame = true;
			}
			if (temp==panel_self && phase=="EndOfStapOp" && playAgain==false) {
				temp.setBackground(Color.yellow);
				playAgain = true;
			}
			if (temp==panel_self && phase=="UseCard" && chosenUse=='x') {
				temp.setBackground(Color.yellow);
				chosenUse = 'z';
			}
			if (temp==panel_status && phase=="UseCard" && chosenUse=='x') {
				temp.setBackground(Color.yellow);
				chosenUse = 'z';
			}
			if (temp==panel_wind && phase=="UseCard" && chosenUse=='x') {
				temp.setBackground(Color.yellow);
				chosenUse = 'z';
			}
			if (temp==panel_comp[0] && phase=="UseCard" && chosenUse=='x') {
				temp.setBackground(Color.yellow);
				chosenUse = 'a';
			}
			if (temp==panel_comp[1] && phase=="UseCard" && chosenUse=='x') {
				temp.setBackground(Color.yellow);
				chosenUse = 'b';
			}
			if (temp==panel_weg && phase=="UseCard" && chosenUse=='x') {
				temp.setBackground(Color.yellow);
				chosenUse = 'w';
			}
		}
		if (e.getComponent() instanceof JTextArea) {
			JTextArea temp2 = (JTextArea) e.getComponent();
			if (temp2==textComp[0] && phase=="UseCard" && chosenUse=='x') {
				panel_comp[0].setBackground(Color.yellow);
				chosenUse = 'a';
			}
			if (temp2==textComp[1] && phase=="UseCard" && chosenUse=='x') {
				panel_comp[1].setBackground(Color.yellow);
				chosenUse = 'b';
			}
			if (temp2==textSelf && phase=="UseCard" && chosenUse=='x') {
				panel_self.setBackground(Color.yellow);
				chosenUse = 'z';
			}
			if (temp2==textSelf && phase=="EndOfGame" && nextGame==false) {
				panel_self.setBackground(Color.yellow);
				nextGame = true;
			}
			if (temp2==textSelf && phase=="EndOfStapOp" && playAgain==false) {
				panel_self.setBackground(Color.yellow);
				playAgain = true;
			}
		}
    }
    
	
    public void mouseReleased(MouseEvent e) {
    }
    

    public void mouseEntered(MouseEvent e) {
    	if (e.getComponent() instanceof JPanel) {
    		JPanel temp = (JPanel) e.getComponent();
    		for (int count = 0; count <= 5; count++) {
    			if (temp==panel_kaart[count] && phase=="ChooseCard" && chosenCard==0) {
    				temp.setBackground(Color.orange);
    				break;
    			}
    		}
    		if (temp==panel_self && phase=="EndOfGame" && nextGame==false) {
				temp.setBackground(Color.orange);
			}
    		if (temp==panel_self && phase=="EndOfStapOp" && playAgain==false) {
				temp.setBackground(Color.orange);
			}
    		if (temp==panel_self && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.orange);
    		}
    		if (temp==panel_status && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.orange);
    		}
    		if (temp==panel_wind && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.orange);
    		}
    		if (temp==panel_comp[0] && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.orange);
    		}
    		if (temp==panel_comp[1] && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.orange);
    		}
    		if (temp==panel_weg && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.orange);
    		}
    	}
    	if (e.getComponent() instanceof JTextArea) {
    		JTextArea temp2 = (JTextArea) e.getComponent();
    		if (temp2==textComp[0] && phase=="UseCard" && chosenUse=='x') {
    			panel_comp[0].setBackground(Color.orange);
    		}
    		if (temp2==textComp[1] && phase=="UseCard" && chosenUse=='x') {
    			panel_comp[1].setBackground(Color.orange);
    		}
    		if (temp2==textSelf && phase=="UseCard" && chosenUse=='x') {
    			panel_self.setBackground(Color.orange);
    		}
    		if (temp2==textSelf && phase=="EndOfGame" && nextGame==false) {
    			panel_self.setBackground(Color.orange);
			}
    		if (temp2==textSelf && phase=="EndOfStapOp" && playAgain==false) {
    			panel_self.setBackground(Color.orange);
			}
    	}
    }
    

    public void mouseExited(MouseEvent e) {
    	if (e.getComponent() instanceof JPanel) {
    		JPanel temp = (JPanel) e.getComponent();
    		for (int count = 0; count <= 5; count++) {
    			if (temp==panel_kaart[count] && phase=="ChooseCard" && chosenCard==0) {
    				temp.setBackground(Color.gray);
    				break;
    			}
    		}
    		if (temp==panel_self && phase=="EndOfGame" && nextGame==false) {
				temp.setBackground(Color.gray);
			}
    		if (temp==panel_self && phase=="EndOfStapOp" && playAgain==false) {
				temp.setBackground(Color.gray);
			}
    		if (temp==panel_self && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.gray);
    		}
    		if (temp==panel_status && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.gray);
    		}
    		if (temp==panel_wind && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.gray);
    		}
    		if (temp==panel_comp[0] && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.gray);
    		}
    		if (temp==panel_comp[1] && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.gray);
    		}
    		if (temp==panel_weg && phase=="UseCard" && chosenUse=='x') {
    			temp.setBackground(Color.gray);
    		}
    	}
    	if (e.getComponent() instanceof JTextArea) {
    		JTextArea temp2 = (JTextArea) e.getComponent();
    		if (temp2==textComp[0] && phase=="UseCard" && chosenUse=='x') {
    			panel_comp[0].setBackground(Color.gray);
    		}
    		if (temp2==textComp[1] && phase=="UseCard" && chosenUse=='x') {
    			panel_comp[1].setBackground(Color.gray);
    		}
    		if (temp2==textSelf && phase=="UseCard" && chosenUse=='x') {
    			panel_self.setBackground(Color.gray);
    		}
    		if (temp2==textSelf && phase=="EndOfGame" && nextGame==false) {
    			panel_self.setBackground(Color.gray);
			}
    		if (temp2==textSelf && phase=="EndOfStapOp" && playAgain==false) {
    			panel_self.setBackground(Color.gray);
			}
    	}
    }
    

    public void mouseClicked(MouseEvent e) {
    }

}
		