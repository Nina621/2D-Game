package com.unknown;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false; //Postavljena konfiguracija kako Main ne možemo mijenjati
		config.width = Main.WIDTH;
		config.height = Main.HEIGHT;
		new LwjglApplication(new Main(), config);
	}
}
