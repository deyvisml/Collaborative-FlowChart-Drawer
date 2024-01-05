package conecction;

import java.io.Serializable;

import memento.Caretaker;
import model.FigureList;


public class Paquete implements Serializable
{
	public ServerController sc = null;
	public FigureList fl = new FigureList();
	public Caretaker ct = new Caretaker();
	public int user_counter = 0;
	
	public Paquete()
	{
		
	}
	
	public Paquete(ServerController sc, FigureList fl, Caretaker ct)
	{
		this.sc = sc;
		this.fl = fl;
		this.ct = ct;
	}
}