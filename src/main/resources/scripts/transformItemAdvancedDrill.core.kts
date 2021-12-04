import codes.som.anthony.koffee.MethodAssembly
import codes.som.anthony.koffee.insert
import codes.som.anthony.koffee.insns.jvm.*
import codes.som.anthony.koffee.koffee
import codes.som.anthony.koffee.types.TypeLike
import codes.som.anthony.koffee.util.constructMethodDescriptor
import dev.su5ed.koremods.dsl.computeFrames
import org.objectweb.asm.Label
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

transformers {
    `class`("com.chocohead.gravisuite.items.ItemAdvancedDrill", ::transformItemAdvancedDrill)
    method(
        "com.chocohead.gravisuite.items.ItemAdvancedDrill",
        "getBrokenBlocks",
        constructMethodDescriptor(
            Collection::class,
            "net/minecraft/entity/player/EntityPlayer",
            "net/minecraft/util/math/RayTraceResult"
        ),
        ::transformBrokenBlocks
    )
    
    ext { 
        computeFrames = true
    }
}

val hookArgs = arrayOf(
    "net/minecraft/item/ItemStack",
    "net/minecraft/world/World",
    "net/minecraft/util/math/BlockPos",
    "net/minecraft/block/state/IBlockState"
)

fun transformItemAdvancedDrill(node: ClassNode) {
    node.koffee {
        method(
            public,
            "energyUse",
            int,
            *hookArgs,
            routine = { generateHookMethod("getAdvDrillEnergyUse", int) }
        )
        method(
            public,
            "breakTime",
            int,
            *hookArgs,
            routine = { generateHookMethod("getAdvDrillBreakTime", int) }
        )
        method(
            public,
            "breakBlock",
            boolean,
            *hookArgs,
            routine = { generateHookMethod("advDrillBreakBlock", boolean) }
        )
    }
}

fun MethodAssembly.generateHookMethod(name: String, returnType: TypeLike) {
    val desc = constructMethodDescriptor(returnType, *hookArgs)

    aload_1
    aload_2
    aload_3
    aload(4)
    invokestatic("mods/su5ed/gravisuitepatch/asm/ASMHooks", name, desc)
    ireturn
}

fun transformBrokenBlocks(node: MethodNode) {
    node.insert { 
        val label = Label()
            
        aload_1
        ifnonnull(label)
        new(HashSet::class)
        dup
        invokespecial(HashSet::class, "<init>", "()V")
        areturn
        label(label)
    }
}
