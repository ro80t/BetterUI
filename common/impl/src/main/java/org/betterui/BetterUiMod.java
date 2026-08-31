package org.betterui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.betterui.api.IBetterUiMod;

@UtilityClass
public class BetterUiMod {
    public static final String MOD_NAME;
    public static final String MOD_ID;

    @Getter
    private static final BuildData BUILD_DATA;
    @Getter
    @Setter
    private static IBetterUiMod instance;

    static {
        BUILD_DATA = BuildData.getInstance();
        MOD_NAME = BUILD_DATA.getModName();
        MOD_ID = BUILD_DATA.getModId();
    }
}
