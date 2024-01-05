package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MenuListener;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class MenuBar extends JMenuBar {

    private Client main;

    public MenuBar(Client main) {
        super();
        this.main = main;
        construct();
    }

    public void construct() {
        this.setBounds(0, 0, main.frame.getWidth(), 30);

        JMenu fileMenu = new JMenu("File");

        Action newFile = new AbstractAction("Nuevo") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.getFigureList().clear();
                main.myPanel.repaint();
                
                main.send();
            }
        };
        newFile.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));

        Action openFile = new AbstractAction("Abrir") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.openFile();
            }
        };
        openFile.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));

        Action saveFile = new AbstractAction("Guardar") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.saveFile();
            }
        };
        saveFile.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));

        Action exit = new AbstractAction("Salir"){
            public void actionPerformed(ActionEvent event) {
                main.frame.dispatchEvent(new WindowEvent(main.frame, WindowEvent.WINDOW_CLOSING));
            }
        };
        exit.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));

        fileMenu.add(new JMenuItem(newFile));
        fileMenu.add(new JMenuItem(openFile));
        fileMenu.add(new JMenuItem(saveFile));
        fileMenu.add(new JMenuItem(exit));


        JMenu editMenu = new JMenu("Acciones");
        Action undo = new AbstractAction("Rehacer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.undo();
            }
        };
        undo.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));

        Action redo = new AbstractAction("Deshacer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.redo();
            }
        };
        redo.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));

        Action delete = new AbstractAction("Eliminar") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.delete();
            }
        };
        delete.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));

        Action copy = new AbstractAction("Copiar") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.copy();
            }
        };
        copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));

        Action paste = new AbstractAction("Pegar") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.paste();
            }
        };
        paste.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));

        editMenu.add(new JMenuItem(undo));
        editMenu.add(new JMenuItem(redo));
        editMenu.add(new JMenuItem(delete));
        editMenu.add(new JMenuItem(copy));
        editMenu.add(new JMenuItem(paste));

        JMenu configureMenu = new JMenu("Configuraciones");
        Action setBkg = new AbstractAction("Cambiar fondo de imagen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setBkgImage();
            }
        };
        Action clearBkg = new AbstractAction("Establecer fondo claro") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.clearBkgImage();
            }
        };

        configureMenu.add(new JMenuItem(setBkg));
        configureMenu.add(new JMenuItem(clearBkg));

        JMenu helpMenu = new JMenu("Ayuda");
        Action help = new AbstractAction("Guia de usuario") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("宋体", Font.PLAIN, 20)));
                JOptionPane.showMessageDialog(null,
                        "1. Seleccionar un tipo de forma y dibujar.\n" +
                        "2. Para dibujar una recta se debe selecionar 2 figuras.\n" +
                        "3. Para copiar o eliminar primero se debe tener la figura seleccionada.\n" +
                        "4. Para agregar texto a una figura se debe hacer doble click sobre la misma.\n" +
                        "5. La fuente y el tamaño se cambian para todos los textos del canvas\n\n" +
                        "Creditos: \n"+ 
                        "* FlowChar Drawer: WangGuox1n (https://github.com/WangGuox1n/FlowChartMaker/)\n" +
                        "* Socket Painter: Brian Weir (https://github.com/bweir27/SocketPainter)",
                        "Guia de usuario",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        helpMenu.add(new JMenuItem(help));


        this.add(fileMenu);
        this.add(configureMenu);
        this.add(editMenu);
        this.add(helpMenu);
    }
}
