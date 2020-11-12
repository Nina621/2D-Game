package com.unknown;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main extends Game {

	public static final int WIDTH = 700;
	public static final int HEIGHT = 600;

	OrthogonalTiledMapRenderer MapRenderer;
	OrthographicCamera camera;
	SpriteBatch batch;
	SpriteBatch hudBatch;
	BitmapFont font;
	Texture character;
	Texture enemyTexture;
	Texture bulletTexture;
	boolean movingRight;
	boolean movingLeft;
	boolean movingForward;
	boolean movingBackwards;
	boolean shooting;
	//Kolekcije - I5
	ArrayList<Bullet> bullets;
	Array<Enemies> enemies;
	String direction;
	String currentScreen;
	String playerName = "UnknownPlayer";
	int playerScore;
	int playerHealth;
	int finalScore;

	private TiledMap map;
	private ShapeRenderer shapeRenderer;

	Animation<TextureRegion> runningAnimation;
	float stateTime; /** A variable for tracking elapsed time for the animation*/
	private Texture runningSheet;
	private static final int FRAME_COLS = 3, FRAME_ROWS = 3; /** Constant rows and columns of the sprite sheet*/

	private int tileHeight = 16;
	private int tileWidth = 16;

	/**
	 * Character starting position
	 */

	int characterX = tileWidth * 9;
	int characterY = tileHeight * 12;

	@Override
	public void create() {
		inputOutput listener = new inputOutput(this);
		Gdx.input.getTextInput(listener, "Name", "", "Enter your name");

		batch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		font = new BitmapFont();
		bullets = new ArrayList<>();
		enemies = new Array<>();
		map = new TmxMapLoader().load("levels/new/level3.tmx");
		MapRenderer = new OrthogonalTiledMapRenderer(map);
		shapeRenderer = new ShapeRenderer();

		direction = "Down";
		playerHealth = 100;
		playerScore = 0;
		currentScreen = "Main";

		character = new Texture(Gdx.files.internal("character/character.png"));
		enemyTexture = new Texture(Gdx.files.internal("enemies/drone.png"));
		bulletTexture = new Texture(Gdx.files.internal("character/bullet.png"));
		runningSheet = new Texture(Gdx.files.internal("character/character_walk.png"));

		// Animation
		TextureRegion[][] tmp = TextureRegion.split(runningSheet,
				runningSheet.getWidth() / FRAME_COLS,
				runningSheet.getHeight() / FRAME_ROWS);

		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}

		runningAnimation = new Animation<>(0.2f, walkFrames);
		stateTime = 0f;

		camera = new OrthographicCamera(16 * 12, 16 * 12);
		camera.position.set(characterX, characterY, 0);
		camera.zoom += 0.1f;
		camera.update();

		setScreen(new TitleScreen(this));

	}


	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
		hudBatch.dispose();
		shapeRenderer.dispose();
		font.dispose();
		character.dispose();
		runningSheet.dispose();
		super.dispose();
	}

	/**
	 * @return
	 */

	boolean inputs() {

		boolean animationStatus = false;

		//MOVEMENT
		if (movingForward) {
			characterY++;
			animationStatus = true;
			direction = "Up";
		} else if (movingBackwards) {
			characterY--;
			animationStatus = true;
			direction = "Down";
		} else if (movingRight) {
			characterX++;
			animationStatus = true;
			direction = "Right";
		} else if (movingLeft) {
			characterX--;
			animationStatus = true;
			direction = "Left";
		}

		//SHOOTING
		if (shooting) for (Bullet bullet : bullets) {
			if (bullet.direction.equals("Up")) {
				bullet.bulletY += 2;
			}
			if (bullet.direction.equals("Down")) {
				bullet.bulletY -= 2;
			}
			if (bullet.direction.equals("Right")) {
				bullet.bulletX += 2;
			}
			if (bullet.direction.equals("Left")) {
				bullet.bulletX -= 2;
			}
		}

		//ENEMIES MOVEMENT
		for (Enemies enemy : enemies) {
			if (enemy.enemyX < characterX) {
				enemy.enemyX += 0.5f;
			}
			if (enemy.enemyX > characterX) {
				enemy.enemyX -= 0.5f;
			}
			if (enemy.enemyY < characterY) {
				enemy.enemyY += 0.5f;
			}
			if (enemy.enemyY > characterY) {
				enemy.enemyY -= 0.5f;
			}
		}

		return animationStatus;
	}

	/**
	 * Collisions
	 */

	void getCellProperties() {
		MapObjects mapObjects = map.getLayers().get(1).getObjects();

		for (MapObject mapObject : mapObjects) {
			MapProperties mapProperties = mapObject.getProperties();

			float width, height, x, y;
			Rectangle objectRectangle = new Rectangle();
			Rectangle playerRectangle = new Rectangle();

			if (mapProperties.containsKey("width") && mapProperties.containsKey("height") && mapProperties.containsKey("x") && mapProperties.containsKey("y")) {
				width = (float) mapProperties.get("width");
				height = (float) mapProperties.get("height");
				x = (float) mapProperties.get("x");
				y = (float) mapProperties.get("y");
				objectRectangle.set(x, y, width, height);
			}

			playerRectangle.set(characterX, characterY, 13, 13);

			for (int i = 0; i < enemies.size; i++) {
				Rectangle enemyRectangle = new Rectangle();
				enemyRectangle.set(enemies.get(i).enemyX, enemies.get(i).enemyY, 16, 16);

				if (Intersector.overlaps(enemyRectangle, playerRectangle)) {
					playerHealth -= 20;
					enemies.removeIndex(i);

					System.out.println("Health:" + playerHealth);

					if (playerHealth == 0) {
						setScreen(new EndScreen(this));
						System.out.println(playerName + " your final score: " + playerScore);
						finalScore = playerScore;
						try {
							writeHighScoreToFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
						playerHealth = 100;
						characterX = tileWidth * 9;
						characterY = tileHeight * 12;
						playerScore = 0;
						enemies.clear();
						bullets.clear();
					}
				}

				for (int it = 0; it < bullets.size(); it++) {
					Rectangle bulletRectangle = new Rectangle();
					bulletRectangle.set(bullets.get(it).bulletX, bullets.get(it).bulletY, 16, 16);

					if (Intersector.overlaps(enemyRectangle, bulletRectangle)) {
						playerScore += 10;
						enemies.removeIndex(i);
						bullets.remove(it);
					}
				}
			}
			/*
			   If the player rectangle and the object rectangle is colliding, return the object
			   1.Hit From Top
			   2.Hit From Bottom
			   3.Hit From right
			   4.Hit From left
			 */


			if (Intersector.overlaps(objectRectangle, playerRectangle)) {
				if (characterY <= objectRectangle.y + 16 && direction.equals("Down"))
				{
					characterY++;
				} else if (characterY + 16 >= objectRectangle.y && direction.equals("Up"))
				{
					characterY--;
				} else if (characterX + 16 >= objectRectangle.x && direction.equals("Right"))
				{
					characterX--;
				} else if (characterX <= objectRectangle.x + 16 && direction.equals("Left"))
				{
					characterX++;
				}
				return;
			}
		}

		 //If no colliding object was found in that layer

	}

	/**
	 * @param min
	 * @param max
	 * @return
	 */
	int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	/**
	 * @param name
	 */

	//Dretve,Exceptions - I8,I3
	void createThread(String name){
		new Thread(() -> {
			System.out.println("Running " + name);
			while (currentScreen.equals("GameScreen")) {
				try {
					createEnemy();
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.out.println("Thread " + name + " interrupted.");
				}
			}


			//Post a Runnable to the rendering thread that processes the result

			Gdx.app.postRunnable(() -> {

				//Process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
				System.out.println("Ending" + name);
			});
		}).start();
	}

	/**
	 * @throws InterruptedException
	 */

	private void createEnemy() throws InterruptedException {
		Enemies enemy = new Enemies(getRandomNumberInRange(0,16 * 20),0);
		Enemies enemy2 = new Enemies(0,getRandomNumberInRange(0,16 * 20));
		Enemies enemy3 = new Enemies(16 * 20,getRandomNumberInRange(0,16 * 20));
		Enemies enemy4 = new Enemies(getRandomNumberInRange(0,16 * 20),16 * 20);
		enemies.add(enemy);
		enemies.add(enemy2);
		enemies.add(enemy3);
		enemies.add(enemy4);
		TimeUnit.SECONDS.sleep(1);
	}

	/**
	 * @throws IOException
	 */

	/*
	I/O tokovi - I6
	 */

	private void writeHighScoreToFile() throws IOException {
		String str = playerName + ": " + playerScore;
		PrintWriter writer = new PrintWriter(new FileWriter("HighScore.txt",true));
		writer.println();
		writer.append(str);
		writer.close();
	}

	/**
	 * @return
	 * @throws FileNotFoundException
	 */

	int getHighScore() throws FileNotFoundException {
		File file = new File("HighScore.txt");
		Scanner sc = new Scanner(file);

		// we just need to use \\Z as delimiter
		int counter = 0;
		int score = 0;
		String name = "null";

		while (sc.hasNextLine()) {
			String data = sc.next();
				if (counter % 2 == 0){
					name = data;
					counter++;
				}
				else{
					if(score < Integer.parseInt(data)){
						score = Integer.parseInt(data);
						counter++;
					}
					else {
						counter++;
					}
				}
			}
		return score;
	}
}

