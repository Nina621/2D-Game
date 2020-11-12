package com.unknown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Json;

import java.io.FileNotFoundException;

// GUI - I7 #1

public class TitleScreen extends ScreenAdapter {

    /**
     * Zadane vrijednosti gumbova
     */

    private static final int EXIT_BUTTON_WIDTH = 100;
    private static final int EXIT_BUTTON_HIGHT = 42;
    private static final int PLAY_BUTTON_WIDTH = 100;
    private static final int PLAY_BUTTON_HIGHT = 42;

    private static final int EXIT_BUTTON_Y = 130;
    private static final int PLAY_BUTTON_Y = 200;

    private Main game;

    private Texture background;
    private Texture playButton1;
    private Texture playButton2;
    private Texture exitButton1;
    private Texture exitButton2;
    private BitmapFont font;
    private CharSequence str = "GAME"; // Naslov
    private int highScore;


    /**
     * Konstruktor
     * @param game
     */

    TitleScreen(Main game) {
        this.game = game;
        this.game.currentScreen = "TitleScreen";
        background = new Texture("p.png");
        exitButton1 = new Texture("button_exit1.png");
        exitButton2 = new Texture("button_exit2.png");
        playButton1 = new Texture("button_play1.png");
        playButton2 = new Texture("button_play2.png");
        font = new BitmapFont();

        // Podrska za visejezicnost - I6 #2

        String file = "Spanish.json";
        Json json = new Json();
        Dictionary d = json.fromJson(Dictionary.class,Gdx.files.internal(file));
        System.out.println(d.getValue(Dictionary.Keys.KeyHello));
        System.out.println(d.getValue(Dictionary.Keys.KeyPlay));

        try {
            highScore = game.getHighScore();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.ESCAPE) { // Pritiskom na ESCAPE izlazimo iz Main-a
                    Gdx.app.exit();
                }

                return true;
            }
        });
    }


    @Override
    public void render(float delta) {
        Gdx.graphics.setWindowedMode(700,600);

        game.hudBatch.begin();
        /*
          Dodana pozadina i postavljene dimenzije pozadine i naslova
         */
        game.hudBatch.draw(background,0,0, 700,600);
        font.draw(game.hudBatch,str,170,470);
        font.setColor(1,0,0,1);
        font.getData().setScale(8);

        game.font.draw(game.hudBatch,"High score is: " + highScore,  280,300);

        /*
          Postavljanje gumbova u određenu poziciju
         */
        int x = Main.WIDTH / 2 - EXIT_BUTTON_WIDTH / 2 ;
        if (Gdx.input.getX() < x + EXIT_BUTTON_WIDTH && Gdx.input.getX() > x && Main.HEIGHT - Gdx.input.getY() < EXIT_BUTTON_Y +EXIT_BUTTON_HIGHT && Main.HEIGHT - Gdx.input.getY() > EXIT_BUTTON_Y) {
            game.hudBatch.draw(exitButton1, 290, 130, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HIGHT);
            if (Gdx.input.isTouched()){
                Gdx.app.exit();
            }
        }else{
            game.hudBatch.draw(exitButton2, 290, 130, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HIGHT);
        }

        x = Main.WIDTH / 2 - PLAY_BUTTON_WIDTH / 2 ;
        if (Gdx.input.getX() < x + PLAY_BUTTON_WIDTH && Gdx.input.getX() > x && Main.HEIGHT - Gdx.input.getY() < PLAY_BUTTON_Y + PLAY_BUTTON_HIGHT && Main.HEIGHT - Gdx.input.getY() > PLAY_BUTTON_Y) {
            game.hudBatch.draw(playButton1, 290, 200, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HIGHT);
            if (Gdx.input.isTouched()){
                this.dispose();
                game.setScreen(new GameScreen(game));
            }

        }else{
            game.hudBatch.draw(playButton2, 290, 200, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HIGHT);

        }
        game.hudBatch.end();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}