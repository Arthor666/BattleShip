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
import java.util.List;
import java.util.Random;

public class Servidor {
	protected static Tablero tableroCliente;
	protected static Tablero tableroServidor;
	public static void main(String []args) throws IOException, ClassNotFoundException {
		int max=65535;		
        DatagramSocket datagramSocket = new DatagramSocket(Constantes.getPUERTO());
        System.out.println("Servidor de datagrama iniciado en el puerto "+datagramSocket.getLocalPort());
        while(true) {
        	 DatagramPacket p = new DatagramPacket(new byte[max],max);        	 
             datagramSocket.receive(p);
             int clientPort = p.getPort(); 
             InetAddress ipClient = p.getAddress();
             ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p.getData()));
             String s  =(String) ois.readObject();
             tableroCliente = new Tablero();
             if(s.equals(Constantes.getINICIAR_JUEGO())) {
            	 System.out.println("Solicitud de juego nuevo aceptada, devolviendo eco");
            	 datagramSocket.send(p);
            	 tableroServidor = new Tablero();
            	 List<Nave>listaNaves = initNaves();            	 
            	 for(Nave n: listaNaves){
            		 while(n.getEspacios() == null ) {
            			 Random r = new Random();            			 
         	            int x = r.nextInt(10);
         	            int y = r.nextInt(10);
         	            System.out.println(x+","+y);
         	            n.setEspacios(intentarAcomodarNave(n,x,y));
             		}
            	 }
            	System.out.println(tableroServidor);
             }
             
             while(true) {
            	System.out.println("Esperando el tiro del cliente");
         		recibirAtaque(datagramSocket,ipClient,clientPort);
         		if(revisarFinJuego()) {
         			break;
         		}
         		enviarAtaque(datagramSocket,ipClient,clientPort);
         		if(revisarFinJuego()) {
         			break;
         		}
             }
                       
             
        }
	}
	
	private static void enviarAtaque(DatagramSocket datagramSocket, InetAddress ipClient, int clientPort) throws IOException {
		Random r = new Random();
		int x = r.nextInt(10);
		int y = r.nextInt(10);
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    	String ataque = x+","+y;
    	objectOutputStream.writeObject(ataque);
    	objectOutputStream.flush();
    	byte [] data = byteArrayOutputStream.toByteArray();
    	DatagramPacket packet = new DatagramPacket(data,data.length,ipClient,clientPort);
    	datagramSocket.send(packet);
    	DatagramPacket peco = new DatagramPacket(new byte[data.length],data.length);
        datagramSocket.receive(peco);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(peco.getData()));
        boolean eco = (boolean) objectInputStream.readBoolean();  
        if(eco) {
        	tableroCliente.mapaCasillas[x][y].setContenido("x");
        }else {
        	tableroCliente.mapaCasillas[x][y].setContenido("-");
        }

		
	}

	private static boolean revisarFinJuego() {
		if(tableroCliente.countString() >=21  || tableroServidor.countNaves() <= 0) {
			System.out.println("Fin del juego");
			return true;
		}
		return false;
	}


	private static void recibirAtaque(DatagramSocket datagramSocket, InetAddress ipClient, int clientPort) throws IOException, ClassNotFoundException {
        System.out.println("Esperando el disparo");
        int max = 65535;
        DatagramPacket p = new DatagramPacket(new byte[max],max);
        datagramSocket.receive(p);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p.getData()));
        String ataqueRecibido = (String) ois.readObject();
        int x = Integer.parseInt(ataqueRecibido.split(",")[0]) , y = Integer.parseInt(ataqueRecibido.split(",")[1]);
        boolean efectivo = false;
        if(tableroServidor.getMapaCasillas()[x][y].getContenido() instanceof Nave) {
        	efectivo = true;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeBoolean(efectivo);
        objectOutputStream.flush();
        byte [] data = byteArrayOutputStream.toByteArray();
       	DatagramPacket packet = new DatagramPacket(data,data.length,ipClient,clientPort);
       	datagramSocket.send(packet);
		if(efectivo) {
			tableroServidor.getMapaCasillas()[x][y].setContenido("x"); 
		}
	}

	private static List<Nave> initNaves() {
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
				tableroServidor.mapaCasillas[x][y-i].setContenido(n);
				c.add(tableroServidor.mapaCasillas[x][y-i]); 
			}
		}else if(revisarHorizontalDerecha(tamanio,x,y)) {
			c = new ArrayList<Casilla>();
			for(int i = 0 ;i < tamanio;i++) {			
				tableroServidor.mapaCasillas[x+i][y].setContenido(n);
				c.add(tableroServidor.mapaCasillas[x+i][y]); 
			}
		}else if(revisarVerticalDescendente(tamanio, x, y)) {
			c = new ArrayList<Casilla>();
			for(int i = 0 ;i < tamanio;i++) {			
				tableroServidor.mapaCasillas[x][y+i].setContenido(n);
				c.add(tableroServidor.mapaCasillas[x][y+i]); 
			}
		}else if(revisarHorizontalIzquierda(tamanio, x, y)) {
			c = new ArrayList<Casilla>();
			for(int i = 0 ;i < tamanio;i++) {			
				tableroServidor.mapaCasillas[x-i][y].setContenido(n);
				c.add(tableroServidor.mapaCasillas[x-i][y]); 
			}
		}
		return c;
	}
	
	
	protected static boolean revisarVerticalDescendente(int longitud, int x, int y) {
		int aux = y + (longitud-1);
		if(aux < 10) {
			for(int yFor = 0; yFor < longitud;yFor++) {
				if(tableroServidor.getMapaCasillas()[x][y + yFor].getContenido() instanceof Nave) {
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
				if(tableroServidor.getMapaCasillas()[x-xFor][y].getContenido() instanceof  Nave) {
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
				if(tableroServidor.getMapaCasillas()[x+xFor][y].getContenido() instanceof Nave) {
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
				if(tableroServidor.getMapaCasillas()[x][y-yFor].getContenido() instanceof  Nave) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
