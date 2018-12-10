/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris3d.blocks;

import com.sun.javafx.geom.Vec3d;
import com.sun.opengl.util.GLUT;
import java.util.ArrayList;
import javax.media.opengl.GL;

/**
 *
 * @author gabri
 */
public abstract class BlockBase
{
    private final ArrayList<Vec3d> pieces;
    private Vec3d color;

    private int height;

    public BlockBase()
    {
        pieces = new ArrayList<>();
        color = new Vec3d(1, 1, 1);

        height = 15;
    }

    public final void draw(GL gl, GLUT glut)
    {
        double[] currentColor = new double[4];
        gl.glGetDoublev(GL.GL_CURRENT_COLOR, currentColor, 0);

        for (Vec3d piece : pieces)
        {
            gl.glPushMatrix();
                gl.glTranslated(piece.x + 0.5, piece.y + 0.5 + height, piece.z + 0.5);
                
                gl.glColor3d(color.x, color.y, color.z);
                glut.glutSolidCube(1);
                
                gl.glColor3d(0, 0, 0);
                glut.glutWireCube(1.01f);
                
            gl.glPopMatrix();
        }
        gl.glColor3d(currentColor[0], currentColor[1], currentColor[2]);
    }

    public Boolean moveDown()
    {
        if (height > 0)
        {
            height--;
            return true;
        }
        return false;
    }

    public final ArrayList<Vec3d> getPieces()
    {
        return pieces;
    }

    public final Vec3d getColor()
    {
        return color;
    }

    public final void setColor(Vec3d color)
    {
        this.color = color;
    }
}
