package com.ahmetardakavakci.survivorbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Timer;

import java.awt.Shape;
import java.util.Random;

public class SurvivorBird extends ApplicationAdapter {

	// essential
	SpriteBatch batch;
	int gameState = 0;
	int animateBackground = 1;

	// bg global vars
	int bgRow = 3;
	float bgDistance;

	// bgStatic
	Texture bgStatic;
	float[] bgStaticX = new float[bgRow];
	float bgStaticVelocity = 1.0f;

	// bgShadow
	Texture bgShadow;
	float[] bgShadowX = new float[bgRow];
	float bgShadowVelocity = 1.4f;

	// bgTrees
	Texture bgTrees;
	float[] bgTreesX = new float[bgRow];
	float bgTreesVelocity = 1.8f;

	// BIRD
	Texture bird;
	float velocity = 0.0f;
	float flySpeed = 10;
	float gravity = 0.5f;
	Circle birdCircle;

	float birdX = 0.0f;
	float birdY = 0.0f;

	// ENEMY
	int enemyCount = 3;
	int enemyRow = 4;
	float enemyVelocity = 2.4f;
	Texture [] enemy = new Texture[enemyCount];
	Circle[][] enemyCircle = new Circle[enemyRow][enemyCount];

	float[] enemyX = new float[enemyRow];
	float[][] enemyOffset = new float[enemyRow][enemyCount];
	float enemyDistance;

	// score
	int score = 0;
	int scoredEnemy = 0;

	// bitmap font
	BitmapFont font;
	BitmapFont fontFinish;

	// randomizer for enemy y
	Random random;

	// shapeRenderer
	ShapeRenderer shapeRenderer;
	ShapeRenderer shapeRenderer2;

	@Override
	public void create () {
		batch = new SpriteBatch();
		bgStatic = new Texture("bg/bg-static.png");
		bgShadow = new Texture("bg/bg-tree-shadow.png");
		bgTrees = new Texture("bg/bg-trees.png");
		bird = new Texture("bird.png");

		bgDistance = Gdx.graphics.getWidth();
		enemyDistance = Gdx.graphics.getWidth() / 2;
		random = new Random();

		birdX = Gdx.graphics.getWidth() / 6;
		birdY = Gdx.graphics.getHeight() / 2;

		birdCircle = new Circle();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(4);

		fontFinish = new BitmapFont();
		fontFinish.setColor(Color.WHITE);
		fontFinish.getData().setScale(4);

		// initialize enemy textures
		for (int i = 0; i < enemy.length; i++){
			enemy[i] = new Texture("enemy.png");
		}

		// x of enemy row
		for (int i = 0; i < enemyRow; i++){

			for (int r = 0; r < enemyCount; r++){
				enemyOffset[i][r] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight());
				enemyCircle[i][r] = new Circle();
			}

			enemyX[i] = Gdx.graphics.getWidth() - enemy[0].getWidth() / 2 + i * enemyDistance;
		}

		// x of backgrounds
		for (int i = 0; i < bgRow; i++) {
			bgStaticX[i] = (i - 1) * Gdx.graphics.getWidth();
			bgShadowX[i] = (i - 1) * Gdx.graphics.getWidth();
			bgTreesX[i] = (i - 1) * Gdx.graphics.getWidth();
		}

	}

	@SuppressWarnings("NewApi")
	@Override
	public void render () {

		batch.begin();

		drawBackground();

		// game started
		if (gameState == 1){

			// bird positioning and drawing
			if (Gdx.input.justTouched()) {
				float delay = 0.1f; // seconds

				bird = new Texture("bird_flap.png");
				velocity = flySpeed;

				// animation delay for a more responsive gameplay
				Timer.schedule(new Timer.Task(){
					@Override
					public void run() {
						bird = new Texture("bird.png");
					}
				}, delay);

			}

			if (enemyX[scoredEnemy] < birdX){
				score++;
				System.out.println(score);

				if (scoredEnemy < 3){
					scoredEnemy++;
				} else {
					scoredEnemy = 0;
				}

			}

			// enemy positioning and drawing
			for (int i = 0; i < enemyRow; i++) {

				if (enemyX[i] < -enemy[0].getWidth()){
					enemyX[i] = enemyX[i] + enemyRow * enemyDistance;

					for (int r = 0; r < enemyCount; r++){
						enemyOffset[i][r] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
					}
				} else {
					enemyX[i] = enemyX[i] - enemyVelocity;
				}

				// draw enemies and their circles
				for (int w = 0; w < enemyCount; w++) {
					batch.draw(enemy[w], enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset[i][w], Gdx.graphics.getWidth()/18,Gdx.graphics.getHeight()/12);
					enemyCircle[i][w] = new Circle(enemyX[i] + Gdx.graphics.getWidth()/36, Gdx.graphics.getHeight() / 2 + enemyOffset[i][w], Gdx.graphics.getWidth()/96);
				}

				birdCircle.set(birdX + Gdx.graphics.getWidth()/36, birdY + Gdx.graphics.getHeight()/24,Gdx.graphics.getWidth()/36);

			}


			// bird falling animation
			if (velocity < 1) {
				bird = new Texture("bird.png");
			}

			// bird gravity and flying physics
			if (birdY > 0) {
				birdY = birdY + velocity;
				velocity = velocity - gravity;
			} else {
				gameState = 2;
			}


		} else if (gameState == 0){
			if (Gdx.input.justTouched()) {
				gameState = 1;
				velocity = flySpeed;
			}
		} else if (gameState == 2) {

			birdY = Gdx.graphics.getHeight() / 2;

			batch.draw(bgStatic,0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			batch.draw(bgShadow,0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			batch.draw(bgTrees, 0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

			fontFinish.draw(batch,"Game over, tap to try again!", 20, 160);

			for (int i = 0; i < bgRow; i++) {
				bgStaticX[i] = (i - 1) * Gdx.graphics.getWidth();
				bgShadowX[i] = (i - 1) * Gdx.graphics.getWidth();
				bgTreesX[i] = (i - 1) * Gdx.graphics.getWidth();
			}

			if (Gdx.input.justTouched()) {
				gameState = 1;

				for (int i = 0; i < enemyRow; i++) {
					for (int r = 0; r < enemyCount; r++) {
						enemyOffset[i][r] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
						enemyCircle[i][r] = new Circle(enemyX[i] + Gdx.graphics.getWidth()/36, Gdx.graphics.getHeight() / 2 + enemyOffset[i][r], Gdx.graphics.getWidth()/36);
					}

					enemyX[i] = Gdx.graphics.getWidth() - enemy[0].getWidth() / 2 + i * enemyDistance;

				}

				velocity = flySpeed;
				scoredEnemy = 0;
				score = 0;


			}
		}

		// finally, draw the bird
		batch.draw(bird, birdX, birdY, Gdx.graphics.getWidth()/18,Gdx.graphics.getHeight()/12);
		font.draw(batch,"Score: " + score, 20, 90);
		batch.end();

		birdCircle.set(birdX + Gdx.graphics.getWidth()/36, birdY + Gdx.graphics.getHeight()/24,Gdx.graphics.getWidth()/36);

		for (int i = 0; i < enemyRow; i++) {
			for (int w = 0; w < enemyCount; w++) {
				if (Intersector.overlaps(birdCircle, enemyCircle[i][w])){
					System.out.println("Collision detected!");
					gameState = 2;
				}
			}
		}
	}

	public void drawBackground() {

		// draw bgStatic
		for (int i = 0; i < bgRow; i++) {

			if (bgStaticX[i] < -Gdx.graphics.getWidth()){
				bgStaticX[i] = bgStaticX[i] + bgRow * bgDistance;
			} else {
				bgStaticX[i] = bgStaticX[i] - bgStaticVelocity;
			}

			batch.draw(bgStatic, bgStaticX[i], 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		}

		// bgShadow
		for (int i = 0; i < bgRow; i++) {

			if (bgShadowX[i] < -Gdx.graphics.getWidth()){
				bgShadowX[i] = bgShadowX[i] + bgRow * (bgDistance - 1);
			} else {
				bgShadowX[i] = bgShadowX[i] - bgShadowVelocity;
			}

			batch.draw(bgShadow, bgShadowX[i], 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		}

		// bgTrees
		for (int i = 0; i < bgRow; i++) {

			if (bgTreesX[i] < -Gdx.graphics.getWidth()){
				bgTreesX[i] = bgTreesX[i] + bgRow * (bgDistance - 1);
			} else {
				bgTreesX[i] = bgTreesX[i] - bgTreesVelocity;
			}

			batch.draw(bgTrees, bgTreesX[i], 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		}
	}

	@Override
	public void dispose () {

	}
}
