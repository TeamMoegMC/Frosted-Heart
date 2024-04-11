package com.teammoeg.frostedheart.content.decoration;

import javax.annotation.Nullable;

import com.teammoeg.frostedheart.base.block.FHBaseBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class OddMark extends FHBaseBlock {
    private static Integer typeCount = 5;
    private static IntegerProperty TYPE = IntegerProperty.create("marktype", 0, typeCount - 1);
    private static Integer colorCount = 2;
    private static IntegerProperty COLOR = IntegerProperty.create("markcolor", 0, colorCount - 1);
    static final VoxelShape shape = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);
    static final VoxelShape shape2 = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);
    public OddMark(AbstractBlock.Properties blockProps) {
        super(blockProps);
        this.setDefaultState(this.stateContainer.getBaseState().with(TYPE, 0).with(COLOR, 0));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
        builder.add(COLOR);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Integer finalType = Math.abs(RANDOM.nextInt()) % typeCount;
        Integer finalColor = Math.abs(RANDOM.nextInt()) % colorCount;
        BlockState newState = this.stateContainer.getBaseState().with(TYPE, finalType).with(COLOR, finalColor);
        worldIn.setBlockState(pos, newState);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
                                        ISelectionContext context) {
        if(state.get(TYPE) <= 1)return shape;
        return shape2;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if(state.get(TYPE) <= 1)return shape;
        return shape2;
    }
}
