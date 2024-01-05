package ui;

import conecction.*;
import memento.*;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Client {
    JFrame frame;
    MyPanel myPanel;

    JLabel statusBar;
    ButtonGroup buttonGroup = new ButtonGroup();  //colección de botones

    Figure selectedFigure;
    Figure copyFigure;
    FigureType figureType = FigureType.NONE;     //El tipo de gráficos seleccionados.
    BufferedImage image = null;                  //Se utiliza para almacenar imágenes en búfer durante el arrastre
    ArrayList<Shape> bufImage = null;

    int xStart, yStart;

    Figure bufferedShape = null;
    Image bkgImage;
    
    FigureList figureList = new FigureList();
    Caretaker caretaker = new Caretaker();
    
    public ClientController cc;
    
    public int id_user;

    public Client(String server_ip, Integer server_port) {
    	
        construct();
        
        MyTextArea.getInstance().setMain(this);
        
        cc = new ClientController(this, server_ip, server_port);
        cc.start();
        
        
        // el try catch solo para poder imprimir el id correcto
        try {
			Thread.sleep(1000);
			System.out.println("** user id: "+id_user);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void construct() {
        frame = new JFrame("Collaborative FlowChart Drawer ( EPIS - UNA PUNO)");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Agregar barra de menú, barra de herramientas
        frame.setJMenuBar(new MenuBar(this));
        frame.getContentPane().add(new ToolBar_Horizontal(this), BorderLayout.NORTH);
        frame.getContentPane().add(new ToolBar_Vertical(this), BorderLayout.WEST);

        statusBar = new JLabel();
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);

        setMyPanel();
        frame.setSize(400, 700);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void setMyPanel() {
        myPanel = new MyPanel(this);
        myPanel.setBackground(Color.white);
        myPanel.add(MyTextArea.getInstance().getjTextArea());
        MyMouseAdapter mouseAdapter = new MyMouseAdapter(this);
        myPanel.addMouseListener(mouseAdapter);
        myPanel.addMouseMotionListener(mouseAdapter);
        myPanel.setPreferredSize(new Dimension(1500, 4000));
        JScrollPane scrollPane = new JScrollPane(myPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.pack();
    }

    public MyPanel getMyPanel() {
        return myPanel;
    }

    public Caretaker getCaretaker() {
        return caretaker;
    }

    public void setCaretaker(Caretaker caretaker) {
    	this.caretaker = caretaker;
    	myPanel.repaint();
    }
    
    public FigureList getFigureList() {
        return figureList;
    }
    
    public void setFigureList(FigureList figureList) {
        this.figureList = figureList;
        myPanel.repaint();
    }
    

    public Figure getSelectedFigure() {
        return selectedFigure;
    }

    public void setBkgImage() {
        FileDialog fd = new FileDialog(frame, "seleccione el archivo de reproducción", FileDialog.LOAD);
        fd.setLocationRelativeTo(null);
        fd.setVisible(true);
        if (fd.getDirectory() == null || fd.getFile() == null) {
            return;
        }
        String filename = fd.getDirectory() + fd.getFile();
        bkgImage = new ImageIcon(filename).getImage();
        myPanel.repaint();
        
        send();
    }

    public void clearBkgImage() {
        bkgImage = null;
        myPanel.repaint();
        
        send();
    }

    public void copy() {
        if (null == selectedFigure) {
            return;
        }
        copyFigure = selectedFigure.clone();
        copyFigure.setX(copyFigure.getX() + 30);
        copyFigure.setY(copyFigure.getY() + 30);
        myPanel.repaint();
    }

    public void paste() {
        if (null == copyFigure)
            return;
        if (selectedFigure == copyFigure) {   //Duplicar
            copyFigure = selectedFigure.clone();
            copyFigure.setX(copyFigure.getX() + 30);
            copyFigure.setY(copyFigure.getY() + 30);
        }
        figureList.add(copyFigure);
        selectedFigure.setColor(Color.black);
        copyFigure.setColor(Color.red);
        selectedFigure = copyFigure;
        myPanel.repaint();
        
        send();
    }

    public void undo() {
        figureList = caretaker.Undo().clone();
        selectedFigure = null;
        myPanel.repaint();
        
        send();
    }

    public void redo() {
        figureList = caretaker.Redo();
        selectedFigure = null;
        myPanel.repaint();
        
        send();
    }

    public void delete() {
        ArrayList<Figure> list = figureList.getFigureList();
        
        for (Figure figure : list)
            if (selectedFigure == figure && (selectedFigure.selected_by_user == id_user)) {
                caretaker.add(new Memento(figureList.clone()));   //Guardar el estado antes de la eliminación
                ArrayList<Figure> del_arrows = new ArrayList<>(); //También elimine todas las líneas conectadas a él.
                for(Figure arrow:list){
                    if(arrow instanceof Arrow){
                        if(((Arrow) arrow).getFigure_from().equals(selectedFigure) ||
                                ((Arrow) arrow).getFigure_goto().equals(selectedFigure)) {
                            del_arrows.add(arrow);
                        }
                    }
                }
                figureList.remove(selectedFigure);
                for(Figure arrow:del_arrows){
                    figureList.remove(arrow);
                }
                caretaker.add(new Memento(figureList.clone())); //Guardar el estado eliminado
                break;
            }
        myPanel.repaint();
        
        send();
    }

    public void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();

        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Nombre de archivo inválido",
                    "Nombre de archivo inválido", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                ObjectOutputStream output = new ObjectOutputStream(fos);
                output.writeInt(figureList.getFigureList().size());
                ArrayList<Figure> list = figureList.getFigureList();
                for (Figure figure : list) {
                    output.writeObject(figure);
                    output.flush();
                }
                output.close();
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        if (fileName == null || fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Nombre de archivo inválido",
                    "Nombre de archivo inválido", JOptionPane.ERROR_MESSAGE);
        } 
        else 
        {
            try {
                FileInputStream fis = new FileInputStream(fileName);
                ObjectInputStream input = new ObjectInputStream(fis);
                int count = input.readInt();
                figureList.clear();
                while (count > 0) {
                    Figure figure = (Figure) input.readObject();
                    figureList.add(figure);
                    count --;
                }
                input.close();
                myPanel.repaint();
                
                send();
            } catch (EOFException endOfFileException) {
                JOptionPane.showMessageDialog(frame, "el archivo no tiene contenido",
                        "pronta informacion", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException classNotFoundException) {
                JOptionPane.showMessageDialog(frame, "error de contenido del archivo",
                        "pronta informacion", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(frame, "lectura de archivo fallida",
                        "pronta informacion", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    
    public void send()
    {
    	Paquete p = new Paquete(null, this.figureList, this.caretaker);
    	
    	this.cc.send(p);
    }
    
    
    public static void main(String[] args) {
		String server_ip = JOptionPane.showInputDialog("Enter server ip");
		Integer server_port = Integer.parseInt(JOptionPane.showInputDialog("Enter server port"));
		
		//if user hits cancel, don't make Painter
		if(null == server_ip || server_port == null) {
			System.exit(0);
		}
		
		//make panel
		new Client(server_ip, server_port);
    	
    	//new Client("7.60.58.20", 9999);
    }
}
