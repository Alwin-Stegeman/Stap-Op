// The class Kaart has as objects the cards of the Stap Op game.
// A card is defined by its type (soort). There are multiple cards of the same type in Stap Op.
// Each type corresponds to a card number.

package stapopspel;

public class Kaart {
	
	public static enum type { KM5, KM6, KM8, KM10, GeslotenOverweg, BomenOpen, LekkeBand,
		Rijwielherstel, Tegenwind, Meewind, Jeugdherberg, StapOp, Geen };
		
	private type soort;

	public Kaart() {
	      soort = type.Geen;
	}
	
	public Kaart(type deSoort) {
		soort = deSoort;
	}
	
	public Kaart (int hetNummer) {
		switch(hetNummer) {
		case 1: soort = type.KM5; break;
		case 2: soort = type.KM6; break;
		case 3: soort = type.KM8; break;
		case 4: soort = type.KM10; break;
		case 5: soort = type.GeslotenOverweg; break;
		case 6: soort = type.BomenOpen; break;
		case 7: soort = type.LekkeBand; break;
		case 8: soort = type.Rijwielherstel; break;
		case 9: soort = type.Tegenwind; break;
		case 10: soort = type.Meewind; break;
		case 11: soort = type.Jeugdherberg; break;
		case 12: soort = type.StapOp; break;
		default: soort = type.Geen;
		}
	}
	
	
	public int getKaartnummer() {
		switch(soort){
		case KM5: return 1;
		case KM6: return 2;
		case KM8: return 3;
		case KM10: return 4;
		case GeslotenOverweg: return 5;
		case BomenOpen: return 6;
		case LekkeBand: return 7;
		case Rijwielherstel: return 8;
		case Tegenwind: return 9;
		case Meewind: return 10;
		case Jeugdherberg: return 11;
		case StapOp: return 12;
		default: return 0;
	   }
	}
	
	
	public type getKaartsoort() {
		return soort;
	}
	
	
	public void setKaartsoort(type deKaart) {
		soort = deKaart;
	}
	
	
	public String getKaartnaam() {
		switch(soort) {
		case KM5: return "5km";
		case KM6: return "6km";
		case KM8: return "8km";
		case KM10: return "10km";
		case GeslotenOverweg: return "Gesloten Overweg";
		case BomenOpen: return "Bomen Gaan Open";
		case LekkeBand: return "Lekke Band";
		case Rijwielherstel: return "Rijwielhersteller";
		case Tegenwind: return "Tegenwind";
		case Meewind: return "Wind Mee";
		case Jeugdherberg: return "Jeugdherberg";
		case StapOp: return "Stap Op";
		default: return "geen";
		}
	}
	
	
	public int getKaartKM() {
		switch(soort) {
		case KM5: return 5; 
		case KM6: return 6; 
		case KM8: return 8; 
		case KM10: return 10;
		default: return 0;
		}
	}
	
	
	public static int getNumberOfCards(Kaart.type deSoort) {
// Function that yields the number of cards of the same type in the game.		
		switch(deSoort) {
		case KM5: return 24;
		case KM6: return 12;
		case KM8: return 8;
		case KM10: return 8;
		case GeslotenOverweg: return 3;
		case BomenOpen: return 8;
		case LekkeBand: return 3;
		case Rijwielherstel: return 8;
		case Tegenwind: return 4;
		case Meewind: return 5;
		case Jeugdherberg: return 2;
		case StapOp: return 14;
		default: return 0;
		}
	}		
}
