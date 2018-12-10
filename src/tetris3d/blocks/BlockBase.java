/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris3d.blocks;

import com.sun.javafx.geom.Vec3d;
import com.sun.opengl.util.GLUT;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.media.opengl.GL;

/**
 *
 * @author gabri
 */
public abstract class BlockBase
{
    private ArrayList<Vec3d> pieces;
    private Vec3d color;

    private final Vec3d position;
    
    private final int[][] rotX;
    private final int[][] rotY;
    private final int[][] rotZ;
    
    public BlockBase()
    {
        pieces = new ArrayList<>();
        color = new Vec3d(1, 1, 1);

        position = new Vec3d(0, 15, 0);
        
        rotX = new int[][] 
        {
            {1, 0, 0},
            {0, 0, -1},
            {0, 1, 0}
        };
        
        rotY = new int[][] 
        {
            {0, 0, 1},
            {0, 1, 0},
            {-1, 0, 0}
        };
        
        rotZ = new int[][] 
        {
            {0, -1, 0},
            {1, 0, 0},
            {0, 0, 1}
        };
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
        
        if (getY() == 0) 
        {
            canMoveDown = false;
        }
        
        List<BlockBase> possibleCollidingBlocks = allBlocks.stream()
                .filter(b ->
                        b != this &&
                        b.getYSpan() >= getY() - 1 &&
                        (getX() <= b.getXSpan() && getXSpan() >= b.getX()) &&
                        (getZ() <= b.getZSpan() && getZSpan() >= b.getZ())
                ).collect(Collectors.toList());
        
        List<Vec3d> myPiecesAbsPos = getPiecesAbsolutePosition();
        for (BlockBase block : possibleCollidingBlocks)
        {
            for (Vec3d piece : block.getPiecesAbsolutePosition())
            {
                if (myPiecesAbsPos.stream().anyMatch(p -> p.x == piece.x && p.y - 1 == piece.y && p.z == piece.z))
                {
                    canMoveDown = false;
                    break;
                }
            }
            
            if (!canMoveDown) break;
        }
        
//        if (allBlocks
//                .stream()
//                .filter(b -> 
//                        b != this &&
//                        b.getYSpan() == getY() - 1 &&
//                        (getX() <= b.getXSpan() && getXSpan() >= b.getX()) &&
//                        (getZ() <= b.getZSpan() && getZSpan() >= b.getZ())
//                )
//                .count() > 0)
//        {
//            canMoveDown = false;
//        }
        
        if (canMoveDown) 
        {
            position.y--;
        }
        
        return canMoveDown;
    }
    
    public void moveX(ArrayList<BlockBase> allBlocks, int direction)
    {
        int signal = (direction < 0 ? -1 : 1);
        
        if ((direction < 0 && getX() == 0) || (direction > 0 && getXSpan() == 7)) return;
        
        if (allBlocks
                .stream()
                .filter(b -> 
                        b != this &&
                        (b.getYSpan() >= getY() - 1) &&
                        ((direction < 0 && b.getXSpan() == getX() - 1) || (direction > 0 && b.getX() == getXSpan() + 1)) &&
                        (getZ() <= b.getZSpan() && getZSpan() >= b.getZ())
                )
                .count() > 0)
        {
            return;
        }
        
        position.x += signal;
    }
    
    public void moveZ(ArrayList<BlockBase> allBlocks, int direction)
    {
        int signal = (direction < 0 ? -1 : 1);
        
        if ((direction < 0 && getZ() == 0) || (direction > 0 && getZSpan() == 7)) return;
        
        if (allBlocks
                .stream()
                .filter(b -> 
                        b != this &&
                        (b.getYSpan() >= getY() - 1) &&
                        ((direction < 0 && b.getZSpan() == getZ() - 1) || (direction > 0 && b.getZ() == getZSpan() + 1)) &&
                        (getX() <= b.getXSpan() && getXSpan() >= b.getX())
                )
                .count() > 0)
        {
            return;
        }
        
        position.z += signal;
    }

    public void rotateX() 
    {
        do
        {
            for (Vec3d piece : pieces)
            {
                matrixMult(piece, rotX);
            }
        } while (getX() < 0 || getX() > 15 || getY() < 0 || getZ() < 0 || getZ() > 15);
    }
    
    public void rotateY() 
    {
        do
        {
            for (Vec3d piece : pieces)
            {
                matrixMult(piece, rotY);
            }
        } while (getX() < 0 || getX() > 15 || getY() < 0 || getZ() < 0 || getZ() > 15);
    }
    
    public void rotateZ() 
    {
        do
        {
            for (Vec3d piece : pieces)
            {
                matrixMult(piece, rotZ);
            }
        } while (getX() < 0 || getX() > 15 || getY() < 0 || getZ() < 0 || getZ() > 15);
    }
    
    public final List<Vec3d> getPieces()
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
//        Iterator<Vec3d> piecesIter = pieces.iterator();
//        while (piecesIter.hasNext())
//        {
//            Vec3d piece = piecesIter.next();
//            
//            if (piece.y + position.y == level)
//            {
//                piecesIter.remove();
//            }
//        }

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
    
    public final int getMinXPiecePos()
    {
        if (pieces.isEmpty())
        {
            return 0;
        }
        
        return ((Double)pieces.stream()
            .mapToDouble(p -> p.x)
            .min()
            .getAsDouble()).intValue();
    }
    
    public final int getMinYPiecePos()
    {
        if (pieces.isEmpty())
        {
            return 0;
        }
        
        return ((Double)pieces.stream()
            .mapToDouble(p -> p.y)
            .min()
            .getAsDouble()).intValue();
    }
    
    public final int getMinZPiecePos()
    {
        if (pieces.isEmpty())
        {
            return 0;
        }
        
        return ((Double)pieces.stream()
            .mapToDouble(p -> p.z)
            .min()
            .getAsDouble()).intValue();
    }
    
    public final int getX()
    {
        return ((Double)position.x).intValue() + getMinXPiecePos();
    }
    
    public final int getY()
    {
        return ((Double)position.y).intValue() + getMinYPiecePos();
    }
    
    public final int getZ()
    {
        return ((Double)position.z).intValue() + getMinZPiecePos();
    }
    
    public final int getXSpan() 
    {
        return getX() + getXWidth();
    }
    
    public final int getYSpan() 
    {
        return getY() + getHeight();
    }
    
    public final int getZSpan() 
    {
        return getZ() + getZWidth();
    }
    
    public final int getXWidth() 
    {
        if (pieces.isEmpty())
        {
            return 0;
        }
            
        return ((Double)pieces.stream().mapToDouble(p -> p.x).max().getAsDouble()).intValue() - getMinXPiecePos();
    }
    
    public final int getHeight() 
    {
        if (pieces.isEmpty())
        {
            return 0;
        }
        
        return ((Double)pieces.stream().mapToDouble(p -> p.y).max().getAsDouble()).intValue() - getMinYPiecePos();
    }
    
    public final int getZWidth() 
    {
        if (pieces.isEmpty())
        {
            return 0;
        }
        
        return ((Double)pieces.stream().mapToDouble(p -> p.z).max().getAsDouble()).intValue() - getMinZPiecePos();
    }
    
    private void matrixMult(Vec3d point, int[][] matrix)
    {
        Vec3d orig = new Vec3d(point.x, point.y, point.z);
        
        point.x = orig.x * matrix[0][0] + orig.y * matrix[1][0] + orig.z * matrix[2][0];
        point.y = orig.x * matrix[0][1] + orig.y * matrix[1][1] + orig.z * matrix[2][1];
        point.z = orig.x * matrix[0][2] + orig.y * matrix[1][2] + orig.z * matrix[2][2];
    }
}
