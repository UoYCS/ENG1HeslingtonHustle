package com.mygdx.game.screens;

// Importing required libraries and classes from libgdx
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.HesHustle;

/**
 * MainGameScreen class represents the game
 * ....
 */
public class MainGameScreen implements Screen {

    // Constant for player movement speed
    public static float SPEED = 150;

    // Game assets
    Texture img;    // Player Texture
    Sprite map;     // Map Background Sprite

    // Game world dimensions
    final float GAME_WORLD_WIDTH = 1024;
    final float GAME_WORLD_HEIGHT = 576;

    // Variables for player position
    // *Temporarily initialised to center of map...will change.
    float player_x = GAME_WORLD_WIDTH / 2;
    float player_y = GAME_WORLD_HEIGHT / 2;

    // Orthographic camera for rendering
    OrthographicCamera camera;

    HesHustle game;


    /**
     * Constructor for MainGameScreen Class
     *
     * @param game The game instance from HesHustle class.
     */
    public MainGameScreen (HesHustle game){
        this.game = game;

        // Setting up the camera with initial position and size
        this.camera = new OrthographicCamera((float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
        this.camera.position.set(GAME_WORLD_WIDTH / 2, GAME_WORLD_HEIGHT / 2, 0);
        this.camera.update();

        // Initialising camera location to player's initial position/
        this.game.camera = this.camera;
        this.game.camera.position.set(player_x, player_y, 0);

    }

    /**
     * show() function is called when MainGameScreen becomes the active screen.
     */
    @Override
    public void show() {
        map = new Sprite(new Texture("temp_map.png"));
        map.setPosition(0,0);
        map.setSize(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT);
        img = new Texture("player.png");
    }

    /**
     * Renders the screen
     *
     * render() method is automatically called by the game loop every frame to update elements
     * and render the game to the screen.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        // Handling input for player movement using WASD and up/down/left/right arrows.
        int up    = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP) ? 1 : 0;
        int down  = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN) ? 1 : 0;
        int left  = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT) ? 1 : 0;
        int right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) ? 1 : 0;


        int horizontal = (right - left);
        int vertical = (up - down);


        // Checking if player is moving diagonally and if so to normalise speed

        // This is important as without normalisation,
        // the player would be faster when moving diagonally due to Pythagorean theorem

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


        //  Bounds checking to keep the player within map boundaries
        player_x = Math.max(0,
                            Math.min(player_x,
                                     GAME_WORLD_WIDTH - img.getWidth()));

        player_y = Math.max(0,
                            Math.min(player_y,
                                     GAME_WORLD_HEIGHT - img.getHeight()));


        // Bounds checking to keep camera within map boundaries
        float camera_x =
                Math.min(
                        Math.max(player_x + (float) img.getWidth() / 2, camera.viewportWidth / 2),
                        GAME_WORLD_WIDTH - camera.viewportWidth / 2);

        float camera_y =
                Math.min(
                        Math.max(player_y + (float) img.getWidth() / 2, camera.viewportHeight / 2),
                        GAME_WORLD_HEIGHT - camera.viewportHeight / 2);




        // Clear Screen and begin rendering
        ScreenUtils.clear(255, 255, 255, 1);
        game.batch.begin();
        map.draw(game.batch);


        // Update the position of the game camera using previous logic
        game.camera.position.set(camera_x, camera_y, 0);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        // Draw player based on previous logic and user input
        game.batch.draw(img, player_x, player_y);

        // End rendering for frame
        game.batch.end();
    }


    /**
     * Called when screen is resized.
     *
     * Currently, not required and unused
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * Called when the game is paused.
     *
     * Currently, not required and unused
     */
    @Override
    public void pause() {

    }

    /**
     * Called when the game is resumed from the paused state.
     *
     * Currently, not required and unused
     */
    @Override
    public void resume() {

    }

    /**
     * Called when the MainGameScreen is no longer the current screen.
     *
     * Currently, not required and unused
     */
    @Override
    public void hide() {

    }


    /**
     * Called when this screen is no longer needed and all resources should be released
     *
     * Used to dispose resources, textures, and other assets, to free up memory.
     */
    @Override
    public void dispose() {
        img.dispose();
    }
}
