package mods.su5ed.gravisuitepatch.asm;

import com.chocohead.gravisuite.items.ItemAdvancedDrill;
import ic2.api.item.ElectricItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class ASMHooks {
    
    public static int getAdvDrillEnergyUse(ItemStack stack, World world, BlockPos pos, IBlockState state) {
        ItemAdvancedDrill.DrillMode mode = ItemAdvancedDrill.readDrillMode(stack);
        
        switch (mode) {
            case NORMAL:
            case BIG_HOLES:
            default:
                return 40;
            case LOW_POWER:
                return 20;
            case FINE:
                return 6;
        }
    }
    
    public static int getAdvDrillBreakTime(ItemStack stack, World world, BlockPos pos, IBlockState state) {
        ItemAdvancedDrill.DrillMode mode = ItemAdvancedDrill.readDrillMode(stack);
        
        switch (mode) {
            case NORMAL:
            default:    
                return 15;
            case BIG_HOLES:    
            case LOW_POWER:
                return 50;
            case FINE:
                return 160;
        }
    }
    
    public static boolean advDrillBreakBlock(ItemStack stack, World world, BlockPos pos, IBlockState state) {
        ItemAdvancedDrill.DrillMode mode = ItemAdvancedDrill.readDrillMode(stack);
        
        return ElectricItem.manager.use(stack, mode.energyCost, null);
    }
}
