package conecction;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.FigureList;
import ui.Client;

public class ClientController extends Thread{
		
	static ObjectInputStream ois;
	static ObjectOutputStream oos;
	
	private Client c;
	
	public ClientController(Client c, String server_ip, Integer server_port)
	{
		this.c = c;
		
		//connect to the server
		try {
			
			Socket s = new Socket(server_ip, server_port);
			System.out.println("CLIENTE CONECTADO !!");

			// Data Streams
			{
				ois = new ObjectInputStream(s.getInputStream()); // entrada
				oos = new ObjectOutputStream(s.getOutputStream()); // salida
			};

		} catch (IOException e) {
			System.out.println("CLIENT_CONTROLLER ERROR: El cliente no se pudo conectar al servidor");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	public  void send(Paquete p) { // sending message to the server
		
		try {
			oos.flush();
		    oos.reset();
		    
			oos.writeObject(p);
			
		} catch (IOException e) {
			System.out.println("CLIENT_CONTROLLER ERROR: No se pudo enviar el mensaje");
			e.printStackTrace();
		}
	}
	
	
	public void run() { // listener to Server
		
		boolean only_in_start = true;
		try {
			while(true) {
				Paquete p = (Paquete) ois.readObject();
				
				this.c.setFigureList(p.fl);
				
				if(only_in_start)
				{
					only_in_start = false;
					this.c.id_user = p.user_counter;
					System.out.println("user id: "+this.c.id_user);
				}
				//this.c.setCaretaker(p.ct);
			}
		}catch(ClassNotFoundException e) {
			System.out.println("CLIENT_CONTROLLER ERROR: Listener server failure");
			e.printStackTrace();
		} catch (EOFException eof) {
			System.out.println("CLIENT_CONTROLLER ERROR: Listener server EOF");
			eof.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}



