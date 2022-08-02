package stapopspel;

// StapOp class contains the main game loop and governs the game play as intermediary between 
// the game board in the StapOpBoard class and the GameState class.
// Note: the game runs within a while game loop that checks game phases and manipulates the game board and
// game state accordingly. The mouse event handlers are in the StapOpBoard class and change variables
// that are checked in the game loop. This is not the recommended way of GUI programming, but it works.
// The alternative of calling functions from the mouse event handlers created problems with 
// Thread.sleep() blocking the GUI, while using Swing timers would make things a lot more complicated.

public class StapOp {

	// initialize game play variables
	private static GameState gameState = new GameState();
	private static final GameBoard gameBoard = new GameBoard();

	private static int selectedCardInHandIndex = -1;
	private static Card selectedCard = new Card();
	private static Card windCard = new Card();
	private static Card statusCard = new Card();
	private static int gameNumber = 1;
	private static CardUseType cardUseType = CardUseType.NOT_DETERMINED_YET;
	private static boolean moveNotAllowed = false;
	private static boolean gameBoardDrawn = false;
	private static boolean welcomeMessagePrinted = false;
	private static boolean continueLoop = true;
	
	
	public static void main(String[] args) {
		gameBoard.setGamePhase(GamePhaseType.CHOOSE_CARD);
		gameBoard.setPlayAgain(false);
		gameBoard.setNextGame(false);

		// start main game loop (for multiple 5-game rounds)
		do {

			do {
				if (welcomeMessagePrinted == false) {
					gameBoard.displayMessage("WELKOM BIJ STAP OP --- ETAPPE " + gameNumber + " van 5 !");
					welcomeMessagePrinted = true;
				}

				// start main game loop (for current game round)
				do {
					// pause thread of game loop so Swing thread with mouse listener gets some CPU time
					doSleep(50);
					if (gameBoard.getGamePhase().equals(GamePhaseType.CHOOSE_CARD) && !gameBoardDrawn) {

						// deal new card from the main deck to player 0
						gameState.dealCard(0);
						gameBoard.drawBoard(gameState);
						gameBoardDrawn = true;
						doSleep(50);
						gameBoard.displayMessage("Welke kaart gebruiken ? ");
					}

					// user chooses card from hand
					else if (gameBoard.getGamePhase().equals(GamePhaseType.CHOOSE_CARD)
							&& gameBoard.getChosenCardIndex() > -1) {
						selectedCardInHandIndex = gameBoard.getChosenCardIndex();
						gameBoard.resetChosenCardIndex();
						selectedCard = gameState.getCardsInHands(0).get(selectedCardInHandIndex);
						gameBoard.displayMessage(selectedCard.getName() + " gebruiken voor uzelf, een tegenstander, of weggooien ? ");
						gameBoard.setGamePhase(GamePhaseType.USE_CARD);
					}

					// user decides how to use card
					else if (gameBoard.getGamePhase().equals(GamePhaseType.USE_CARD)
							&& !gameBoard.getChosenUse().equals(CardUseType.NOT_DETERMINED_YET)) {
						cardUseType = gameBoard.getChosenUse();
						gameBoard.resetChosenUse();
						processUserMove();
						
						if (isPlayerWinnerOfGame(0)) {
							gameNumber += 1;
							break;
						}

						// check size of main deck and merge with discarded deck and shuffle if necessary
						if (gameState.checkAndProcessEmptyMainDeck()) {
							gameBoard.displayMessage("Kaarten in de stapel worden opnieuw geschud.");
							gameBoard.drawBoard(gameState);
						}
						if (gameBoard.getGamePhase().equals(GamePhaseType.USE_CARD)) {
							gameBoard.setGamePhase(GamePhaseType.COMPUTER);
						}
					}

					else if (gameBoard.getGamePhase().equals(GamePhaseType.COMPUTER)) {

						// generate moves for Computer A and Computer B
						for (int computerPlayerNumber = 1; computerPlayerNumber <= 2; computerPlayerNumber++) {
							gameState.dealCard(computerPlayerNumber);
							final Move computerMove = generateComputerMove(computerPlayerNumber);
							processComputerMove(computerMove);
							
							if (isPlayerWinnerOfGame(computerPlayerNumber)) {
								gameNumber += 1;
								break;
							}
							// check size of main deck and merge with discarded deck and shuffle if necessary
							if (gameState.checkAndProcessEmptyMainDeck()) {
								gameBoard.displayMessage("Kaarten in de stapel worden opnieuw geschud.");
								gameBoard.drawBoard(gameState);
							}
							if (gameBoard.getGamePhase().equals(GamePhaseType.COMPUTER)) {
								gameBoard.setGamePhase(GamePhaseType.CHOOSE_CARD);
							}
						}
					}

				} while (!gameBoard.getGamePhase().equals(GamePhaseType.END_OF_GAME)
						&& !gameBoard.getGamePhase().equals(GamePhaseType.END_OF_STAP_OP));

				if (gameBoard.isNextGame() == true) {
					resetForNewGame();
				}
			} while (gameNumber <= 5);

			if (gameBoard.isPlayAgain()) {
				resetForNewGame();
				gameNumber = 1;
				gameState = new GameState();
				gameBoard.setPlayAgain(false);
			}
		} while (continueLoop);
	}
	
	
	private static void doSleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
	private static void resetForNewGame() {
		selectedCardInHandIndex = -1;
		selectedCard = new Card();
		windCard = new Card();
		statusCard = new Card();
		cardUseType = CardUseType.NOT_DETERMINED_YET;
		moveNotAllowed = false;
		gameBoardDrawn = false;
		welcomeMessagePrinted = false;

		gameBoard.setNextGame(false);
		gameBoard.setGamePhase(GamePhaseType.CHOOSE_CARD);
		gameBoard.resetMsg();

		gameState.newGameReset();
	}
	
	
	private static void processUserMove() {
		// option: discard card
		if (cardUseType.equals(CardUseType.DISCARD)) {
			gameState.discardCard(0, selectedCardInHandIndex);
			gameBoard.blinkPanelsWhenDiscardingCard(0, selectedCardInHandIndex);
		}
		// option: play card
		else {
			int toPlayer = 0;
			boolean toSelf = true;
			if (cardUseType.equals(CardUseType.COMPUTER_A)) {
				toPlayer = 1;
				toSelf = false;
			}
			if (cardUseType.equals(CardUseType.COMPUTER_B)) {
				toPlayer = 2;
				toSelf = false;
			}
			windCard = gameState.getWindCard(toPlayer);
			statusCard = gameState.getStatusCard(toPlayer);
			moveNotAllowed = !MoveUtil.isLegitimateMove(selectedCard, windCard, statusCard,
					gameState.getNumberOfKilometerCardsPlayed(toPlayer), toSelf);

			// move not allowed: card is discarded
			if (moveNotAllowed) {
				gameBoard.displayMessage("U maakt een fout! De kaart wordt weggegooid.");
				gameState.discardCard(0, selectedCardInHandIndex);
				gameBoard.blinkPanelsWhenDiscardingCard(0, selectedCardInHandIndex);
			} else {
				gameState.playCard(0, selectedCardInHandIndex, toPlayer);
				gameBoard.blinkPanelsWhenPlayingCard(0, selectedCardInHandIndex, toPlayer, selectedCard.getNumber());
			}
		}
		gameBoard.drawBoard(gameState);
		gameBoardDrawn = false;
	}
	
	
	private static void processComputerMove(Move computerMove) {
		final int computerPlayerNumber = computerMove.getFromPlayer();
		final String fromComputerPlayerString = (computerPlayerNumber == 1)? "Computer A" : "Computer B";
		if (computerMove.isCardSelected()) {
			final int cardInHandIndexToPlay = computerMove.getCardInHandIndex();
			final String playedCardName = computerMove.getCardType().getName();
			final int playedCardNumber = computerMove.getCardType().getNumber();
			int toPlayerNumber = computerMove.getToPlayer();
			
			String toPlayerString = null;
			if (computerMove.getToPlayer() != computerPlayerNumber) {
				switch (computerMove.getToPlayer()) {
				case 0: toPlayerString = "u."; break;
				case 1: toPlayerString = "Computer A."; break;
				case 2: toPlayerString = "Computer B."; 
				}
			} else {
				toPlayerNumber = computerPlayerNumber;
				toPlayerString = "zichzelf.";
			}
			gameState.playCard(computerPlayerNumber, cardInHandIndexToPlay, toPlayerNumber);
			doSleep(50);
			gameBoard.displayMessage(fromComputerPlayerString + " legt " + playedCardName + " bij " + toPlayerString);
			gameBoard.blinkPanelsWhenPlayingCard(computerPlayerNumber, 0, toPlayerNumber, playedCardNumber);
		}
		// discard card if there are no options to play card
		else {
			final int cardInHandIndexToDiscard = MoveUtil.smartDiscard(
					gameState.getCardsInHands(computerPlayerNumber),
					gameState.getStatusCard(computerPlayerNumber),
					gameState.getNumberOfKilometerCardsPlayed(computerPlayerNumber));
			final CardType playedCardType = gameState.getCardsInHands(computerPlayerNumber).get(cardInHandIndexToDiscard).getType();
			gameState.discardCard(computerPlayerNumber, cardInHandIndexToDiscard);
			doSleep(50);
			gameBoard.displayMessage(fromComputerPlayerString + " legt " + playedCardType.getName() + " weg.");
			gameBoard.blinkPanelsWhenDiscardingCard(computerPlayerNumber, 0);
		}
		gameBoard.drawBoard(gameState);
		gameBoardDrawn = false;
		if (computerPlayerNumber == 1) {
			doSleep(75);
		}
	}
	
	
	private static boolean isPlayerWinnerOfGame(int player) {
		boolean playerIsWinner = false;
		if (gameState.getTotalDistanceForPlayer(player) == 100) {
			int diffKM1;
			if (player == 0) {
				diffKM1 = 100 - gameState.getTotalDistanceForPlayer(1);
			} else {
				diffKM1 = 100 - gameState.getTotalDistanceForPlayer(0);
			}
			int diffKM2;
			if (player == 2) {
				diffKM2 = 100 - gameState.getTotalDistanceForPlayer(1);
			} else {
				diffKM2 = 100 - gameState.getTotalDistanceForPlayer(2);
			}
			String winPlayerString = null;
			switch (player) {
				case 0: winPlayerString = "U"; break;
				case 1: winPlayerString = "COMPUTER A"; break;
				case 2: winPlayerString = "COMPUTER B";
			}
			gameBoard.drawBoard(gameState);
			gameBoard.displayMessage(winPlayerString + " HEEFT GEWONNEN MET " + diffKM1 + "KM EN " + diffKM2 + "KM VOORSPRONG !!");
			doSleep(200);
			gameBoard.blinkPanelsForWinner(player);
			gameBoard.setBackgroundGray();
			gameState.processEndOfGame(gameNumber);
			if (gameNumber < 5) {
				gameBoard.displayMessage("Op naar etappe " + (int) (gameNumber + 1) + " van 5 !  Klik op Uzelf.");
			}
			doSleep(150);
			gameBoard.setGamePhase(GamePhaseType.END_OF_GAME);
			gameBoard.printStandings(gameState.getTotalDistancesPerPlayedGame());
			if (gameNumber == 5) {
				gameBoard.setGamePhase(GamePhaseType.END_OF_STAP_OP);
				doSleep(50);
				gameBoard.printWinner(gameState.getTotalDistancesPerPlayedGame());
			}
			playerIsWinner = true;
		}
		return playerIsWinner;
	}
	
	
	private static Move generateComputerMove(int computerPlayerNumber) {
		final int[] otherPlayerNumber = {0, 2};
		if (computerPlayerNumber == 2) {
			otherPlayerNumber[1] = 1;
		}
		int leadingOtherPlayerNumber = otherPlayerNumber[1];
		int followingOtherPlayerNumber = 0;
		int toPlayerNumber = -1;
		CardType playedCardType = null;
		if (gameState.getTotalDistanceForPlayer(0) > gameState
				.getTotalDistanceForPlayer(leadingOtherPlayerNumber)) {
			leadingOtherPlayerNumber = 0;
			followingOtherPlayerNumber = otherPlayerNumber[1];
		}
		final int cardInHandIndexToPlayForLeadingOtherPlayer = MoveUtil.makeMoveForComputerPlayer(
				gameState, computerPlayerNumber, leadingOtherPlayerNumber);
		final int cardInHandIndexToPlayForFollowingOtherPlayer = MoveUtil.makeMoveForComputerPlayer(
				gameState, computerPlayerNumber, followingOtherPlayerNumber);
		int cardInHandIndexToPlay = -1;
		boolean cardInHandSelected = false;
		if (cardInHandIndexToPlayForLeadingOtherPlayer > -1) {
			cardInHandIndexToPlay = cardInHandIndexToPlayForLeadingOtherPlayer;
			toPlayerNumber = leadingOtherPlayerNumber;
			cardInHandSelected = true;
		} else if (cardInHandIndexToPlayForFollowingOtherPlayer > -1) {
			cardInHandIndexToPlay = cardInHandIndexToPlayForFollowingOtherPlayer;
			toPlayerNumber = followingOtherPlayerNumber;
			cardInHandSelected = true;
		}
		if (cardInHandSelected) {
			playedCardType = gameState.getCardsInHands(computerPlayerNumber).get(cardInHandIndexToPlay).getType();
			if (MoveUtil.playCardToSelf.get(playedCardType)) {
				toPlayerNumber = computerPlayerNumber;
			}
		}
		return new Move(computerPlayerNumber, cardInHandSelected, cardInHandIndexToPlay, playedCardType, toPlayerNumber);
	}

	
}
