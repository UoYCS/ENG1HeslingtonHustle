package com.mygdx.game.screens;

// Importing required libraries and classes
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
import com.mygdx.game.HesHustle;

import java.util.Arrays;

/**
 * Screen used to show the final score to the user at the end of the game.
 */
public class EndGameScreen implements Screen {


    private final BitmapFont font; // Font used for rendering text
    private final SpriteBatch scoreSummaryBatch; //SpriteBatch used to render screen
    private final HesHustle game; // Main Game instance reference
    private final Texture background; // Background art of screen

    private int score = 0; // Players game score

    // Arrays to store counts each activity for each day
    private final int[] studyCounter;
    private final int[] recCounter;
    private final int[][] eatCounter;

    // Variables to store amount of times activities were done
    private int timesEaten;
    private int studyCount;
    private int daysStudied;
    private int recCount;


    /**
     * Constructor for EndGameScreen class
     *
     * @param game Game instance
     * @param studyCounter Array containing study counts for each day
     * @param recCounter Array containing recreational activity counts for each day
     * @param eatCounter Array containing times meals are eaten for each day
     */
    public EndGameScreen(HesHustle game, int[] studyCounter, int[] recCounter, int[][] eatCounter) {
        this.game = game;
        this.studyCounter = studyCounter;
        this.recCounter = recCounter;
        this.eatCounter = eatCounter;

        // Initialise SpriteBatch for rendering
        scoreSummaryBatch = new SpriteBatch();

        // Setting font for rendering text
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(2); // Adjust the scale as needed

        // Setting background artwork
        background = new Texture("endScreenBackground.png");

        calculateScore();

    }

    /**
     * Function called when EndGameScreen becomes the active screen.
     */
    @Override
    public void show() {
        // Not used
    }

    /**
     * render() function called every frame to render the screen.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Check if F is pressed, and if so return to Main Menu screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(this.game, null));
        }

        // Get the Width and Height of the screen to use for centering text
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Set text layouts
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

        // Render the screen background
        scoreSummaryBatch.begin();
        scoreSummaryBatch.draw(background, 0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw the text on the screen
        font.draw(scoreSummaryBatch, gameOver, (screenWidth - gameOver.width) / 2, (float) (screenHeight*0.85));
        font.draw(scoreSummaryBatch, finalScore, (screenWidth - finalScore.width) / 2, (float) (screenHeight*0.8));
        font.draw(scoreSummaryBatch, passFail, (screenWidth - passFail.width) / 2, (float) (screenHeight*0.7));

        font.draw(scoreSummaryBatch, summary, (screenWidth - summary.width) / 2, (float) (screenHeight*0.5));
        font.draw(scoreSummaryBatch, totalStudy, (screenWidth - totalStudy.width) / 2, (float) (screenHeight*0.4));
        font.draw(scoreSummaryBatch, totalDaysStudy, (screenWidth - totalDaysStudy.width) / 2, (float) (screenHeight*0.3));
        font.draw(scoreSummaryBatch, totalRec, (screenWidth - totalRec.width) / 2, (float) (screenHeight*0.2));
        font.draw(scoreSummaryBatch, totalEat, (screenWidth - totalEat.width) / 2, (float) (screenHeight*0.1));
        scoreSummaryBatch.end();

    }

    /**
     * Calculates the score using defined rules/logic
     */
    public void calculateScore(){

        /*
         * Studying gives the player points
         * Score increases up to studying 10 times a week
         * Studying more than this will remove points from the player
         * There is a bonus for studying each day
         */

        // Number of times studied
        studyCount = Arrays.stream(studyCounter).sum();

        // Number of different days the player studied
        daysStudied = (int) ((int) 7 - (Arrays.stream(studyCounter).filter(num -> num == 0).count()));

        // Add points for number of times studied in the week
        if (studyCount <= 10){
            score += studyCount * 50;
        } else {
            score += 10 * 50;
            // Penalise over-studying
            score -= (studyCount-10) * 25;
        }

        // Bonus for working everyday
        if (daysStudied == 7){
            score += 100;
        } else if (daysStudied == 6) {
            if (studyCount > 6){
                // Checking if the player missed a day but caught up
                score += 25;
            }
        } else {
            score -= 100;
        }



        /*
         * Eating gives the player points
         * More points are awarded if they eat 3 times a day
         * Bonus for eating 3 reasonably spaced meals a day
         */

        int totalDays = eatCounter.length;
        int mealsPerDay = eatCounter[0].length;

        // Loop through each day to check logic
        for (int i = 0; i < totalDays; i++) {
            int mealsEaten = (int) Arrays.stream(eatCounter[i]).filter(num -> num != 0).count();

            switch (mealsEaten){
                // If the player didn't eat, penalise them
                case 0:
                    score -= 25;
                    break;

                // If the player only ate once, reward a small amount of points
                case 1:
                    score += 10;
                    timesEaten++;
                    break;

                // If the player only ate twice, add more points
                case 2:
                    score += 20;
                    timesEaten += 2;
                    break;
                case 3:
                    // If the player ate 3 times, reward them more points
                    score += 40;
                    timesEaten += 3;

                    // Check if the player ate at reasonable intervals
                    if ((eatCounter[i][1] - eatCounter[i][0] > 240) && (eatCounter[i][2] - eatCounter[i][1] > 240)){
                        score += 50;
                    }
                    break;
            }
        }



        // Recreational activities = Good
        // Points every time
        // Bonus for doing it everyday


        /*
         * Feeding the ducks gives the player points.
         * Points are rewarded every time it is done but only up to 7 times
         */

        // Number of times recreational activity was done
        recCount = Arrays.stream(recCounter).sum();

        // Points for doing recreational activities, only up to 7 times
        score += Math.min(recCount, 7) * 25;

    }


    @Override
    public void resize(int width, int height) {
        // Not used
    }

    @Override
    public void pause() {
        // Not used
    }

    @Override
    public void resume() {
        // Not used
    }

    @Override
    public void hide() {
        // Not used
    }

    @Override
    public void dispose() {
        scoreSummaryBatch.dispose();
        font.dispose();
        background.dispose();
    }
}
