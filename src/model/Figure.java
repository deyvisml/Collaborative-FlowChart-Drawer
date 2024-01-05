package model;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Figure implements Serializable {
	
    protected Shape shape;
    protected Color color = Color.black;
    protected String text;
    protected int x, y, width, height;
    protected int inputTextX, inputTextY;    //La posición del cuadro de entrada.
    protected int stringX, stringY;          //donde se muestra el texto
    protected ArrayList<String> strings = new ArrayList<>(); //
    
    public int selected_by_user = -1; // si la figura no esta seleccionada
    

    public Figure() {
    }

    public Figure(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        text = "";
    }

    public void setStringLocation(){
        //Cálculo de coordenadas de visualización de texto, ajuste de texto
        strings.clear();
        
        // Get the FontMetrics
        Canvas c = new Canvas();
        FontMetrics metrics  = c.getFontMetrics(MyTextArea.getInstance().getFont());
        
        int textWidth = metrics.stringWidth(text);
        int char_len = 1;
        
        if(text.length() != 0)
        {
        	char_len = textWidth/text.length();
        }
        
        if (textWidth > width) {
            int charCount = (width - 30) / char_len;
 
            int index = 0, lineCount = 0;

            while (index + charCount < text.length()) {
                System.out.println(index+" "+charCount+" "+text.substring(index, index + charCount));
                
                strings.add(text.substring(index, index + charCount));
                
                index = index + charCount;
                lineCount++;
            }
            if (index < text.length()) {
                strings.add(text.substring(index));
                lineCount++;
            }
            
            stringX = x + 10;
            stringY = y + (((height - lineCount*metrics.getHeight()) / 2) + metrics.getAscent());
        }
        else{
            strings.add(text);
            
            // Determine the X coordinate for the text
            stringX = x + (width - metrics.stringWidth(text)) / 2;
            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            stringY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Shape getShape() {
        return shape;
    }

    public void resetShape() {
    }

    public boolean contains(Point p) {
        if (shape.contains(p)) {
            //color = Color.red;
            return true;
        } else
            return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setInputTextX(int inputTextX) {
        this.inputTextX = inputTextX;
    }

    public void setInputTextY(int inputTextY) {
        this.inputTextY = inputTextY;
    }

    public void setStringX(int stringX) {
        this.stringX = stringX;
    }

    public void setStringY(int stringY) {
        this.stringY = stringY;
    }

    public int getInputTextX() {
        return inputTextX;
    }

    public int getInputTextY() {
        return inputTextY;
    }

    public int getStringX() {
        return stringX;
    }

    public int getStringY() {
        return stringY;
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }
    
    public void set_selected_by_user(int id_user)
    {
    	this.selected_by_user = id_user;
    }

    public abstract Figure clone();
}
