package org.betterui.compat.fabric.common;

import lombok.Getter;
import lombok.Setter;
import org.betterui.api.IBetterUiMod;

public class FabricBetterUiMod implements IBetterUiMod, IFabricBetterUiMod {
    @Setter
    @Getter
    ICompatFabric compat;

    public FabricBetterUiMod() {
    }
}
