package Battleship.Battleship;

import java.io.Serializable;
import java.util.List;

public class Nave implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String nombre;
	List<Casilla> espacios;
	
	public Nave() {
		
	}
	
	public Nave(String nombre, List<Casilla> espacios) {
		this.nombre = nombre;
		this.espacios = espacios;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<Casilla> getEspacios() {
		return espacios;
	}
	public void setEspacios(List<Casilla> espacios) {
		this.espacios = espacios;
	}
	
}
