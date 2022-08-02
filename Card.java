package stapopspel;

import java.util.Arrays;
import java.util.Optional;

public class Card {
	
	private CardType type;

	public Card() {
	      type = CardType.GEEN;
	}
	
	public Card(CardType type) {
		this.type = type;
	}
	
	public Card(int number) {
		final Optional<CardType> cardType = Arrays.stream(CardType.values()).
				filter(type -> type.getNumber() == number).findFirst();
		if (cardType.isPresent()) {
			type = cardType.get();
		} else {
			throw new IllegalArgumentException("Onbekend kaartnummer: " + number);
		}
	}
	
	public int getNumber() {
		return type.getNumber();
	}
	
	public CardType getType() {
		return type;
	}
	
	public void setType(CardType type) {
		this.type = type;
	}
	
	public String getName() {
		return type.getName();
	}
	
	public int getKilometers() {
		return type.getKilometers();
	}

}

