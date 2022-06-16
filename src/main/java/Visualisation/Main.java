package Visualisation;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Point3d;
import java.awt.*;

//JFrame class inherits the code from the Swing JPanel class.
public class Main extends JPanel {

    //The constructor method
    public Main() {

        //Set the layout, this is just a standard thing.
        setLayout(new BorderLayout());

        //Get the preferred graphics configuration for the default screen
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        //Create a Canvas3D using the preferred configuration
        Canvas3D canvas = new Canvas3D(config);

        //Create a Simple Universe with view branch
        SimpleUniverse universe = new SimpleUniverse(canvas);

        //Create the root of the branch graph
        BranchGroup group = new BranchGroup();

        //Create a new Transform group
        TransformGroup tGroup = new TransformGroup();

        //Allows the cube to the rotated
        tGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        //Add the transform group to the BranchGroup
        group.addChild(tGroup);

        //Add a new cube to the
        tGroup.addChild(new ColorCube(0.25f));

        //Create Alpha object for the rotation of the cube
        Alpha spinAlpha = new Alpha(-1, 1000);

        //Create object for the spin of the cube passing the Alpha value and the transform group to target
        RotationInterpolator spin = new RotationInterpolator(spinAlpha, tGroup);

        //Set the bounds for the spin
        spin.setSchedulingBounds(new BoundingSphere(new Point3d(), 1000));

        //Add the spin to the transform group
        tGroup.addChild(spin);

        //Add the canvas into the center of the screen
        add("Center", canvas);

        //This will move the ViewPlatform back so the objects in the scene can be viewed
        universe.getViewingPlatform().setNominalViewingTransform();

        //Set the render distance of objects
        universe.getViewer().getView().setBackClipDistance(100.0);

        //Add the branch into the Universe
        universe.addBranchGraph(group);

    }

    //Main method
    public static void main(String[] args) {
        //Set the No Erase Background property to true
        System.setProperty("sun.awt.noerasebackground", "true");

        //Disable the JPopupMenu
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        //Create an instance for the ToolTipManager
        ToolTipManager ttm = ToolTipManager.sharedInstance();

        //Disable the ToolTipManager
        ttm.setLightWeightPopupEnabled(false);

        //Create object instance
        Main main = new Main();

        //Create new JFrame instance
        JFrame frame = new JFrame("Just Java 3D - Spinning Cube!");

        //Set the size of the JFrame
        frame.setSize(200, 200);

        //The frame will now hold the main canvas
        frame.getContentPane().add(main);

        //Make sure the frame is actually visible
        frame.setVisible(true);
    }
}