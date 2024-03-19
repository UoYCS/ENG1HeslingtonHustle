package com.mygdx.game.screens;

// Importing required libraries and classes from libgdx
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.ScreenUtils;


import com.mygdx.game.HesHustle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


import java.util.*;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * MainGameScreen class represents the game
 * ....
 */
public class MainGameScreen implements Screen {

    // Constant for player movement speed
    public static int DEFAULT_SPEED = 150;
    public static float SPEED = DEFAULT_SPEED;

    // Game assets
    Texture player_texture;    // Player Texture
  

    private TiledMap map;

    private OrthogonalTiledMapRenderer mapRenderer;

    private static final int TILE_SIZE = 32;


    int studyPopupIndex;
    int eatPopupIndex;
    int recPopupIndex;
    int sleepPopupIndex;

    Sprite map;     // Map Background Sprite
    Texture signTexture = new Texture(Gdx.files.internal("sign.png"));

    // The textures for the activity markers
    TextureRegion recreationMarker;
    TextureRegion eatMarker;
    TextureRegion studyMarker;
    TextureRegion sleepMarker;

    // Game world dimensions
    final float GAME_WORLD_WIDTH = 1568;
    final float GAME_WORLD_HEIGHT = 1056;



    // Variables for player position
    float player_x = 1360;
    float player_y = 620;

    float camera_x = player_x;
    float camera_y = player_y;

    int energy = 100;


    // Objects used for avatar animation and other textures
    Animation<TextureRegion> walkDownAnimation;
    Animation<TextureRegion> walkRightAnimation;
    Animation<TextureRegion> walkUpAnimation;
    Animation<TextureRegion> walkLeftAnimation;
    Texture spriteSheet = new Texture(Gdx.files.internal("SpriteSheet.png"));
    Texture markersPNG = new Texture(Gdx.files.internal("Markers.png"));

    Texture popupsPNG = new Texture(Gdx.files.internal("InteractionPopups.png"));

    // A variable for tracking elapsed time for the animation
    float stateTime;


    // Minutes from 8am
    // Max Value = 960 @ 12am.
    int time;
    final int MAX_TIME = 960;

    private Timer timer;
    private int timeInterval = 1000;
    public boolean gameTimerStarted = false;
    int day = 0;
    final int gameDaysLength = 7;

    // Counters to track what activities are done on each day
    int[] studyCounter = new int[gameDaysLength];
    int[] recCounter = new int[gameDaysLength];
    int[][] eatCounter = new int[gameDaysLength][3];
    int mealsEaten = 0;

    private int timeLastInteraction = 0;
  
    BitmapFont font = new BitmapFont();


    // Orthographic camera for rendering
    OrthographicCamera camera;

    HesHustle game;

    TextureRegion[][] popups;

    // Initialise an ArrayList to store details about the activities players can interact with
    private final List<Activity> activities = new ArrayList<>();


    /**
     * Constructor for MainGameScreen Class
     *
     * @param game The game instance from HesHustle class.
     */
    public MainGameScreen (HesHustle game){
        this.game = game;

        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("map/GameWorld.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);


        // Setting up the camera with initial position and size
        this.camera = new OrthographicCamera((float) ((float) Gdx.graphics.getWidth() * 0.8), (float) ((float) Gdx.graphics.getHeight()* 0.8));
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


        popups = TextureRegion.split(popupsPNG, popupsPNG.getWidth() / 2, popupsPNG.getHeight() / 6);

        studyPopupIndex = 0;
        recPopupIndex = 1;
        sleepPopupIndex = 2;
        eatPopupIndex = 3;

        // Create Activity instances and add them to the activities ArrayList
        activities.add(new Activity("study", 315, 535, -10, 20, studyMarker, studyPopupIndex));
        activities.add(new Activity("sleep", 1375, 550, 0, 0, sleepMarker, sleepPopupIndex));
        activities.add(new Activity("rec", 700, 360, -20, 20, recreationMarker, recPopupIndex));
        activities.add(new Activity("eat", 1340, 150, 10, 20, eatMarker, eatPopupIndex));


        
    }

    /**
     * show() function is called when MainGameScreen becomes the active screen.
     */
    @Override
    public void show() {
        player_texture = new Texture("Character.png");
        //if (!gameTimerStarted) {
            startGameTimer(time);
        //    gameTimerStarted = true;
        //}

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

        handleMovement(horizontal, vertical);



        if (time == MAX_TIME){
            newDay();
        }

        // Clear Screen and begin rendering
        ScreenUtils.clear(255, 255, 255, 1);
        game.batch.begin();

        // Update the position of the game camera using previous logic
        game.camera.position.set(camera_x, camera_y, 0);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        mapRenderer.setView(game.camera);
        mapRenderer.render();


        for (Activity activity : activities) {
            // For each activity, draw it on the map with its corresponding marker
            game.batch.draw(activity.getMarker(),
                    activity.getX_location() - ((float) activity.getMarker().getRegionWidth() /2),
                    activity.getY_location() - ((float) activity.getMarker().getRegionHeight() /2));


            // If the player is close enough, display the activity popup
            if (activity.isPlayerClose(player_x + ((float) player_texture.getWidth() / 2), player_y + ((float) player_texture.getHeight() / 2))) {
                drawInteractionPopup(activity, 0);
            }
        }




        // Draw player based on previous logic and user input with the corresponding animation
        // Play correct walking animation based on player direction

        if (horizontal == 1){
            spriteAnimate(walkRightAnimation, player_x, player_y);}

        else if (horizontal == -1){
            spriteAnimate(walkLeftAnimation, player_x, player_y);}

        else if (vertical == -1){
            spriteAnimate(walkDownAnimation, player_x, player_y);}

        else if (vertical == 1){
            spriteAnimate(walkUpAnimation, player_x, player_y);}

        // Show the idle character model if the player isn't moving
        else {
            game.batch.draw(player_texture, player_x, player_y);}

        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            System.out.println(player_x + " " + player_y);
        }
        // End rendering for frame

        for (Activity activity : activities) {
            // If the player is close enough, display the activity popup
            if (activity.isPlayerClose(player_x + ((float) player_texture.getWidth() / 2), player_y + ((float) player_texture.getHeight() / 2))) {
                drawInteractionPopup(activity, 0);
            }
        }

        // Allow for user interaction with activities.
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            handleActivityInteraction();}

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            stopGameTimer();
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(this.game, this));

        }

      
      
        // Set colour of energy bar based on energy level
        // If health above 66, green, if above 33, yellow, if below 33, red
        String colour;
        if (this.energy > 66){
            colour = "green";
        }
        else if (this.energy > 33){
            colour = "yellow";
        }
        else {
            colour = "red";
        }

        float energyBarX = game.camera.position.x + game.camera.viewportWidth / 2 - 128 - 375; // 128 is the width of the health bar, 10 is the offset
        float energyBarY = game.camera.position.y - game.camera.viewportHeight / 2 + 10; // 10 is the offset


        // Draw the energy bar in bottom right corner of the screen
        game.batch.draw(new Texture("energy_fill_" + colour + ".png"), energyBarX, energyBarY, (int) (this.energy * 1.28), 16);
        game.batch.draw(new Texture("energy_bar.png"), energyBarX, energyBarY, 128, 16);

        // Calculate the position for the sign
        float signX = game.camera.position.x - game.camera.viewportWidth / 2;
        float signY = game.camera.position.y + game.camera.viewportHeight / 2 - 1.5f * signTexture.getHeight();

        // Draw the sign with double the width and 1.5 times the height
        game.batch.draw(signTexture, signX, signY, 3 * signTexture.getWidth(), 2f * signTexture.getHeight());

        // Calculate the position for the font
        float fontX = signX + 15;
        float fontY = signY + 1f * signTexture.getHeight();

        //Calculate time from the time variable
        int hours = (time / 60) + 8;
        int minutes = time % 60;

        if (hours > 23){
            hours = 0;
        }

        if (hours > 12){
            hours -= 12;
        }

        if (hours == 0){
            hours = 12;
        }

        if (minutes < 10){
            font.draw(game.batch, "Time: " + hours + ":0" + minutes, fontX, fontY);
        }
        else {
            font.draw(game.batch, "Time: " + hours + ":" + minutes, fontX, fontY);
        }

      
      
      
      
        game.batch.end();



    }

    private void drawInteractionPopup(Activity activity, int mode){

        float popupXLocation = activity.getX_location() - ((float) popups[activity.getPopupIndex()][mode].getRegionWidth() / 2);
        float popupYLocation = activity.getY_location() + ((float) activity.getMarker().getRegionHeight() / 3);



        if (time - timeLastInteraction <= 1 && timeLastInteraction != 0) {
            //System.out.println(timeLastInteraction);
            game.batch.draw(popups[activity.getPopupIndex()][1],popupXLocation,popupYLocation);
            return;
        }
        
        else{
            if (mode ==1){
                timeLastInteraction = time;
            }
            game.batch.draw(popups[activity.getPopupIndex()][mode],popupXLocation,popupYLocation);

        }

        if ((Objects.equals(activity.getType(), "eat") && mealsEaten == 3) && time - timeLastInteraction > 3){
            game.batch.draw(popups[4][0], popupXLocation, popupYLocation);
        }

        else if (energy + activity.getEnergyUsage() < 0 && time - timeLastInteraction > 3){
            game.batch.draw(popups[4][1],popupXLocation,popupYLocation);
        }

        else if (time + activity.getTimeUsage() >= MAX_TIME && time - timeLastInteraction > 3){
            game.batch.draw(popups[5][0],popupXLocation,popupYLocation);
        }
    }

    public void setPlayerPosition(int x, int y){
        player_x = x;
        player_y = y;
    }

    private void handleMovement(int horizontal, int vertical) {

        if (energy <= 10){
            SPEED = (float) DEFAULT_SPEED / 2;
        } else {
            SPEED = DEFAULT_SPEED;
        }

        // Checking if player is moving diagonally and if so to normalise speed

        // This is important as without normalisation,
        // the player would be faster when moving diagonally due to Pythagorean theorem

        if (horizontal != 0 && vertical != 0) {
            float length = (float) Math.sqrt(Math.pow(horizontal, 2) + Math.pow(vertical, 2));
            float horizontal_normalised = horizontal / length;
            float vertical_normalised = vertical / length;

            float y_movement = vertical_normalised * SPEED * Gdx.graphics.getDeltaTime();
            float x_movement = horizontal_normalised * SPEED * Gdx.graphics.getDeltaTime();

            if (!tileBlocked((int) player_x + (player_texture.getWidth()/2), (int) ((int) player_y + y_movement))){
                player_y += y_movement;
            }

            if (!tileBlocked((int) ((int) player_x + (player_texture.getWidth()/2) + x_movement), (int) player_y)){
                player_x += x_movement;
            }

        } else {
            float y_movement = (vertical   * SPEED) * Gdx.graphics.getDeltaTime();
            float x_movement = (horizontal * SPEED) * Gdx.graphics.getDeltaTime();

            if (!tileBlocked((int) ((int) player_x + (player_texture.getWidth()/2) + x_movement), (int) ((int) player_y-1 + y_movement))){
                player_y += y_movement;
                player_x += x_movement;
            }
        }


        //  Bounds checking to keep the player within map boundaries
        player_x = Math.max(0,
                Math.min(player_x,
                        GAME_WORLD_WIDTH - player_texture.getWidth()));

        player_y = Math.max(0,
                Math.min(player_y,
                        GAME_WORLD_HEIGHT - player_texture.getHeight()));

        // Bounds checking to keep camera within map boundaries
        camera_x = Math.min(
                Math.max(player_x + (float) player_texture.getWidth() / 2, camera.viewportWidth / 2),
                GAME_WORLD_WIDTH - camera.viewportWidth / 2);

        camera_y = Math.min(
                Math.max(player_y + (float) player_texture.getWidth() / 2, camera.viewportHeight / 2),
                GAME_WORLD_HEIGHT - camera.viewportHeight / 2);
    }

    private void handleActivityInteraction(){
        for (Activity activity : activities) {
            if (activity.isPlayerClose(player_x + ((float) player_texture.getWidth() /2), player_y + ((float) player_texture.getHeight() /2))){
                if (Objects.equals(activity.getType(), "sleep")) {
                    drawInteractionPopup(activity, 1);
                    newDay();
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
                        drawInteractionPopup(activity, 1);
                    }
                }

                else if (this.energy + activity.getEnergyUsage() >= 0) {
                    this.energy += (int) activity.getEnergyUsage();
                    this.time += (int) activity.getTimeUsage();
                    drawInteractionPopup(activity, 1);
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



    private void newDay(){
        stopGameTimer();
        day += 1;
        energy = 100;
        time = 0;
        mealsEaten = 0;
        timeLastInteraction = 0;
        ((Game) Gdx.app.getApplicationListener()).setScreen(new DayScreen(this.game, this, day, studyCounter, recCounter, eatCounter));
    }


    // method for displaying the avatar animation to the screen
    public void spriteAnimate(Animation<TextureRegion> animation, float player_x, float player_y){
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        game.batch.draw(currentFrame, player_x, player_y);
    }

    private boolean tileBlocked(int x, int y){
        for(MapLayer layer : map.getLayers()){
            TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;

            TiledMapTileLayer.Cell cell = tileLayer.getCell(x / TILE_SIZE, y / TILE_SIZE);
            if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked")) {
                return true;
            }
        }
    return false;
    }

    public void startGameTimer(int timerTime) {
        stopGameTimer();
        timer = new Timer();
        time = timerTime;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time += 1;
            }
        }, 0, timeInterval/10);
    }

    public void stopGameTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
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
        map.dispose();
        mapRenderer.dispose();
        //stopGameTimer();
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
    private final int popup;

    public Activity(String type, int x_location, int y_location, float energyUsage, float timeUsage, TextureRegion marker, int popupIndex) {
        this.type = type;
        this.x_location = x_location;
        this.y_location = y_location;
        this.energyUsage = energyUsage;
        this.timeUsage = timeUsage;
        this.marker = marker;
        this.popup = popupIndex;
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

    public int getPopupIndex() { return popup; }


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