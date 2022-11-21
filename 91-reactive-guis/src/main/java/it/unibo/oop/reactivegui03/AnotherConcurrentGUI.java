package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final int WAITING_TIME= 10000;
    private List<JButton> _buttons =new ArrayList<>();
    private final JLabel display = new JLabel();
    final Agent _agent=new Agent();
    final Stopper _stopper=new Stopper();

    /**
     * Creates a new JFrame
     */
    public AnotherConcurrentGUI(){
        super() ;
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel main_panel=new JPanel();
        

       
        final JButton stop = new JButton("stop");
        final JButton up=new JButton("up");
        final JButton down=new JButton("down");

        this._buttons.add(stop);
        this._buttons.add(up);
        this._buttons.add(down);


        stop.addActionListener((e)->_agent.stopCounting());
        up.addActionListener((e)-> _agent.upCounting());
        down.addActionListener((e)-> _agent.downCounting());

        this._buttons.forEach((elem)->main_panel.add(elem));
        main_panel.add(this.display);

        //Starting the GUI
        new Thread(_agent).start();
        new Thread(_stopper).start();
        this.setContentPane(main_panel);
        this.setVisible(true);
    }

    public void disableButtons(){
        
        this._buttons.forEach((elem)->{
            elem.setEnabled(false);
        });

    }

    public void stop(){
        this._agent.stopCounting();
    }


   
    public class Agent implements Runnable{
        private boolean stop;
        private int counter=0;
        private boolean up=true;

        @Override
        public void run(){
            while (!this.stop) {
                try {
                    // The EDT doesn't access `counter` anymore, it doesn't need to be volatile 
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if(this.up)
                        this.counter++;
                    else
                        this.counter--;
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
            AnotherConcurrentGUI.this.disableButtons();
        }

        public void upCounting(){
            this.up=true;
        }

        public void downCounting(){
            this.up=false;
        }
        
    }

    public class Stopper implements Runnable{
        @Override
        public void run(){
            try{
                Thread.sleep(AnotherConcurrentGUI.WAITING_TIME);
            }catch(InterruptedException excp){
                excp.printStackTrace();
            }
            AnotherConcurrentGUI.this.stop();
        }
    }
}
