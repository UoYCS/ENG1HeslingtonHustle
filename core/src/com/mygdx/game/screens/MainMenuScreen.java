package com.mygdx.game.screens;

// Importing required libraries and classes
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.HesHustle;
import com.badlogic.gdx.math.Rectangle;

/**
 * Screen used to show the Main menu screen
 * Also used as the Pause screen from the game
 */
public class MainMenuScreen implements Screen {

    private final SpriteBatch menuBatch; // SpriteBatch for rendering

    Texture playButtonTexture;
    Texture resumeButtonTexture;
    Texture quitButtonTexture;

    Texture muteButtonTexture;
    Texture fullButtonTexture;
    Texture title;
    Texture background;

    private Rectangle playButtonBounds;
    private Rectangle quitButtonBounds;
    private Rectangle muteButtonBounds;
    private Rectangle fullButtonBounds;

    boolean muted;

    HesHustle game;

    MainGameScreen gameScreen = null;

    /**
     * Constructor for MainMenuScreen
     *
     * @param game Instance of HesHustle game
     * @param gameScreen Instance of a MainGameScreen
     *                   Used to resume a game after a game is paused
     *                   Null if no game is in progress
     */
    public MainMenuScreen(HesHustle game, MainGameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;



        // Initialising rendering variables
        menuBatch = new SpriteBatch();

        title = new Texture(Gdx.files.internal("Title.png"));
        background = new Texture(Gdx.files.internal("background.png"));

        playButtonTexture = new Texture(Gdx.files.internal("StartButton.png"));
        resumeButtonTexture = new Texture(Gdx.files.internal("ResumeButton.png"));
        quitButtonTexture = new Texture(Gdx.files.internal("QuitButton.png"));
        muteButtonTexture = new Texture(Gdx.files.internal("VolumeButton.png"));

        // Change the fullscreen button depending on whether the game is fullscreen or windowed
        if (Gdx.graphics.isFullscreen()){
            fullButtonTexture = new Texture(Gdx.files.internal("MinimiseButton.png"));
        }
        else{
            fullButtonTexture = new Texture(Gdx.files.internal("FullScreenButton.png"));
        }

        muted = false;

        updateButtonBounds();
    }

    /**
     * Function used to update the bounding boxes for button clicks
     * Required for when resizing screen
     */
    private void updateButtonBounds() {

        // Define the bounding rectangles for the buttons
        float longButtonWidth = playButtonTexture.getWidth() * 1.5f;
        float longButtonHeight = playButtonTexture.getHeight() * 1.5f;

        float shortButtonWidth = muteButtonTexture.getWidth() * 1.5f;
        float shortButtonHeight = muteButtonTexture.getHeight() * 1.5f;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float startX = (screenWidth - longButtonWidth * 2) / 2;

        int offset = 30;

        playButtonBounds = new Rectangle(startX - offset , (float) (screenHeight * 0.3), longButtonWidth, longButtonHeight);
        quitButtonBounds = new Rectangle(startX + offset + longButtonWidth, (float) (screenHeight * 0.3), longButtonWidth, longButtonHeight);

        int gap = 5;
        fullButtonBounds = new Rectangle(screenWidth - shortButtonWidth * 2 - gap * 2, gap, shortButtonWidth, shortButtonHeight);
        muteButtonBounds = new Rectangle(screenWidth - shortButtonWidth, gap, shortButtonWidth, shortButtonHeight);
    }


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
        // Clear Screen
        ScreenUtils.clear(0, 0, 0, 1);


        menuBatch.begin();

        // Render the background, buttons, and title
        menuBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (this.gameScreen != null){
            menuBatch.draw(resumeButtonTexture, playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        }
        else{
            menuBatch.draw(playButtonTexture, playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        }
        menuBatch.draw(quitButtonTexture, quitButtonBounds.x, quitButtonBounds.y, quitButtonBounds.width, quitButtonBounds.height);

        if (this.game.gameMuted){
            muteButtonTexture = new Texture(Gdx.files.internal("Muted.png"));
        }else{
            muteButtonTexture = new Texture(Gdx.files.internal("VolumeButton.png"));
        }


        menuBatch.draw(muteButtonTexture, muteButtonBounds.x, muteButtonBounds.y, muteButtonBounds.width, muteButtonBounds.height);
        menuBatch.draw(fullButtonTexture, fullButtonBounds.x, fullButtonBounds.y, fullButtonBounds.width, fullButtonBounds.height);
        menuBatch.draw(title, (float)(Gdx.graphics.getWidth() - title.getWidth())/2, (float) (Gdx.graphics.getHeight()*0.8), title.getWidth(), title.getHeight());

        // End rendering
        menuBatch.end();

        // If a mouse click is detected
        if (Gdx.input.justTouched()) {

            // Getting X and Y location of mouse press

            float touchX = Gdx.input.getX();
            // Invert Y-Coordinate as input.getY() has origin at top left of screen
            // Whereas rendering and other logic is done from the bottom left
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Invert Y-coordinate


            // For each button bounding box, check if it contains the location of the mouse click


            if (playButtonBounds.contains(touchX, touchY)) {
                // Check if there is already a MainGameScreen in progress
                if (this.gameScreen != null){
                    // If so, set the existing gameScreen as the active screen
                    ((Game) Gdx.app.getApplicationListener()).setScreen(this.gameScreen);

                }else {
                    // If there is no active gameScreen, set a new MainGameScreen as the active screen
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainGameScreen(this.game));
                }

            } else if (quitButtonBounds.contains(touchX, touchY)) {
                // If quit button pressed, exit the game
                Gdx.app.exit();

            } else if (muteButtonBounds.contains(touchX, touchY)) {
                // If mute button pressed, mute/unmute the game
                this.game.gameMuted = !this.game.gameMuted;

                if (this.game.gameMuted){
                    this.game.backgroundMusic.setVolume(0);
                } else{
                    this.game.backgroundMusic.setVolume(this.game.defaultVolume);
                }

            } else if (fullButtonBounds.contains(touchX, touchY)) {
                // If fullscreen button pressed, fullscreen/window the game

                if (!Gdx.graphics.isFullscreen()) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(this.game, this.gameScreen));
                } else {
                    Gdx.graphics.setWindowedMode(1024, 576); // Set your preferred window size
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(this.game, this.gameScreen));
                }
                updateButtonBounds();

            }
        }

    }


    /**
     * Called when screen is resized.
     * Updates the button bounding boxes and correctly redraws the buttons
     * @param width Width of screen
     * @param height Height of screen
     */
    @Override
    public void resize(int width, int height) {
        updateButtonBounds();

        menuBatch.begin();

        menuBatch.draw(playButtonTexture, playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        menuBatch.draw(quitButtonTexture, quitButtonBounds.x, quitButtonBounds.y, quitButtonBounds.width, quitButtonBounds.height);

        menuBatch.draw(muteButtonTexture, muteButtonBounds.x, muteButtonBounds.y, muteButtonBounds.width, muteButtonBounds.height);
        menuBatch.draw(fullButtonTexture, fullButtonBounds.x, fullButtonBounds.y, fullButtonBounds.width, fullButtonBounds.height);

        menuBatch.end();
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
     * Called when MainMenuScreen is no longer needed
     */
    @Override
    public void dispose() {
        menuBatch.dispose();
        playButtonTexture.dispose();
        quitButtonTexture.dispose();
        muteButtonTexture.dispose();
        fullButtonTexture.dispose();
        title.dispose();
        background.dispose();
    }
}

