/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris3d;

import tetris3d.game.GameManager;
import tetris3d.renderers.GameRenderer;

/**
 *
 * @author gabri
 */
public class Tetris3d
{
    private static GameRenderer gameRenderer;
    
    public static void main(String[] args)
    {
        gameRenderer = new GameRenderer(new GameManager());
    }
}
