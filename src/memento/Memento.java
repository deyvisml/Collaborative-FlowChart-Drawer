package memento;

import java.io.Serializable;

import model.FigureList;

public class Memento implements Serializable{
    final FigureList  state; // el estado es la colección de graficos dibujados
    public Memento(FigureList state) {
        this.state = state;
    }
    public FigureList getState(){
        return state;
    }
}
