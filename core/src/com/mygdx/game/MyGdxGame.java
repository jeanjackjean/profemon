package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class MyGdxGame extends Game {
	SpriteBatch batch;
	Texture img;
	Sprite sprite = new Sprite();
	OrthographicCamera camera;
	Vector2 targetSpeed= new Vector2(1,2);
	Vector2 targetPosition = new Vector2(0,0);
	
	World world;
	Body targetBody;
	
	int horizontalWind=-2;
	int horizontalGravity=-30;
	
	 public BitmapFont font;
	 
	@Override
	public void create () {
		batch = new SpriteBatch();
		//camera = new OrthographicCamera();
		 font = new BitmapFont();
		   //camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//img = new Texture("guy.png");
		
		this.setScreen(new MainMenuScreen(this));
		
	/*	sprite.setTexture(img);
		targetPosition.x = Gdx.graphics.getWidth()/2 - img.getWidth()/2;
		targetPosition.y= Gdx.graphics.getHeight()/2 - img.getHeight()/2;
		
		sprite.setX(targetPosition.x);
		sprite.setY(targetPosition.y);
		
		Rectangle murGauche = new Rectangle();
		Sprite spriteMurGauche = new Sprite();
		
	/*	world = new World(new Vector2(horizontalWind,horizontalGravity),true);
		
		BodyDef targetBodyDef = new BodyDef();
		
		targetBodyDef.type = BodyDef.BodyType.DynamicBody;
		targetBodyDef.position.set(targetPosition.x,targetPosition.y);
		
		targetBody = world.createBody(targetBodyDef);
		
		CircleShape shape = new CircleShape();
		shape.setRadius(sprite.getWidth()/2);
		
		FixtureDef spriteFixtureDef= new FixtureDef();
		spriteFixtureDef.shape=shape;
		spriteFixtureDef.density = 20f;
		spriteFixtureDef.restitution=4;
	
		Fixture spriteFixture = targetBody.createFixture(spriteFixtureDef);
		shape.dispose();
		
		*/
	}


	
	
	@Override
	public void render () {
		super.render();
		/*Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//camera.update();
		
		
		if(Gdx.input.isTouched()) {
		      Vector3 touchPos = new Vector3();
		      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		      camera.unproject(touchPos);
		      sprite.setPosition(touchPos.x, touchPos.y ) ;
		   }
		
		
		//world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
		/*if(sprite.getX() >0 && sprite.getY() >0)
		{
			sprite.setPosition(targetBody.getPosition().x, targetBody.getPosition().y);
		}*/
		 //batch.setProjectionMatrix(camera.combined);
		/*
		batch.begin();
		batch.draw(img, sprite.getX(),sprite.getY());
		batch.end();*/
	}
	 public void dispose() {
	        batch.dispose();
	        font.dispose();
	    }
}
