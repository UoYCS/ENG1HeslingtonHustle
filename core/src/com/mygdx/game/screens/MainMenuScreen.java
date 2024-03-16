package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.HesHustle;
import com.badlogic.gdx.math.Rectangle;

public class MainMenuScreen implements Screen {

    private BitmapFont font;
    private SpriteBatch menuBatch;

    Texture playButtonTexture;
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

    public MainMenuScreen(HesHustle game) {

        this.game = game;

        menuBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(5); // Adjust the scale as needed


        title = new Texture(Gdx.files.internal("Title.png"));
        background = new Texture(Gdx.files.internal("background.png"));

        playButtonTexture = new Texture(Gdx.files.internal("StartButton.png"));
        quitButtonTexture = new Texture(Gdx.files.internal("QuitButton.png"));

        muteButtonTexture = new Texture(Gdx.files.internal("VolumeButton.png"));

        if (Gdx.graphics.isFullscreen()){
            fullButtonTexture = new Texture(Gdx.files.internal("MinimiseButton.png"));
        }
        else{
            fullButtonTexture = new Texture(Gdx.files.internal("FullScreenButton.png"));
        }

        muted = false;

        updateButtonBounds();
    }

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

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);



        menuBatch.begin();

//        GlyphLayout layout = new GlyphLayout();
//        layout.setText(font, "Main Menu\nPress F to Continue");
//        font.draw(menuBatch, layout, (screenWidth - layout.width) / 2, (float) (screenHeight * 0.8));



        menuBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        menuBatch.draw(playButtonTexture, playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        menuBatch.draw(quitButtonTexture, quitButtonBounds.x, quitButtonBounds.y, quitButtonBounds.width, quitButtonBounds.height);

        menuBatch.draw(muteButtonTexture, muteButtonBounds.x, muteButtonBounds.y, muteButtonBounds.width, muteButtonBounds.height);
        menuBatch.draw(fullButtonTexture, fullButtonBounds.x, fullButtonBounds.y, fullButtonBounds.width, fullButtonBounds.height);

        menuBatch.draw(title, (float)(Gdx.graphics.getWidth() - title.getWidth())/2, (float) (Gdx.graphics.getHeight()*0.8), title.getWidth(), title.getHeight());

        menuBatch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Invert Y-coordinate
            if (playButtonBounds.contains(touchX, touchY)) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainGameScreen(this.game));
            } else if (quitButtonBounds.contains(touchX, touchY)) {
                Gdx.app.exit();
            } else if (muteButtonBounds.contains(touchX, touchY)) {
                muted = !muted;

                if (muted){
                    muteButtonTexture = new Texture(Gdx.files.internal("Muted.png"));
                }
                else{
                    muteButtonTexture = new Texture(Gdx.files.internal("VolumeButton.png"));
                }
            } else if (fullButtonBounds.contains(touchX, touchY)) {
                if (!Gdx.graphics.isFullscreen()) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(this.game));
                } else {
                    Gdx.graphics.setWindowedMode(1024, 576); // Set your preferred window size
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(this.game));
                }
                updateButtonBounds();

            }
        }




    }

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

