/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris3d.renderers;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import tetris3d.game.GameManager;

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
    
    private final GameManager gameManager;
    
    private int xRot = 0;
    private int yRot = 0;
    
    private Point mousePt;

    public GameRenderer(GameManager gameManager)
    {
        glu = new GLU();
        glut = new GLUT();
        jFrame = new JFrame();
        
        this.gameManager = gameManager;

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
        
        jFrame.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e) { }
            
            @Override
            public void keyReleased(KeyEvent e) { }
            
            @Override
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyCode()) 
                {
                    case KeyEvent.VK_W: gameManager.getCurrentBlock().moveZ(gameManager.getBlocks(), -1); break;
                    case KeyEvent.VK_S: gameManager.getCurrentBlock().moveZ(gameManager.getBlocks(), 1); break;
                    case KeyEvent.VK_A: gameManager.getCurrentBlock().moveX(gameManager.getBlocks(), -1); break;
                    case KeyEvent.VK_D: gameManager.getCurrentBlock().moveX(gameManager.getBlocks(), 1); break;
                    
                    case KeyEvent.VK_SHIFT: gameManager.getCurrentBlock().moveDown(gameManager.getBlocks()); break;
                    
                    case KeyEvent.VK_I: gameManager.getCurrentBlock().rotateX(); break;
                    case KeyEvent.VK_O: gameManager.getCurrentBlock().rotateY(); break;
                    case KeyEvent.VK_P: gameManager.getCurrentBlock().rotateZ(); break;
                }
            }
        });
        
        jFrame.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) 
            {
                mousePt = e.getPoint();
            }
        });
        
        jFrame.addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                xRot += (e.getY() - mousePt.y) * 0.05;
                yRot += (e.getX() - mousePt.x) * 0.05;
                
                if (xRot > 360) xRot = 0;
                if (yRot > 360) yRot = 0;
            }
        });

        gljPanel = new GLJPanel();

        gljPanel.addGLEventListener(this);
        jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jFrame.setUndecorated(true);
        jFrame.getContentPane().add(gljPanel);
        
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
        
        gl.glPushMatrix();
        gl.glRotated(30 + xRot, 1, 0, 0);
        gl.glRotated(-45 + yRot, 0, 1, 0);
        
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
        
            gameManager.gameTick(gl, glut);
            
        gl.glPopMatrix();
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
