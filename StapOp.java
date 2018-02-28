// Computer version of the Dutch card game Stap Op with three players: the user and two computer players.
// The game is played via a mouse controlled GUI. 
// For (Dutch) game rules, see https://nl.wikipedia.org/wiki/Stap_op

package stapopspel; 

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

// StapOp class contains the main game loop and governs the game play as intermediary between 
// the game board in the StapOpBoard class and the GameState class.
// Note: the game runs within a while game loop that checks game phases and manipulates the game board and
// game state accordingly. The mouse event handlers are in the StapOpBoard class and change variables
// that are checked in the game loop. This is not the recommended way of GUI programming, but it works.
// The alternative of calling functions from the mouse event handlers created problems with 
// Thread.sleep() blocking the GUI, while using Swing timers would make things a lot more complicated.
public class StapOp {
	
	public static boolean checkLegitMove(Kaart SelCard1, Kaart Wind1, Kaart Status1, 
			int[] numKMcards1, boolean self) {
// Function to check whether a user move is legitimate.		
		boolean error = false;
		int numSelCard = SelCard1.getKaartnummer();
		Kaart.type SelCard = SelCard1.getKaartsoort();
		Kaart.type Wind = Wind1.getKaartsoort();
		Kaart.type Status = Status1.getKaartsoort();
		int[] maxNumKMcards = {8, 4, 2, 2};
// 5km or 6km card    	
		if (SelCard==Kaart.type.KM5 || SelCard==Kaart.type.KM6) {
			if (!(Status==Kaart.type.StapOp && numKMcards1[numSelCard-1] < maxNumKMcards[numSelCard-1] 
					&& self==true)) {
				error = true;
			}
		}
// 8km or 10km card
		else if (SelCard==Kaart.type.KM8 || SelCard==Kaart.type.KM10) {
			if (!(Status==Kaart.type.StapOp && Wind==Kaart.type.Meewind 
					&& numKMcards1[numSelCard-1] < maxNumKMcards[numSelCard-1] && self==true)) {
				error = true;
			}
		}
// Gesloten Overweg
		else if (SelCard==Kaart.type.GeslotenOverweg && !(Status==Kaart.type.StapOp)) {
				error = true;
		}
// Bomen Open 
		else if (SelCard==Kaart.type.BomenOpen && !(Status==Kaart.type.GeslotenOverweg)) {
				error = true;
		}
// Lekke Band
		else if (SelCard==Kaart.type.LekkeBand && !(Status==Kaart.type.StapOp)) {
				error = true;
		}
// Rijwielhersteller 
		else if (SelCard==Kaart.type.Rijwielherstel && !(Status==Kaart.type.LekkeBand)) {
				error = true;
		}
// Tegenwind
		else if (SelCard==Kaart.type.Tegenwind && !(Wind==Kaart.type.Meewind || Wind==Kaart.type.Geen) ) {
			error = true;
		}
// Wind Mee
		else if (SelCard==Kaart.type.Meewind && !(Wind==Kaart.type.Tegenwind || Wind==Kaart.type.Geen) ) {
			error = true;
		}
// Jeugdherberg
		else if (SelCard==Kaart.type.Jeugdherberg && !(Status==Kaart.type.StapOp)) {
			error = true;
		}
// Stap Op
		else if (SelCard==Kaart.type.StapOp) {
			if (!(Status==Kaart.type.Geen || Status==Kaart.type.BomenOpen 
					|| Status==Kaart.type.Rijwielherstel || Status==Kaart.type.Jeugdherberg)) {
				error = true;
			}
		}
		return error;
	}
	
	
	public static boolean checkSmartMove(Kaart SelCard1, Kaart Wind1, Kaart Status1, 
			int[] numKMcards1, boolean self) {
// Function to check whether a (legitimate) move is smart.		
		boolean error = checkLegitMove(SelCard1, Wind1, Status1, numKMcards1, self);
		Kaart.type SelCard = SelCard1.getKaartsoort();
		Kaart.type Wind = Wind1.getKaartsoort();
		Kaart.type Status = Status1.getKaartsoort();
		int[] maxNumKMcards = {8, 4, 2, 2};
		if (error==false && (SelCard==Kaart.type.Meewind || SelCard==Kaart.type.Tegenwind) 
				&& numKMcards1[2]==maxNumKMcards[2] && numKMcards1[3]==maxNumKMcards[3]) {
			error = true;
		}
		if (error==false && SelCard==Kaart.type.Tegenwind && Wind==Kaart.type.Geen) {
			error = true;
		}
		if (error==false && SelCard==Kaart.type.Meewind && Status!=Kaart.type.StapOp) {
			error = true;
		}
		return error;
	}
	
	
	public static int[] makeMovePC(GameState GS, int self, int other) {
// Function that generates a smart move for a computer player.		
		boolean[] cardSelf = {false, true, true, true, true, false, true, false, true,
    			false, true, false, true};
    	int[] cardScore = {0, 30, 30, 40, 40, 30, 50, 30, 50, 25, 40, 30, 50};
    	if (GS.gameTotalKM(other)>GS.gameTotalKM(self)) {
    		cardScore[1] = 5;
    		cardScore[2] = 6;
    		cardScore[3] = 8;
    		cardScore[4] = 10;
    	}
    	boolean error = false;
    	int[] selected = {10, 10};		// selected[0]=kaart, selected[1]=self (1/0)
// determine score for each card in hand   		
		List<Integer> handCardScore = new ArrayList<Integer>();
		for (int card = 0; card <= 5; card++) {
			Kaart SelCard = GS.hand.get(self).get(card);
			int numSelCard = GS.hand.get(self).get(card).getKaartnummer();
			handCardScore.add(card, 0);
			if (cardSelf[numSelCard]==false) { 
				error = checkSmartMove(SelCard, GS.wind[other], GS.status[other], 
						GS.numKMcards[other], false);
				if (error==false) {
					handCardScore.set(card, cardScore[numSelCard]);
				}  
			}
			else {
				error = checkSmartMove(SelCard, GS.wind[self], GS.status[self], 
						GS.numKMcards[self], true);
				if (error==false) {
					handCardScore.set(card, cardScore[numSelCard]);
				} 
			}	  			
		}
// play km card to self if you can win the game
		for (int card = 0; card <= 5; card++) {
			int numSelCard = GS.hand.get(self).get(card).getKaartnummer();
			int kmSelCard = GS.hand.get(self).get(card).getKaartKM();
			if (numSelCard <= 4 && GS.gameTotalKM(self)+kmSelCard==100 && handCardScore.get(card)>0) {
				selected[0] = card;
				selected[1] = 1;
				break;
			}
		}
//select first card with max score    		
		int maxScore = Collections.max(handCardScore);
		if (selected[0]==10 && maxScore>0) {
			for (int card = 0; card <= 5; card++) {
				if (handCardScore.get(card)==maxScore) {
					selected[0] = card;
					selected[1] = 1;
					if (cardSelf[GS.hand.get(self).get(card).getKaartnummer()]==false) {
						selected[1] = 0;
					}
					break;
				}
			}	
		}
		return selected;
	}
	
	
	public static int smartDiscard(List<Kaart> hand, Kaart statusself, int[] numKMcards1) {
// Function that discards a card in a smart way for a computer player.		
		int[] maxNumKMcards = {8, 4, 2, 2};
		int selected = 10;
		List<Integer> numHandCards = new ArrayList<Integer>();
		for (int card = 0; card <= 5; card++) {
			Kaart.type SelCard = hand.get(card).getKaartsoort();
			int numSelCard = hand.get(card).getKaartnummer();
			numHandCards.add(numSelCard);
// discard km card of completed distance
			if (numSelCard<=4 && numKMcards1[numSelCard-1]==maxNumKMcards[numSelCard-1]) {
				selected = card;
				break;
			}
// discard Wind Mee when 8km and 10km distances are completed 			
			if (SelCard==Kaart.type.Meewind && numKMcards1[2]==maxNumKMcards[2] 
					&& numKMcards1[3]==maxNumKMcards[3]) {
				selected = card;
				break;
			}
		}
// discard identical card		
		if (selected==10) {
			Collections.sort(numHandCards);
			int doubleNumber = 20;
			for (int card = 0; card <= 4; card++) {
				if (numHandCards.get(card)==numHandCards.get(card+1)) {
					doubleNumber = numHandCards.get(card);
				}
			}
			if (doubleNumber<20) {
				for (int card = 0; card <= 5; card++) {
					if (hand.get(card).getKaartnummer()==doubleNumber) {
						selected = card;
						break;
					}
				}
			}
		}
		if (selected==10) {
			for (int card = 0; card <= 5; card++) {
				Kaart.type SelCard = hand.get(card).getKaartsoort();
// discard card to hamper other player			
				if (SelCard==Kaart.type.GeslotenOverweg || SelCard==Kaart.type.LekkeBand 
						|| SelCard==Kaart.type.Tegenwind || SelCard==Kaart.type.Jeugdherberg) {
					selected = card;
					break;
				}
			}
		}
		if (selected==10) {
			for (int card = 0; card <= 5; card++) {
// discard first card that is not Stap Op	
				Kaart.type SelCard = hand.get(card).getKaartsoort();
				if (SelCard!=Kaart.type.StapOp) {
					selected = card;
					break;
				}
			}
		}
// discard first card when no card has been discarded yet	
		if (selected==10) {
			selected = 0;
		}
		return selected;
	}
	
	
	public static void main(String[] args) {
    	
		GameState GS = new GameState();
		StapOpBoard GB = new StapOpBoard();		
		
 // initialize game play variables
    	int selCardHand = 0;
    	Kaart selCard = new Kaart();
    	int numSelCard = 0;
    	Kaart Wind = new Kaart();
    	Kaart Status = new Kaart(); 
    	int game  = 1;
    	char useCard = 'x';
    	boolean error = false;
    	boolean GB_drawn = false;
    	boolean WC_printed = false;
    	boolean cont = true;
    	GB.setGamePhase("ChooseCard");
    	GB.setPlayAgain(false);
    	GB.setNextGame(false);
    	
// start main game loop (for multiple 5-game rounds)
    	do {
    	
    	do { if (WC_printed==false) { 
    		GB.displayMsg("WELKOM BIJ STAP OP --- ETAPPE " + game + " van 5 !");    			
    		WC_printed = true;
    		}
// start main game loop (for current game round)
    	do {
// pause thread of game loop for 10 milliseconds so Swing thread 
// with mouse listener gets some CPU time     		
    		try {Thread.sleep(10);} catch (Exception e) {System.out.println(e);}
    		if (GB.getGamePhase()=="ChooseCard" && GB_drawn==false) {
// get new card from the main deck    		
    			GS.hand.get(0).add(5, GS.maindeck.get(GS.maindeck.size()-1));
    			GS.maindeck.remove(GS.maindeck.size()-1);
    			GB.drawBoard(GS);
    			GB_drawn = true;
    			try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
    			GB.displayMsg("Welke kaart gebruiken ? ");
    		}
// user chooses card from hand    	
    	else if (GB.getGamePhase()=="ChooseCard" && GB.getChosenCard()>0) {
    			selCardHand = GB.getChosenCard();
    			GB.resetChosenCard();
    			selCard = GS.hand.get(0).get(selCardHand-1);
    			numSelCard = GS.hand.get(0).get(selCardHand-1).getKaartnummer();
    			GB.displayMsg(GS.hand.get(0).get(selCardHand-1).getKaartnaam() + 
    				" gebruiken voor uzelf, een tegenstander, of weggooien ? ");
    			GB.setGamePhase("UseCard");
    		}
// user decides how to use card    		
    	else if (GB.getGamePhase()=="UseCard" && GB.getChosenUse()!='x') {
    			useCard = GB.getChosenUse();
    			GB.resetChosenUse();
// option: discard card
    			if (useCard=='w') { 
    				GS.discardCard(0, selCardHand-1); 
    				GB.knipperPanels(0, selCardHand-1, -1, 0, false);
    				GB.drawBoard(GS);
    				GB_drawn = false;
    				}
// option: play card  		
    			else {
    				int toPlayer = 0;
    				boolean toSelf = true;
    				if (useCard=='a') { 
    					toPlayer = 1; toSelf = false; 
    				}
    				if (useCard=='b') { 
    					toPlayer = 2; toSelf = false; 
    				}
    				Wind = GS.wind[toPlayer];
    				Status = GS.status[toPlayer];
    				error = checkLegitMove(selCard, Wind, Status, GS.numKMcards[toPlayer], toSelf);
// error: card is discarded    			
    				if (error==true) {
    					GB.displayMsg("U maakt een fout! De kaart wordt weggegooid.");
    					GS.discardCard(0, selCardHand-1);
    					GB.knipperPanels(0, selCardHand-1, -1, 0, false);
    				}
    				else { 
    					GS.playCard(0, selCardHand-1, toPlayer); 
    					GB.knipperPanels(0, selCardHand-1, toPlayer, numSelCard, false);
    					}
    				GB.drawBoard(GS);
    				GB_drawn = false;
    			}

// check if user is a winner 
    			if (GS.gameTotalKM(0)==100) {
    				int diffKM1 = 100 - GS.gameTotalKM(1);
    				int diffKM2 = 100 - GS.gameTotalKM(2);
    				GB.displayMsg("U HEEFT GEWONNEN MET " + diffKM1 + "KM EN " + 
    						diffKM2 + "KM VOORSPRONG !!");
    				try {Thread.sleep(200);} catch (Exception e) {System.out.println(e);}
    				GB.knipperPanels(0, 10, 0, 0, true);
    				GB.setBackgroundGray();
    				for (int player = 0; player <= 2; player++) {
    					GS.playerTotalKM[player][game-1] = GS.gameTotalKM(player);
    				}
    				if (game < 5) {
    					GB.displayMsg("Op naar etappe " + (int)(game+1) + " van 5 !  Klik op Uzelf.");
    				}
    				try {Thread.sleep(150);} catch (Exception e) {System.out.println(e);}
    				GB.setGamePhase("EndOfGame");
    				GB.standings(GS.playerTotalKM);
    				if (game==5) {
    					GB.setGamePhase("EndOfStapOp");
    					try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
    					GB.printWinner(GS.playerTotalKM);
    				}
    				game += 1;
    				break;
    			}
    		
// check size of main deck and merge with discarded deck and shuffle if necessary
    			if (GS.maindeck.size()==0) {
					GS.maindeck.addAll(GS.discardeddeck);
					GS.discardeddeck.clear();
					GB.displayMsg("Kaarten in de stapel worden opnieuw geschud.");
					GS.weg.setKaartsoort(Kaart.type.Geen);
					Collections.shuffle(GS.maindeck);
					GB.drawBoard(GS);
    			}
    			if (GB.getGamePhase()=="UseCard") { GB.setGamePhase("Computer"); }
    		}
    		
    	else if (GB.getGamePhase()=="Computer") {
// generate moves for Computer A and Computer B
    			for (int comp = 1; comp <= 2; comp++) {
    				GS.hand.get(comp).add(5, GS.maindeck.get(GS.maindeck.size()-1));
    				GS.maindeck.remove(GS.maindeck.size()-1);
    				String playedCard = "joker";
    				String toPlayerString = "zichzelf.";
    				String fromPlayerString = "Computer A";
    				int[] otherUsers = {0, 2};
    				if (comp==2) {
    					fromPlayerString = "Computer B";
    					otherUsers[1] = 1;
    				}
    				int winUser = otherUsers[1];
    				int secUser = 0;
    				int recUser = 10;
    				if (GS.gameTotalKM(0)>GS.gameTotalKM(winUser)) {
    					winUser = 0;
    					secUser = otherUsers[1];
    				}
    				int[] card2playWin = makeMovePC(GS, comp, winUser);
    				int[] card2playSec = makeMovePC(GS, comp, secUser);
    				int[] card2play = {10, 10};
    				if (card2playWin[0]<10) {
    					card2play = card2playWin;
    					recUser = winUser;
    				}
    				else if (card2playSec[0]<10) {
    					card2play = card2playSec;
    					recUser = secUser;
    				}
    				if (recUser<10)	{
    					playedCard = GS.hand.get(comp).get(card2play[0]).getKaartnaam();
    					numSelCard = GS.hand.get(comp).get(card2play[0]).getKaartnummer();
    					if (card2play[1]==0) {
    						card2play[1] = recUser;
    						switch (recUser) {
    						case 0: toPlayerString = "u."; break;
    						case 1: toPlayerString = "Computer A."; break;
    						case 2: toPlayerString = "Computer B."; break;
    						}
    					}
    					else {
    						card2play[1] = comp;
    					}
    					GS.playCard(comp, card2play[0], card2play[1]);
    					try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
    					GB.displayMsg(fromPlayerString + " legt " + playedCard + " bij " + toPlayerString);
    					GB.knipperPanels(comp, 0, card2play[1], numSelCard, false);
    					GB.drawBoard(GS);
    					if (comp==1) {
    						try {Thread.sleep(75);} catch (Exception e) {System.out.println(e);}
    					}
    				}
// discard card if there are no options to play card
    				else {
    					int card2discard = smartDiscard(GS.hand.get(comp), GS.status[comp], GS.numKMcards[comp]);
    					playedCard = GS.hand.get(comp).get(card2discard).getKaartnaam();
    					GS.discardCard(comp, card2discard);
    					try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
    					GB.displayMsg(fromPlayerString + " legt " + playedCard + " weg.");
    					GB.knipperPanels(comp, 0, -2, 0, false);
    					GB.drawBoard(GS);
    					if (comp==1) {
    						try {Thread.sleep(75);} catch (Exception e) {System.out.println(e);}
    					}
    				}
    		
// check if computer is a winner 
    				if (GS.gameTotalKM(comp)==100) {
    					int diffKM1 = 100 - GS.gameTotalKM(0);
    					int diffKM2 = 100 - GS.gameTotalKM(2);
    					String winPlayerString = "COMPUTER A";
    					if (comp==2) {
    						diffKM2 = 100 - GS.gameTotalKM(1);
    						winPlayerString = "COMPUTER B";
    					}
    				GB.drawBoard(GS);
    				GB.displayMsg(winPlayerString + " HEEFT GEWONNEN MET " + diffKM1 + "KM EN " + 
    						diffKM2 + "KM VOORSPRONG !!");
    				try {Thread.sleep(200);} catch (Exception e) {System.out.println(e);}
    				GB.knipperPanels(comp, 0, comp, 0, true);
    				GB.setBackgroundGray();
    				for (int player = 0; player <= 2; player++) {
    					GS.playerTotalKM[player][game-1] = GS.gameTotalKM(player);
    				}
    				if (game < 5) {
    					GB.displayMsg("Op naar etappe " + (int)(game+1) + " van 5 !  Klik op Uzelf.");
    				}
    				try {Thread.sleep(150);} catch (Exception e) {System.out.println(e);}
    				GB.setGamePhase("EndOfGame");
    				GB.standings(GS.playerTotalKM);
    				if (game==5) {
    					GB.setGamePhase("EndOfStapOp");
    					try {Thread.sleep(50);} catch (Exception e) {System.out.println(e);}
    					GB.printWinner(GS.playerTotalKM);
    				}
    				game += 1;
    				break;
    				}    		
    		
// check size of main deck and merge with discarded deck and shuffle if necessary
    				if (GS.maindeck.size()==0) {
    					GS.maindeck.addAll(GS.discardeddeck);
    					GS.discardeddeck.clear();
    					GB.displayMsg("Kaarten in de stapel worden opnieuw geschud.");
    					GS.weg.setKaartsoort(Kaart.type.Geen);
    					Collections.shuffle(GS.maindeck);
    					GB.drawBoard(GS);
    				}
    				if (GB.getGamePhase()=="Computer") { GB.setGamePhase("ChooseCard"); }
    			}
    		}
    			
    	} while ( GB.getGamePhase()!="EndOfGame" && GB.getGamePhase()!="EndOfStapOp" );
    	
    	if (GB.getNextGame()==true) {
    		selCardHand = 0;
        	selCard = new Kaart();
        	numSelCard = 0;
        	Wind = new Kaart();
        	Status = new Kaart();
    		useCard = 'x';
    		error = false;
    		GB_drawn = false;
    		WC_printed = false;
    		
    		GB.setNextGame(false);
    		GB.setGamePhase("ChooseCard");
    		GB.resetMsg();
    		
    		GS.newGameReset();    		
    	}    	
    	} while (game <= 5);
    		    	
    	if (GB.getPlayAgain()==true) {
    		selCardHand = 0;
        	selCard = new Kaart();
        	numSelCard = 0;
        	Wind = new Kaart();
        	Status = new Kaart();
        	game  = 1;
        	useCard = 'x';
        	error = false;
        	GB_drawn = false; 
        	WC_printed = false;
        	
        	GS = new GameState();
        	GB.resetMsg();
        	GB.setGamePhase("ChooseCard");
        	GB.setPlayAgain(false);
        	GB.setNextGame(false);
     	}    	    	
    	} while ( cont==true );    	
	}
}
