package Battleship.Battleship;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class Cliente 
{
	protected static Tablero tableroCliente;
	protected static Tablero tableroServidor;
	protected static List<Nave> listNaves = initNaves();
	public static void main( String[] args ) throws IOException, ClassNotFoundException{
    	tableroCliente = new Tablero();
    	tableroServidor = new Tablero();
    	Scanner sc = new Scanner(System.in);
    	String coordenadas = "";
    	char randPositions = 'n';
    	System.out.println("Quieres que se acomoden tus naves de manera aleatoria? s/n");
    	randPositions = sc.next().charAt(0);
    	for(Nave n: listNaves) {
    		if(randPositions == 's') {
    			while(n.getEspacios() == null ) {    			    		
    				Random r = new Random();
    	            int x = r.nextInt(10);
    	            int y = r.nextInt(10);
    	            System.out.println(x+","+y);
    	            n.setEspacios(intentarAcomodarNave(n,x,y));
        		}    			
    		}else {
    			while(n.getEspacios() == null ) {    			    		
    	            System.out.println("Dame la coordenadas para el: "+ n.getNombre()+" separadas por una coma x,y");
    	            coordenadas = sc.nextLine();
    	            int x = Integer.parseInt(coordenadas.split(",")[0]);
    	            int y = Integer.parseInt(coordenadas.split(",")[1]);
    	            n.setEspacios(intentarAcomodarNave(n,x,y));
    	            if(n.getEspacios() == null) {
    	            	System.out.println("No pude acomodar la Nave, reingresa las coordenadas");
    	            	
    	            }
        		}
    		}
        }    	
    	//System.out.println(tableroCliente);
    	System.out.println("Conectando con el servidor...");    	
    	DatagramSocket datagramSocket = new DatagramSocket();
    	//Enviamos la solicitud para iniciar el juego
    	String ataque = "";
    	if(solicitudNuevoJuego(datagramSocket)) {
    		while(true) {
    			printEstadoJuego();
        		if(enviarAtaque(datagramSocket,ataque)) {
        			System.out.println("En el blanco");            		
        		}else {
        			System.out.println("Fallaste el tiro");        			
        		}        		
        		printEstadoJuego();
        		if(revisarFinJuego()) {
        			break;
        		}
        		System.out.println("Esperando el tiro del servidor");
        		recibirAtaque(datagramSocket);
        		if(revisarFinJuego()) {
        			break;
        		}
    		}    		
    	}    	
    	
    }
	
	
	private static boolean revisarFinJuego() {
		if(tableroCliente.countNaves() <= 0 || tableroServidor.countString() >= 21) {
			System.out.println("Fin del juego");
			return true;
		}
		return false;
	}


	private static void recibirAtaque(DatagramSocket datagramSocket) throws IOException, ClassNotFoundException {
		String ataque ="";
		int max = 65535;
		InetAddress direccion = InetAddress.getByName(Constantes.getIPSERVER());
		DatagramPacket p = new DatagramPacket(new byte[max],max);
		datagramSocket.receive(p);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p.getData()));
		ataque = (String) ois.readObject();
		int x = Integer.parseInt(ataque.split(",")[0]);
		int y = Integer.parseInt(ataque.split(",")[1]);
		boolean efectivo = false;		
		if(tableroCliente.getMapaCasillas()[x][y].getContenido() instanceof Nave) {
			System.out.println("Nos dieron");
			tableroCliente.getMapaCasillas()[x][y].setContenido("x");
			efectivo = true;
		}else {
			System.out.println("Nos salvamos");
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
     	ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
     	objectOutputStream.writeBoolean(efectivo);
     	objectOutputStream.flush();
     	byte [] data = byteArrayOutputStream.toByteArray();
    	DatagramPacket packet = new DatagramPacket(data,data.length,direccion,Constantes.getPUERTO());
    	datagramSocket.send(packet);
	}


	private static void printEstadoJuego() {		
		System.out.println("----------Tu tablero------------");
		System.out.println(tableroCliente);
		System.out.println("----------Servidor------------");
		System.out.println(tableroServidor.toString2());
		
	}


	private static boolean enviarAtaque(DatagramSocket datagramSocket, String ataque) throws IOException, ClassNotFoundException {
		System.out.println("Dame las coordenadas para atacar");
		Scanner sc = new Scanner(System.in);
		ataque = sc.nextLine();
		int x = Integer.parseInt(ataque.split(",")[0]);
		int y = Integer.parseInt(ataque.split(",")[1]);
		InetAddress direccion = InetAddress.getByName(Constantes.getIPSERVER());
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    	objectOutputStream.writeObject(ataque);
    	objectOutputStream.flush();
    	byte [] data = byteArrayOutputStream.toByteArray();
    	DatagramPacket packet = new DatagramPacket(data,data.length,direccion,Constantes.getPUERTO());
    	datagramSocket.send(packet);
    	DatagramPacket peco = new DatagramPacket(new byte[data.length],data.length);
        datagramSocket.receive(peco);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(peco.getData()));
        boolean eco = (boolean) objectInputStream.readBoolean();  
        if(eco) {
        	tableroServidor.mapaCasillas[x][y].setContenido("x");
        }else {
        	tableroServidor.mapaCasillas[x][y].setContenido("-");
        }
		return eco;
	}


	private static void comenzarJuego() {
		
		
		
	}
	private static boolean solicitudNuevoJuego(DatagramSocket datagramSocket) throws IOException, ClassNotFoundException {
		byte [] b = Constantes.getINICIAR_JUEGO().getBytes();
		InetAddress direccion = InetAddress.getByName(Constantes.getIPSERVER());
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    	objectOutputStream.writeObject(Constantes.getINICIAR_JUEGO());
    	objectOutputStream.flush();
    	byte [] data = byteArrayOutputStream.toByteArray();
    	DatagramPacket packet = new DatagramPacket(data,data.length,direccion,Constantes.getPUERTO());
    	datagramSocket.send(packet);
    	DatagramPacket peco = new DatagramPacket(new byte[data.length],data.length);
        datagramSocket.receive(peco);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(peco.getData()));
        String s = (String) objectInputStream.readObject();
        if(s.equals(Constantes.getINICIAR_JUEGO())) {
        	System.out.println("Servidor conectado, juego aceptado");
        	return true;
        }
		return false;
	}
	protected static List<Casilla> intentarAcomodarNave(Nave n, int x, int y) {
		List<Casilla> c = null;
		if(n.getNombre().equals(NavesEnum.ACORAZADO.toString())) {
			c = intentarAcomodarNave(4,x,y,n);
		}else if(n.getNombre().equals(NavesEnum.CRUCERO.toString())) {
			c = intentarAcomodarNave(3,x,y,n);
		}else if(n.getNombre().equals(NavesEnum.SUBMARINO.toString())) {
			c = intentarAcomodarNave(5,x,y,n);
		}else if(n.getNombre().equals(NavesEnum.DESTRUCTOR.toString())) {
			c = intentarAcomodarNave(2,x,y,n);
		}
		return c;
	}
	
	protected static List<Casilla> intentarAcomodarNave(int tamanio, int x, int y,Nave n) {
		List<Casilla> c = null;
		if(revisarVerticalAscendente(tamanio,x, y)) {
			c = new ArrayList<Casilla>();
			for(int i = 0 ;i < tamanio;i++) {			
				tableroCliente.mapaCasillas[x][y-i].setContenido(n);
				c.add(tableroCliente.mapaCasillas[x][y-i]); 
			}
		}else if(revisarHorizontalDerecha(tamanio,x,y)) {
			c = new ArrayList<Casilla>();
			for(int i = 0 ;i < tamanio;i++) {			
				tableroCliente.mapaCasillas[x+i][y].setContenido(n);
				c.add(tableroCliente.mapaCasillas[x+i][y]); 
			}
		}else if(revisarVerticalDescendente(tamanio, x, y)) {
			c = new ArrayList<Casilla>();
			for(int i = 0 ;i < tamanio;i++) {			
				tableroCliente.mapaCasillas[x][y+i].setContenido(n);
				c.add(tableroCliente.mapaCasillas[x][y+i]); 
			}
		}else if(revisarHorizontalIzquierda(tamanio, x, y)) {
			c = new ArrayList<Casilla>();
			for(int i = 0 ;i < tamanio;i++) {			
				tableroCliente.mapaCasillas[x-i][y].setContenido(n);
				c.add(tableroCliente.mapaCasillas[x-i][y]); 
			}
		}
		return c;
	}
	
	
	protected static boolean revisarVerticalDescendente(int longitud, int x, int y) {
		int aux = y + (longitud-1);
		if(aux < 10) {
			for(int yFor = 0; yFor < longitud;yFor++) {
				if(tableroCliente.getMapaCasillas()[x][y + yFor].getContenido() instanceof Nave) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	protected static boolean revisarHorizontalIzquierda(int longitud, int x, int y) {
		int aux = x - (longitud-1);
		if(aux >=0) {
			for(int xFor = 0; xFor <longitud;xFor++) {
				if(tableroCliente.getMapaCasillas()[x-xFor][y].getContenido() instanceof  Nave) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	protected static boolean revisarHorizontalDerecha(int longitud, int x, int y) {
		int aux = x + (longitud-1);
		if(aux < 10) {
			for(int xFor = 0; xFor < longitud; xFor++) {
				if(tableroCliente.getMapaCasillas()[x+xFor][y].getContenido() instanceof Nave) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	protected static boolean revisarVerticalAscendente(int longitud, int x, int y) {
		int aux = y-(longitud-1);
		if(aux >= 0) {
			for(int yFor = 0;yFor < longitud;yFor++) {
				if(tableroCliente.getMapaCasillas()[x][y-yFor].getContenido() instanceof  Nave) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	private static  List<Nave> initNaves() {
		List<Nave> listNaves = new ArrayList<Nave>();
		listNaves.add(new Nave(NavesEnum.ACORAZADO.toString(), null));
		listNaves.add(new Nave(NavesEnum.CRUCERO.toString(), null));
		listNaves.add(new Nave(NavesEnum.CRUCERO.toString(), null));
		listNaves.add(new Nave(NavesEnum.DESTRUCTOR.toString(), null));
		listNaves.add(new Nave(NavesEnum.DESTRUCTOR.toString(), null));
		listNaves.add(new Nave(NavesEnum.DESTRUCTOR.toString(), null));
		listNaves.add(new Nave(NavesEnum.SUBMARINO.toString(), null));
		return listNaves;
	}
}
