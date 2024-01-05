package conecction;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;

import model.FigureList;

public class ServerController extends Thread implements Serializable{

	private transient Socket client;
	private Server server;
	transient ObjectInputStream ois;
	transient ObjectOutputStream oos;
	
	public ServerController(Socket client, Server server) {
		
		this.client = client;
		this.server = server;

		try {
			this.ois = new ObjectInputStream(this.client.getInputStream());
			this.oos = new ObjectOutputStream(this.client.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("SERVER_CONTROLLER ERROR: oos/ois init failure");
			e.printStackTrace();
		}
	}
	
	/**
	 * send -- Send a message to ClientController
	 */
	public void send(Paquete p) { // sending a message to ClientController
		try {
			oos.flush();
		    oos.reset();
		    
			oos.writeObject(p);
			
		} catch (IOException e) {
			System.out.println("SERVER_CONTROLLER ERROR: send failure");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * run -- Listener to ClientController
	 */
	public void run() {
		try {
			while(true) {
				Paquete p = (Paquete) ois.readObject();
				
				Paquete p_aux = new Paquete(this, p.fl, p.ct);
				
				this.server.broadcast(p_aux);
			}
		}catch(ClassNotFoundException e) {
			System.out.println("SERVER_CONTROLLER ERROR: Listener to Client failure");
			e.printStackTrace();
		} catch (EOFException eof) {
			System.out.println("SERVER_CONTROLLER ERROR: Listener to Client EOF");
			eof.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}