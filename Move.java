package stapopspel;

public class Move {
	
	private final int fromPlayer;
	private final boolean cardSelected;
	private final int cardInHandIndex;
	private final CardType cardType;
	private final int toPlayer;
	
	Move(int fromPlayer, boolean cardSelected, int cardInHandIndex, CardType cardType, int toPlayer) {
		this.fromPlayer = fromPlayer;
		this.cardSelected = cardSelected;
		this.cardInHandIndex = cardInHandIndex;
		this.cardType = cardType;
		this.toPlayer = toPlayer;
	}

	public int getFromPlayer() {
		return fromPlayer;
	}

	public boolean isCardSelected() {
		return cardSelected;
	}

	public int getCardInHandIndex() {
		return cardInHandIndex;
	}
	
	public CardType getCardType() {
		return cardType;
	}

	public int getToPlayer() {
		return toPlayer;
	}
	
}
