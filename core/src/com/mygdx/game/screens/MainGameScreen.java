package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.HesHustle;

public class MainGameScreen implements Screen {

    public static final float SPEED = 100;

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
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += SPEED * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= SPEED * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= SPEED * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += SPEED * Gdx.graphics.getDeltaTime();
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
