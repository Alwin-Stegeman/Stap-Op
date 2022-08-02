package stapopspel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameState {

	// initialize game play variables
	private final List<Card> mainDeck = new ArrayList<>();
	private final List<ArrayList<Card>> cardsInHands = new ArrayList<>();
	private final List<Card> discardedDeck = new ArrayList<>();
	private final Card[] windCards = new Card[3];
	private final Card[] statusCards = new Card[3];
	private final Card lastDiscardedCard = new Card();
	private final Map<Integer, Map<CardType, Integer>> numberOfKilometerCardsPlayed = new HashMap<>();
	private final int[][] totalDistancesPerPlayedGame = { { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 } };

	
	public GameState() {	
		for (int player = 0; player <= 2; player++) {
			numberOfKilometerCardsPlayed.put(player, new HashMap<CardType, Integer>());
		}
		newGameReset();
	}
	
	
	public void newGameReset() {
		mainDeck.clear();
		cardsInHands.clear();
		discardedDeck.clear();
		lastDiscardedCard.setType(CardType.GEEN);
		for (int player = 0; player <= 2; player++) {
			windCards[player] = new Card();
			statusCards[player] = new Card();
			numberOfKilometerCardsPlayed.get(player).put(CardType.KM5, 0);
			numberOfKilometerCardsPlayed.get(player).put(CardType.KM6, 0);
			numberOfKilometerCardsPlayed.get(player).put(CardType.KM8, 0);
			numberOfKilometerCardsPlayed.get(player).put(CardType.KM10, 0);
		}

		// create main deck of 99 cards and shuffle it
		int cardCounter = 0;
		for (CardType cardType : CardType.values()) {
			for (int card = 1; card <= cardType.getNumberOfCards(); card++) {
				mainDeck.add(cardCounter, new Card(cardType));
				cardCounter++;
			}
		}
		Collections.shuffle(mainDeck);

		// deal 5 cards to each player
		final List<Card> hand0 = new ArrayList<Card>();
		final List<Card> hand1 = new ArrayList<Card>();
		final List<Card> hand2 = new ArrayList<Card>();
		for (int cardIndex = 0; cardIndex < 5; cardIndex++) {
			hand0.add(cardIndex, mainDeck.get(mainDeck.size() - 1));
			mainDeck.remove(mainDeck.size() - 1);
			hand1.add(cardIndex, mainDeck.get(mainDeck.size() - 1));
			mainDeck.remove(mainDeck.size() - 1);
			hand2.add(cardIndex, mainDeck.get(mainDeck.size() - 1));
			mainDeck.remove(mainDeck.size() - 1);
		}
		cardsInHands.add(new ArrayList<Card>(hand0));
		cardsInHands.add(new ArrayList<Card>(hand1));
		cardsInHands.add(new ArrayList<Card>(hand2));
	}
	
	
	public void dealCard(int toPlayer) {
		cardsInHands.get(toPlayer).add(5, mainDeck.get(mainDeck.size() - 1));
		mainDeck.remove(mainDeck.size() - 1);
	}

	
	public void playCard(int fromPlayer, int cardIndex, int toPlayer) {
		final CardType selectedCardType = cardsInHands.get(fromPlayer).get(cardIndex).getType();
		final CardType windCardTypeToPlayer = windCards[toPlayer].getType();
		final CardType statusCardTypeToPlayer = statusCards[toPlayer].getType();
		// 5km, 6km, 8km, 10km card
		if (selectedCardType.equals(CardType.KM5) || selectedCardType.equals(CardType.KM6) || selectedCardType.equals(CardType.KM8) || selectedCardType.equals(CardType.KM10)) {
			final int currentNumberOfKilometerCards = numberOfKilometerCardsPlayed.get(toPlayer).get(selectedCardType);
			numberOfKilometerCardsPlayed.get(toPlayer).put(selectedCardType, currentNumberOfKilometerCards + 1);
			cardsInHands.get(fromPlayer).remove(cardIndex);
		}
		// status card
		else if (selectedCardType.equals(CardType.GESLOTEN_OVERWEG) || selectedCardType.equals(CardType.BOMEN_OPEN) || selectedCardType.equals(CardType.LEKKE_BAND)
				|| selectedCardType.equals(CardType.RIJWIELHERSTELLER) || selectedCardType.equals(CardType.JEUGDHERBERG)
				|| selectedCardType.equals(CardType.STAP_OP)) {
			if (!statusCardTypeToPlayer.equals(CardType.GEEN)) {
				discardedDeck.add(new Card(statusCards[toPlayer].getType()));
			}
			statusCards[toPlayer].setType(selectedCardType);
			cardsInHands.get(fromPlayer).remove(cardIndex);
		}
		// wind card
		else if (selectedCardType.equals(CardType.TEGENWIND) || selectedCardType.equals(CardType.WIND_MEE)) {
			if (!windCardTypeToPlayer.equals(CardType.GEEN)) {
				discardedDeck.add(new Card(windCards[toPlayer].getType()));
			}
			windCards[toPlayer].setType(selectedCardType);
			cardsInHands.get(fromPlayer).remove(cardIndex);
		}
	}

	
	public void discardCard(int fromPlayer, int card) {
		discardedDeck.add(cardsInHands.get(fromPlayer).get(card));
		lastDiscardedCard.setType(cardsInHands.get(fromPlayer).get(card).getType());
		cardsInHands.get(fromPlayer).remove(card);
	}

	
	public int getTotalDistanceForPlayer(int player) {
		int total = 0;
		total += 5 * numberOfKilometerCardsPlayed.get(player).get(CardType.KM5);
		total += 6 * numberOfKilometerCardsPlayed.get(player).get(CardType.KM6);
		total += 8 * numberOfKilometerCardsPlayed.get(player).get(CardType.KM8);
		total += 10 * numberOfKilometerCardsPlayed.get(player).get(CardType.KM10);
		return total;
	}
	
	
	public void processEndOfGame(int gameNumber) {
		for (int player = 0; player <= 2; player++) {
			totalDistancesPerPlayedGame[player][gameNumber - 1] = getTotalDistanceForPlayer(player);
		}
	}
	
	
	public boolean checkAndProcessEmptyMainDeck() {
		boolean isMainDeckEmptyAndProcessed = false;
		if (mainDeck.size() == 0) {
			mainDeck.addAll(discardedDeck);
			discardedDeck.clear();
			lastDiscardedCard.setType(CardType.GEEN);
			Collections.shuffle(mainDeck);
			isMainDeckEmptyAndProcessed = true;
		}
		return isMainDeckEmptyAndProcessed;
	}
	
	
	public ArrayList<Card> getCardsInHands(int player) {
		return cardsInHands.get(player);
	}
	
	public Card getWindCard(int player) {
		return windCards[player];
	}
	
	public Card getStatusCard(int player) {
		return statusCards[player];
	}
	
	public Card getLastDiscardedCard() {
		return lastDiscardedCard;
	}
	
	public Map<CardType, Integer> getNumberOfKilometerCardsPlayed(int player) {
		return numberOfKilometerCardsPlayed.get(player);
	}
	
	public int[] getTotalDistancesPerPlayedGame(int player) {
		return totalDistancesPerPlayedGame[player];
	}
	
	public int[][] getTotalDistancesPerPlayedGame() {
		return totalDistancesPerPlayedGame;
	}

}
