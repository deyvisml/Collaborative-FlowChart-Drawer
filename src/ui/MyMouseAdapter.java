package ui;

import memento.Memento;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MyMouseAdapter extends MouseAdapter {
    
	Client main;
	
    //coordenadas gráficas
    private int xStart;
    private int yStart;
    private int xEnd;
    private int yEnd;
    private int xMove;
    private int yMove;

    private boolean pressed = false;   //Se utiliza para determinar si el botón es "Crear un nuevo gráfico" o "Arrastrar un gráfico".
    private boolean dragged = false;

    private boolean selectFlag = false;
    private Arrow arrow;


    public MyMouseAdapter(Client main) {
        this.main = main;
    }

    public void mousePressed(MouseEvent e) {
        main.statusBar.setText("     Mouse Pressed @:[" + e.getX() + ", " + e.getY() + "]");//Establecer recordatorio de estado
        main.buttonGroup.clearSelection();
        main.xStart = e.getX();
        main.yStart = e.getY();
        xStart = e.getX();
        yStart = e.getY();

        main.selectedFigure = getClickedFigure(e.getPoint());
        if (main.selectedFigure != null) {
            xMove = main.selectedFigure.getX() - xStart;
            yMove = main.selectedFigure.getY() - yStart;
            return;
        }
        pressed = true;
    }

    public void mouseDragged(MouseEvent e) {
        System.out.println("in mouseDragged");

        //main.flowChart.setColor(Color.black);
        dragged = true;

        MyTextArea.getInstance().setVisible(false);

        //Arrastrar un gráfico existente
        if (main.selectedFigure != null) {
            main.selectedFigure.setX(xMove + e.getX());
            main.selectedFigure.setY(yMove + e.getY());
            main.selectedFigure.setColor(Color.black);
            main.selectedFigure.resetShape();
            pressed = false;
            
            main.myPanel.repaint();
            main.send();
            
            return;
        }

        //nuevos gráficos
        switch (main.figureType) {
            case RECTANGLE: //rectángulo
                MyRectangle myRectangle = new MyRectangle(xStart, yStart, Math.abs(xStart - e.getX()), Math.abs(yStart - e.getY()));
                main.bufferedShape = myRectangle;
                //main.bufImage = ob.draw();
                break;
            case PARALLELOGRAM://Paralelogramo
                MyParallelogram myParallelogram = new MyParallelogram(xStart, yStart, Math.abs(xStart - e.getX()), Math.abs(yStart - e.getY()));
                main.bufferedShape = myParallelogram;
                break;
            case ELLIPSE:  //oval
                MyEllipse myEllipse = new MyEllipse(xStart, yStart, Math.abs(xStart - e.getX()), Math.abs(yStart - e.getY()));
                main.bufferedShape = myEllipse;
                break;
            case RHOMBUS:  //Rombo prismático
                main.bufferedShape = new MyRhombus(xStart, yStart, Math.abs(xStart - e.getX()), Math.abs(yStart - e.getY()));
                break;
            default:
                break;
        }
        main.myPanel.repaint();
        
        main.send();
    }

    public void mouseReleased(MouseEvent e) {
        main.statusBar.setText("     Mouse Released @:[" + e.getX() + ", " + e.getY() + "]");
        
        //main.clickedElement = null;
        if (pressed == true) {

            xEnd = e.getX();
            yEnd = e.getY();
            if (Math.abs(xStart - xEnd) == 0 || Math.abs(yStart - yEnd) == 0)
                return;
            addFigure(xStart, yStart, Math.abs(xStart - xEnd), Math.abs(yStart - yEnd));

            pressed = false;
            main.figureType = FigureType.NONE;
            main.myPanel.repaint();
            
            main.send();
        }

        main.bufImage = null;
        xEnd = 0;
        yEnd = 0;
        if (dragged) {     //Terminar de arrastrar los gráficos existentes y guardar el nuevo estado
            System.out.println("in drag end");
            createMemento();
            dragged = false;
            
            main.send();
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            onRightMouseClick(e);
            return;
        }
        
        main.statusBar.setText("     Mouse clicked @:[" + e.getX() + ", " + e.getY() + "]");
        xStart = e.getX();
        yStart = e.getY();
        dragged = false;

        Figure clickedFigure = getClickedFigure(new Point(xStart, yStart));
        
        //se vuelve rojo cuando se selecciona
        if (clickedFigure != null && clickedFigure.selected_by_user == -1) {
        	//clickedFigure.getColor().equals(new Color(0, 0, 0)
            clickedFigure.setColor(Color.red);
            
            // 1. que el usuario solo pueda seleccionar como maximo 2 figuras
            // 2. si una figura ya esta en azul el usuario no pueda modificarla (por lo tal se debe guardar quien puso azul una figura)
            clickedFigure.set_selected_by_user(main.id_user);
        }
        main.myPanel.repaint();
        
        main.send();

        //Actualmente hay una acción para ingresar texto
        if (MyTextArea.getInstance().isInputTextBegin()) {
            if (MyTextArea.getInstance().getFigure() != null) {
                MyTextArea.getInstance().getFigure().setText(MyTextArea.getInstance().getText());
                System.out.println("add TEXT 1");
                createMemento();
            }
            MyTextArea.getInstance().setInputTextBegin(false);
            main.myPanel.repaint();
            
            main.send();
            return;
        }

        //Haga doble clic en el gráfico para comenzar a escribir texto
        if (e.getClickCount() == 2) {
            Figure doubleClickFigure = getClickedFigure(new Point(xStart, yStart));
            if (doubleClickFigure != null) {
                MyTextArea.getInstance().setFigure(doubleClickFigure);
                MyTextArea.getInstance().inputText();
            }
            return;
        }

        if (main.figureType == FigureType.TEXT) {
            if (clickedFigure != null) {
                MyTextArea.getInstance().setFigure(clickedFigure);
                MyTextArea.getInstance().inputText();
                System.out.println("add TEXT 3");
                createMemento();
            }
        }
        
        //Dibujar una línea entre dos figuras
        if (main.figureType == FigureType.ARROW) {
            //Figure f = getClickedFigure(new Point(xStart, yStart));
            if (null == clickedFigure)  //Los gráficos deben seleccionarse para conectarse
                return;
            if (selectFlag == false) {  //no hay gráficos seleccionados todavía
                arrow = new Arrow();
                arrow.setFigure_from(clickedFigure);
                selectFlag = true;
            } else {  //Se ha seleccionado una forma.
                //Si no se selecciona el segundo gráfico, o si el segundo gráfico es el mismo que el primero, no se realizará ninguna conexión.
                if (clickedFigure == null || clickedFigure == arrow.getFigure_from()) {
                    arrow.setFigure_from(null);
                    selectFlag = false;
                    main.figureType = FigureType.NONE;
                    return;
                }
                
                arrow.setFigure_goto(clickedFigure);
                
                main.figureList.add(arrow);
                
                selectFlag = false;
                
                main.myPanel.repaint();
                main.send();
                
                
                main.figureType = FigureType.NONE;
                System.out.println("add ARROW");
                createMemento();
            }
        }
    }

    public void onRightMouseClick(MouseEvent e) {
        JMenuItem mCopy, mPaste, mDel, mSetSize;
        JPopupMenu menu = new JPopupMenu();
        mCopy = new JMenuItem("Copiar");
        mPaste = new JMenuItem("pegar");
        mDel = new JMenuItem("Eliminar");
        mSetSize = new JMenuItem("establecer el tamaño del gráfico");

        if (main.selectedFigure != null) {
            //menu.add(mCopy);
            //menu.add(mPaste);
            menu.add(mDel);
            menu.add(mSetSize);
            menu.show(main.myPanel, e.getX(), e.getY());
        }
        //menu.show(main.myPanel, e.getX(), e.getY());

        //mCopy.addActionListener(a -> main.copy());
        //mPaste.addActionListener(a -> main.paste());
        mDel.addActionListener(a -> main.delete());
        mSetSize.addActionListener(a -> {
            MyInputDialog inputDialog = new MyInputDialog(main);
            int lx = main.selectedFigure.getX() + main.selectedFigure.getWidth();
            int ly = main.selectedFigure.getY() + main.selectedFigure.getHeight();
            inputDialog.setLocation(lx, ly);
            inputDialog.setVisible(true);
        });
    }

    public void addFigure(int x1, int y1, int width, int height) {
        System.out.println("in addFigure");
        switch (main.figureType) {
            case RECTANGLE:
                main.figureList.add(new MyRectangle(x1, y1, width, height));
                break;
            case PARALLELOGRAM:
                main.figureList.add(new MyParallelogram(x1, y1, width, height));
                break;
            case ELLIPSE:
                main.figureList.add(new MyEllipse(x1, y1, width, height));
                break;
            case RHOMBUS:
                main.figureList.add(new MyRhombus(x1, y1, width, height));
                break;
            default:
                break;
        }
    }

    //Devuelve la forma seleccionada con un clic del mouse
    public Figure getClickedFigure(Point p) {
        System.out.println("in getClickedFigure");
        
        //Recorrer de atrás hacia adelante es seleccionar primero el más externo cuando dos gráficos se superponen (el más externo generalmente se crea más tarde)
        ArrayList<Figure> list = main.figureList.getFigureList();
        
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).contains(p))
            {
            	if((list.get(i).selected_by_user == -1 || list.get(i).selected_by_user == main.id_user)) // bloqueo pesimista
            	{
            		list.get(i).setColor(Color.red);
            		return list.get(i);      	            		
            	}
            }
            else
            {
            	// colorear de negro y deseleccionar
            	if(list.get(i).selected_by_user == -1 || list.get(i).selected_by_user == main.id_user) // bloqueo pesimista
            	{
            		list.get(i).setColor(Color.black);            		
            		list.get(i).set_selected_by_user(-1);  	
            	}
            }
        }
        
        return null;
    }

    public void createMemento() {
        System.out.println("in createMemento");
        main.caretaker.add(new Memento(main.getFigureList()));
    }
}
