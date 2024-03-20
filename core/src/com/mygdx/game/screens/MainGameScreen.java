package com.mygdx.game.screens;

// Importing required libraries and classes
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.HesHustle;
import java.util.*;

/**
 * MainGameScreen class represents the game
 * Handles rendering of the game and all game logic.
 */
public class MainGameScreen implements Screen {

    // Players Default movement speed
    public static int DEFAULT_SPEED = 150;
    public static float SPEED = DEFAULT_SPEED;


    // Player Texture
    Texture player_texture;

    // Variables for Map
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private static final int TILE_SIZE = 32;

    // Hanging Sign texture for time HUD
    Texture signTexture = new Texture(Gdx.files.internal("sign.png"));


    // Texture region to store activity interaction popups and relevant indices
    Texture popupsPNG = new Texture(Gdx.files.internal("InteractionPopups.png"));
    TextureRegion[][] popups;
    int studyPopupIndex;
    int eatPopupIndex;
    int recPopupIndex;
    int sleepPopupIndex;

    // Textures for the activity markers
    Texture markersPNG = new Texture(Gdx.files.internal("Markers.png"));
    TextureRegion recreationMarker;
    TextureRegion eatMarker;
    TextureRegion studyMarker;
    TextureRegion sleepMarker;


    // Objects used for avatar animation and other textures
    Animation<TextureRegion> walkDownAnimation;
    Animation<TextureRegion> walkRightAnimation;
    Animation<TextureRegion> walkUpAnimation;
    Animation<TextureRegion> walkLeftAnimation;
    Texture spriteSheet = new Texture(Gdx.files.internal("SpriteSheet.png"));

    // Variable for tracking elapsed time for the animation
    float stateTime;

    // Game world dimensions
    final float GAME_WORLD_WIDTH = 1568;
    final float GAME_WORLD_HEIGHT = 1056;

    public final float SPAWN_POINT_X = 1360;
    public final float SPAWN_POINT_Y = 620;

    // Variables for player position initialised to spawn point
    float player_x = SPAWN_POINT_X;
    float player_y = SPAWN_POINT_Y;

    // Camera position variables initialised to players position
    float camera_x = player_x;
    float camera_y = player_y;

    // Variable to store players energy initialised to 100
    int energy = 100;


    // Time variable stored as an integer value
    // Minutes from 8am
    int time;

    // Max Value of time = 960 @ 12am.
    final int MAX_TIME = 960;

    // Timer used to progress time in game
    private Timer timer;

    // Variable to store current day, initialised at 0
    int day = 0;

    // Number of days in the game
    final int gameDaysLength = 7;

    // Counters to track what activities are done on each day
    int[] studyCounter = new int[gameDaysLength];
    int[] recCounter = new int[gameDaysLength];
    int[][] eatCounter = new int[gameDaysLength][3];
    int mealsEaten = 0;

    // Initialise an ArrayList to store details about the activities players can interact with
    private final List<Activity> activities = new ArrayList<>();

    // Timestamp of last interaction with an activity
    private int timeLastInteraction = 0;


    BitmapFont font = new BitmapFont();


    // Orthographic camera for rendering the game
    OrthographicCamera camera;

    // Viewport size used to keep camera at consistent zoom
    final float originalViewportWidth = 800;
    final float originalViewportHeight = 450;

    // HesHustle game instance
    HesHustle game;

    // Initialise sound effect variables
    Sound eating_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/eating.mp3"));
    Sound rec_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/ducks.mp3"));
    Sound low_energy_sound = Gdx.audio.newSound(Gdx.files.internal("sfx/no_energy.mp3"));
    Sound low_energy_sound_long = Gdx.audio.newSound(Gdx.files.internal("sfx/no_energy_long.mp3"));
    Music sleep_sound = Gdx.audio.newMusic(Gdx.files.internal("sfx/sleeping_fade_out.mp3"));
    Music study_sound = Gdx.audio.newMusic(Gdx.files.internal("sfx/studying.mp3"));
  
    boolean bgMuted = false;



    /**
     * Constructor for MainGameScreen Class
     *
     * @param game The game instance from HesHustle class.
     */
    public MainGameScreen (HesHustle game){
        this.game = game;

        // Load the TMX map
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("map/GameWorld.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);


        // Setting up the camera with initial position and size
        this.camera = new OrthographicCamera((float) ((float) Gdx.graphics.getWidth() * 0.8), (float) ((float) Gdx.graphics.getHeight()* 0.8));
        this.camera.position.set(GAME_WORLD_WIDTH / 2, GAME_WORLD_HEIGHT / 2, 0);
        this.camera.update();

        // Initialising camera location to player's initial position
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


        // Stores the marker textures in the corresponding variables
        TextureRegion[][] tmpMarkers = TextureRegion.split(markersPNG, markersPNG.getWidth() / 4, markersPNG.getHeight());
       
        recreationMarker = tmpMarkers[0][0];
        eatMarker = tmpMarkers[0][1];
        studyMarker = tmpMarkers[0][2];
        sleepMarker = tmpMarkers[0][3];


        // Initialise the interaction popup TextureRegion and assign activities specific indices
        popups = TextureRegion.split(popupsPNG, popupsPNG.getWidth() / 2, popupsPNG.getHeight() / 6);

        studyPopupIndex = 0;
        recPopupIndex = 1;
        sleepPopupIndex = 2;
        eatPopupIndex = 3;

        // Create Activity instances and add them to the activities ArrayList
        activities.add(new Activity("study", 315, 535, -40, 150, studyMarker, studyPopupIndex));
        activities.add(new Activity("sleep", 1375, 550, 0, 0, sleepMarker, sleepPopupIndex));
        activities.add(new Activity("rec", 700, 360, -30, 60, recreationMarker, recPopupIndex));
        activities.add(new Activity("eat", 1340, 150, 10, 30, eatMarker, eatPopupIndex));

    }

    /**
     * show() function is called when MainGameScreen becomes the active screen.
     */
    @Override
    public void show() {
        player_texture = new Texture("Character.png");
        startGameTimer(time);
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

        // Use handleMovement method to apply player movement and handle logic
        handleMovement(horizontal, vertical);


        // Check if the time has reached the end of the day
        if (time == MAX_TIME){
            // If so, progress to next day
            newDay();
        }

        // Clear Screen and begin rendering
        ScreenUtils.clear(255, 255, 255, 1);
        game.batch.begin();

        // Update the position of the game camera using logic within handleMovement()
        game.camera.position.set(camera_x, camera_y, 0);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        // Render the map with the game camera
        mapRenderer.setView(game.camera);
        mapRenderer.render();


        for (Activity activity : activities) {
            // For each activity, draw it on the map with its corresponding marker
            game.batch.draw(activity.getMarker(),
                    activity.getXLocation() - ((float) activity.getMarker().getRegionWidth() /2),
                    activity.getYLocation() - ((float) activity.getMarker().getRegionHeight() /2));
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



        // If the player is close enough, display the activity popup
        for (Activity activity : activities) {
            if (activity.isPlayerClose(player_x + ((float) player_texture.getWidth() / 2), player_y + ((float) player_texture.getHeight() / 2))) {
                drawInteractionPopup(activity, 0);
            }
        }

        // Allow for user interaction with activities.
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            handleActivityInteraction();}

        // Allow the user to pause the game with Escape key
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            stopGameTimer(); // Stop game timer to pause day progression
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(this.game, this));
        }

      
        // Draw game HUD
        drawEnergyBar();
        drawTimeSign();
      

        // If the background is currently muted or the game is muted
        // set the bg music volume to 0
        if (bgMuted || this.game.gameMuted){
            this.game.backgroundMusic.setVolume(0);
        } else{
            this.game.backgroundMusic.setVolume(this.game.defaultVolume);
        }


        // End rendering for the frame
        game.batch.end();
    }

    /**
     * Set the player position.
     * Used by DayScreen class to reset player at spawn point
     * @param x X coordinate of player
     * @param y Y coordinate of player
     */
    public void setPlayerPosition(int x, int y){
        player_x = x;
        player_y = y;
    }

    /**
     * Handle the players movement including collisions and bounds detection
     * Changes camera position also to be centered on player or locked to edge of screen
     *
     * @param horizontal Horizontal (x) movement input
     * @param vertical Vertical (y) movement input
     */
    private void handleMovement(int horizontal, int vertical) {

        // Check if the player has low energy
        if (energy <= 15){
            // If so, half the movement speed
            SPEED = (float) DEFAULT_SPEED / 2;
        } else {
            SPEED = DEFAULT_SPEED;
        }


        /*
         * Check if player is moving diagonally and if so normalise the speed
         *
         * This is important as without normalisation, the player would be faster
         * when moving diagonally due to the diagonal of a square being longer than
         * the side.
         */

        if (horizontal != 0 && vertical != 0) {

            // Normalise speed
            float length = (float) Math.sqrt(Math.pow(horizontal, 2) + Math.pow(vertical, 2));
            float horizontal_normalised = horizontal / length;
            float vertical_normalised = vertical / length;

            float y_movement = vertical_normalised * SPEED * Gdx.graphics.getDeltaTime();
            float x_movement = horizontal_normalised * SPEED * Gdx.graphics.getDeltaTime();

            // Only add movement to player if resulting tile is not blocked
            if (!tileBlocked((int) player_x + (player_texture.getWidth()/2), (int) ((int) player_y + y_movement))){
                player_y += y_movement;
            }
            if (!tileBlocked((int) ((int) player_x + (player_texture.getWidth()/2) + x_movement), (int) player_y)){
                player_x += x_movement;
            }

        } else {
            float y_movement = (vertical   * SPEED) * Gdx.graphics.getDeltaTime();
            float x_movement = (horizontal * SPEED) * Gdx.graphics.getDeltaTime();

            // Only add movement to player if resulting tile is not blocked
            if (!tileBlocked((int) ((int) player_x + (player_texture.getWidth()/2) + x_movement), (int) ((int) player_y + y_movement))){
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


        // Bounds checking to keep camera within map boundaries at all times
        camera_x = Math.min(
                Math.max(player_x + (float) player_texture.getWidth() / 2, camera.viewportWidth / 2),
                GAME_WORLD_WIDTH - camera.viewportWidth / 2);

        camera_y = Math.min(
                Math.max(player_y + (float) player_texture.getWidth() / 2, camera.viewportHeight / 2),
                GAME_WORLD_HEIGHT - camera.viewportHeight / 2);
    }

    /**
     * Checks to see if the x and y position provided is within a tile that
     * has the blocked property in the map files.
     *
     * @param x X coordinate of position to check
     * @param y Y coordinate of position to check
     * @return true if position (x,y) on the map is blocked, otherwise false
     */
    private boolean tileBlocked(int x, int y){

        // Iterate through all layers of the map to check if there is a blocked tile
        for(MapLayer layer : map.getLayers()){
            TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
            TiledMapTileLayer.Cell cell = tileLayer.getCell(x / TILE_SIZE, y / TILE_SIZE);
            if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("blocked")) {
                return true;
            }
        }
        // If no blocked tile, return false
        return false;
    }

    /**
     * Progresses the game to the next day.
     * Stops the days timer and updates relevant variables,
     * then displays the day summary screen
     */
    private void newDay(){
        
        if (!this.game.gameMuted){
            bgMuted = true;
            sleep_sound.play();
            sleep_sound.setOnCompletionListener(music -> bgMuted = false);
        }
      
        stopGameTimer();
        day += 1;
        energy = 100;
        time = 0;
        mealsEaten = 0;
        timeLastInteraction = 0;
        ((Game) Gdx.app.getApplicationListener()).setScreen(new DayScreen(this.game, this, day, studyCounter, recCounter, eatCounter));
    }
  

    /**
     * Draws the energy bar onto the game
     */
    private void drawEnergyBar(){

        // Set colour of energy bar based on energy level
      
        // If energy above 60, green
        // If above 20, yellow
        // If below 20, red, and play low energy sound
      
        String colour;
        if (this.energy > 60){
            colour = "green";
        }
        else if (this.energy > 20){
            colour = "yellow";
        }
        else {
            colour = "red";
        }

        // Set the position of the bar to the bottom left of the screen (plus an offset)
        float energyBarX = game.camera.position.x - game.camera.viewportWidth / 2 + 11;
        float energyBarY = game.camera.position.y - game.camera.viewportHeight / 2 + 10;


        // Draw the energy bar in bottom right corner of the screen
        game.batch.draw(new Texture("energy_fill_" + colour + ".png"), energyBarX, energyBarY, (int) (this.energy * 1.28), 16);
        game.batch.draw(new Texture("energy_bar_temp.png"), energyBarX, energyBarY, 128, 16);
    }

    /**
     * Draw the sign with current time onto the game
     */
    private void drawTimeSign(){

        // Calculate the position for the sign
        float signX = game.camera.position.x - game.camera.viewportWidth / 2;
        float signY = game.camera.position.y + game.camera.viewportHeight / 2 - 1.5f * signTexture.getHeight();

        // Draw the sign with double the width and 1.5 times the height
        game.batch.draw(signTexture, signX, signY, 3 * signTexture.getWidth(), 2f * signTexture.getHeight());

        //Calculate time from the time variable
        int hours = (time / 60) + 8;
        int minutes = time % 60;

        // Adjust font size
        font.getData().setScale(1.5F);
        // Make sure text position can have float values of X,Y to prevent shaking
        font.setUseIntegerPositions(false);

        // Round the minutes to the lower 10 so the timer only goes up in intervals of 10 minutes
        int rounded_minutes = (int) (Math.floor(minutes / 10.0) * 10);


        // Draw the time onto the screen

        GlyphLayout layout;

        if (rounded_minutes == 0){
            layout = new GlyphLayout(font, hours + ":0" + rounded_minutes);
        }
        else {
            layout = new GlyphLayout(font, hours + ":" + rounded_minutes);
        }

        float fontX = signX + ((float) 1.5 * signTexture.getWidth()) - layout.width / 2;
        float fontY = signY + 1f * signTexture.getHeight();

        if (rounded_minutes == 0){
            font.draw(game.batch, hours + ":0" + rounded_minutes, fontX, fontY);
        }
        else {
            font.draw(game.batch, hours + ":" + rounded_minutes, fontX, fontY);
        }


    }

    /**
     * Draws the interaction popup that appears above activity markers
     * and handles logic to display the correct type.
     *
     * @param activity Activity object which the popup is being drawn for
     * @param mode Whether the activity has been pressed or not (1 or 0)
     */
    private void drawInteractionPopup(Activity activity, int mode){

        // Location of popup
        float popupXLocation = activity.getXLocation() - ((float) popups[activity.getPopupIndex()][mode].getRegionWidth() / 2);
        float popupYLocation = activity.getYLocation() + ((float) activity.getMarker().getRegionHeight() / 3);


        // If the activity has recently been done, the popup will keep drawing the green (pressed) popup.
        if (time - timeLastInteraction <= 1 && timeLastInteraction != 0) {
            game.batch.draw(popups[activity.getPopupIndex()][1],popupXLocation,popupYLocation);
            return;
        }

        else{
            // If the activity is being completed, update the time of last interaction
            if (mode ==1){
                timeLastInteraction = time;
            }
            game.batch.draw(popups[activity.getPopupIndex()][mode],popupXLocation,popupYLocation);

        }

        // Draw a message popup if the player has already eaten 3 times today and cant eat anymore
        if ((Objects.equals(activity.getType(), "eat") && mealsEaten == 3) && time - timeLastInteraction > 3){
            game.batch.draw(popups[4][0], popupXLocation, popupYLocation);
        }

        // Draw message popups if the player does not have the energy/time to do an activity
        else if (energy + activity.getEnergyUsage() < 0 && time - timeLastInteraction > 3){
            game.batch.draw(popups[4][1],popupXLocation,popupYLocation);
        }
        else if (time + activity.getTimeUsage() >= MAX_TIME && time - timeLastInteraction > 3){
            game.batch.draw(popups[5][0],popupXLocation,popupYLocation);
        }
    }

    /**
     * Handles the logic behind interacting with activities
     */
    private void handleActivityInteraction(){
        for (Activity activity : activities) {

            // Only let the player interact with an activity if they are close enough
            if (activity.isPlayerClose(player_x + ((float) player_texture.getWidth() /2), player_y + ((float) player_texture.getHeight() /2))){

                // If the player has interacted with the sleep activity, go to the next day
                if (Objects.equals(activity.getType(), "sleep")) {
                    drawInteractionPopup(activity, 1);
                    newDay();
                }

                /*
                 * If the player has interacted with the eat activity,
                 * If they haven't eaten 3 times today, append the time to the
                 * eatCounter array and increase their energy/the game time
                 * and increment the mealsEaten counter.
                 */
                else if (Objects.equals(activity.getType(), "eat")) {
                    if (mealsEaten != 3){
                     
                        if(!this.game.gameMuted){
                            eating_sound.play(1.0f);
                        }
                      
                        eatCounter[day][mealsEaten] = time;
                        mealsEaten++;
                        this.energy += (int) activity.getEnergyUsage();
                        this.time += (int) activity.getTimeUsage();
                        drawInteractionPopup(activity, 1);
                    }
                }

                /*
                 * Otherwise, check if the user has enough energy to do the task,
                 * if so, decrease the energy and increase the time,
                 * and increment the relevant activities counter
                 */
                else if (this.energy + activity.getEnergyUsage() >= 0) {
                    this.energy += (int) activity.getEnergyUsage();
                    this.time += (int) activity.getTimeUsage();
                    drawInteractionPopup(activity, 1);
                    if (Objects.equals(activity.getType(), "study")) {

                        if(!this.game.gameMuted){
                            bgMuted = true;
                            study_sound.play();
                            study_sound.setOnCompletionListener(music -> bgMuted = false);
                        }



                        studyCounter[day]++;
                    }

                    if (Objects.equals(activity.getType(), "rec")) {
                        if(!this.game.gameMuted){
                            rec_sound.play(1.0f);
                        }
                        recCounter[day]++;
                    }

                } else {
                    low_energy_sound.play(1.0f);
                }

                // Make sure the users energy can't go above 100 from eating
                if (this.energy >= 100) {
                    this.energy = 100;
                }

            }
        }

    }



    /**
     * Method for displaying the avatar animation to the screen
     *
     * @param animation The animation to be shown
     * @param player_x Players current x coordinate
     * @param player_y Players current y coordinate
     */
    public void spriteAnimate(Animation<TextureRegion> animation, float player_x, float player_y){
        // Update the games state time
        stateTime += Gdx.graphics.getDeltaTime();

        // Get the current frame of animation required
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        // Draw this frame at the players position
        game.batch.draw(currentFrame, player_x, player_y);
    }


    /**
     * Starts a new game timer at the time specified and updates the
     * in game time at a set rate.
     *
     * @param timerTime Time to start the timer at
     */
    public void startGameTimer(int timerTime) {
        stopGameTimer();
        timer = new Timer();
        time = timerTime;
        int timeInterval = 1000;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time += 1;
            }
        }, 0, timeInterval /10);
    }

    /**
     * Stop the game timer
     */
    public void stopGameTimer() {
        if(timer != null) {
            timer.cancel();
            // Set the timer variable to null once it has been cancelled
            timer = null;
        }
    }


    /**
     * Called when screen is resized.
     *
     * @param width Width of screen.
     * @param height Height of screen.
     */
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = originalViewportWidth;
        camera.viewportHeight= originalViewportHeight;
        camera.update();
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
        eating_sound.dispose();
        study_sound.dispose();
        low_energy_sound.dispose();
        rec_sound.dispose();
    }
}


/**
 * Activity Class used to represent an activity.
 * Stores the activities position, type, resource requirements, marker texture and popup index
 */
class Activity {
    private final String type;
    private final int xLocation;
    private final int yLocation;
    private final float energyUsage;
    private final float timeUsage;
    private final TextureRegion marker;
    private final int popup;

    /**
     * Activity clas constructor
     *
     * @param type Type of activity of : "sleep", "eat", "study", "rec"
     * @param x_location X coordinate of activity
     * @param y_location Y coordinate of activity
     * @param energyUsage Energy used by doing activity
     * @param timeUsage Time taken to do activity
     * @param marker Marker texture to draw at all times
     * @param popupIndex Index of which interaction popup to use for this activity.
     */
    public Activity(String type, int x_location, int y_location, float energyUsage, float timeUsage, TextureRegion marker, int popupIndex) {
        this.type = type;
        this.xLocation = x_location;
        this.yLocation = y_location;
        this.energyUsage = energyUsage;
        this.timeUsage = timeUsage;
        this.marker = marker;
        this.popup = popupIndex;
    }

    /**
     * @return X coordinate of player
     */
    public int getXLocation() {
        return xLocation;
    }

    /**
     * @return Y coordinate of player
     */
    public int getYLocation() {
        return yLocation;
    }

    /**
     * @return type of activity
     */
    public String getType() {
        return type;
    }

    /**
     * @return Energy used by activity
     */
    public float getEnergyUsage() {
        return energyUsage;
    }

    /**
     * @return Time taken to do activity
     */
    public float getTimeUsage() {
        return timeUsage;
    }

    /**
     * @return Activity marker Texture Region
     */
    public TextureRegion getMarker() {
        return marker;
    }

    /**
     * @return Index for interaction popup texture region
     */
    public int getPopupIndex() {
        return popup;
    }


    /**
     * Check if the player is close enough to interact with the activity.
     *
     * @param playerX x-coordinate of the player
     * @param playerY y-coordinate of the player
     * @return True if player is close enough to interact, false otherwise.
     */
    public boolean isPlayerClose(float playerX, float playerY){
        int distanceThreshold = 50;
        double distance = Math.sqrt(Math.pow(this.xLocation - playerX, 2) + Math.pow(this.yLocation - playerY, 2));
        return distance <= distanceThreshold;
    }
}