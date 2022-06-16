package Visualisation;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;


public class Display extends Applet implements MouseListener {

    private SimpleUniverse universe = null;
    public Canvas3D canvas = null;
    private TransformGroup viewtrans = null;

    private Transform3D t3dstep = new Transform3D();
    private Matrix4d matrix = new Matrix4d();

    private int numOfObjects = 0;
    //private ArrayList<TransformGroup> tgArr;
    //private ArrayList<Transform3D> tg3Arr;
    //private ArrayList<PartOfRobot> parts;


    public Display(ToVisualise... parts) {

        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse
                .getPreferredConfiguration();

        canvas = new Canvas3D(config);
        add("Center", canvas);
        universe = new SimpleUniverse(canvas);

        BranchGroup scene = createSceneGraph(parts);
        universe.getViewingPlatform().setNominalViewingTransform();

        universe.getViewer().getView().setBackClipDistance(150.0);

        //canvas.addKeyListener(this);

        universe.addBranchGraph(scene);
    }

    public TransformGroup objTrans;

    public  static Point3d viewersLocation = new Point3d(100,0,0);
    public  static Point3d gazePoint = new Point3d(0,0,0);  //point viewer is looking at

// and initialize as follows:

    public   TransformGroup viewingTransformGroup ;
    private Transform3D viewingTransform =  new Transform3D();;
    public void updateViewerGeometryJ3D(Vector3d change) {
        viewersLocation.add(change);
        Point3d eye = viewersLocation;
        Point3d center = gazePoint;
        Vector3d up = new Vector3d(0,0,0);
        viewingTransform.lookAt(eye, center, up);
        viewingTransform.invert();
        viewingTransformGroup.setTransform(viewingTransform);
    }

    private BranchGroup createSceneGraph(ToVisualise... parts) {
        BranchGroup objRoot = new BranchGroup();

        BoundingSphere bounds = new BoundingSphere(new Point3d(), 10000.0);

        viewingTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();
        viewtrans = universe.getViewingPlatform().getViewPlatformTransform();


       viewingTransform = new Transform3D();

        objTrans = new TransformGroup( );
        objTrans.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
        objTrans.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );

//        for(ToVisualise part : parts) {
//            part.setter(matrix,t3dstep);
//            objTrans.addChild(createObject(part));
//        }
//        for(int i = parts.length - 1; i > 0;i--) {
//                parts[i - 1].tg.addChild(parts[i].tg);
//
//        }
        		Transform3D yAxis = new Transform3D( );
        		Alpha rotationAlpha = new Alpha( -1, Alpha.INCREASING_ENABLE,
        			0, 0,
        			4000, 0, 0,
        			0, 0, 0 );

        		RotationInterpolator rotator = new RotationInterpolator( rotationAlpha, objTrans, yAxis, 0.0f, (float) Math.PI*2.0f );
        		///rotator.setSchedulingBounds( createApplicationBounds( ) );
        		//objTrans.addChild( rotator );
        //

        objRoot.addChild(createLight());

        Background background = new Background();
        background.setColor(0.5f, 0.5f, 0.5f);
        background.setApplicationBounds(bounds);
        objRoot.addChild(background);
//

        addHead( objTrans );
//

        objRoot.addChild(objTrans);

        return objRoot;
    }

    //
    public TransformGroup rbase, rplatform, rhand,rarm,rmainarm,barrier;

    private void addHead( Group parentGroup )
    {
        // add a cylinder for the Neck
        //TransformGroup tgNeck = addLimb( parentGroup, "Neck", 0.05, 0.2, 0.0, 0.0 );
        rplatform = addLimb(parentGroup,"model/platform.obj", 180, 0,0,3,new Vector3d(0,0,0));
        rbase = addLimb(rplatform,"model/base.obj", 0,90,0, 1,new Vector3d(0,1.5,1.5));
        rmainarm = addLimb(rbase,"model/mainarm.obj", 90, 0,0,1.7,new Vector3d(1,0,-1.85));
        rarm = addLimb(rmainarm,"model/arm.obj", 180, 90,0,2.3,new Vector3d(0,-2.1,1.1));
        rhand = addLimb(rarm,"model/hand.obj", 0, 0,-45, 0.6,new Vector3d(4.5,10,0));
        //fix
        barrier = createLimb("model/barrier.obj", 180, 90,0, 1,new Vector3d(0,-1,4));
        objTrans.addChild(barrier);
    }

    private TransformGroup addLimb( Group parentGroup, String path, double rotateX, double rotateY,double rotateZ,double scale, Vector3d move )
    {
        // create the rotator
        TransformGroup tgJoint = new TransformGroup( );
        tgJoint.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
        tgJoint.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );

        // add a rotator if necessary
      /*
            Transform3D xAxis = new Transform3D( );
            xAxis.rotX( Math.PI/2.0 );
            Alpha rotationAlpha = new Alpha( -1, Alpha.INCREASING_ENABLE,
                    0, 0,
                    4000, 0, 0,
                    0, 0, 0 );

            RotationInterpolator rotator = new RotationInterpolator( rotationAlpha, tgJoint, xAxis, (float) rotMin, (float) rotMax );
            rotator.setSchedulingBounds(new BoundingSphere( new Point3d( 0.0,0.0,0.0 ), 100.0 ));
          //  tgJoint.addChild( rotator );
        */

        // create a cylinder using length and radius
        tgJoint.addChild( createLimb( path, rotateX,rotateY,rotateZ, scale,move) );

        // create the joint (the *next* TG should
        // be offset by the length of this limb)
        TransformGroup tgOffset = new TransformGroup( );
        tgOffset.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
        tgOffset.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );

        Transform3D t3d = new Transform3D( );
        t3d.setTranslation( new Vector3d( 0, 0, 0 ) );
        tgOffset.setTransform( t3d );

        tgJoint.addChild( tgOffset );
        parentGroup.addChild( tgJoint );

        // return the offset TG, so any child TG's will be added
        // in the correct position.
        return tgOffset;
    }


    TransformGroup createLimb( String path,double rotateX,double rotateY,double rotateZ,double scale,Vector3d move ) {

        TransformGroup tg = new TransformGroup();
        Transform3D t3d = new Transform3D();
        t3d.setTranslation(new Vector3d(0, 0, 0));
        t3d.setScale(scale);
        tg.setTransform(t3d);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        //
        t3dstep.rotX(Math.PI * rotateX / 180);
        tg.getTransform(t3d);
        t3d.get(matrix);
        //t3d.setTranslation(vector);
        t3d.mul(t3dstep);
        t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
        tg.setTransform(t3d);
        //
        t3dstep.rotY(Math.PI * rotateY / 180);
        tg.getTransform(t3d);
        t3d.get(matrix);
        //t3d.setTranslation(vector);
        t3d.mul(t3dstep);
        t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
        tg.setTransform(t3d);
        //
        t3dstep.rotZ(Math.PI * rotateZ / 180);
        tg.getTransform(t3d);
        t3d.get(matrix);
        //t3d.setTranslation(vector);
        t3d.mul(t3dstep);
        t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
        tg.setTransform(t3d);
        //
        t3dstep.set(move);
        tg.getTransform(t3d);
        t3d.mul(t3dstep);
        tg.setTransform(t3d);
        //

        ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
        Scene s = null;

        // нужно убрать строки начинающиеся с o
        File obj = new java.io.File(path);

        try {
            s = loader.load(obj.toURI().toURL());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
        tg.addChild(s.getSceneGroup());
        return tg;
    }

//    private void moveOneVector(TransformGroup tg,Transform3D t3d,double x, double y, double z){
//        t3dstep.set(new Vector3d(x, y, z));
//        tg.getTransform(t3d);
//        t3d.mul(t3dstep);
//        tg.setTransform(t3d);
//    }
//
//    private void rotateOneVector(TransformGroup tg,Transform3D t3d,double x, double y, double z){
//        t3dstep.rotY(Math.PI / 32);
//        tg.getTransform(t3d);
//        t3d.get(matrix);
//        t3d.setTranslation(new Vector3d(0.0, 0.0, 0.0));
//        t3d.mul(t3dstep);
//        t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
//        tg.setTransform(t3d);
//    }
//
//    private void moveAll(double x, double y, double z){
//        for (int i = 0; i < numOfObjects; i++) {
//            moveOneVector(tgArr.get(i),tg3Arr.get(i),x,y,z);
//        }
//    }
//
//    private void rotateAll(double x, double y, double z){
//        for (int i = 0; i < numOfObjects; i++) {
//            rotateOneVector(tgArr.get(i),tg3Arr.get(i),x,y,z);
//        }
//    }

    private Light createLight() {
        DirectionalLight light = new DirectionalLight(true, new Color3f(1.0f,
                1.0f, 1.0f), new Vector3f(-0.3f, 0.2f, -1.0f));

        light.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));

        return light;
    }

//    public void run() {
//        //Display applet = new Display();
//        Frame frame = new MainFrame(this, 800, 600);
//
//        //For test
//       // Display testGlove = new Display();
//       // Frame frame2 = new MainFrame(applet, 800, 600);
//    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

//    @Override
//    public void mouseWheelMoved(MouseEvent e) {
//
//    }
}
