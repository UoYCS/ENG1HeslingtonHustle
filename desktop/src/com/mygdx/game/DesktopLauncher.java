// Package declaration for DesktopLauncher class
package com.mygdx.game;

// Importing required libraries and classes
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.HesHustle;

// Pleasenote that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument

/**
 * Main class to handle how the game is launched.
 *
 * ...
 */
public class DesktopLauncher {

	public static void main (String[] arg) {

		// Creating configuration settings for the Lwjgl3 application.
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		// Set the game frames per second (FPS)
		config.setForegroundFPS(60);

		// Set the games windowed mode dimensions (width, height)
		config.setWindowedMode(1024, 576);

		// Set the title of the application window
		config.setTitle("ENGHeslingtonHustle");

		// Create the Lwjgl3Application instance using the defined config settings and HesHustle game class
		new Lwjgl3Application(new HesHustle(), config);
	}
}
