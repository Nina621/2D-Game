package com.unknown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;

// GUI - I7 #2

public class EndScreen extends ScreenAdapter {

    /**
     * Zadane vrijednosti gumbova
     */

    private static final int CONTINUE_BUTTON_WIDTH = 150;
    private static final int CONTINUE_BUTTON_HIGHT = 50;
    private static final int MAIN_BUTTON_WIDTH = 150;
    private static final int MAIN_BUTTON_HIGHT = 48;

    private static final int CONTINUE_BUTTON_Y = 300;
    private static final int MAIN_BUTTON_Y = 200;


    private Main game;

    private Texture background;
    private Texture continueButton1;
    private Texture continueButton2;
    private Texture mainButton1;
    private Texture mainButton2;


    /**
     * Konstruktor
     * @param game
     */

    EndScreen(Main game) {
        this.game = game;
        this.game.currentScreen = "EndScreen";
        background = new Texture("p.png");
        continueButton1 = new Texture("button_continue 1.png");
        continueButton2= new Texture("button_continue2.png");
        mainButton1= new Texture("button_main 1.png");
        mainButton2 = new Texture("button_main2.png");


        // Podrska za visejezicnost - I6 #3

        String file = "Spanish.json";
        Json json = new Json();
        Dictionary d = json.fromJson(Dictionary.class,Gdx.files.internal(file));
        System.out.println(d.getValue(Dictionary.Keys.KeyInterrupt));


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {

                if (keyCode == Input.Keys.ENTER) {
                    game.setScreen(new TitleScreen(game)); // Pritiskom natipku ENTER odlazimo natrag u Main
                }

                return true;
            }
        });
    }


    @Override
    public void render(float delta) {
        /*
          Postavljena pozadina i gumbovi
         */
        Gdx.graphics.setWindowedMode(700,600);
        game.hudBatch.begin();
        game.hudBatch.draw(background,0,0, 700,600);
        game.font.draw(game.hudBatch,game.playerName + " your score was: " + game.finalScore,  270,500);

        int x = Main.WIDTH / 2 - CONTINUE_BUTTON_WIDTH / 2 ;
        if (Gdx.input.getX() < x + CONTINUE_BUTTON_WIDTH && Gdx.input.getX() > x && Main.HEIGHT - Gdx.input.getY() < CONTINUE_BUTTON_Y + CONTINUE_BUTTON_HIGHT && Main.HEIGHT - Gdx.input.getY() > CONTINUE_BUTTON_Y) {
            game.hudBatch.draw(continueButton1, 270, 300, CONTINUE_BUTTON_WIDTH, CONTINUE_BUTTON_HIGHT);
            if (Gdx.input.isTouched()){ // Ukoliko kliknemo na gumb pokrenut  ce se igrica
                this.dispose();
                game.setScreen(new GameScreen(game));
            }
        }else{
            game.hudBatch.draw(continueButton2, 270, 300, CONTINUE_BUTTON_WIDTH, CONTINUE_BUTTON_HIGHT);
        }

        x = Main.WIDTH / 2 - MAIN_BUTTON_WIDTH / 2 ;
        if (Gdx.input.getX() < x + MAIN_BUTTON_WIDTH && Gdx.input.getX() > x && Main.HEIGHT - Gdx.input.getY() < MAIN_BUTTON_Y + MAIN_BUTTON_HIGHT && Main.HEIGHT - Gdx.input.getY() > MAIN_BUTTON_Y) {
            game.hudBatch.draw(mainButton1, 270, 200, MAIN_BUTTON_WIDTH, MAIN_BUTTON_HIGHT);
            if (Gdx.input.isTouched()){
                this.dispose();
                game.setScreen(new TitleScreen(game));
            }
        }else{
            game.hudBatch.draw(mainButton2, 270, 200, MAIN_BUTTON_WIDTH, MAIN_BUTTON_HIGHT); //Postavljene dimenzije gumbova

        }
        game.hudBatch.end();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}