/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris3d.blocks;

import com.sun.javafx.geom.Vec3d;
import com.sun.opengl.util.GLUT;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.media.opengl.GL;

/**
 *
 * @author gabri
 */
public abstract class BlockBase
{
    private final ArrayList<Vec3d> pieces;
    private Vec3d color;

    private final Vec3d position;
    
    public BlockBase()
    {
        pieces = new ArrayList<>();
        color = new Vec3d(1, 1, 1);

        position = new Vec3d(0, 15, 0);
    }
    
    public final void draw(GL gl, GLUT glut)
    {
        double[] currentColor = new double[4];
        gl.glGetDoublev(GL.GL_CURRENT_COLOR, currentColor, 0);

        for (Vec3d piece : pieces)
        {
            gl.glPushMatrix();
                gl.glTranslated(
                        piece.x + 0.5 + position.x,
                        piece.y + 0.5 + position.y,
                        piece.z + 0.5 + position.z
                );
                
                gl.glColor3d(color.x, color.y, color.z);
                glut.glutSolidCube(1);
                
                gl.glColor3d(0, 0, 0);
                glut.glutWireCube(1.01f);
                
            gl.glPopMatrix();
        }
        gl.glColor3d(currentColor[0], currentColor[1], currentColor[2]);
    }
    
    public Boolean moveDown(ArrayList<BlockBase> allBlocks)
    {
        Boolean canMoveDown = true;
        
        if (position.y == 0) 
        {
            canMoveDown = false;
        }
        
        if (allBlocks
                .stream()
                .filter(b -> 
                        b != this &&
                        b.getYSpan() == position.y - 1 &&
                        (position.x <= b.getXSpan() && getXSpan() >= b.getPosition().x) &&
                        (position.z <= b.getZSpan() && getZSpan() >= b.getPosition().z)
                )
                .count() > 0)
        {
            canMoveDown = false;
        }
        
        if (canMoveDown) 
        {
            position.y--;
        }
        
        return canMoveDown;
    }

    public final ArrayList<Vec3d> getPieces()
    {
        return pieces;
    }
    
    public final List<Vec3d> getPiecesAbsolutePosition() 
    {
        return pieces.stream()
            .map(p -> new Vec3d(p.x + position.x, p.y + position.y, p.z + position.z))
            .collect(Collectors.toList());
    }
    
    public final void removePiecesFromLevel(int level)
    {
        pieces.removeIf(p -> p.y + position.y == level);
        
        pieces.stream().filter(p -> p.y + position.y > level).forEach(p -> p.y--);
        
        while (pieces.stream().anyMatch(p -> p.y < 0))
        {
            position.y--;
            pieces.stream().filter(p -> p.y < 0).forEach(p -> p.y++);
        }
    }

    public final Vec3d getColor()
    {
        return color;
    }

    public final void setColor(Vec3d color)
    {
        this.color = color;
    }
    
    public final Vec3d getPosition() 
    {
        return position;
    }
    
    public final int getXSpan() 
    {
        return getXWidth() + ((Double)position.x).intValue();
    }
    
    public final int getYSpan() 
    {
        return getHeight() + ((Double)position.y).intValue();
    }
    
    public final int getZSpan() 
    {
        return getZWidth() + ((Double)position.z).intValue();
    }
    
    public final int getXWidth() 
    {
        return ((Double)pieces.stream().mapToDouble(p -> p.x).max().getAsDouble()).intValue();
    }
    
    public final int getHeight() 
    {
        return ((Double)pieces.stream().mapToDouble(p -> p.y).max().getAsDouble()).intValue();
    }
    
    public final int getZWidth() 
    {
        return ((Double)pieces.stream().mapToDouble(p -> p.z).max().getAsDouble()).intValue();
    }
}
