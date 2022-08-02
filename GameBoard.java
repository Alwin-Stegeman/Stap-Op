package stapopspel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;


public class GameBoard implements MouseListener {
	
    private static final Color myRed = new Color(230,18,14);
    private static final Color backgroundColor = Color.gray;
    private static final Font textAreaTitleFont = new Font("Arial", Font.BOLD, 20);
    private static final Font textAreaTextFont = new Font("Serif", Font.BOLD, 16);
    private static final Color[] colorsForBlinking = {Color.gray, Color.blue, Color.yellow, Color.red};

	private final JFrame frameMain = new JFrame("Stap Op");
	private final JTextArea[] textAreaComputer = new JTextArea[2];
	private final JTextArea textAreaMessage = new JTextArea();
	private final JTextArea textAreaSelf = new JTextArea();
    
	private final JLabel[] cardsInHand = new JLabel[6];
	private final JLabel statusCard = new JLabel();
	private final JLabel windCard = new JLabel();
	private final JLabel discardedCard = new JLabel();
	
    private final JPanel[] panelCardsInHand = new JPanel[6];
    private final JPanel[] panelComputer = new JPanel[2];
    private final JPanel panelSelf = new JPanel(new GridBagLayout());
    private final JPanel panelDiscarded = new JPanel();
    private final JPanel panelStatus = new JPanel();
    private final JPanel panelWind = new JPanel();
    private final JScrollPane panelMessages = new JScrollPane(textAreaMessage);
    
    private final ImageIcon[] scaledCard = new ImageIcon[13];
    private final ImageIcon[] scaledCardHor = new ImageIcon[13];
    
    private int chosenCardIndex = -1;
    private CardUseType cardUseType = CardUseType.NOT_DETERMINED_YET;
    private GamePhaseType gamePhase = GamePhaseType.CHOOSE_CARD;
    private boolean playAgain = false;
    private boolean nextGame = false;
        
    
	public GameBoard() {
        frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelMessages.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        for (int cardIndex = 0; cardIndex <= 5; cardIndex++) {
        	panelCardsInHand[cardIndex] = new JPanel();
        	panelCardsInHand[cardIndex].setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        	panelCardsInHand[cardIndex].setBackground(backgroundColor);
        	panelCardsInHand[cardIndex].addMouseListener(this);
        }
        
        panelDiscarded.addMouseListener(this);
        panelSelf.addMouseListener(this);
        panelStatus.addMouseListener(this);
        panelWind.addMouseListener(this);
        
        panelStatus.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        panelWind.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        panelDiscarded.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        panelSelf.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        panelMessages.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        
        panelStatus.setBackground(backgroundColor);
        panelWind.setBackground(backgroundColor);
        panelDiscarded.setBackground(backgroundColor);
        panelSelf.setBackground(backgroundColor);
        panelMessages.setBackground(backgroundColor);
        
        final JPanel panelLowerHalf = new JPanel(new GridBagLayout());
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 0;
        constraints.weightx = 0.5;
        for (int cardIndex = 0; cardIndex <= 5; cardIndex++) {
            constraints.gridx = cardIndex;
            panelLowerHalf.add(panelCardsInHand[cardIndex], constraints);
        }
        
        final JPanel panelUpperHalf = new JPanel(new GridBagLayout());
        constraints.gridx = 0;
        panelUpperHalf.add(panelStatus, constraints);
        constraints.gridx = 1;
        panelUpperHalf.add(panelWind, constraints);
        constraints.gridx = 2;
        panelUpperHalf.add(panelSelf, constraints);
        
        for (int index = 0; index <= 1; index++) {
        	panelComputer[index] = new JPanel(new GridBagLayout());
        	panelComputer[index].setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        	panelComputer[index].setBackground(backgroundColor);
        	panelComputer[index].addMouseListener(this);
        	constraints.gridx += 1;
        	panelUpperHalf.add(panelComputer[index], constraints);
        }
    
        constraints.gridx = 0; constraints.gridy = 1; constraints.gridwidth = 2;
        panelUpperHalf.add(panelDiscarded, constraints);        
        constraints.gridx = 2; constraints.gridy = 1; constraints.gridwidth = 3;
        panelUpperHalf.add(panelMessages, constraints);
        
        constraints.gridx = 0; constraints.gridy = 0; constraints.gridwidth = 1;
        final JLabel computerA = new JLabel("Computer A", SwingConstants.CENTER);
        computerA.setFont(textAreaTitleFont);
        panelComputer[0].add(computerA, constraints);
        final JLabel computerB = new JLabel("Computer B", SwingConstants.CENTER);
        computerB.setFont(textAreaTitleFont);
        panelComputer[1].add(computerB, constraints);
        final JLabel self = new JLabel("Uzelf", SwingConstants.CENTER);
        self.setFont(textAreaTitleFont);
        panelSelf.add(self, constraints);
                
        // add text areas in panels
        textAreaMessage.setFont(textAreaTextFont);
    	textAreaMessage.setLineWrap(false);
    	textAreaMessage.setEditable(false);
    	textAreaMessage.setText("");
    	
    	constraints.gridy = 1; constraints.fill = GridBagConstraints.HORIZONTAL;
		textAreaSelf.setFont(textAreaTextFont);
		textAreaSelf.setLineWrap(false);
		textAreaSelf.setEditable(false);
		textAreaSelf.addMouseListener(this);
		panelSelf.add(textAreaSelf, constraints);
    	
    	for (int index = 0; index <= 1; index++) {
    		textAreaComputer[index] = new JTextArea();
    		textAreaComputer[index].setFont(textAreaTextFont);
    		textAreaComputer[index].setLineWrap(false);
    		textAreaComputer[index].setEditable(false);
    		textAreaComputer[index].addMouseListener(this);
    		panelComputer[index].add(textAreaComputer[index], constraints);
    	}
    	
    	// scale cards
        for (int cardNumber = 0; cardNumber <= 12; cardNumber++) {
			final String cardNumberAsString = Integer.toString(cardNumber);
			final URL url = GameBoard.class.getResource("/images/kaart" + cardNumberAsString + "xs.png");
			scaledCard[cardNumber] = new ImageIcon(url);
		}    	
        for (int cardNumber = 0; cardNumber <= 12; cardNumber++) {
			final String cardNumberAsString = Integer.toString(cardNumber);
			final URL url = GameBoard.class.getResource("/images/kaart" + cardNumberAsString + "xhs.png");
			scaledCardHor[cardNumber] = new ImageIcon(url);
		}
    	
        // add dummy cards
		for (int cardIndex = 0; cardIndex <= 5; cardIndex++) {
			cardsInHand[cardIndex] = new JLabel();
			cardsInHand[cardIndex].setIcon(scaledCard[0]);
			panelCardsInHand[cardIndex].add(cardsInHand[cardIndex]);
		}
		statusCard.setIcon(scaledCard[0]);
		windCard.setIcon(scaledCard[0]);
		discardedCard.setIcon(scaledCardHor[0]);
    	panelStatus.add(statusCard);
    	panelWind.add(windCard);
    	panelDiscarded.add(discardedCard);
        
        final JSplitPane panelMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelUpperHalf, panelLowerHalf);
        
        frameMain.add(panelMain);
        frameMain.pack();
        frameMain.setResizable(false);
	}
	
	
	public void drawBoard(GameState gameState) {
		
		// set background and border of panel with mouse listener back to normal
		for (int cardIndex = 0; cardIndex <= 5; cardIndex++) {
			panelCardsInHand[cardIndex].setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        	panelCardsInHand[cardIndex].setBackground(backgroundColor);
		}
		for (int index = 0; index <= 1; index++) {
        	panelComputer[index].setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
        	panelComputer[index].setBackground(backgroundColor);
		}
		panelSelf.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
		panelSelf.setBackground(backgroundColor);
		panelStatus.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
		panelStatus.setBackground(backgroundColor);
		panelWind.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
		panelWind.setBackground(backgroundColor);
		panelDiscarded.setBorder(BorderFactory.createMatteBorder(4,4,4,4,myRed));
		panelDiscarded.setBackground(backgroundColor);
	
		// cards in user's hand		
		for (int cardIndex = 0; cardIndex < gameState.getCardsInHands(0).size(); cardIndex++) {
			final int cardNumber = gameState.getCardsInHands(0).get(cardIndex).getNumber();
			cardsInHand[cardIndex].setIcon(scaledCard[cardNumber]);
		}
		if (gameState.getCardsInHands(0).size() == 5) {
			cardsInHand[5].setIcon(scaledCard[0]);
		}
        
		// status and wind cards of user		
        statusCard.setIcon(scaledCard[gameState.getStatusCard(0).getNumber()]);
        windCard.setIcon(scaledCard[gameState.getWindCard(0).getNumber()]);

        // top card of discarded deck
        discardedCard.setIcon(scaledCardHor[gameState.getLastDiscardedCard().getNumber()]);
        
        // cycled distances of user
        final String textPrintSelf = "--------------------------------------------\n" +    
        		" Bossen:  " + gameState.getNumberOfKilometerCardsPlayed(0).get(CardType.KM5) * 5 + " van 40 KM \n" +
				" Hei:        " + gameState.getNumberOfKilometerCardsPlayed(0).get(CardType.KM6) * 6 + " van 24 KM \n" +
				" Zee:        " + gameState.getNumberOfKilometerCardsPlayed(0).get(CardType.KM8) * 8 + " van 16 KM \n" +
				" Plassen:  " + gameState.getNumberOfKilometerCardsPlayed(0).get(CardType.KM10) * 10 + " van 20 KM \n" +
				" Totaal:    " + gameState.getTotalDistanceForPlayer(0) + " KM \n \n \n" ;
        textAreaSelf.setText(textPrintSelf);
        
        // cycled distances of computer players
        for (int comp = 1; comp <= 2; comp++) {
        	final String textPrintComputer =  "-------------------------------------------\n" +   
        				 " Bossen:  " + gameState.getNumberOfKilometerCardsPlayed(comp).get(CardType.KM5) * 5 + " van 40 KM \n" +
        				 " Hei:        " + gameState.getNumberOfKilometerCardsPlayed(comp).get(CardType.KM6) * 6 + " van 24 KM \n" +
        				 " Zee:        " + gameState.getNumberOfKilometerCardsPlayed(comp).get(CardType.KM8) * 8 + " van 16 KM \n" +
        				 " Plassen:  " + gameState.getNumberOfKilometerCardsPlayed(comp).get(CardType.KM10) * 10 + " van 20 KM \n" +
        				 " Totaal:    " + gameState.getTotalDistanceForPlayer(comp) + " KM \n \n" +
        				 " Status: " + gameState.getStatusCard(comp).getName() + "\n" +
        				 " Wind:  " + gameState.getWindCard(comp).getName();
        	textAreaComputer[comp-1].setText(textPrintComputer);
        }
        frameMain.setVisible(true);
	}
	
	
	public void printStandings(int[][] totalKM) {
		// Function to print standings after a completed game		
		final String textPrint[] = {"-------------------------------------------\n", 
				"-------------------------------------------\n", 
				"------------------------------------------\n"};
		for (int player = 0; player <= 2; player++) {
			int sumPlayer = 0;
			for (int count = 0; count <= 4; count++) {
				textPrint[player] += " Etappe " + (int)(count + 1) + ":     " + totalKM[player][count] + " KM \n";
				sumPlayer += totalKM[player][count];
			}
			textPrint[player] += "\n" + " Totaal:         " + sumPlayer + " KM \n";
		}
		textAreaSelf.setText(textPrint[0]);
		textAreaComputer[0].setText(textPrint[1]);
		textAreaComputer[1].setText(textPrint[2]);
	}
	
	
	public void printWinner(int[][] totalKM) {		
		final int[] sumPlayer = {0, 0, 0};
		for (int player = 0; player <= 2; player++) {
			for (int gameIndex = 0; gameIndex <= 4; gameIndex++) {
				sumPlayer[player] += totalKM[player][gameIndex];
			}
		}
		String winPlayer = "U";
		int winPlayerNumber = 0;
		if (sumPlayer[1] > sumPlayer[0] && sumPlayer[1] > sumPlayer[2]) {
			winPlayer = "COMPUTER A";
			winPlayerNumber = 1;
			
		}
		if (sumPlayer[2] > sumPlayer[0] && sumPlayer[2] > sumPlayer[1]) {
			winPlayer = "COMPUTER B";
			winPlayerNumber = 2;
		}
		try {Thread.sleep(1000);} catch (Exception e) {System.out.println(e);}
		textAreaMessage.append(" " + winPlayer + " HEEFT STAP OP GEWONNEN !!\n");
		panelMessages.getVerticalScrollBar().setValue(panelMessages.getVerticalScrollBar().getMaximum());
		textAreaMessage.append(" Nog een keer Stap Op ? Klik op Uzelf.\n");
		panelMessages.getVerticalScrollBar().setValue(panelMessages.getVerticalScrollBar().getMaximum());
		blinkPanelsForWinner(winPlayerNumber);
	}
	
	
	public void displayMessage(String textToDisplay) {		
		textAreaMessage.append(" " + textToDisplay + "\n");
		panelMessages.getVerticalScrollBar().setValue(panelMessages.getVerticalScrollBar().getMaximum());
	}
	
	
	public void blinkPanelsWhenDiscardingCard(int player, int cardIndexPlayed) {
		for (int blinkIndex = 0; blinkIndex <= 3; blinkIndex++) {
			try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
			if (player == 0) {
				for (int cardIndex = 0; cardIndex <= 5; cardIndex++) {
					if (cardIndexPlayed == cardIndex) {
						panelCardsInHand[cardIndex].setBackground(colorsForBlinking[blinkIndex % 2 + 1]);
					}
				}
			}
			else {
				panelComputer[player - 1].setBackground(colorsForBlinking[blinkIndex % 2 + 1]);
			}
			if (player == 0) {
				panelComputer[0].setBackground(backgroundColor);
				panelComputer[1].setBackground(backgroundColor);
				panelSelf.setBackground(backgroundColor);
				panelStatus.setBackground(backgroundColor);
				panelWind.setBackground(backgroundColor);
				panelDiscarded.setBackground(colorsForBlinking[blinkIndex % 2 + 2]);
			}
			else {
				panelDiscarded.setBackground(colorsForBlinking[blinkIndex % 2 + 2]);
			}
			try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
		}
	}
	
	
	public void blinkPanelsWhenPlayingCard(int fromPlayer, int cardIndexPlayed, int toPlayer, int cardNumberPlayed) {
		for (int blinkIndex = 0; blinkIndex <= 3; blinkIndex++) {
			try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
			if (fromPlayer == 0) {
				for (int cardIndex = 0; cardIndex <= 5; cardIndex++) {
					if (cardIndexPlayed == cardIndex) {
						panelCardsInHand[cardIndex].setBackground(colorsForBlinking[blinkIndex % 2 + 1]);
					}
				}
			}
			else {
				panelComputer[fromPlayer - 1].setBackground(colorsForBlinking[blinkIndex % 2 + 1]);
			}
			if (toPlayer > 0) {
				panelComputer[toPlayer - 1].setBackground(colorsForBlinking[blinkIndex % 2 + 2]);
			}
			else {
				if (cardNumberPlayed <= 4) {
					panelSelf.setBackground(colorsForBlinking[blinkIndex % 2 + 2]);
				}
				else if (cardNumberPlayed == 9 || cardNumberPlayed == 10) {
					panelSelf.setBackground(backgroundColor);
					panelStatus.setBackground(backgroundColor);
					panelWind.setBackground(colorsForBlinking[blinkIndex % 2 + 2]);
				}
				else {
					panelSelf.setBackground(backgroundColor);
					panelWind.setBackground(backgroundColor);
					panelStatus.setBackground(colorsForBlinking[blinkIndex % 2 + 2]);
				}				
			}
			try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
		}
	}
	
	
	public void blinkPanelsForWinner(int player) {
		for (int blinkIndex = 0; blinkIndex <= 10; blinkIndex++) {
			if (player == 0) {
				panelSelf.setBackground(colorsForBlinking[blinkIndex % 2 + 1]);
			}
			else {
				panelComputer[player - 1].setBackground(colorsForBlinking[blinkIndex % 2 + 1]);
			}
			try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
			if (player == 0) {
				panelSelf.setBackground(colorsForBlinking[blinkIndex % 2 + 2]);
			}
			else {
				panelComputer[player - 1].setBackground(colorsForBlinking[blinkIndex % 2 + 2]);
			}
			try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
		}
	}
	
	
	// Next are auxiliary functions and some getters and setters.	
	public void setBackgroundGray() {
		panelSelf.setBackground(backgroundColor);
		panelComputer[0].setBackground(backgroundColor);
		panelComputer[1].setBackground(backgroundColor);
	}
	
	
	public int getChosenCardIndex() {
		return chosenCardIndex;
	}
	
	public void resetChosenCardIndex() {
		chosenCardIndex = -1;
	}
	
	public CardUseType getChosenUse() {
		return cardUseType;
	}
	
	public void resetChosenUse() {
		cardUseType = CardUseType.NOT_DETERMINED_YET;
	}
	
	public GamePhaseType getGamePhase() {
		return gamePhase;
	}
	
	public void setGamePhase(GamePhaseType thePhase) {
		gamePhase = thePhase;
	}
	
	public boolean isPlayAgain() {
		return playAgain;
	}
	
	public void setPlayAgain(boolean thePA) {
		playAgain = thePA;
	}

	public boolean isNextGame() {
		return nextGame;
	}
	
	public void setNextGame(boolean theGame) {
		nextGame = theGame;
	}
	
	public void resetMsg() {
    	textAreaMessage.setText("");
	}

	
	// Next are the mouse event handlers.	
	public void mousePressed(MouseEvent e) {
		if (e.getComponent() instanceof JPanel) {
			final JPanel clickedPanel = (JPanel) e.getComponent();
			for (int cardIndex = 0; cardIndex <= 5; cardIndex++) {
				if (clickedPanel.equals(panelCardsInHand[cardIndex]) && gamePhase.equals(GamePhaseType.CHOOSE_CARD) && chosenCardIndex == -1) {
					chosenCardIndex = cardIndex;
					clickedPanel.setBackground(Color.yellow);
					break;
				}
			}
			if (clickedPanel.equals(panelSelf) && gamePhase.equals(GamePhaseType.END_OF_GAME) && !nextGame) {
				clickedPanel.setBackground(Color.yellow);
				nextGame = true;
			}
			if (clickedPanel.equals(panelSelf) && gamePhase.equals(GamePhaseType.END_OF_STAP_OP) && !playAgain) {
				clickedPanel.setBackground(Color.yellow);
				playAgain = true;
			}
			if ((clickedPanel.equals(panelSelf) || clickedPanel.equals(panelStatus) || clickedPanel.equals(panelWind) ) 
					&& gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
				clickedPanel.setBackground(Color.yellow);
				cardUseType = CardUseType.SELF;
			}
			if (clickedPanel.equals(panelComputer[0]) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
				clickedPanel.setBackground(Color.yellow);
				cardUseType = CardUseType.COMPUTER_A;
			}
			if (clickedPanel.equals(panelComputer[1]) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
				clickedPanel.setBackground(Color.yellow);
				cardUseType = CardUseType.COMPUTER_B;
			}
			if (clickedPanel.equals(panelDiscarded) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
				clickedPanel.setBackground(Color.yellow);
				cardUseType = CardUseType.DISCARD;
			}
		}
		if (e.getComponent() instanceof JTextArea) {
			final JTextArea clickedTextArea = (JTextArea) e.getComponent();
			if (clickedTextArea.equals(textAreaComputer[0]) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
				panelComputer[0].setBackground(Color.yellow);
				cardUseType = CardUseType.COMPUTER_A;
			}
			if (clickedTextArea.equals(textAreaComputer[1]) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
				panelComputer[1].setBackground(Color.yellow);
				cardUseType = CardUseType.COMPUTER_B;
			}
			if (clickedTextArea.equals(textAreaSelf) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
				panelSelf.setBackground(Color.yellow);
				cardUseType = CardUseType.SELF;
			}
			if (clickedTextArea.equals(textAreaSelf) && gamePhase.equals(GamePhaseType.END_OF_GAME) && !nextGame) {
				panelSelf.setBackground(Color.yellow);
				nextGame = true;
			}
			if (clickedTextArea.equals(textAreaSelf) && gamePhase.equals(GamePhaseType.END_OF_STAP_OP) && !playAgain) {
				panelSelf.setBackground(Color.yellow);
				playAgain = true;
			}
		}
    }
    
	
    public void mouseReleased(MouseEvent e) {
    }
    

    public void mouseEntered(MouseEvent e) {
    	if (e.getComponent() instanceof JPanel) {
    		final JPanel enteredPanel = (JPanel) e.getComponent();
    		for (int cardIndex = 0; cardIndex <= 5; cardIndex++) {
    			if (enteredPanel.equals(panelCardsInHand[cardIndex]) && gamePhase.equals(GamePhaseType.CHOOSE_CARD) && chosenCardIndex == -1) {
    				enteredPanel.setBackground(Color.orange);
    				break;
    			}
    		}
    		if (enteredPanel.equals(panelSelf) && gamePhase.equals(GamePhaseType.END_OF_GAME) && !nextGame) {
				enteredPanel.setBackground(Color.orange);
			}
    		if (enteredPanel.equals(panelSelf) && gamePhase.equals(GamePhaseType.END_OF_STAP_OP) && !playAgain) {
				enteredPanel.setBackground(Color.orange);
			}
    		if ((enteredPanel.equals(panelSelf) || enteredPanel.equals(panelStatus) || enteredPanel.equals(panelWind) ||
    			enteredPanel.equals(panelComputer[0]) || enteredPanel.equals(panelComputer[1]) || enteredPanel.equals(panelDiscarded) ) 
    				&& gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
    			enteredPanel.setBackground(Color.orange);
    		}
    	}
    	if (e.getComponent() instanceof JTextArea) {
    		final JTextArea enteredTextArea = (JTextArea) e.getComponent();
    		if (enteredTextArea.equals(textAreaComputer[0]) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
    			panelComputer[0].setBackground(Color.orange);
    		}
    		if (enteredTextArea.equals(textAreaComputer[1]) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
    			panelComputer[1].setBackground(Color.orange);
    		}
    		if (enteredTextArea.equals(textAreaSelf) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
    			panelSelf.setBackground(Color.orange);
    		}
    		if (enteredTextArea.equals(textAreaSelf) && gamePhase.equals(GamePhaseType.END_OF_GAME) && !nextGame) {
    			panelSelf.setBackground(Color.orange);
			}
    		if (enteredTextArea.equals(textAreaSelf) && gamePhase.equals(GamePhaseType.END_OF_STAP_OP) && !playAgain) {
    			panelSelf.setBackground(Color.orange);
			}
    	}
    }
    

    public void mouseExited(MouseEvent e) {
    	if (e.getComponent() instanceof JPanel) {
    		final JPanel exitedPanel = (JPanel) e.getComponent();
    		for (int cardIndex = 0; cardIndex <= 5; cardIndex++) {
    			if (exitedPanel.equals(panelCardsInHand[cardIndex]) && gamePhase.equals(GamePhaseType.CHOOSE_CARD) && chosenCardIndex == -1) {
    				exitedPanel.setBackground(backgroundColor);
    				break;
    			}
    		}
    		if (exitedPanel.equals(panelSelf) && gamePhase.equals(GamePhaseType.END_OF_GAME) && !nextGame) {
				exitedPanel.setBackground(backgroundColor);
			}
    		if (exitedPanel.equals(panelSelf) && gamePhase.equals(GamePhaseType.END_OF_STAP_OP) && !playAgain) {
				exitedPanel.setBackground(backgroundColor);
			}
    		if ((exitedPanel.equals(panelSelf) || exitedPanel.equals(panelStatus) || exitedPanel.equals(panelWind) ||
    			exitedPanel.equals(panelComputer[0]) || exitedPanel.equals(panelComputer[1]) || exitedPanel.equals(panelDiscarded) ) 
    				&& gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
    			exitedPanel.setBackground(backgroundColor);
    		}
    	}
    	if (e.getComponent() instanceof JTextArea) {
    		final JTextArea exitedTextArea = (JTextArea) e.getComponent();
    		if (exitedTextArea.equals(textAreaComputer[0]) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
    			panelComputer[0].setBackground(backgroundColor);
    		}
    		if (exitedTextArea.equals(textAreaComputer[1]) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
    			panelComputer[1].setBackground(backgroundColor);
    		}
    		if (exitedTextArea.equals(textAreaSelf) && gamePhase.equals(GamePhaseType.USE_CARD) && cardUseType.equals(CardUseType.NOT_DETERMINED_YET)) {
    			panelSelf.setBackground(backgroundColor);
    		}
    		if (exitedTextArea.equals(textAreaSelf) && gamePhase.equals(GamePhaseType.END_OF_GAME) && !nextGame) {
    			panelSelf.setBackground(backgroundColor);
			}
    		if (exitedTextArea.equals(textAreaSelf) && gamePhase.equals(GamePhaseType.END_OF_STAP_OP) && !playAgain) {
    			panelSelf.setBackground(backgroundColor);
			}
    	}
    }
    

    public void mouseClicked(MouseEvent e) {
    }

}
