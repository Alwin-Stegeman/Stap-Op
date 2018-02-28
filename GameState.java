// The GameState class has as object the game state of the Stap Op game. 

package stapopspel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

 
public class GameState {

// initialize game play variables
	public List<Kaart> maindeck = new ArrayList<Kaart>(0);
	public List<ArrayList<Kaart>> hand = new ArrayList<ArrayList<Kaart>>();
	public List<Kaart> discardeddeck = new ArrayList<Kaart>(0);
	public Kaart[] wind = {new Kaart(), new Kaart(), new Kaart()};
	public Kaart[] status = {new Kaart(), new Kaart(), new Kaart()};
	public Kaart weg = new Kaart();
	public int[][] numKMcards = {{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}}; 
	public int[][] playerTotalKM = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};  
	
	
	public void playCard(int fromPlayer, int card, int toPlayer) {
// Function to update the game state when a card is played.		
		Kaart.type SelCard = hand.get(fromPlayer).get(card).getKaartsoort();
		int numSelCard = hand.get(fromPlayer).get(card).getKaartnummer();
		Kaart.type Wind = wind[toPlayer].getKaartsoort();
		Kaart.type Status = status[toPlayer].getKaartsoort();
// 5km, 6km, 8km, 10km card  				
		if (SelCard==Kaart.type.KM5 || SelCard==Kaart.type.KM6 || SelCard==Kaart.type.KM8 
				|| SelCard==Kaart.type.KM10) {
			numKMcards[toPlayer][numSelCard-1] += 1;
			hand.get(fromPlayer).remove(card);
		}
//status card    				
		else if (SelCard==Kaart.type.GeslotenOverweg || SelCard==Kaart.type.BomenOpen 
				|| SelCard==Kaart.type.LekkeBand || SelCard==Kaart.type.Rijwielherstel 
				|| SelCard==Kaart.type.Jeugdherberg || SelCard==Kaart.type.StapOp) {
			if (Status!=Kaart.type.Geen) { discardeddeck.add(new Kaart(status[toPlayer].getKaartsoort())); }	
			status[toPlayer].setKaartsoort(SelCard);
			hand.get(fromPlayer).remove(card);
		}
//wind card
		else if (SelCard==Kaart.type.Tegenwind || SelCard==Kaart.type.Meewind) {
			if (Wind!=Kaart.type.Geen) { discardeddeck.add(new Kaart(wind[toPlayer].getKaartsoort())); }
			wind[toPlayer].setKaartsoort(SelCard);
			hand.get(fromPlayer).remove(card);
		}
	}
	
	
	public void discardCard(int fromPlayer, int card) {
// Function to update the game state when a card is discarded.		
		discardeddeck.add(hand.get(fromPlayer).get(card));
		weg.setKaartsoort(hand.get(fromPlayer).get(card).getKaartsoort());
		hand.get(fromPlayer).remove(card);
	}
	
	
	
	public int gameTotalKM(int player) {
// Function to compute the total km cycled by some player.		
		int total = 0;
		for (int teller = 0; teller <= 3; teller++) {
			switch (teller) {
			case 0: total += 5 * numKMcards[player][teller]; break;
			case 1: total += 6 * numKMcards[player][teller]; break;
			case 2: total += 8 * numKMcards[player][teller]; break;
			case 3: total += 10 * numKMcards[player][teller]; break;
			}
		}
		return total;
	}

	
	public void newGameReset() {
// Function to reset the game state when starting a new game (etappe).		
		maindeck = new ArrayList<Kaart>(0);
		hand = new ArrayList<ArrayList<Kaart>>();
		discardeddeck = new ArrayList<Kaart>(0);
		weg = new Kaart();
		for (int player = 0; player <= 2; player++) {
			wind[player] = new Kaart();
			status[player] = new Kaart();
			for (int count = 0; count <= 3; count++) {
				numKMcards[player][count] = 0;
			}
		}
		
		// specify number of cards for each card type
		int[] aantalPerSoort = {0, 24, 12, 8, 8, 3, 8, 3, 8, 4, 5, 2, 14};
					    
		// create main deck of 99 cards and shuffle it	    
			    int cardCounter = 0;
		        for ( int soort = 1; soort <= 12; soort++ ) {
		            for ( int card = 1; card <= aantalPerSoort[soort]; card++ ) {
		                maindeck.add(cardCounter, new Kaart(soort));
		                cardCounter++;
		            }
		        }   
		        Collections.shuffle(maindeck);
				        
		// deal 5 cards to person and PC
		        List<Kaart> hand0 = new ArrayList<Kaart>();
		        List<Kaart> hand1 = new ArrayList<Kaart>();
		        List<Kaart> hand2 = new ArrayList<Kaart>();
		    	for (int teller = 0; teller < 5; teller++) {
		    		hand0.add(teller, maindeck.get(maindeck.size()-1));
		    		maindeck.remove(maindeck.size()-1);
		    		hand1.add(teller, maindeck.get(maindeck.size()-1));
		    		maindeck.remove(maindeck.size()-1);
		    		hand2.add(teller, maindeck.get(maindeck.size()-1));
		    		maindeck.remove(maindeck.size()-1);
		    	}   	 
		    	hand.add(new ArrayList<Kaart>(hand0));
		    	hand.add(new ArrayList<Kaart>(hand1));
		    	hand.add(new ArrayList<Kaart>(hand2));
	}
	
	
	public GameState() {
// specify number of cards for each card type
		int[] aantalPerSoort = {0, 24, 12, 8, 8, 3, 8, 3, 8, 4, 5, 2, 14};
			    
// create main deck of 99 cards and shuffle it	    
	    int cardCounter = 0;
        for ( int soort = 1; soort <= 12; soort++ ) {
            for ( int card = 1; card <= aantalPerSoort[soort]; card++ ) {
                maindeck.add(cardCounter, new Kaart(soort));
                cardCounter++;
            }
        }   
        Collections.shuffle(maindeck);
		        
// deal 5 cards to person and PC
        List<Kaart> hand0 = new ArrayList<Kaart>();
        List<Kaart> hand1 = new ArrayList<Kaart>();
        List<Kaart> hand2 = new ArrayList<Kaart>();
    	for (int teller = 0; teller < 5; teller++) {
    		hand0.add(teller, maindeck.get(maindeck.size()-1));
    		maindeck.remove(maindeck.size()-1);
    		hand1.add(teller, maindeck.get(maindeck.size()-1));
    		maindeck.remove(maindeck.size()-1);
    		hand2.add(teller, maindeck.get(maindeck.size()-1));
    		maindeck.remove(maindeck.size()-1);
    	}   	 
    	hand.add(new ArrayList<Kaart>(hand0));
    	hand.add(new ArrayList<Kaart>(hand1));
    	hand.add(new ArrayList<Kaart>(hand2));

	}		

}
