// Package declaration for DesktopLauncher class
package com.mygdx.game;

// Importing required libraries and classes
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.game.HesHustle;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument

/**
 * Main class to handle how the game is launched.
 * This class is responsible for configuring and launching the game.
 */
public class DesktopLauncher {


	/**
	 * Main method used to launch the game windowed
	 * @param arg Command-line arguments (Not used)
	 */
	public static void main (String[] arg) {

		// Creating configuration settings for the Lwjgl3 application.
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		// Set the game frames per second (FPS)
		config.setForegroundFPS(60);

		// Set the games windowed mode dimensions (width, height)
		config.setWindowedMode(1024, 576);

		// Disable use of resizable window
		config.setResizable(false);

		// Set the title of the application window
		config.setTitle("Heslington Hustle - ENG1 Cohort 3 Team 22");

		// Set game window Icon
		config.setWindowIcon("Logo.png");

		// Create the Lwjgl3Application instance using the defined config settings and HesHustle game class
		new Lwjgl3Application(new HesHustle(), config);
	}
}
