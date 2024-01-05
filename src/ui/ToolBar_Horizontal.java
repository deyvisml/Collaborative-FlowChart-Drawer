package ui;

import javax.swing.*;

import model.FigureType;
import model.MyTextArea;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ToolBar_Horizontal extends JToolBar {
    Client main;


    public ToolBar_Horizontal(Client main) {
        super("ToolbarHorizontal", JToolBar.HORIZONTAL);
        this.main = main;
        construct();
    }

    public void construct() {
        this.setSize(50, 50);
        this.setFloatable(false);
        JToggleButton delButton = new JToggleButton(new ImageIcon("images/del.png"));
        JToggleButton arrowButton = new JToggleButton(new ImageIcon("images/arrow.png"));
        JToggleButton undoButton = new JToggleButton(new ImageIcon("images/undo.png"));
        JToggleButton redoButton = new JToggleButton(new ImageIcon("images/redo.png"));
        JToggleButton textButton = new JToggleButton(new ImageIcon("images/text.png"));

        delButton.addActionListener(e -> {
            main.delete();
            main.buttonGroup.clearSelection();
        });
        arrowButton.addActionListener(e -> main.figureType = FigureType.NONE);
        undoButton.addActionListener(e -> main.undo());
        redoButton.addActionListener(e -> main.redo());
        textButton.addActionListener(e -> main.figureType = FigureType.TEXT);

        // style: bold or italic
        JToggleButton boldButton = new JToggleButton(new ImageIcon("images/bold.png"));
        JToggleButton italicButton = new JToggleButton(new ImageIcon("images/italic.png"));
        boldButton.addActionListener(e -> {
            if (MyTextArea.getInstance().getBold()) {
                MyTextArea.getInstance().setBold(false);
                boldButton.setIcon(new ImageIcon("images/bold.png"));
            } else {
                MyTextArea.getInstance().setBold(true);
                boldButton.setIcon(new ImageIcon("images/boldDown.png"));
            }
            
            this.main.myPanel.repaint();
        	this.main.send();
        });
        italicButton.addActionListener(e -> {
            if (MyTextArea.getInstance().getItalic()) {
                MyTextArea.getInstance().setItalic(false);
                italicButton.setIcon(new ImageIcon("images/italic.png"));
            } else {
                MyTextArea.getInstance().setItalic(true);
                italicButton.setIcon(new ImageIcon("images/italicDown.png"));
            }
            
            this.main.myPanel.repaint();
        	this.main.send();
        });

        //font type
        String styleNames[] = {"Times New Roman", "Monospaced", "SansSerif"};

        JComboBox styleButton = new JComboBox(styleNames);
        styleButton.setMaximumRowCount(8);
        styleButton.setSelectedIndex(0);
        
        styleButton.addItemListener(e -> {
        	MyTextArea.getInstance().setTextStyle(styleNames[styleButton.getSelectedIndex()]);
        	this.main.myPanel.repaint();
            this.main.send();
        });
        
        String textSizes[] = {"14", "16", "18", "20", "22", "24"};
        JComboBox textSizesButton = new JComboBox(textSizes);
        textSizesButton.setMaximumRowCount(8);
        textSizesButton.setSelectedIndex(2);
        
        textSizesButton.addItemListener(e -> {
            int size = Integer.parseInt(textSizes[textSizesButton.getSelectedIndex()]);
            MyTextArea.getInstance().setTextSize(size);
            
            this.main.myPanel.repaint();
        	this.main.send();	
        });
        

        this.add(delButton);   this.addSeparator();
        this.add(arrowButton); this.addSeparator();
        this.add(undoButton);  this.addSeparator();
        this.add(redoButton);  this.addSeparator();
        this.add(textButton);  this.addSeparator();
        this.add(boldButton);  this.addSeparator();
        this.add(italicButton);this.addSeparator();
        this.add(styleButton); this.addSeparator();
        this.add(textSizesButton);
        styleButton.setMinimumSize(new Dimension(50, 20));
        styleButton.setMaximumSize(new Dimension(100, 20));
        textSizesButton.setMinimumSize(new Dimension(50, 20));
        textSizesButton.setMaximumSize(new Dimension(100, 20));

        main.buttonGroup.add(delButton);
        main.buttonGroup.add(arrowButton);
        main.buttonGroup.add(undoButton);
        main.buttonGroup.add(redoButton);
        main.buttonGroup.add(textButton);
        main.buttonGroup.add(boldButton);
        main.buttonGroup.add(italicButton);
       
    }
}
