package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.HesHustle;

public class MainGameScreen implements Screen {

    public static float SPEED = 100;

    Texture img;
    float x;
    float y;

    HesHustle game;

    public MainGameScreen (HesHustle game){
        this.game = game;
    }


    @Override
    public void show() {
        img = new Texture("badlogic.jpg");
    }

    @Override
    public void render(float delta) {

        int up    = Gdx.input.isKeyPressed(Input.Keys.W) ? 1 : 0;
        int down  = Gdx.input.isKeyPressed(Input.Keys.S) ? 1 : 0;
        int left  = Gdx.input.isKeyPressed(Input.Keys.A) ? 1 : 0;
        int right = Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0;

        int horizontal = (right - left);
        int vertical = (up - down);


        // Checking if player is moving diagonally and to normalise speed
        // Otherwise, player would be faster when moving diagonally due to Pythagorean theorem
        
        if (horizontal != 0 && vertical != 0) {
            float length = (float) Math.sqrt(Math.pow(horizontal, 2) + Math.pow(vertical, 2));
            float horizontal_normalised = horizontal / length;
            float vertical_normalised = vertical / length;

            y += vertical_normalised * SPEED * Gdx.graphics.getDeltaTime();
            x += horizontal_normalised * SPEED * Gdx.graphics.getDeltaTime();
        } else {

            y += (vertical   * SPEED) * Gdx.graphics.getDeltaTime();
            x += (horizontal * SPEED) * Gdx.graphics.getDeltaTime();
        }


        ScreenUtils.clear(1, 0, 0, 1);
        game.batch.begin();
        game.batch.draw(img, x, y);
        game.batch.end();
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
        img.dispose();
    }
}
