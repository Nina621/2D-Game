package com.unknown;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Iterator;

public class GameScreen extends ScreenAdapter {

    private Main game;
    private Music music;

    /**
     * Konstruktor
     * @param game
     */
    GameScreen(Main game) {
        this.game = game;
        this.game.currentScreen = "GameScreen";
        this.game.createThread("Thread1");


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {

                //CAMERA MOVEMENT

                if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                    game.camera.position.x -= 10;
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    game.camera.position.x += 10;
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                    game.camera.position.y += 10;
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
                    game.camera.position.y -= 10;
                }

                //PLAYER MOVEMENT

                if (Gdx.input.isKeyPressed(Input.Keys.A)){
                    game.movingLeft = true;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.D)){
                    game.movingRight = true;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.W)){
                    game.movingForward = true;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.S)){
                    game.movingBackwards = true;
                }

                if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
                    game.setScreen(new EndScreen(game));
                }

                //SHOOTING

                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                    game.shooting = true;
                    Bullet bullet = new Bullet(game.characterX, game.characterY, game.direction);
                    game.bullets.add(bullet);
                }

                //SPAWN ENEMIES MANUALY

                if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)){
                    Enemies enemy = new Enemies(game.getRandomNumberInRange(16 * 2,16 * 19),game.getRandomNumberInRange(16 * 2,16 * 19));
                    game.enemies.add(enemy);
                }

                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.A){
                    game.movingLeft = false;
                }

                if (keycode == Input.Keys.D){
                    game.movingRight = false;
                }

                if (keycode == Input.Keys.W){
                    game.movingForward = false;
                }

                if (keycode == Input.Keys.S){
                    game.movingBackwards = false;
                }
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;

            }

            @Override
            public boolean scrolled(int amount) {
                /*
                if(amount == 1){
                    game.camera.zoom += .1f;
                }
                else if(amount == -1){
                    game.camera.zoom -= .1f;
                }

                 */
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {

        //BACKGROUND

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //MUSIC

        music = Gdx.audio.newMusic(Gdx.files.internal("Song.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();

        //ANIMATION

        game.stateTime += Gdx.graphics.getDeltaTime();

        //CAMERA

        game.camera.position.set(game.characterX,game.characterY,0);
        game.camera.update();
        game.MapRenderer.setView(game.camera);
        game.MapRenderer.render();
        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();

        //COLLISIONS

        game.getCellProperties();

        game.font.draw(game.batch,"Score:" + game.playerScore,game.camera.position.x - 80 ,game.camera.position.y - 90);
        game.font.draw(game.batch,"Health:" + game.playerHealth,game.camera.position.x - 0,game.camera.position.y - 90);
        //ANIMATION

        TextureRegion currentFrame = game.runningAnimation.getKeyFrame(game.stateTime, true);
        if (game.inputs()){
            game.batch.draw(currentFrame, game.characterX, game.characterY); // Draw current frame at (characterX & characterY)
        }
        else {
            game.batch.draw(game.character, game.characterX, game.characterY); // No animation
        }

        //ENEMIES

        for(Enemies enemy: game.enemies)
        {
            game.batch.draw(game.enemyTexture, enemy.enemyX, enemy.enemyY);
        }

        //BULLETS

        for(Bullet bullet: game.bullets)
        {
            game.batch.draw(game.bulletTexture, bullet.bulletX, bullet.bulletY);
        }

        Iterator<Bullet> i = game.bullets.iterator();
        while(i.hasNext()){

            Bullet bullet = i.next();

            //OUT OF BOUNDS FOR BULLETS

            if(bullet.bulletX >= 16* 20){
                i.remove();
            }

            if(bullet.bulletY >= 16*20) {
                i.remove();
            }

            if(bullet.bulletX < 0){
                i.remove();
            }

            if(bullet.bulletY < 0){
                i.remove();
            }
        }

        game.batch.end();

    }

    @Override
    public void dispose(){
        music.dispose();

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}

// OOP - Klase,extends i abstract #2

class Bullet{
    int bulletX;
    int bulletY;
    String direction;

    Bullet(int x,int y,String direction){
        this.bulletX = x;
        this.bulletY = y;
        this.direction = direction;
    }
}

class Enemies{
    float enemyX;
    float enemyY;

    Enemies(int x,int y){
        this.enemyX = x;
        this.enemyY = y;
    }
}