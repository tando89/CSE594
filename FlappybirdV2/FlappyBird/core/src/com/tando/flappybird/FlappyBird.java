package com.tando.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	//image background
	Texture background;
	Texture gameover;
	//new batch for shape
    //ShapeRenderer shapeRenderer;
	Texture[] birds;
	//flip between bird and bird 2
	int flapState = 0;
	float birdY = 0;
	//how fast the bird will be moving
	float velocity = 0;
	//track the sate of game
	int gameState = 0;
	float gravity = 2;
    //shape of the bird for collision detection
	Circle birdCircle;
	//Scores variables
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;

	//Tubes
	Texture topTube;
	Texture bottomTube;

	//The gap between tubes  can make the game easier or harder
	float gap = 700; //1000 would be super easy
	//The distance of the tubes up/down
	float maxTubeOffset;
	//Generate random gaps
	Random randomGenerator;

	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameover = new Texture("flappybirdgameover.png");
		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		//font
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		font.getData().setScale(10);

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");


		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");

		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;

		randomGenerator = new Random();

		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4; // / 2 only will be hard

		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		startGame();

	}
	//start game method
	public void startGame() {
		//start height position
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		for (int i = 0; i < numberOfTubes; i++) {

			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();

		}
	}


	@Override
	public void render () {
		batch.begin();
		//display the bg first
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//start processing when user touches the screen
		if (gameState == 1) {

			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
				score++;

				Gdx.app.log("Score", String.valueOf(score));

				if (scoringTube < numberOfTubes - 1) {
					scoringTube ++;
				}
				else {
					scoringTube = 0;
				}
			}

			if (Gdx.input.justTouched()) {
				Gdx.app.log("tapped", "yep!");

				//gameState = 1;
				// the height of the bird when tap
				velocity = -30;


			}

			//display the tube

			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < - topTube.getWidth()) {

					tubeX[i] += numberOfTubes * distanceBetweenTubes;

					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

				} else {

					tubeX[i] = tubeX[i] - tubeVelocity;



				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], topTube.getWidth(), topTube.getHeight());

			}

			//bird responds to taps

			if (birdY > 0) {
				//increase the velocity each time the render loop called
				velocity = velocity + gravity;
				//decrease the position of the bird by the velocity (fall faster)
				birdY -= velocity;

			} else {
				gameState = 2;
			}
		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {

				gameState = 1;
			}
		} else if (gameState == 2) {
			batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

			if (Gdx.input.justTouched()) {

				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}
		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}


		//display the bird at the centen of screen lastly
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth(), birdY);
		//display score
		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
		//Circle will be overlapped of the bird image
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2,  birds[flapState].getHeight() / 2);
		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);*/

		//Display the score


		for (int i = 0; i < numberOfTubes; i++) {

			/*shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], topTube.getWidth(), topTube.getHeight());*/

			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
				Gdx.app.log("Collision" , "Yes!" );

				gameState = 2;
			}
		}
		//shapeRenderer.end();
	}
	


}
