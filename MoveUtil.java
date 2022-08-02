package stapopspel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

public class MoveUtil {
	
	public final static Map<CardType, Boolean> playCardToSelf = new ImmutableMap.Builder<CardType, Boolean>()
			.put(CardType.KM5, true).put(CardType.KM6, true).put(CardType.KM8, true).put(CardType.KM10, true)
			.put(CardType.GESLOTEN_OVERWEG, false).put(CardType.BOMEN_OPEN, true).put(CardType.LEKKE_BAND, false)
			.put(CardType.RIJWIELHERSTELLER, true).put(CardType.JEUGDHERBERG, false).put(CardType.STAP_OP, true)
			.put(CardType.TEGENWIND, false).put(CardType.WIND_MEE, true).build();
	
	private final static Map<CardType, Integer> numberOfKilometerCardsNeeded = 
			ImmutableMap.of(CardType.KM5, 8, CardType.KM6, 4, CardType.KM8, 2, CardType.KM10, 2);	
	
	private final static Map<CardType, Integer> cardScore = Stream.of(new Object[][] { 
	     {CardType.KM5, 30}, {CardType.KM6, 30}, {CardType.KM8, 40}, {CardType.KM10, 40},
	     {CardType.GESLOTEN_OVERWEG, 30}, {CardType.BOMEN_OPEN, 50}, {CardType.LEKKE_BAND, 30},
	     {CardType.RIJWIELHERSTELLER, 50}, {CardType.TEGENWIND, 25}, {CardType.WIND_MEE, 40},
	     {CardType.JEUGDHERBERG, 30}, {CardType.STAP_OP, 50}})
			.collect(Collectors.toMap(data -> (CardType) data[0], data -> (Integer) data[1]));
	
	
	public static boolean isLegitimateMove(Card selectedCard, Card windCard, Card statusCard, 
			Map<CardType, Integer> numberOfKilometerCardsPlayed, boolean cardPlayedToSelf) {

		boolean isLegitimate = false;
		final CardType selectedCardType = selectedCard.getType();
		final CardType windCardType = windCard.getType();
		final CardType statusCardType = statusCard.getType();
   	
		if ((selectedCardType.equals(CardType.KM5) || selectedCardType.equals(CardType.KM6)) &&
				(cardPlayedToSelf && statusCardType.equals(CardType.STAP_OP) && 
				numberOfKilometerCardsPlayed.get(selectedCardType) < numberOfKilometerCardsNeeded.get(selectedCardType) )) {
				isLegitimate = true;
		}
		else if ((selectedCardType.equals(CardType.KM8) || selectedCardType.equals(CardType.KM10)) &&
				(cardPlayedToSelf && statusCardType.equals(CardType.STAP_OP) && windCardType.equals(CardType.WIND_MEE) 
					&& numberOfKilometerCardsPlayed.get(selectedCardType) < numberOfKilometerCardsNeeded.get(selectedCardType) )) {
				isLegitimate = true;
		}
		else if (selectedCardType.equals(CardType.GESLOTEN_OVERWEG) && statusCardType.equals(CardType.STAP_OP)) {
				isLegitimate = true;
		}
		else if (selectedCardType.equals(CardType.BOMEN_OPEN) && statusCardType.equals(CardType.GESLOTEN_OVERWEG)) {
				isLegitimate = true;
		}
		else if (selectedCardType.equals(CardType.LEKKE_BAND) && statusCardType.equals(CardType.STAP_OP)) {
				isLegitimate = true;
		}
		else if (selectedCardType.equals(CardType.RIJWIELHERSTELLER) && statusCardType.equals(CardType.LEKKE_BAND)) {
				isLegitimate = true;
		}
		else if (selectedCardType.equals(CardType.TEGENWIND) && (windCardType.equals(CardType.WIND_MEE) || windCardType.equals(CardType.GEEN)) ) {
			isLegitimate = true;
		}
		else if (selectedCardType.equals(CardType.WIND_MEE) && (windCardType.equals(CardType.TEGENWIND) || windCardType.equals(CardType.GEEN)) ) {
			isLegitimate = true;
		}
		else if (selectedCardType.equals(CardType.JEUGDHERBERG) && statusCardType.equals(CardType.STAP_OP)) {
			isLegitimate = true;
		}
		else if (selectedCardType.equals(CardType.STAP_OP) && (statusCardType.equals(CardType.GEEN) || statusCardType.equals(CardType.BOMEN_OPEN) 
					|| statusCardType.equals(CardType.RIJWIELHERSTELLER) || statusCardType.equals(CardType.JEUGDHERBERG))) {
				isLegitimate = true;
		}
		return isLegitimate;
	}
	
	
	public static boolean isSmartMove(Card selectedCard, Card windCard, Card statusCard, 
			Map<CardType, Integer> numberOfKilometerCardsPlayed, boolean cardPlayedToSelf) {

		final boolean isLegitimateMove = isLegitimateMove(selectedCard, windCard, statusCard, numberOfKilometerCardsPlayed, cardPlayedToSelf);
		boolean isSmartMove = isLegitimateMove;
		final CardType selectedCardType = selectedCard.getType();
		final CardType windType = windCard.getType();
		final CardType statusType = statusCard.getType();

		if (isLegitimateMove && (selectedCardType.equals(CardType.WIND_MEE) || selectedCardType.equals(CardType.TEGENWIND)) 
				&& numberOfKilometerCardsPlayed.get(CardType.KM8) == numberOfKilometerCardsNeeded.get(CardType.KM8) 
				&& numberOfKilometerCardsPlayed.get(CardType.KM10) == numberOfKilometerCardsNeeded.get(CardType.KM10)) {
			isSmartMove = false;
		}
		if (isLegitimateMove && selectedCardType.equals(CardType.TEGENWIND) && windType.equals(CardType.GEEN)) {
			isSmartMove = false;
		}
		if (isLegitimateMove && selectedCardType.equals(CardType.WIND_MEE) && !statusType.equals(CardType.STAP_OP)) {
			isSmartMove = false;
		}
		return isSmartMove;
	}
	
	
	public static int makeMoveForComputerPlayer(GameState gameState, int playerNumberSelf, int playerNumberOther) {
		
    	int selectedCardInHandIndex = -1;
    	boolean cardSelected = false;
		setKilometerCardScores(gameState, playerNumberSelf, playerNumberOther);
    	boolean isSmartMove = true;
    	
    	// determine score for each card in hand   		
		final List<Integer> handCardScore = new ArrayList<Integer>();
		for (int cardInHandIndex = 0; cardInHandIndex <= 5; cardInHandIndex++) {
			final Card handCard = gameState.getCardsInHands(playerNumberSelf).get(cardInHandIndex);
			final CardType handCardType = handCard.getType();
			handCardScore.add(cardInHandIndex, 0);
			
			if (playCardToSelf.get(handCardType)) { 
				isSmartMove = isSmartMove(handCard, gameState.getWindCard(playerNumberSelf), gameState.getStatusCard(playerNumberSelf), gameState.getNumberOfKilometerCardsPlayed(playerNumberSelf), true);
			}
			else {
				isSmartMove = isSmartMove(handCard, gameState.getWindCard(playerNumberOther), gameState.getStatusCard(playerNumberOther), gameState.getNumberOfKilometerCardsPlayed(playerNumberOther), false);  
			}	  
			if (isSmartMove) {
				handCardScore.set(cardInHandIndex, cardScore.get(handCardType));
			}
		}
		
		// play km card to self if you can win the game
		for (int cardInHandIndex = 0; cardInHandIndex <= 5; cardInHandIndex++) {
			final CardType handCardType = gameState.getCardsInHands(playerNumberSelf).get(cardInHandIndex).getType();
			final int handCardKilometers = gameState.getCardsInHands(playerNumberSelf).get(cardInHandIndex).getKilometers();
			if (handCardType.isKolometerCard() && gameState.getTotalDistanceForPlayer(playerNumberSelf) + handCardKilometers == 100 
					&& handCardScore.get(cardInHandIndex)>0) {
				selectedCardInHandIndex = cardInHandIndex;
				cardSelected = true;
				break;
			}
		}
		
		//select first card with max score    		
		final int maxScore = Collections.max(handCardScore);
		if (!cardSelected && maxScore > 0) {
			for (int cardInHandIndex = 0; cardInHandIndex <= 5; cardInHandIndex++) {
				if (handCardScore.get(cardInHandIndex) == maxScore) {
					selectedCardInHandIndex = cardInHandIndex;
					cardSelected = true;
					break;
				}
			}	
		}
		return selectedCardInHandIndex;
	}
	
	
	private static void setKilometerCardScores(GameState gameState, int playerNumberSelf, int playerNumberOther) {
    	if (gameState.getTotalDistanceForPlayer(playerNumberOther) > gameState.getTotalDistanceForPlayer(playerNumberSelf)) {
    		cardScore.put(CardType.KM5, 5);
    		cardScore.put(CardType.KM6, 6);
    		cardScore.put(CardType.KM8, 8);
    		cardScore.put(CardType.KM10, 10);
    	} else {
    		cardScore.put(CardType.KM5, 30);
    		cardScore.put(CardType.KM6, 30);
    		cardScore.put(CardType.KM8, 40);
    		cardScore.put(CardType.KM10, 40);
    	}
	}
	
	
	public static int smartDiscard(List<Card> cardsInHand, Card statusSelf, Map<CardType, Integer> numberOfKilometerCardsPlayed) {
	
		int selectedCardInHandIndex = -1;
		boolean cardSelected = false;
		final List<Integer> cardNumbersInHand = new ArrayList<Integer>();
		for (int cardInHandIndex = 0; cardInHandIndex <= 5; cardInHandIndex++) {
			final CardType handCardType = cardsInHand.get(cardInHandIndex).getType();
			final int handCardNumber = cardsInHand.get(cardInHandIndex).getNumber();
			cardNumbersInHand.add(handCardNumber);
			
			// discard km card of completed distance
			if (handCardType.isKolometerCard() && numberOfKilometerCardsPlayed.get(handCardType) == numberOfKilometerCardsNeeded.get(handCardType)) {
				selectedCardInHandIndex = cardInHandIndex;
				cardSelected = true;
				break;
			}
			
			// discard Wind Mee when 8km and 10km distances are completed 			
			if (handCardType.equals(CardType.WIND_MEE) && numberOfKilometerCardsPlayed.get(CardType.KM8) == numberOfKilometerCardsNeeded.get(CardType.KM8)
					&& numberOfKilometerCardsPlayed.get(CardType.KM10) == numberOfKilometerCardsNeeded.get(CardType.KM10)) {
				selectedCardInHandIndex = cardInHandIndex;
				cardSelected = true;
				break;
			}
		}
		
		// discard identical card		
		if (!cardSelected) {
			Collections.sort(cardNumbersInHand);
			int identicalCardNumber = 0;
			boolean identicalCardFound = false;
			for (int cardInHandIndex = 0; cardInHandIndex <= 4; cardInHandIndex++) {
				if (cardNumbersInHand.get(cardInHandIndex) == cardNumbersInHand.get(cardInHandIndex + 1)) {
					identicalCardNumber = cardNumbersInHand.get(cardInHandIndex);
					identicalCardFound = true;
				}
			}
			if (identicalCardFound) {
				for (int cardInHandIndex = 0; cardInHandIndex <= 5; cardInHandIndex++) {
					if (cardsInHand.get(cardInHandIndex).getNumber() == identicalCardNumber) {
						selectedCardInHandIndex = cardInHandIndex;
						cardSelected = true;
						break;
					}
				}
			}
		}
		if (!cardSelected) {
			for (int cardInHandIndex = 0; cardInHandIndex <= 5; cardInHandIndex++) {
				final CardType cardInHandType = cardsInHand.get(cardInHandIndex).getType();

				// discard card to hamper other player			
				if (cardInHandType.equals(CardType.GESLOTEN_OVERWEG) || cardInHandType.equals(CardType.LEKKE_BAND) 
						|| cardInHandType.equals(CardType.TEGENWIND) || cardInHandType.equals(CardType.JEUGDHERBERG)) {
					selectedCardInHandIndex = cardInHandIndex;
					cardSelected = true;
					break;
				}
			}
		}
		if (!cardSelected) {
			for (int cardInHandIndex = 0; cardInHandIndex <= 5; cardInHandIndex++) {
				// discard first card that is not Stap Op	
				final CardType handCardType = cardsInHand.get(cardInHandIndex).getType();
				if (!handCardType.equals(CardType.STAP_OP)) {
					selectedCardInHandIndex = cardInHandIndex;
					cardSelected = true;
					break;
				}
			}
		}

		// discard first card when no card has been discarded yet	
		if (!cardSelected) {
			selectedCardInHandIndex = 0;
			cardSelected = true;
		}
		return selectedCardInHandIndex;
	}

}
