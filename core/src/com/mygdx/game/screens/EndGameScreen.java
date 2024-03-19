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

public class EndGameScreen implements Screen {

    private final Screen MainGameScreen;
    private final int[] studyCounter;
    private final int[] recCounter;
    private final int[][] eatCounter;

    private BitmapFont font;
    private SpriteBatch dayBatch;
    private HesHustle game;


    public EndGameScreen(HesHustle game, Screen MainGameScreen, int[] studyCounter, int[] recCounter, int[][] eatCounter) {
        this.game = game;
        this.MainGameScreen = MainGameScreen;

        dayBatch = new SpriteBatch();

        font = new BitmapFont();

        this.studyCounter = studyCounter;
        this.recCounter = recCounter;
        this.eatCounter = eatCounter;

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
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(this.game, null));
        }

        // Calculate the position to center the text
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        GlyphLayout layout = new GlyphLayout();
        String text = "GAME OVER";
        layout.setText(font, text);

        float x = (screenWidth - layout.width) / 2;
        float y = (float) (screenHeight * 0.8);

        // Draw the text on the screen
        dayBatch.begin();
        font.draw(dayBatch, layout, x, y);
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
