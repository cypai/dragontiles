package com.pipai.dragontiles.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
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

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = gameConfig.getResolution().getWidth();
        config.height = gameConfig.getResolution().getHeight();
        new LwjglApplication(new DragonTilesGame(gameConfig), config);
    }
}
