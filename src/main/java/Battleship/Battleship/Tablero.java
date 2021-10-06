package Battleship.Battleship;

import java.io.Serializable;

public class Tablero implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Casilla[][] mapaCasillas;

	public Tablero() {
		mapaCasillas = new Casilla[10][10];		
		for(int i = 0; i <10;i++){
			for(int j = 0;j<10;j++){
				mapaCasillas [i][j] = new Casilla();
				mapaCasillas[i][j].setX(i);
				mapaCasillas[i][j].setY(j);
			}
		}
	}
	
	public Casilla[][] getMapaCasillas() {
		return mapaCasillas;
	}

	public void setMapaCasillas(Casilla[][] mapaCasillas) {
		this.mapaCasillas = mapaCasillas;
	}
	
	public boolean dioEnElBlanco(Casilla tiro) {
		boolean tiroExitoso = false;
		for(int x = 0; x < 10;x++) {
			for(int y =0;y < 10;y++) {
				if(mapaCasillas[x][y].getContenido() instanceof Nave) {
					return !tiroExitoso;
				}
			}			
		}
		return tiroExitoso;
	}
	
	public String toString2() {
		String salida ="";
		for(int i = 0; i< 10 ;i++) {
			salida += i+"\t";
		}
		salida +="\n";
		for(int x = 0; x <10;x++) {
			for(int y = 0; y< 10;y++) {
				if(this.mapaCasillas[y][x].getContenido() == null) {
					salida += "?";
				}else if(this.mapaCasillas[y][x].getContenido() instanceof Nave) {
					salida += "+";
				}else if(this.mapaCasillas[y][x].getContenido() instanceof String) {
					salida += this.mapaCasillas[y][x].getContenido();
				}
				salida +="\t";
			}
			salida += x;
			salida += "\n";
		}
		return salida;
	}
	
	public String toString() {
		String salida ="";
		for(int i = 0; i< 10 ;i++) {
			salida += i+"\t";
		}
		salida +="\n";
		for(int x = 0; x <10;x++) {
			for(int y = 0; y< 10;y++) {
				if(this.mapaCasillas[y][x].getContenido() == null) {
					salida += "-";
				}else if(this.mapaCasillas[y][x].getContenido() instanceof Nave) {
					salida += "+";
				}else if(this.mapaCasillas[y][x].getContenido() instanceof String) {
					salida += this.mapaCasillas[y][x].getContenido();
				}
				salida +="\t";
			}
			salida += x;
			salida += "\n";
		}
		return salida;
	}

	public int countNaves() {
		int count = 0;
		for(int x = 0;x<10 ;x++) {
			for(int y = 0; y<10;y++) {
				if(this.mapaCasillas[x][y].getContenido() instanceof Nave) {
					count++;
				}
			}
		}
		return count;
	}
	
	public int countString() {
		int count = 0;
		for(int x = 0;x<10 ;x++) {
			for(int y = 0; y<10;y++) {
				if(this.mapaCasillas[x][y].getContenido() instanceof String) {
					count++;
				}
			}
		}
		return count;
	}

}
