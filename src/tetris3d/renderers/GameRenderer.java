/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris3d.renderers;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import tetris3d.game.GameManager;
import tetris3d.blocks.*;

/**
 *
 * @author gabri
 */
public class GameRenderer implements GLEventListener
{
    private final GLU glu;
    private final GLUT glut;

    private final JFrame jFrame;
    private final GLJPanel gljPanel;
    
    private final ArrayList<BlockBase> blocks;
    
    private long lastMovement = -1;

    public GameRenderer(GameManager gameManager)
    {
        glu = new GLU();
        glut = new GLUT();
        jFrame = new JFrame();
        
        blocks = new ArrayList<>();
        
        blocks.add(new BlockI());

        jFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                new Thread(() ->
                {
                    System.exit(0);
                }).start();
            }
        });

        gljPanel = new GLJPanel();

        gljPanel.addGLEventListener(this);
        jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jFrame.setUndecorated(true);
        jFrame.getContentPane().add(gljPanel);

//        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
//        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
//        jFrame.getContentPane().setCursor(blankCursor);
        jFrame.setVisible(true);
    }

    @Override
    public void init(GLAutoDrawable glad)
    {
        Animator animator = new Animator(glad);
        animator.start();
    }

    @Override
    public void display(GLAutoDrawable glad)
    {
        GL gl = glad.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glColor3d(1, 1, 1);

        int n = 8;
        
        if (lastMovement < 0) 
        {
            lastMovement = System.currentTimeMillis();
        }

        gl.glPushMatrix();
        gl.glRotated(30, 1, 0, 0);
        gl.glRotated(-45, 0, 1, 0);
        
        gl.glTranslated(0, -5, 0);

            // Desenha o chão
            gl.glPushMatrix();
                for (int i = 0; i <= n; i++)
                {
                    gl.glBegin(GL.GL_LINES);
                        gl.glVertex3d(-i, 0, 0);
                        gl.glVertex3d(n - i, 0, 0);

                        gl.glVertex3d(0, 0, -i);
                        gl.glVertex3d(0, 0, n - i);
                    gl.glEnd();

                    gl.glTranslated(1, 0, 1);
                }
            gl.glPopMatrix();
            
            
            gl.glPushMatrix();
            
                // Paredes laterais
                for (int j = 0; j < 2; j++) 
                {
                    
                    // Desenha a parede do eixo X
                    gl.glPushMatrix();
                        for (int i = 0; i <= n; i++)
                        {
                            gl.glBegin(GL.GL_LINES);
                                gl.glVertex3d(-i, 0, 0);
                                gl.glVertex3d(n - i, 0, 0);

                                gl.glVertex3d(0, -i, 0);
                                gl.glVertex3d(0, n - i, 0);
                            gl.glEnd();

                            gl.glTranslated(1, 1, 0);
                        }
                    gl.glPopMatrix();

                    // Desenha a parede do eixo Z
                    gl.glPushMatrix();
                        for (int i = 0; i <= n; i++)
                        {
                            gl.glBegin(GL.GL_LINES);
                                gl.glVertex3d(0, -i, 0);
                                gl.glVertex3d(0, n - i, 0);

                                gl.glVertex3d(0, 0, -i);
                                gl.glVertex3d(0, 0, n - i);
                            gl.glEnd();

                            gl.glTranslated(0, 1, 1);
                        }
                    gl.glPopMatrix();
                    
                    gl.glTranslated(0, n, 0);
                }
            gl.glPopMatrix();

            // Cubo eixo X
            gl.glPushMatrix();
                gl.glTranslated(1, 0, 0);

                gl.glColor3d(1, 0, 0);
                glut.glutSolidCube(0.1f);
                gl.glColor3d(1, 1, 1);
            gl.glPopMatrix();
            
            // Cubo eixo Y
            gl.glPushMatrix();
                gl.glTranslated(0, 1, 0);

                gl.glColor3d(0, 1, 0);
                glut.glutSolidCube(0.1f);
                gl.glColor3d(1, 1, 1);
            gl.glPopMatrix();
            
            // Cubo eixo Z
            gl.glPushMatrix();
                gl.glTranslated(0, 0, 1);
                gl.glScaled(0.1, 0.1, 0.1);

                gl.glColor3d(0, 0, 1);
                glut.glutSolidCube(1);
                gl.glColor3d(1, 1, 1);
            gl.glPopMatrix();
        
            gameTick(gl);
            
        gl.glPopMatrix();
    }
    
    private void gameTick(GL gl) 
    {
        long curFrame = System.currentTimeMillis();
        Boolean canMoveDown = false;
        
        if ((curFrame - lastMovement) >= 100) 
        {
            lastMovement = curFrame;
            canMoveDown = true;
        }
        
        for (BlockBase block : blocks) 
        {
            if (canMoveDown) 
            {
                block.moveDown();
            }
            
            block.draw(gl, glut);
        }
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int w, int h)
    {
        GL gl = glad.getGL();

        gl.glMatrixMode(GL.GL_PROJECTION);

        double n = 20;
        gl.glOrtho(
                -n,                 // left
                n,                  // right
                -n * (float) h / w, // bottom
                n * (float) h / w,  // top
                -n,                 // near
                n                   // far
        );
        
        gl.glMatrixMode(GL.GL_MODELVIEW);

        gl.glClearDepth(1);
        gl.glClearColor(0, 0, 0, 0);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);

        gl.glEnable(GL.GL_CULL_FACE);

        gl.glLoadIdentity();
    }

    @Override
    public void displayChanged(GLAutoDrawable glad, boolean bln, boolean bln1)
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
