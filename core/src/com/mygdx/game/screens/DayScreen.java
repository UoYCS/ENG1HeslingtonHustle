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
    private final int[] studyCounter;
    private final int[] recCounter;
    private final int[][] eatCounter;


    int day;

    private BitmapFont font;
    private SpriteBatch dayBatch;


    public DayScreen(HesHustle game, Screen MainGameScreen, int day, int[] studyCounter, int[] recCounter, int[][] eatCounter) {
        this.game = game;
        this.MainGameScreen = MainGameScreen;
        this.day = day;


        // Passing in activity counters to create day summary
        this.studyCounter = studyCounter;
        this.recCounter = recCounter;
        this.eatCounter = eatCounter;



        dayBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(3);

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
                ((Game) Gdx.app.getApplicationListener()).setScreen(new EndGameScreen(this.game, this.MainGameScreen, studyCounter, recCounter, eatCounter));

            }
        }

        dayBatch.begin();

        // Calculate the position to center the text
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Displays the text for what day it is and the time remaining
        GlyphLayout layout = new GlyphLayout();

        GlyphLayout days_left = new GlyphLayout();
        GlyphLayout hours_left = new GlyphLayout();

        layout.setText(font, "Dawn of");
        font.draw(dayBatch, layout, (screenWidth - layout.width) / 2, (float) (screenHeight * 0.95));

        if (day == 0){
            days_left.setText(font,  "The First Day");
        }
        if (day == 1){
            days_left.setText(font,  "The Second Day");
        }
        else if (day == 2){
            days_left.setText(font, "The Third Day");
        }
        else if (day == 3){
            days_left.setText(font, "The Fourth Day");
        }
        else if (day == 4){
            days_left.setText(font, "The Fifth Day");
        }
        else if (day == 5){
            days_left.setText(font, "The Sixth Day");
        }
        else if (day == 6){
            days_left.setText(font, "The Final Day");
        }
        else{
            days_left.setText(font, "The Exam Day");
        }

        hours_left.setText(font, "-"+(24*(7 - day)) +" Hours Remain-");


        font.draw(dayBatch, days_left, (screenWidth - days_left.width) / 2, (float) (screenHeight * 0.85));
        font.draw(dayBatch, hours_left, (screenWidth - hours_left.width) / 2, (float) (screenHeight * 0.7));

        // Displays the summary for what the user did in the day
        GlyphLayout summary_title = new GlyphLayout();
        GlyphLayout study = new GlyphLayout();
        GlyphLayout eaten = new GlyphLayout();
        GlyphLayout rec = new GlyphLayout();

        summary_title.setText(font, "Summary for Day "+ day +":");
        study.setText(font, "Times Studied: "+studyCounter[day-1]);

        int countEat = 0;
        for (int time:eatCounter[day-1]){
            if (time != 0){
                countEat++;
            }
        }

        eaten.setText(font, "Times Eaten: "+countEat);
        rec.setText(font, "Recreational Activities:"+recCounter[day-1]);

        font.draw(dayBatch, summary_title, (screenWidth - summary_title.width) / 2, (float) (screenHeight * 0.5));
        font.draw(dayBatch, study, (screenWidth - 750) / 2, (float) (screenHeight * 0.35));
        font.draw(dayBatch, eaten, (screenWidth - 750) / 2, (float) (screenHeight * 0.25));
        font.draw(dayBatch, rec, (screenWidth - 750) / 2, (float) (screenHeight * 0.15));

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
