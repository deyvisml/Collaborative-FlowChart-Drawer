package conecction;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import memento.Caretaker;
import model.FigureList;

public class Server extends Thread implements Serializable{

	public ArrayList<ServerController> clients = new ArrayList<ServerController>();	
	public transient ServerSocket ss;
	public Paquete currently_p = new Paquete();
	
	public Server(Integer port_number)
	{
		try {
			this.ss = new ServerSocket(port_number);
			System.out.println("Server started, awaiting Client connections...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * broadcast -- receives a package from ServerController, and broadcasts it to all connected Clients
	 * @param shape: PaintingPrimitive
	 */
	public void broadcast(Paquete p) {
		
		this.currently_p = p;
		
		ServerController sc_sender = p.sc;

		for(ServerController sc : clients) {
			if(!sc.equals(sc_sender))
			{				
				try {
					sc.send(p);
				}catch(Exception e) {
					System.out.println("SERVER ERROR: Broadcast");
					// se elimina el cliente ya que posiblemente este se haya desconectado
					removeClient(sc);
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
	}

	/**
	 * broadcastDisconnect -- Delete a client when the broadcast can't send a message
	 */
	public void removeClient(ServerController sc) {
		clients.remove(sc);
	}

	/**
	 * run -- listens for Clients
	 */
	public void run() {
		
		try {
			while(true) { // se queda esperando nuevas conexiones al servidor
				Socket s = this.ss.accept();
				ServerController sc = new ServerController(s, this);
				
				clients.add(sc);
				
				sc.send(this.currently_p); // al nuevo cliente se le establecen la lista de figuras actual
				this.currently_p.user_counter++;
				
				sc.start();
			}
		}catch(IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		
		Server s = new Server(9999);
		
		s.start();
	}
}
