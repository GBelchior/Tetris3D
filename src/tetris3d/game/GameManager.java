/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris3d.game;

import java.util.ArrayList;
import java.util.Random;
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

    private long lastBlockTick = -1;
    private final int blockTickTimeMillis = 1000;

    private final int blockTypesCount = 2;

    private final Random randomSource;

    public GameManager()
    {
        blocks = new ArrayList<>();
        randomSource = new Random();

        generateNextBlock();
    }

    private void generateNextBlock()
    {
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

    public void gameTick()
    {
        long currentTime = System.currentTimeMillis();

        if (lastBlockTick < 0)
        {
            lastBlockTick = System.currentTimeMillis();
        }

        if (currentTime - lastBlockTick >= blockTickTimeMillis)
        {
            Boolean moved = currentBlock.moveDown();

            if (!moved)
            {
                currentBlock = nextBlock;
                generateNextBlock();
                blocks.add(nextBlock);
            }
        }
    }
}
