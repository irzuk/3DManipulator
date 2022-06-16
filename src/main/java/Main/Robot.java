package Main;


import Arduino.BlueTooth;
import Arduino.Servo;
import Visualisation.Display;
import Visualisation.Serial;
import Visualisation.ToVisualise;
import com.sun.j3d.utils.applet.MainFrame;
import jssc.SerialPortException;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.sql.rowset.serial.SerialException;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Robot implements KeyListener {

    //TODO: need connect each servo with Robot part are effected
    //Bluetooth on Manipulator is blocking
//TODO: make thread for the taking servos data and ultrasonic
//if block is too close, sent notification to the desktop and stop the drivers
//TODO: make thread that take data from gyroscope and send to the manipulator with errors
    Display display;

    ArrayList<Servo> manipulator;

    //add scale
    static ToVisualise barrier = new ToVisualise("model/barrier.obj", new Vector3d(0.0, 0.0, 10.0), new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f));
    static ToVisualise Rplatform = new ToVisualise("model/platform.obj", new Vector3d(0.0, 0.0, 0.0), new AxisAngle4f(0.0f, 1.0f, 1.0f, 0.0f));
    static ToVisualise Rbase = new ToVisualise("model/base.obj", new Vector3d(0.0, 5.0, 0.0), new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f));
    static ToVisualise Rmainarm = new ToVisualise("model/mainarm.obj", new Vector3d(0.0, 6.0, 0.0), new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f));
    static ToVisualise Rarm = new ToVisualise("model/arm.obj", new Vector3d(0.0, 6.0, 1.0), new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f));
    static ToVisualise Rhand = new ToVisualise("model/hand.obj", new Vector3d(0.0, 6.0, 5.0), new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f));
    ///
    static Servo handS = new Servo(1, Rhand, null);
    static Servo armS = new Servo(2, Rarm, handS);
    static Servo mainarmS = new Servo(3, Rmainarm, armS);
    static Servo baseS = new Servo(4, Rbase, mainarmS);
    // + servo for claw?

    BlueTooth BDrive = new BlueTooth("/dev/cu.HC-06-DevB-2", "Drive", 0);
    //  BlueTooth BManipulator = new BlueTooth("/dev/cu.HC-06-DevB-1", "Manipulator", 8);
    // BlueTooth BManipulator = new BlueTooth("dev/cu.usbserial-1430", 8, "Manipulator");
    // BlueTooth BGlove = new BlueTooth("/dev/cu.HC-06-DevB", "Glove",2);
    //BlueTooth BGlove = new BlueTooth("dev/cu.usbserial-1430", 3);

    private static int ultrasonicDisance = 0;

    private int[] previousData, supposedData;

    //private int baseServoMin = , baseServoMax = ;
    //private int baseServoMin = , baseServoMax = ;
    //private int baseServoMin = , baseServoMax = ;
    //private int baseServoMin = , baseServoMax = ;
    //private int baseServoMin = , baseServoMax = ;

    private int gyroscopeXmin, gyroscopeXmax;
    private int gyroscopeYmin, gyroscopeYmax;
    private int gyroscopeZmin, gyroscopeZmax;

    private final double[] lenght = {13.5, 20};

    private final double LearningRate = 1;
    private final double SamplingDistance = 1;

    public boolean isReadyToVisualise;
    public boolean isReadyToWrite;

    public int[] data = new int[8];
    public int[] angs = new int[2];
    // public Lock dataLock = new ReentrantLock();
    // public final Condition readyToVisualise = dataLock.newCondition();
    // public final Condition readyToWrite = dataLock.newCondition();

    Robot() throws InterruptedException {  //get pc settings
        //stabilization?
        //обратная задача кинематики для стабилизации
        manipulator = new ArrayList<Servo>();

        // Servo base = new Servo(.getMatrix(),.gett3d(),);
        this.manipulator.add(handS);
        this.manipulator.add(armS);
        this.manipulator.add(mainarmS);
        this.manipulator.add(baseS);
        data[0] = 90;
        data[1] = 90;
        data[2] = 180;
        data[3] = 90;
        data[4] = 90;
    }

    private static void changeUltrasonic(int data) {
        // check the scale
        System.out.println("Ultrasonic");
        ultrasonicDisance = data;
    }

    private TransformGroup viewingTransformGroup;
    private Transform3D viewingTransform = new Transform3D();

    Point3d target = new Point3d(20.0, 0.0, 13.5);

    static public void main(String[] args) throws InterruptedException, SerialException, SerialPortException, IOException {
        Robot robot = new Robot();
        Display display = new Display(barrier, Rplatform, Rbase, Rmainarm, Rarm, Rhand);
        display.canvas.addKeyListener(robot);

        Rplatform.tg = display.objTrans;
        Rbase.tg = display.rplatform;
        Rmainarm.tg = display.rbase;
        Rarm.tg = display.rmainarm;
        Rhand.tg = display.rarm;
        barrier.tg = display.barrier;
        robot.viewingTransformGroup = display.viewingTransformGroup;
        robot.updateViewerGeometryJ3D();
        Rhand.move(new Vector3d(-0.3, 2, 0));

        Frame frame = new MainFrame(display, 800, 600);
        Thread.sleep(500);

        //init glove
        //{
        //     int[] startData = robot.BGlove.read(), Data;
        //new window for debug - shows the arm placement
        // Frame frame = new MainFrame(???, 800, 600);
     /* //TODO:      while(diff(recievedData,startData) ) {
                recievedData = robot.BGlove.read();
                (x,y,z) = calculate(recievedData);
                robot.gyroscopeXmax = Math.max(robot.gyroscopeXmax,);
                robot.gyroscopeYmax = Math.max(robot.gyroscopeYmax,);
                robot.gyroscopeZmax = Math.max(robot.gyroscopeZmax,);
                robot.gyroscopeXmin = Math.min(robot.gyroscopeXmin,);
                robot.gyroscopeYmin = Math.min(robot.gyroscopeYmin,);
                robot.gyroscopeZmin = Math.min(robot.gyroscopeZmin,);

            }
        }*/

        //robot.BManipulator.read();
        //
        int[] dataPlatformAngels = new int[2];
        Serial s = null;
        Serial glove = null;
        try {
            //manipulator
            PipedWriter pipedWriter = new PipedWriter();
            PipedReader pipedReader = new PipedReader(pipedWriter);
            s = new Serial("/dev/cu.HC-06-DevB-1", 9600, (char) 8, 1, 0, true, true);
            s.setPipe(pipedWriter);

            PipedWriter pipedGloveW = new PipedWriter();
            PipedReader pipedGlove = new PipedReader(pipedGloveW);
            glove = new Serial("/dev/cu.HC-06-DevB", 9600, (char) 8, 1, 0, true, true);
            glove.setPipe(pipedGloveW);

            int[] recievedData = new int[8];
            recievedData[0] = 90; //base
            recievedData[1] = 90; // forward
            recievedData[2] = 180; //vertical
            recievedData[3] = 90;
            recievedData[4] = 90;
            int[] recievedDataPrev = new int[8];
            recievedDataPrev[0] = 90; //base
            recievedDataPrev[1] = 90; // forward
            recievedDataPrev[2] = 180;
            int[] prevSentData = new int[8];
            prevSentData[0] = 90; //base
            prevSentData[1] = 90; // forward
            prevSentData[2] = 180; //vertical
            prevSentData[3] = 90;
            int counter = 0;
            while (true) {
                s.write(1 + "\n");

                System.out.println("Manipulator:" + Arrays.toString(robot.data));

                prevSentData[0] = robot.data[0];
                prevSentData[1] = robot.data[1];
                prevSentData[2] = robot.data[2];

                s.write((robot.data[0]) + "\n");
                s.write(robot.data[1] + "\n");
                s.write(robot.data[2] + "\n");
                s.write(robot.data[3] + "\n");
                s.write(robot.data[4] + "\n");

                recievedDataPrev[1] = recievedData[1];
                recievedDataPrev[0] = recievedData[0];
                recievedDataPrev[2] = recievedData[2];

                int count = 0;
                int num;
                while (count < 8) {
                    if (!pipedReader.ready()) {
                        Thread.sleep(200);
                        continue;
                    }
                    num = pipedReader.read();
                    recievedData[count] = num;
                    count++;
                }
                { //visualisation

                    //check if servo moved!
                    if(!robot.movedCorrect(recievedData,prevSentData)) {
                     robot.showError("Cant move manipulator correctly!");}

                    System.out.println("Received Manipulator:" + Arrays.toString(recievedData));
                    Point3d p = robot.taskKinematic(robot.data, robot.lenght);
                    System.out.println("Point: " + p);
                    //robot.target = p;

                    barrier.move(new Vector3d(0, 0, (recievedData[5] - ultrasonicDisance) / 3));
                    ultrasonicDisance = recievedData[5];


                    if (Math.abs(recievedDataPrev[1] - recievedData[1]) > 0) {
                        System.out.println("Main arm moved for: " + (recievedDataPrev[1] - recievedData[1]));
                        Rmainarm.rotate(new Vector3d(1.5, 1.8, 0), 0, 0, recievedDataPrev[1] - recievedData[1]);
                        Rarm.rotate(new Vector3d(2, 4.6, 0), 0, 0, recievedData[1] - recievedDataPrev[1]);
                    }
                    if (Math.abs(recievedDataPrev[2] - recievedData[2]) > 0) {
                        System.out.println("Arm moved for: " + (recievedDataPrev[2] - recievedData[2]));
                        Rarm.rotate(new Vector3d(2, 4.6, 0), 0, 0, recievedDataPrev[2] - recievedData[2]);
                    }
                    if (Math.abs(recievedDataPrev[0] - recievedData[0]) > 0) {
                        System.out.println("Base moved for: " + (recievedDataPrev[0] - recievedData[0]));
                        Rbase.rotate(new Vector3d(1.65, 0, 0), 0, recievedDataPrev[0] - recievedData[0], 0);
                    }
                }


                //read info from glove

                int[] data2 = new int[3];
                int count2 = 0;
                int num2;
                if (glove.isOpened()) {
                    while (count2 < 3) {
                        if (!pipedGlove.ready()) {
                            Thread.sleep(200);
                            continue;
                        }
                        num2 = pipedGlove.read();
                        data2[count2] = num2;
                        count2++;
                    }
                }
                System.out.println("Glove" + Arrays.toString(data2));

               // robot.target.y += (float)(data2[0] - robot.angs[0]) / 5;
               // robot.target.x += (float)(data2[1] - robot.angs[1]) / 5;

                robot.angs[0] = data2[0];
                robot.angs[1] = data2[1];
//                if (data2[2] == 1) {
//                    robot.data[] = ;
//                } else {
//                    robot.data[] = ;
//                }


                //stabilization
                //angs[] = data[6-7]

               // robot.stabilize(robot.angs[0],robot.angs[1]);

                robot.inverseCalculate(robot.target, robot.data);

                System.out.println("Result angels" + Arrays.toString(robot.data));
                System.out.println("--------------------------------");
            }
        } finally {
            glove.dispose();
            s.dispose();
        }
    }

    private void stabilize(double angleX, double angleY) {

        double X = target.x * Math.cos(Math.toRadians(angleX - angs[0])) - target.y * Math.sin(Math.toRadians(angleX - angs[0]));
        double Y = target.x * Math.sin(Math.toRadians(angleX - angs[0])) + target.y * Math.cos(Math.toRadians(angleX - angs[0]));
        target.x = X;
        target.y = Y;
      /*  double X = target.x * Math.cos(0.2) - target.y * Math.sin(0.2);
        double Y = target.x * Math.sin(0.2) + target.y * Math.cos(0.2);
        target.x = X;
        target.y = Y;*/

    }

    private void inverseCalculate(Point3d target, int[] degrees) {
        degrees[0] = (int) Math.round(Math.toDegrees(Math.atan2(target.y, target.x)));
        degrees[1] = (int) Math.round(Math.toDegrees(Math.atan2(Math.sqrt(target.x * target.x + target.y * target.y), target.z)));
        degrees[1] -= (int) Math.round(Math.toDegrees(Math.acos((target.x * target.x + target.y * target.y + target.z * target.z + lenght[0] * lenght[0] - lenght[1] * lenght[1]) / (2 * lenght[0] * Math.sqrt(target.x * target.x + target.y * target.y + target.z * target.z)))));
        degrees[2] = (int) Math.round(Math.toDegrees(Math.asin((lenght[0] * lenght[0] + lenght[1] * lenght[1] - target.x * target.x - target.y * target.y - target.z * target.z) / (2 * lenght[0] * lenght[1]))));
        degrees[0] *= -1;
        degrees[0] += 90;
        //degrees[1] *= -1;
        degrees[1] += 90;
        degrees[2] *= -1;
        degrees[2] += 180;
    }


    private double distanceFromTarget(Point3d target, int[] angel) {
        return target.distance(taskKinematic(angel, lenght));
    }

    private boolean movedCorrect(int[] data,int[] data2) {
        for (int i = 0; i < data.length; i++) {
            if (Math.abs(data[i] - data2[i]) > 1) {
                return false;
            }
        }
        return true;
    }

    private Point3d taskKinematic(int[] d, double[] l) {
        int[] degree = new int[3];
        degree[0] = 90 - d[0];
        degree[1] = d[1] - 90;
        degree[2] = 180 - d[2];
        //System.out.println(Arrays.toString(degree));
        return new Point3d(Math.cos(Math.toRadians(degree[0])) * (l[0] * Math.sin(Math.toRadians(degree[1])) + l[1] * Math.cos(Math.toRadians(degree[1] + degree[2])))
                , Math.sin(Math.toRadians(degree[0])) * (l[0] * Math.sin(Math.toRadians(degree[1])) + l[1] * Math.cos(Math.toRadians(degree[1] + degree[2]))),
                l[0] * Math.cos(Math.toRadians(degree[1])) - l[1] * Math.sin(Math.toRadians(degree[1] + degree[2])));
    }


    public double partialGradient(Point3d target, int[] angles, int i) {
        // Сохраняет угол,
        // который будет восстановлен позже
        int angle = angles[i];
        // Градиент: [F(x+SamplingDistance) - F(x)] / h
        double f_x = distanceFromTarget(target, angles);
        angles[i] += SamplingDistance;
        double f_x_plus_d = distanceFromTarget(target, angles);
        double gradient = (f_x_plus_d - f_x) / SamplingDistance;
        // Восстановление
        angles[i] = angle;

        return gradient;
    }

    public void inverseKinematics(Point3d target, int[] angles) {
       /* if (distanceFromTarget(target, angles) < DistanceThreshold)
            return;

        for (int i = Joints.Length -1; i >= 0; i --)
        {
            // Градиентный спуск
            // Обновление : Solution -= LearningRate * Gradient
            double gradient = partialGradient(target, angles, i);
            angles[i] -= LearningRate * gradient;

            // Ограничение
            angles[i] = Mathf.Clamp(angles[i], Joints[i].MinAngle, Joints[i].MaxAngle);

            // Преждевременное завершение
            if (distanceFromTarget(target, angles) < DistanceThreshold)
                return;
        }*/
    }

    public static Point3d viewersLocation = new Point3d(15, 15, 15);


    @Override
    public synchronized void keyTyped(KeyEvent e) { // add symbol INTERRUPT if ultrasonic find some shit
        char key = e.getKeyChar();

        if (key == 's') {
            if (ultrasonicDisance <= 5) {
                showError("Some barrier. Can't move forward!");
                ///
            } else {
                //barrier.move(new Vector3d(0.0, 0.0, 0.1));
                BDrive.write("F");
                System.out.println("F");
            }
        }
        if (key == 'w') {
            //barrier.move(new Vector3d(0.0, 0.0, -0.1));
            BDrive.write("B");
            System.out.println("B");
        }

        if (key == 'a') {
            //Rplatform.rotate(new Vector3d(0.1,0.0,0.0),0.5);
            BDrive.write("L");
            System.out.println("L");
        }

        if (key == 'd') {
            BDrive.write("R");
            System.out.println("R");
        }

        ///test movement
        if (key == 'b') {
            target.x += 1;
        }
        if (key == 'n') {
            target.x -= 1;
        }
        if (key == 'g') {
            target.y += 1;
        }
        if (key == 'h') {
            target.y -= 1;
        }
        if (key == 't') {
            target.z += 1;
        }
        if (key == 'y') {
            target.z -= 1;
        }
        if (key == 'u') {
            ultrasonicDisance += 2;
        }
        if (key == 'i') {
            ultrasonicDisance -= 2;
        }
        /// placement of camera
        if (key == 'o') {
            System.out.println(key + "left");
            double X = viewersLocation.x * Math.cos(0.2) - viewersLocation.y * Math.sin(0.2);
            double Y = viewersLocation.x * Math.sin(0.2) + viewersLocation.y * Math.cos(0.2);
            viewersLocation.x = X;
            viewersLocation.y = Y;
            updateViewerGeometryJ3D();
        }
        if (key == 'l') {
            double X = viewersLocation.x * Math.cos(-0.2) - viewersLocation.y * Math.sin(-0.2);
            double Y = viewersLocation.x * Math.sin(-0.2) + viewersLocation.y * Math.cos(-0.2);
            viewersLocation.x = X;
            viewersLocation.y = Y;
            updateViewerGeometryJ3D();
        }
        if (key == 'k') {
            double X = viewersLocation.x * Math.cos(0.2) - viewersLocation.z * Math.sin(0.2);
            double Z = viewersLocation.x * Math.sin(0.2) + viewersLocation.z * Math.cos(0.2);
            viewersLocation.x = X;
            viewersLocation.z = Z;
            updateViewerGeometryJ3D();
        }
        if (key == ';') {
            double X = viewersLocation.x * Math.cos(-0.2) - viewersLocation.z * Math.sin(-0.2);
            double Z = viewersLocation.x * Math.sin(-0.2) + viewersLocation.z * Math.cos(-0.2);
            viewersLocation.x = X;
            viewersLocation.z = Z;
            updateViewerGeometryJ3D();
        }
        if (key == ',') {
            viewersLocation.scale(0.9);
            updateViewerGeometryJ3D();
        }
        if (key == '.') {
            viewersLocation.scale(1.1);
            updateViewerGeometryJ3D();
        }
        if (key == 'm') {
            target.x += 1;
        }
        if (key == 'n') {
            target.x -= 1;
        }
        if (key == 'j') {
            target.y += 1;
        }
        if (key == 'h') {
            target.y -= 1;
        }
        if (key == 'u') {
            target.z += 1;
        }
        if (key == 'y') {
            target.z -= 1;
        }
        //test plafform
        if (key == 'r') {
            angs[0] += 1;
        }
        if (key == 't') {
            angs[0] -= 1;
        }
        if (key == 'f') {
            angs[1] += 1;
        }
        if (key == 'g') {
            angs[1] -= 1;
        }
    }


    public void updateViewerGeometryJ3D() {
        Point3d eye = viewersLocation;
        Point3d center = new Point3d(0, 2, 0);
        Vector3d up = new Vector3d(0, 1, 0);
        viewingTransform.lookAt(eye, center, up);
        viewingTransform.invert();
        viewingTransformGroup.setTransform(viewingTransform);
    }


    private void updateViewing() {
        ArrayList<Servo> newData = manipulator;

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void showError(String error) {
        System.out.println(error);
    }
}


class GloveVisualisation extends Frame {

}