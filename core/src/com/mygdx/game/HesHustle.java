// Package declaration for HesHustle Class
package com.mygdx.game;

// Importing required libraries and classes from libgdx
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.MainMenuScreen;


/**
 * Main class to represent the game Application
 * Extends the Game class from LibGDX to manage the
 * game state through the use of LibGDX Screens
 */
public class HesHustle extends Game {

	// SpriteBatch used for rendering graphics.
	public SpriteBatch batch;

	public OrthographicCamera camera;


	/**
	 * create() function is called when application is first created.
	 * ...
	 * ...
	 */
	@Override
	public void create () {
		batch = new SpriteBatch();
		this.setScreen(new MainMenuScreen(this, null));
		//this.setScreen(new MainGameScreen(this));
	}



	/**
	 * render() is called every frame to render the game.
	 * Rendering a frame is delegated to the current active screen.
	 */
	@Override
	public void render () {
		super.render();
	}

}
