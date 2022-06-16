package Arduino;

import Visualisation.ToVisualise;

import javax.vecmath.Vector3d;

public class Servo {
    private int portNum;
    private Vector3d placement;
    private int degree;

    ToVisualise robot;
    Servo connectedServo;

    public Servo(int port, ToVisualise part, Servo connectedServo) {
        placement = new Vector3d(0.0,0.0,0.0);
        this.portNum = port;
        robot = part;
        this.connectedServo = connectedServo;
    }

    public void rotate(double degreeX,double degreeY,double degreeZ) {                //servo rotates himself
        robot.rotate(placement, degreeX, degreeY, degreeZ);
        connectedServo.move(placement, degreeX, degreeY, degreeZ);
    }

    public Vector3d calculatePlacement(Vector3d vector) {
        //some math
        return placement;
    }

    public void move(Vector3d vector, double degreeX,double degreeY,double degreeZ) { //other servo rotates and placement changes
        robot.rotate(vector,degreeX, degreeY, degreeZ);
        if (connectedServo != null) {
            connectedServo.calculatePlacement(vector);
            connectedServo.move(vector,degreeX, degreeY, degreeZ);
        }
    }

    public void setDegree(int degree){
        this.degree = degree;
    }

    public int getDegree(){
        return degree;
    }

}
