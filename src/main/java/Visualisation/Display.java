package Visualisation;

import java.awt.*;

import javax.swing.JFrame;

public class Display extends Canvas implements Runnable{

    private static final long serialVersionUID = 1L;

    private Thread thread;
    private JFrame frame;
    private static String title = "3D Rendering";
    private static final int WIGHT = 800;
    private static final int HEIGHT = 600;
    private static boolean running = false;


    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long time = System.currentTimeMillis();
        final double ns = 1e10 / 60;
        double delta = 0;
        int frames = 0;

        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) /ns;

            render();
            update();
        }
    }

    private void render() {

    }

    private void update(){

    }

    public Display() {
        this.frame = new JFrame();
        Dimension size = new Dimension(WIDTH,HEIGHT);
        this.setPreferredSize(size);
    }
    public synchronized void start() {
        running = true;
        this.thread = new Thread(this,"Display");
        this.thread.start();
    }

    public  static void main(String[] args) {
        Display display = new Display();
        display.frame.setTitle(title);
        display.frame.add(display);
        display.frame.pack();
        display.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display.frame.setLocationRelativeTo(null);
        display.frame.setVisible(true);
        display.start();
    }
    public synchronized void stop() throws InterruptedException {
        running = false;
        this.thread.join();

    }
}

