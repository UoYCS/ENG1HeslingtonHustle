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

public class DayScreen implements Screen {

    private final Screen MainGameScreen;
    private final HesHustle game;
    int day;

    private BitmapFont font;
    private SpriteBatch dayBatch;


    public DayScreen(HesHustle game, Screen MainGameScreen, int day) {
        this.game = game;
        this.MainGameScreen = MainGameScreen;
        this.day = day;
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
            if (this.day != 7) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(this.MainGameScreen);
            }
            else {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new EndGameScreen(this.game, this.MainGameScreen));

            }
        }

        dayBatch.begin();

        // Calculate the position to center the text
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, "Day " + day + " Complete");



        font.draw(dayBatch, layout, (screenWidth - layout.width) / 2, (float) (screenHeight * 0.8));

        GlyphLayout days_left = new GlyphLayout();

        if (day != 7) {
            days_left.setText(font, (7 - day) + " Days till Exams!");
        }
        else{
            days_left.setText(font,  "Exams Over!");
        }
        font.draw(dayBatch, days_left, (screenWidth - days_left.width) / 2, (float) (screenHeight * 0.5));


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
