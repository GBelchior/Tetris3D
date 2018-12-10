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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final int blockTickTimeMillis = 500;

    private final int maxWidth = 7;
    private final int maxHeight = 15;

    private final int blockTypesCount = 8;

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
        int numNextBlock = randomSource.nextInt(blockTypesCount);

        switch (numNextBlock)
        {
            default:
            case 0:
                nextBlock = new BlockCorner();
                break;
            case 1:
                nextBlock = new BlockI();
                break;
            case 2:
                nextBlock = new BlockJ();
                break;
            case 3:
                nextBlock = new BlockL();
                break;
            case 4:
                nextBlock = new BlockO();
                break;
            case 5:
                nextBlock = new BlockS();
                break;
            case 6:
                nextBlock = new BlockT();
                break;
            case 7:
                nextBlock = new BlockZ();
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

        if (gameOver)
        {
            try
            {
                Thread.sleep(blockTickTimeMillis);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            blocks.clear();
            gameOver = false;
        }
    }

    public ArrayList<BlockBase> getBlocks()
    {
        return blocks;
    }

    public BlockBase getCurrentBlock()
    {
        return currentBlock;
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
                    if (piece.y == i)
                        levelPiecesCount++;
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
                if (b.moveDown(blocks))
                    atLeastOneMoved = true;
            }
        } while (atLeastOneMoved);
    }
}
