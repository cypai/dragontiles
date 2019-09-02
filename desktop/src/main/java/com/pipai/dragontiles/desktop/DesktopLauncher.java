package com.pipai.dragontiles.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.pipai.dragontiles.GameConfig;
import com.pipai.dragontiles.DragonTilesGame;

import java.io.File;

public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    // @cs.suppress [UncommentedMain] this is the main entry point
    public static void main(String[] arg) {
        GameConfig gameConfig = new GameConfig(new FileHandle(new File("config/config.properties")));
        gameConfig.writeToFile();

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(gameConfig.getResolution().getWidth(), gameConfig.getResolution().getHeight());
        config.setResizable(false);
        config.useVsync(true);
        new Lwjgl3Application(new DragonTilesGame(gameConfig), config);
    }
}
