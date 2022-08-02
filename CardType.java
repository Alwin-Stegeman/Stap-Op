package stapopspel;

public enum CardType {

	KM5 (1, "5km", 5, 24), 
	KM6 (2, "6km", 6, 12), 
	KM8 (3, "8km", 8, 8), 
	KM10 (4, "10km", 10, 8), 
	GESLOTEN_OVERWEG (5, "Gesloten Overweg", 0, 3), 
	BOMEN_OPEN (6, "Bomen Gaan Open", 0, 8), 
	LEKKE_BAND (7, "Lekke Band", 0, 3),
	RIJWIELHERSTELLER (8, "Rijwielhersteller", 0, 8), 
	TEGENWIND (9, "Tegenwind", 0, 4), 
	WIND_MEE (10, "Wind Mee", 0, 5), 
	JEUGDHERBERG (11, "Jeugdherberg", 0, 2), 
	STAP_OP (12, "Stap Op", 0, 14), 
	GEEN (0, "geen", 0, 0);
	
	private final int number;
	private final String name;
	private final int kilometers;
	private final int numberOfCards;
	
	CardType(int number, String name, int kilometers, int numberOfCards) {
		this.number = number;
		this.name = name;
		this.kilometers = kilometers;
		this.numberOfCards = numberOfCards;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getKilometers() {
		return this.kilometers;
	}
	
	public int getNumberOfCards() {
		return this.numberOfCards;
	}
	
	public boolean isKolometerCard() {
		return this.number > 0 && this.number < 5;
	}
	
}
