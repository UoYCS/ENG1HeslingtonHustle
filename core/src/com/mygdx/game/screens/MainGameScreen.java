package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.HesHustle;

public class MainGameScreen implements Screen {

    public static float SPEED = 150;

    Texture img;
    Sprite map;
    float player_x;
    float player_y;

    final float GAME_WORLD_WIDTH = 1024;
    final float GAME_WORLD_HEIGHT = 576;

    OrthographicCamera camera;

    HesHustle game;

    public MainGameScreen (HesHustle game){
        this.game = game;

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.set(GAME_WORLD_WIDTH / 2, GAME_WORLD_HEIGHT / 2, 0);
        this.camera.update();

        this.game.camera = this.camera;
        this.game.camera.position.set(player_x, player_y, 0);

    }


    @Override
    public void show() {
        map = new Sprite(new Texture("temp_map.png"));
        map.setPosition(0,0);
        map.setSize(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT);
        img = new Texture("player.png");
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

            player_y += vertical_normalised * SPEED * Gdx.graphics.getDeltaTime();
            player_x += horizontal_normalised * SPEED * Gdx.graphics.getDeltaTime();
        } else {
            player_y += (vertical   * SPEED) * Gdx.graphics.getDeltaTime();
            player_x += (horizontal * SPEED) * Gdx.graphics.getDeltaTime();
        }


        ScreenUtils.clear(255, 255, 255, 1);
        game.camera.update();

        game.batch.begin();

        map.draw(game.batch);
        //game.batch.draw(map_tex, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.camera.position.set(player_x, player_y, 0);
        game.camera.update();

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.draw(img, player_x, player_y);
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
