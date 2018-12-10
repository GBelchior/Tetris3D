/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris3d.game;

import com.sun.javafx.geom.Vec3d;
import com.sun.opengl.util.GLUT;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.media.opengl.GL;
import tetris3d.blocks.*;

/**
 *
 * @author gabri
 */
public class GameManager
{
    private final ArrayList<BlockBase> blocks;

    private BlockBase currentBlock;
    private BlockBase nextBlock;

    private long lastBlockTick;
    private final int blockTickTimeMillis = 100;
    
    private final int maxWidth = 7;
    private final int maxHeight = 15;

    private final int blockTypesCount = 2;

    private final Random randomSource;
    
    private Boolean gameOver = false;

    public GameManager()
    {
        blocks = new ArrayList<>();
        randomSource = new Random();

        generateNextBlock();
        
        currentBlock = nextBlock;
        blocks.add(currentBlock);
        
        generateNextBlock();

        lastBlockTick = System.currentTimeMillis();
    }

    private void generateNextBlock()
    {
        if (true) 
        {
            nextBlock = new BlockI();
            return;
        }
        
        int numNextBlock = randomSource.nextInt(blockTypesCount);

        switch (numNextBlock)
        {
            default:
            case 0:
                nextBlock = new BlockI();
                break;
            case 1:
                nextBlock = new BlockPoint();
                break;
        }
    }

    public void gameTick(GL gl, GLUT glut)
    {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBlockTick >= blockTickTimeMillis)
        {
            lastBlockTick = currentTime;

            Boolean moved = currentBlock.moveDown(blocks);

            if (!moved)
            {
                cleanLevels();
                
                if (currentBlock.getYSpan() == maxHeight) 
                {
                    gameOver = true;
                }
                
                if (!gameOver) 
                {
                    currentBlock = nextBlock;
                    blocks.add(currentBlock);
                    generateNextBlock();
                }
            }
        }

        for (BlockBase block : blocks)
        {
            block.draw(gl, glut);
        }
    }
    
    public void moveCurrentBlock(int relativeX, int relativeY, int relativeZ)
    {
        Vec3d curPos = new Vec3d(
                currentBlock.getPosition().x,
                currentBlock.getPosition().y,
                currentBlock.getPosition().z
        );
        
        curPos.x += relativeX;
        curPos.y += relativeY;
        curPos.z += relativeZ;
        
        if (curPos.x >= 0 && curPos.x + currentBlock.getXWidth() <= maxWidth) 
        {
            currentBlock.getPosition().x = curPos.x;
        }
        
        if (curPos.y >= 0 && curPos.y + currentBlock.getHeight() <= maxHeight)
        {
            currentBlock.getPosition().y = curPos.y;
        }
        
        if (curPos.z >= 0 && curPos.z + currentBlock.getZWidth() <= maxWidth) 
        {
            currentBlock.getPosition().z = curPos.z;
        }
    }
    
    private void cleanLevels() 
    {
        for (int i = 0; i < maxHeight; i++)
        {
            int levelPiecesCount = 0;
            for (BlockBase block : blocks) 
            {
                if (!(i >= block.getPosition().y && i <= block.getYSpan()))
                {
                    continue;
                }
                
                for (Vec3d piece : block.getPiecesAbsolutePosition())
                {
                    if (piece.y == i) levelPiecesCount++;
                }
            }
            
            if (levelPiecesCount == (maxWidth + 1) * (maxWidth + 1))
            {
                for (BlockBase block : blocks)
                {
                    block.removePiecesFromLevel(i);
                    
                    if (block.getPieces().isEmpty())
                    {
                        blocks.remove(block);
                    }
                }
            }
        }
        
        Boolean atLeastOneMoved = false;
        do
        {
            for (BlockBase b : blocks)
            {
                if (b.moveDown(blocks)) atLeastOneMoved = true;
            }
        } while (atLeastOneMoved);
    }
}
