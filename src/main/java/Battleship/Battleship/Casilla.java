package Battleship.Battleship;

import java.io.Serializable;
import java.util.List;

public class Casilla implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Object contenido;
	int x,y;

	public Casilla (Object contenido) {
		this.contenido = contenido;			
	}
	
	public Casilla() {
		
	}
	
	public Object getContenido() {
		return contenido;
	}

	public void setContenido(Object contenido) {
		this.contenido = contenido;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}	
	
	
}
