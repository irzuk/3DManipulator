package Visualisation;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class ToVisualise{
     public String path;

     protected Transform3D t3dstep;
     protected Matrix4d matrix;

     protected Vector3d currentPlacement;
     protected AxisAngle4f currentAngle;

     public ToVisualise(){

     }

     public TransformGroup tg;
     protected Transform3D t3d;

     public ToVisualise(String path,Vector3d currentPlacement,AxisAngle4f currentAngle) {
         this.path = path;
         this.currentPlacement = currentPlacement;
         this.currentAngle = currentAngle;
         t3dstep = new Transform3D();
         t3d = new Transform3D();
         matrix = new Matrix4d();
     }

    public void rotateMove(Vector3d vector, double degreeX, double degreeY, double degreeZ){


        t3dstep.rotX(Math.PI*degreeX/180);
        tg.getTransform(t3d);
        t3d.get(matrix);
        t3d.setTranslation(vector);
        t3d.mul(t3dstep);
        t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
        tg.setTransform(t3d);

        t3dstep.rotY(Math.PI*degreeY/180);
        tg.getTransform(t3d);
        t3d.get(matrix);
        t3d.setTranslation(vector);
        t3d.mul(t3dstep);
        t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
        tg.setTransform(t3d);

        t3dstep.rotZ(Math.PI*degreeZ/180);
        tg.getTransform(t3d);
        t3d.get(matrix);
        t3d.setTranslation(vector);
        t3d.mul(t3dstep);
        t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
        tg.setTransform(t3d);
    }

     public void rotate(Vector3d vector, double degreeX,double degreeY,double degreeZ){
        //System.out.printf("Diff angles: %f %f %f \n",degreeX,degreeY,degreeZ);

         t3dstep.set(vector);
         tg.getTransform(t3d);
         t3d.mul(t3dstep);
         tg.setTransform(t3d);

         t3dstep.rotX(Math.PI*degreeX/180);
         tg.getTransform(t3d);
         t3d.get(matrix);
         t3d.setTranslation(vector);
         t3d.mul(t3dstep);
         t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
         tg.setTransform(t3d);

         t3dstep.rotY(Math.PI*degreeY/180);
         tg.getTransform(t3d);
         t3d.get(matrix);
         t3d.setTranslation(vector);
         t3d.mul(t3dstep);
         t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
         tg.setTransform(t3d);

         t3dstep.rotZ(Math.PI*degreeZ/180);
         tg.getTransform(t3d);
         t3d.get(matrix);
         t3d.setTranslation(vector);
         t3d.mul(t3dstep);
         t3d.setTranslation(new Vector3d(matrix.m03, matrix.m13, matrix.m23));
         tg.setTransform(t3d);

         vector.scale(-1);
         t3dstep.set(vector);
         tg.getTransform(t3d);
         t3d.mul(t3dstep);
         tg.setTransform(t3d);

//         Transform3D t1 = new Transform3D();
//
//         Transform3D t2 = new Transform3D();
//         Transform3D t3 = new Transform3D();
//// first do t1, then t2, then t3
//         Transform3D all = new Transform3D(); // all = identity
//         all.mul(t1, all); // all = t1 * all
//         all.mul(t2, all); // all = t2 * all
//         all.mul(t3, all);
     }

    public void move(Vector3d vector){
        t3dstep.set(vector);
        tg.getTransform(t3d);
        t3d.mul(t3dstep);
        tg.setTransform(t3d);
    }

}

