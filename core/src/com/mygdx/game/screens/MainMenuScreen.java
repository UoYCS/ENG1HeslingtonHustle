package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.HesHustle;

public class MainMenuScreen implements Screen {

    private BitmapFont font;
    private SpriteBatch dayBatch;

    HesHustle game;

    public MainMenuScreen(HesHustle game) {

        this.game = game;

        dayBatch = new SpriteBatch();
        font = new BitmapFont();



        font.setColor(Color.WHITE);
        font.getData().setScale(5); // Adjust the scale as needed
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainGameScreen(this.game));
            }

        dayBatch.begin();

        // Calculate the position to center the text
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, "Main Menu");

        font.draw(dayBatch, layout, (screenWidth - layout.width) / 2, (float) (screenHeight * 0.8));

        dayBatch.end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

