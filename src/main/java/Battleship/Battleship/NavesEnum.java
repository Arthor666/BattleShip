package Battleship.Battleship;

public enum NavesEnum {
	ACORAZADO("acorazado"),
	DESTRUCTOR("destructor"),
	SUBMARINO("submarino"),
	CRUCERO("crucero");
	public final String label;
	private NavesEnum(String string) {
		this.label = string;
	}
	
}
