// Package declaration for HesHustle Class
package com.mygdx.game;

// Importing required libraries and classes from libgdx
import com.badlogic.gdx.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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

	public Music backgroundMusic;

	public boolean gameMuted;

	/**
	 * create() function is called when application is first created.
	 * ...
	 * ...
	 */
	@Override
	public void create() {
		batch = new SpriteBatch();
		this.setScreen(new MainMenuScreen(this, null));
		//this.setScreen(new MainGameScreen(this));

		// set background music
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sfx/bg_music.wav"));

		// set background music to loop and play
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(0.5f);
		backgroundMusic.play();
	}


	/**
	 * render() is called every frame to render the game.
	 * Rendering a frame is delegated to the current active screen.
	 */
	@Override
	public void render() {
		super.render();
	}

	public void muteGame(){
		gameMuted = true;
	}

	public void unmuteGame(){
		gameMuted = false;
	}
}