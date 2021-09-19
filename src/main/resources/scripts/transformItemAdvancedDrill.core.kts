@file:Allow("net.minecraft.", "mods.su5ed.gravisuitepatch.asm.")

import codes.som.anthony.koffee.MethodAssembly
import codes.som.anthony.koffee.assembleBlock
import codes.som.anthony.koffee.insns.jvm.*
import codes.som.anthony.koffee.koffee
import codes.som.anthony.koffee.types.TypeLike
import codes.som.anthony.koffee.util.constructMethodDescriptor
import mods.su5ed.gravisuitepatch.asm.ASMHooks
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import org.objectweb.asm.Label
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

transformers {
    `class`("com.chocohead.gravisuite.items.ItemAdvancedDrill", ::transformItemAdvancedDrill)
    method(
        "com.chocohead.gravisuite.items.ItemAdvancedDrill",
        "getBrokenBlocks",
        constructMethodDescriptor(Collection::class, EntityPlayer::class, RayTraceResult::class),
        ::transformBrokenBlocks
    )
}

fun transformItemAdvancedDrill(node: ClassNode) {
    node.koffee {
        method(
            public,
            "energyUse",
            int,
            ItemStack::class, World::class, BlockPos::class, IBlockState::class,
            routine = { generateHookMethod("getAdvDrillEnergyUse", int) }
        )
        method(
            public,
            "breakTime",
            int,
            ItemStack::class, World::class, BlockPos::class, IBlockState::class,
            routine = { generateHookMethod("getAdvDrillBreakTime", int) }
        )
        method(
            public,
            "breakBlock",
            boolean,
            ItemStack::class, World::class, BlockPos::class, IBlockState::class,
            routine = { generateHookMethod("advDrillBreakBlock", boolean) }
        )
    }
}

fun MethodAssembly.generateHookMethod(name: String, returnType: TypeLike) {
    val desc = constructMethodDescriptor(returnType, ItemStack::class, World::class, BlockPos::class, IBlockState::class)

    aload_1
    aload_2
    aload_3
    aload(4)
    invokestatic(ASMHooks::class, name, desc)
    ireturn
}

fun transformBrokenBlocks(node: MethodNode) {
    node.koffee {
        val insnlist = assembleBlock {
            val label = Label()
            aload_1
            ifnonnull(label)
            new(HashSet::class)
            dup
            invokespecial(HashSet::class, "<init>", "()V")
            areturn
            label(label)
            f_same
        }.first

        node.instructions.insert(node.instructions.first, insnlist)
    }
}
