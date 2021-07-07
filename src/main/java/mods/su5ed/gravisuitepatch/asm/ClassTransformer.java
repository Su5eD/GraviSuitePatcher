package mods.su5ed.gravisuitepatch.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.objectweb.asm.Opcodes.*;

public class ClassTransformer implements IClassTransformer {
    private static final Map<String, BiFunction<Integer, ClassVisitor, ClassVisitor>> TRANSFORMERS = new HashMap<>();
    
    static {
        TRANSFORMERS.put("com.chocohead.gravisuite.items.ItemAdvancedDrill", ItemAdvancedDrillVisitor::new);
        TRANSFORMERS.put("com.chocohead.gravisuite.Recipes", RecipesVisitor::new);
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (TRANSFORMERS.containsKey(name)) {
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            
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
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("getBrokenBlocks") && desc.equals("(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/RayTraceResult;)Ljava/util/Collection;")) {
                return new GetBrokenBlocksVisitor(ASM4, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
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
        
        private static class GetBrokenBlocksVisitor extends MethodVisitor {
            
            public GetBrokenBlocksVisitor(int api, MethodVisitor mv) {
                super(api, mv);
            }

            @Override
            public void visitCode() {
                Label label = new Label();
                visitVarInsn(ALOAD, 1);
                visitJumpInsn(IFNONNULL, label);
                visitTypeInsn(NEW, "java/util/HashSet");
                visitInsn(DUP);
                visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V", false);
                visitInsn(ARETURN);
                visitLabel(label);
                
                super.visitCode();
            }
        }
    }
    
    private static class RecipesVisitor extends ClassVisitor {
        
        public RecipesVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (name.equals("addCraftingRecipes") && desc.equals("()V")) return new AddCraftingRecipesVisitor(ASM4, super.visitMethod(access, name, desc, signature, exceptions)); 
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        
        private static class AddCraftingRecipesVisitor extends MethodVisitor {
            private boolean foundEntryPoint;
            private boolean inject;
            private boolean injected;
            private final Label label = new Label();

            public AddCraftingRecipesVisitor(int api, MethodVisitor mv) {
                super(api, mv);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                if (opcode == GETSTATIC && owner.equals("com/chocohead/gravisuite/Config") && name.equals("canCraftUltiLappack") && desc.equals("Z")) {
                    this.foundEntryPoint = true;
                }
                
                super.visitFieldInsn(opcode, owner, name, desc);
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                if (opcode == IFEQ && this.foundEntryPoint) {
                    this.inject = true;
                }
                
                super.visitJumpInsn(opcode, label);
            }
            
            @Override
            public void visitTypeInsn(int opcode, String type) {
                if (opcode == NEW && type.equals("net/minecraft/item/ItemStack") && this.inject) {
                    if (!this.injected) {
                        visitFieldInsn(GETSTATIC, "ic2/core/IC2", "version", "Lic2/core/profile/Version;");
                        visitMethodInsn(INVOKEVIRTUAL, "ic2/core/profile/Version", "isClassic", "()Z", false);
                        visitJumpInsn(IFEQ, this.label);
                                            
                        super.visitTypeInsn(opcode, type);
                        this.injected = true;
                        return;
                    }
                    else {
                        this.foundEntryPoint = this.inject = this.injected = false;
                        visitLabel(this.label);
                        visitFrame(F_SAME, 0, null, 0, null);
                    }
                }
                            
                super.visitTypeInsn(opcode, type);
            }
        }
    }
}
