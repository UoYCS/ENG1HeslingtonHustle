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

import java.util.Arrays;

public class EndGameScreen implements Screen {

    private final Screen MainGameScreen;
    private final int[] studyCounter;
    private final int[] recCounter;
    private final int[][] eatCounter;

    private BitmapFont font;
    private SpriteBatch dayBatch;
    private HesHustle game;

    private int score = 0;


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

        ///
        /// Score Calculation
        ///

        // Studying = Points
        // Higher score if the student studies up to 10 times a week
        // Points are removed for studying over 11 times
        // Bonus for working everyday


        // Number of times studied
        int studyCount = Arrays.stream(studyCounter).sum();

        // Number of different days the player studied
        int daysStudied = (int) ((int) 7 - (Arrays.stream(studyCounter).filter(num -> num == 0).count()));


        if (studyCount <= 10){
            score += studyCount * 50;
        } else {
            score += 10 * 50;
            // Penalising over-studying
            score -= (studyCount-10) * 25;
        }

        // Bonus for working everyday
        if (daysStudied == 7){
            score += 100;
        } else if (daysStudied == 6) {
            if (studyCount > 6){
                // Missed a day but caught up
                score += 25;
            }
        } else if (daysStudied < 6) {
            score -= 100;
        }


        // Eating at reasonable intervals = Good
        // Bonus for eating 3 reasonably spaced meals a day

        // Loop through each day of the week

        int totalDays = eatCounter.length;
        int mealsPerDay = eatCounter[0].length;

        for (int i = 0; i < totalDays; i++) {
            int mealsEaten = (int) Arrays.stream(eatCounter[i]).filter(num -> num != 0).count();
            System.out.println(mealsEaten);

            switch (mealsEaten){
                case 0:
                    score -= 25;
                case 1:
                    score += 10;
                case 2:
                    score += 30;
                case 3:
                    score += 50;
            }
        }





        // Recreational activities = Good
        // Points every time
        // Bonus for doing it everyday

        // Number of times studied
        int recCount = Arrays.stream(recCounter).sum();

        // Number of different days the player studied
        int daysRec = (int) ((int) 7 - (Arrays.stream(recCounter).filter(num -> num == 0).count()));


        // Points for doing recreational activities, only up to 7 times
        score += Math.min(recCount, 7) * 25;

        // Bonus for working everyday
        if (daysRec == 7){
            score += 100;
        }
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0, 0, 0, 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(this.game));
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
