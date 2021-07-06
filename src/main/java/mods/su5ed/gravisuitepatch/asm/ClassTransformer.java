package mods.su5ed.gravisuitepatch.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.objectweb.asm.Opcodes.*;

public class ClassTransformer implements IClassTransformer {
    private static final Map<String, BiFunction<Integer, ClassVisitor, ClassVisitor>> TRANSFORMERS = new HashMap<>();
    
    static {
        TRANSFORMERS.put("com.chocohead.gravisuite.items.ItemAdvancedDrill", ItemAdvancedDrillVisitor::new);
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (TRANSFORMERS.containsKey(name)) {
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(reader, 0);
            
            reader.accept(TRANSFORMERS.get(name).apply(ASM4, writer), 0);
            
            return writer.toByteArray();
        }
        
        return bytes;
    }
    
    private static class ItemAdvancedDrillVisitor extends ClassVisitor {
        
        public ItemAdvancedDrillVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public void visitEnd() {
            generateHookMethod(
                    "energyUse", 
                    "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)I", 
                    "getAdvDrillEnergyUse"
            );
            
            generateHookMethod(
                    "breakTime", 
                    "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)I", 
                    "getAdvDrillBreakTime"
            );
            
            generateHookMethod(
                    "breakBlock", 
                    "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z", 
                    "advDrillBreakBlock"
            );
            
            super.visitEnd();
        }
        
        private void generateHookMethod(String name, String desc, String targetName) {
            MethodVisitor mv = visitMethod(ACC_PUBLIC, name, desc, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "mods/su5ed/gravisuitepatch/asm/ASMHooks", targetName, desc, false);
            mv.visitInsn(IRETURN);
            mv.visitEnd();
        }
    }
}
