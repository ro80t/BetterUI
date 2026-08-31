package com.ro80t.betterui.compat.fabric.common;

import lombok.Getter;
import lombok.Setter;
import com.ro80t.betterui.api.IBetterUiMod;

public class FabricBetterUiMod implements IBetterUiMod, IFabricBetterUiMod {
    @Setter
    @Getter
    ICompatFabric compat;

    public FabricBetterUiMod() {
    }
}
