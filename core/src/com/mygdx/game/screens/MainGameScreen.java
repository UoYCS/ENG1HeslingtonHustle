package com.mygdx.game.screens;

// Importing required libraries and classes from libgdx
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.ScreenUtils;


import com.mygdx.game.HesHustle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * MainGameScreen class represents the game
 * ....
 */
public class MainGameScreen implements Screen {

    // Constant for player movement speed
    public static float SPEED = 150;

    // Game assets
    Texture player_texture;    // Player Texture
    Sprite temp_map;     // Map Background Sprite

    private TiledMap map;




    // The textures for the activity markers
    TextureRegion recreationMarker;
    TextureRegion eatMarker;
    TextureRegion studyMarker;
    TextureRegion sleepMarker;

    // Game world dimensions
    final float GAME_WORLD_WIDTH = 1024;
    final float GAME_WORLD_HEIGHT = 576;

    // Variables for player position
    // *Temporarily initialised to center of map...will change.
    float player_x = GAME_WORLD_WIDTH / 2;
    float player_y = GAME_WORLD_HEIGHT / 2;

    int energy = 100;

    // Objects used for avatar animation and other textures
    Animation<TextureRegion> walkDownAnimation;
    Animation<TextureRegion> walkRightAnimation;
    Animation<TextureRegion> walkUpAnimation;
    Animation<TextureRegion> walkLeftAnimation;
    Texture spriteSheet = new Texture(Gdx.files.internal("SpriteSheet.png"));
    Texture markersPNG = new Texture(Gdx.files.internal("Markers.png"));

    // A variable for tracking elapsed time for the animation
    float stateTime;

    // Minutes from 8am
    // Max Value = 960 @ 12am.
    int time = 0;

    int day = 0;
    final int gameDaysLength = 7;

    // Counters to track what activities are done on each day
    int[] studyCounter = new int[gameDaysLength];
    int[] recCounter = new int[gameDaysLength];
    int[][] eatCounter = new int[gameDaysLength][3];
    int mealsEaten = 0;

  
    // Orthographic camera for rendering
    OrthographicCamera camera;

    HesHustle game;


    // Initialise an ArrayList to store details about the activities players can interact with
    private final List<Activity> activities = new ArrayList<>();


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

        // Creates the walking animation cycles for the avatar
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / 4, spriteSheet.getHeight()/4);

        TextureRegion[] walkDownFrames = new TextureRegion[4];
        TextureRegion[] walkRightFrames = new TextureRegion[4];
        TextureRegion[] walkUpFrames = new TextureRegion[4];
        TextureRegion[] walkLeftFrames = new TextureRegion[4];


        for (int i = 0; i < 4; i++) {
            walkDownFrames[i] = tmp[0][i];
            walkRightFrames[i] = tmp[1][i];
            walkUpFrames[i] = tmp[2][i];
            walkLeftFrames[i] = tmp[3][i];
        }

        walkDownAnimation = new Animation<TextureRegion>(0.125f, walkDownFrames);
        walkRightAnimation = new Animation<TextureRegion>(0.125f, walkRightFrames);
        walkUpAnimation = new Animation<TextureRegion>(0.125f, walkUpFrames);
        walkLeftAnimation = new Animation<TextureRegion>(0.125f, walkLeftFrames);

        stateTime = 0f;

        // stores the marker textures in the corresponding variables

        TextureRegion[][] tmpMarkers = TextureRegion.split(markersPNG, markersPNG.getWidth() / 4, markersPNG.getHeight());
       
        recreationMarker = tmpMarkers[0][0];
        eatMarker = tmpMarkers[0][1];
        studyMarker = tmpMarkers[0][2];
        sleepMarker = tmpMarkers[0][3];

        // Create Activity instances and add them to the activities ArrayList
        activities.add(new Activity("study", 600, 400, -10, 20, studyMarker));
        activities.add(new Activity("sleep", 600, 300, 20, 20, sleepMarker));
        activities.add(new Activity("rec", 500, 400, -20, 20, recreationMarker));
        activities.add(new Activity("eat", 500, 300, 10, 20, eatMarker));


        
    }

    /**
     * show() function is called when MainGameScreen becomes the active screen.
     */
    @Override
    public void show() {
        temp_map = new Sprite(new Texture("temp_map.png"));
        temp_map.setPosition(0,0);
        temp_map.setSize(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT);
        player_texture = new Texture("Character.png");
    }

    /**
     * Renders the screen
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
                                     GAME_WORLD_WIDTH - player_texture.getWidth()));

        player_y = Math.max(0,
                            Math.min(player_y,
                                     GAME_WORLD_HEIGHT - player_texture.getHeight()));


        // Bounds checking to keep camera within map boundaries
        float camera_x =
                Math.min(
                        Math.max(player_x + (float) player_texture.getWidth() / 2, camera.viewportWidth / 2),
                        GAME_WORLD_WIDTH - camera.viewportWidth / 2);

        float camera_y =
                Math.min(
                        Math.max(player_y + (float) player_texture.getWidth() / 2, camera.viewportHeight / 2),
                        GAME_WORLD_HEIGHT - camera.viewportHeight / 2);


        // Allow for user interaction with activities.
        // When Interact button is pressed, check if the player is close to an activity, and process logic accordingly.
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            for (Activity activity : activities) {
                if (activity.isPlayerClose(player_x + ((float) player_texture.getWidth() /2), player_y + ((float) player_texture.getHeight() /2))){

                    if (Objects.equals(activity.getType(), "sleep")) {
                        System.out.println("Day completed:" + day);
                        System.out.println(Arrays.toString(studyCounter));
                        System.out.println(Arrays.toString(eatCounter[day]));
                        System.out.println(Arrays.toString(recCounter));

                        day += 1;
                        energy = 100;
                        time = 0;
                        mealsEaten = 0;

                        ((Game) Gdx.app.getApplicationListener()).setScreen(new DayScreen(this.game, this, day, studyCounter, recCounter, eatCounter));
                    }

                    else if (Objects.equals(activity.getType(), "eat")) {
                        if (mealsEaten == 3){
                            System.out.println("Already eaten 3 times today");
                        }
                        else {
                            eatCounter[day][mealsEaten] = time;
                            mealsEaten++;
                            this.energy += (int) activity.getEnergyUsage();
                            this.time += (int) activity.getTimeUsage();
                        }
                    }

                    else if (this.energy + activity.getEnergyUsage() >= 0) {
                        this.energy += (int) activity.getEnergyUsage();
                        this.time += (int) activity.getTimeUsage();

                        if (Objects.equals(activity.getType(), "study")) {
                            studyCounter[day]++;
                        }

                        if (Objects.equals(activity.getType(), "rec")) {
                            recCounter[day]++;
                        }

                    } else {
                        System.out.println("Not enough energy to perform activity");
                        }

                    if (this.energy >= 100) {
                        this.energy = 100;
                    }

                }
            }
        }






        // Clear Screen and begin rendering
        ScreenUtils.clear(255, 255, 255, 1);
        game.batch.begin();
        temp_map.draw(game.batch);



        // For each activity, draw it on the map with its corresponding marker
        for (Activity activity : activities) {
            game.batch.draw(activity.getMarker(), activity.getX_location() - ((float) activity.getMarker().getRegionWidth() /2), activity.getY_location() - ((float) activity.getMarker().getRegionHeight() /2));
        }



        // Update the position of the game camera using previous logic
        game.camera.position.set(camera_x, camera_y, 0);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
      

        // Draw player based on previous logic and user input with the corresponding animation

        // if the player is moving right, play the walking up animation

        if (horizontal == 1){
            spriteAnimate(walkRightAnimation, player_x, player_y);
        }

        // if the player is moving left, play the walking left animation

        else if (horizontal == -1){
            spriteAnimate(walkLeftAnimation, player_x, player_y);
        }

        // if the player is moving down, play the walking down animation

        else if (vertical == -1){
            spriteAnimate(walkDownAnimation, player_x, player_y);
        }

        // if the player is moving up, play the walking up animation

        else if (vertical == 1){
            spriteAnimate(walkUpAnimation, player_x, player_y);
        }

        // if the player isn't moving, display the idle character model

        else {
            game.batch.draw(player_texture, player_x, player_y);
        }



        // End rendering for frame
        game.batch.end();
    }


    // method for displaying the avatar animation to the screen
    public void spriteAnimate(Animation<TextureRegion> animation, float player_x, float player_y){
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        game.batch.draw(currentFrame, player_x, player_y);
    }


    /**
     * Called when screen is resized.
     * Currently, not required and unused
     * @param width ...
     * @param height ...
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * Called when the game is paused.
     * Currently, not required and unused
     */
    @Override
    public void pause() {

    }

    /**
     * Called when the game is resumed from the paused state.
     * Currently, not required and unused
     */
    @Override
    public void resume() {

    }

    /**
     * Called when the MainGameScreen is no longer the current screen.
     * Currently, not required and unused
     */
    @Override
    public void hide() {

    }


    /**
     * Called when this screen is no longer needed and all resources should be released
     * Used to dispose resources, textures, and other assets, to free up memory.
     */
    @Override
    public void dispose() {
        player_texture.dispose();
        spriteSheet.dispose();
    }
}


/**
 * Activity Class used to represent an activity with its coordinates, type, resource requirements and marker texture
 */
class Activity {
    private final String type;
    private final int x_location;
    private final int y_location;
    private final float energyUsage;
    private final float timeUsage;
    private final TextureRegion marker;

    public Activity(String type, int x_location, int y_location, float energyUsage, float timeUsage, TextureRegion marker) {
        this.type = type;
        this.x_location = x_location;
        this.y_location = y_location;
        this.energyUsage = energyUsage;
        this.timeUsage = timeUsage;
        this.marker = marker;
    }

    public int getX_location() {
        return x_location;
    }

    public int getY_location() {
        return y_location;
    }

    public String getType() {
        return type;
    }

    public float getEnergyUsage() {
        return energyUsage;
    }

    public float getTimeUsage() {
        return timeUsage;
    }

    public TextureRegion getMarker() { return marker; }

    /**
     * Check if the player is close enough to interact with the activity.
     *
     * @param playerX x-coordinate of the player
     * @param playerY y-coordinate of the player
     * @return True if player is close enough to interact, false otherwise.
     */
    public boolean isPlayerClose(float playerX, float playerY){
        int distanceThreshold = 50;
        double distance = Math.sqrt(Math.pow(this.x_location - playerX, 2) + Math.pow(this.y_location - playerY, 2));
        return distance <= distanceThreshold;
    }



}