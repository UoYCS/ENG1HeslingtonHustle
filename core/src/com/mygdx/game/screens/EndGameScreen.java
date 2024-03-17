package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    private Texture background;

    private int score = 0;
    private int timesEaten;
    private int studyCount;
    private int daysStudied;
    private int recCount;


    public EndGameScreen(HesHustle game, Screen MainGameScreen, int[] studyCounter, int[] recCounter, int[][] eatCounter) {
        this.game = game;
        this.MainGameScreen = MainGameScreen;

        dayBatch = new SpriteBatch();

        font = new BitmapFont();

        this.studyCounter = studyCounter;
        this.recCounter = recCounter;
        this.eatCounter = eatCounter;

        font.setColor(Color.BLACK);
        font.getData().setScale(2); // Adjust the scale as needed

        background = new Texture("endScreenBackground.png");
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
        studyCount = Arrays.stream(studyCounter).sum();

        // Number of different days the player studied
        daysStudied = (int) ((int) 7 - (Arrays.stream(studyCounter).filter(num -> num == 0).count()));


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
                    break;
                case 1:
                    score += 10;
                    timesEaten++;
                    break;
                case 2:
                    score += 30;
                    timesEaten += 2;
                    break;
                case 3:
                    score += 50;
                    timesEaten += 3;
                    break;
            }
        }

        // Add Check for balanced meal times





        // Recreational activities = Good
        // Points every time
        // Bonus for doing it everyday

        // Number of times studied
        recCount = Arrays.stream(recCounter).sum();

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
        GlyphLayout gameOver = new GlyphLayout();
        gameOver.setText(font, "Game Over!");

        GlyphLayout finalScore = new GlyphLayout();
        GlyphLayout passFail = new GlyphLayout();
        finalScore.setText(font, "Final Score: "+score);

        if (score < 100){ //value for passing to be changed later
            passFail.setText(font, "Fail");
        }
        else{
            passFail.setText(font, "Pass");
        }

        GlyphLayout summary = new GlyphLayout();
        GlyphLayout totalStudy = new GlyphLayout();
        GlyphLayout totalDaysStudy = new GlyphLayout();
        GlyphLayout totalRec = new GlyphLayout();
        GlyphLayout totalEat = new GlyphLayout();

        summary.setText(font, "Total Amount of: ");
        totalStudy.setText(font, "Times Studied = "+studyCount);
        totalDaysStudy.setText(font, "Days Studied = "+daysStudied);
        totalRec.setText(font, "Recreation Activities = "+recCount);
        totalEat.setText(font, "Times Ate = "+timesEaten);

        //Draws the background
        dayBatch.begin();
        dayBatch.draw(background, 0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw the text on the screen
        font.draw(dayBatch, gameOver, (screenWidth - gameOver.width) / 2, (float) (screenHeight*0.85));
        font.draw(dayBatch, finalScore, (screenWidth - finalScore.width) / 2, (float) (screenHeight*0.8));
        font.draw(dayBatch, passFail, (screenWidth - passFail.width) / 2, (float) (screenHeight*0.7));

        font.draw(dayBatch, summary, (screenWidth - summary.width) / 2, (float) (screenHeight*0.5));
        font.draw(dayBatch, totalStudy, (screenWidth - totalStudy.width) / 2, (float) (screenHeight*0.4));
        font.draw(dayBatch, totalDaysStudy, (screenWidth - totalDaysStudy.width) / 2, (float) (screenHeight*0.3));
        font.draw(dayBatch, totalRec, (screenWidth - totalRec.width) / 2, (float) (screenHeight*0.2));
        font.draw(dayBatch, totalEat, (screenWidth - totalEat.width) / 2, (float) (screenHeight*0.1));
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
