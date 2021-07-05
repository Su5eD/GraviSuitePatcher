package mods.su5ed.gravisuitepatch.asm;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.Collections;

public class PluginModContainer extends DummyModContainer {

    public PluginModContainer() {
        super(new ModMetadata());
        ModMetadata meta = super.getMetadata();
        meta.modId = "gravisuitepatchcore";
        meta.name = "Gravitation Suite Patcher Core";
        meta.version = "1.0";
        meta.authorList = Collections.singletonList("Su5eD");
        meta.description = "Gravitation Suite Patcher's companion CoreMod";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}
