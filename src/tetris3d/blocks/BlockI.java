/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris3d.blocks;

import com.sun.javafx.geom.Vec3d;

/**
 *
 * @author gabri
 */
public class BlockI extends BlockBase
{
    public BlockI()
    {
        setColor(new Vec3d(0, 1, 1));

        getPieces().add(new Vec3d(0, 0, 0));
        getPieces().add(new Vec3d(1, 0, 0));
        getPieces().add(new Vec3d(2, 0, 0));
        getPieces().add(new Vec3d(3, 0, 0));
    }

}
