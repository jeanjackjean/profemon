package com.mygdx.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class GameScreen implements Screen {
	 final MyGdxGame game;
	 boolean canMove = true;
	 int cameraWidth = 800;
	 int cameraHeight = 480;
	 
	 int speed = 3; //vitesse de déplacement du personnage ET de la caméra(pour que la caméra suive le perso au bon rythme
	 
	private    Texture dropImage;
	   private Texture bucketImage;//Le personnage
	   //Les fleches directionnelles, images + rectangle
	   private Texture rightArrowImage;
	   private Rectangle rightArrow;
	   private Texture leftArrowImage;
	   private Rectangle leftArrow;
	   private Texture upArrowImage;
	   private Rectangle upArrow;
	   private Texture downArrowImage;
	   private Rectangle downArrow;
	   // Les boutons A et B
	   private Texture buttonAImage;
	   private Rectangle buttonA;
	   private Texture buttonBImage;
	   private Rectangle buttonB;
	   
	   
	   private Texture floorImage;//Image du sol
	   private Texture wallImage;//Image du mur du fond (le seul a être représenté)
	    
	    
	    Music rainMusic;//fichier musique...
	    OrthographicCamera camera; // La caméra: on la déplace à la même vitesse que le personnage pour qu'il reste au milieu de l'écran
	    Rectangle bucket; // bucket = personnage
	    Array<Rectangle> raindrops;
	    long lastDropTime;
	    int dropsGathered;
	    Zone zone; // Une zone est une salle. Le but est de pouvoir créer des salles rapidement.
	  //Chaque salle est découpée en plusieurs cases. Les coordonnées sont récupérées via la méthode getPoints de Zone et tout est stocké dans cases
	    ArrayList<ArrayList<Vector2>> cases = new ArrayList<ArrayList<Vector2>>(); 
	    int [] currentPosition = new int[2]; //Coordonée de la position du perso: cases contient les coordonées en pixels des cases. Nous, on change de case vi les indices

	    public GameScreen(final MyGdxGame gam) {
	        this.game = gam;
	        
	       floorImage = new Texture(Gdx.files.internal("floor.png"));
			wallImage =  new Texture(Gdx.files.internal("wall.png"));
	        
	        Gdx.input.setCatchBackKey(true);//Empeche de quitter l'appli avec retour
	        // create the camera and the SpriteBatch
	        camera = new OrthographicCamera();
	        camera.setToOrtho(false, cameraWidth, cameraHeight);
	       
	        // On définie une zone, on envoie la taille de la caméra. La taille de caméra est en fait le découpage de l'écran.
	        zone= new Zone(game, camera, cameraWidth, cameraHeight);
	        //On créé la zone où on peut marcher via l'image, la hitbox, le nombre de cases voulues en largeur et le nombre de cases voulues en hauteur
	        zone.setWalkArea(floorImage, new Rectangle(0,0,zone.cameraWidth,zone.cameraHeight), 10, 12);
	        //Pour le wall, seul la hauteur est à ajuster
	        zone.setWall(wallImage, zone.cameraHeight/4);
	        //On récupère la découpe de la zone, à faire après le setWalkArea()
	        cases = zone.getPoints();
	       
	        //On set la position actuelle au milieu de la zone
	        currentPosition[0] = cases.get(0).size()/2;
	        currentPosition[1] = cases.size()/2;
	        
	        
	        // load the images for the droplet and the bucket, 64x64 pixels each
	        dropImage = new Texture(Gdx.files.internal("arrow.png"));
	        bucketImage = new Texture(Gdx.files.internal("guy.png"));
	     
	        rightArrowImage = new Texture(Gdx.files.internal("leftArrow.png"));
	        upArrowImage = new Texture(Gdx.files.internal("upArrow.png"));
	        downArrowImage = new Texture(Gdx.files.internal("downArrow.png"));
	        leftArrowImage = new Texture(Gdx.files.internal("leftArrow.png"));
	        buttonBImage = new Texture(Gdx.files.internal("buttonB.png"));
	        buttonAImage = new Texture(Gdx.files.internal("buttonA.png"));
	        
	        rightArrow = new Rectangle(120,60,50,50);
	        leftArrow = new Rectangle(0,60,50,50);
	        downArrow = new Rectangle(60,0,50,50);
	        upArrow = new Rectangle(60,120,50,50);
	        buttonA = new Rectangle(60,0,100,100);
	        buttonB = new Rectangle(60,120,100,100);

	        // load the drop sound effect and the rain background "music"
	      
	        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("sympa.mp3"));
	        rainMusic.setLooping(true);

	       
	        // create a Rectangle to logically represent the bucket
	        bucket = new Rectangle();
	        bucket.x = currentPosition[0]; // center the bucket horizontally
	       
	                        // the bottom screen edge
	        bucket.width = 64;
	        bucket.height = bucketImage.getHeight();
	        bucket.y = currentPosition[1];

	        // create the raindrops array and spawn the first raindrop
	        raindrops = new Array<Rectangle>();
	        spawnRaindrop();

	    }

	    private void spawnRaindrop() {
	        Rectangle raindrop = new Rectangle();
	        raindrop.x = MathUtils.random(0, 800 - 64);
	        raindrop.y = 480;
	        raindrop.width = 64;
	        raindrop.height = 64;
	        raindrops.add(raindrop);
	        lastDropTime = TimeUtils.nanoTime();
	    }

	    @Override
	    public void render(float delta) {
	        // clear the screen with a dark blue color. The
	        // arguments to glClearColor are the red, green
	        // blue and alpha component in the range [0,1]
	        // of the color to be used to clear the screen.
	    	
	    	//Un fond noir là où il n'y a pas de texture
	        Gdx.gl.glClearColor(0, 0, 0, 1);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	        // tell the camera to update its matrices.
	        camera.update();
	        
	        //On redéfinit la position des touches pour qu'elles restent sur l'écran
	        rightArrow.x = camera.position.x - cameraWidth/2 + 120;
	        downArrow.x = camera.position.x - cameraWidth/2 + 60;
	        upArrow.x = camera.position.x - cameraWidth/2 + 60;
	        leftArrow.x = camera.position.x - cameraWidth/2 ;
	        
	        rightArrow.y = camera.position.y - cameraHeight/2 + 60;
	        downArrow.y = camera.position.y - cameraHeight/2 ;
	        upArrow.y = camera.position.y - cameraHeight/2 + 120;
	        leftArrow.y = camera.position.y - cameraHeight/2 +60;
	        
	        buttonA.setPosition(new Vector2(camera.position.x + cameraWidth/2 - buttonA.width,camera.position.y - cameraHeight/2));
	        buttonB.setPosition(new Vector2(camera.position.x + cameraWidth/2 - 2*buttonB.width,camera.position.y - cameraHeight/2));
		        
	        // tell the SpriteBatch to render in the
	        // coordinate system specified by the camera.
	        game.batch.setProjectionMatrix(camera.combined);

	        // begin a new batch and draw the bucket and
	        // all drops
	        game.batch.begin();
	       zone.draw();//Méthode dessinant tous les éléments de la zone
	        game.batch.draw(dropImage, rightArrow.x, rightArrow.y, rightArrow.width, rightArrow.height);
	        game.batch.draw(leftArrowImage, leftArrow.x, leftArrow.y, leftArrow.width, leftArrow.height);
	        game.batch.draw(upArrowImage, upArrow.x, upArrow.y, upArrow.width, upArrow.height);
	        game.batch.draw(downArrowImage, downArrow.x, downArrow.y, downArrow.width, downArrow.height);
	        game.batch.draw(downArrowImage, 790, 20, downArrow.width, downArrow.height);
	        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
		    game.font.draw(game.batch,"taille: "+camera.position.x+", "+ bucket.x,camera.position.x,50);
	        game.batch.draw(bucketImage, bucket.x, bucket.y);
	        game.batch.draw(buttonAImage, buttonA.x, buttonA.y, buttonA.width, buttonA.height);
	        game.batch.draw(buttonBImage, buttonB.x, buttonB.y, buttonB.width, buttonB.height);
	      /*  for (Rectangle raindrop : raindrops) {
	            game.batch.draw(dropImage, raindrop.x, raindrop.y);
	        }*/
	        game.batch.end();

	      
	    
				  inputsDetection(); //Pour détecter si on touche l'écran et où
		
	     
	        //On bouge la caméra pour q'elle suive le personnage 
	        if(bucket.x +bucket.getWidth() > camera.position.x + 50)
		       {
		    	   camera.translate(speed,0,0);
		       }
	        if(bucket.x  < camera.position.x -50)
		       {
		    	   camera.translate(-speed,0,0);
		       }
	        if(bucket.y +bucket.getHeight() > camera.position.y +50)
		       {
		    	   camera.translate(0,speed,0);
		       }
	        if(bucket.y < camera.position.y - 50)
		       {
		    	   camera.translate(0,-speed,0);
		       }

	       

	        // check if we need to create a new raindrop
	        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
	            spawnRaindrop();

	        // move the raindrops, remove any that are beneath the bottom edge of
	        // the screen or that hit the bucket. In the later case we increase the 
	        // value our drops counter and add a sound effect.
	        Iterator<Rectangle> iter = raindrops.iterator();
	        while (iter.hasNext()) {
	            Rectangle raindrop = iter.next();
	            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
	            if (raindrop.y + 64 < 0)
	                iter.remove();
	            if (raindrop.overlaps(bucket)) {
	                dropsGathered++;
	               
	                iter.remove();
	            }
	        }
	    }

	    private void inputsDetection()
	    {
	    	
	    	  // process user input
	        if (Gdx.input.isTouched()) {
	        	
	            Vector3 touchPos = new Vector3();
	            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0); //On récupère la position du toucher
	            camera.unproject(touchPos);
	            
	           
	            boolean moved = false; // Boolean qui nous dit si on a bougé récemment
	            
	            //canMove est a false si le delai d'attente pour se déplacer n'est pas terminé
	            if(canMove)
	            {
	            //Définition des hitbox
	            if(touchPos.x > leftArrow.x && touchPos.y > leftArrow.y && 
	            		touchPos.x < leftArrow.x + leftArrow.width && 
	            		touchPos.y < leftArrow.y + leftArrow.height
	            		)
	            {
	            	if(currentPosition[0] > 0)
	            	{
	            	currentPosition[0] -=1;//On change l'index de position
	            	moved = true; //On dit qu'on a bougé
	            	}
	            }
	            else
	            	if(touchPos.x > rightArrow.x && touchPos.y > rightArrow.y && 
		            		touchPos.x < rightArrow.x + rightArrow.width && 
		            		touchPos.y < rightArrow.y + rightArrow.height
		            		)
	            	{
	            		if(currentPosition[0] < cases.get(0).size()-1)
	            		{
		            		currentPosition[0] +=1;
		            		moved = true;	
	            		}
	            	}
	            
	            else
	 	            if(touchPos.x > downArrow.x && touchPos.y > downArrow.y && 
	 		            	touchPos.x < downArrow.x + downArrow.width && 
	 		            	touchPos.y < downArrow.y + downArrow.height)
	 	            {
	 	            	if(currentPosition[1] > 0)
		            	{
		 	            	currentPosition[1] -=1;
		 	            	moved = true;
	 	            	}
	 	            }
	            
	 	           else
			            if(touchPos.x > upArrow.x && touchPos.y > upArrow.y && 
				            	touchPos.x < upArrow.x + upArrow.width && 
				            	touchPos.y < upArrow.y + upArrow.height)
			            {
			            	if(currentPosition[1] < cases.size()-1)
		 	            	{
				            	currentPosition[1] +=1;
				            	moved = true;
			            	}
			            }
			            	
	            if(moved)
	            {
	            	canMove=false; //On a bougé, on bloque le mouvement
	            	
	            	//un nouveau thread qui remettra canMove a true au bout d'un certain temps.
	            	// Il faudra calculer le délai de sorte à ce qu'il soit adapté au temps de travel entre deux cases
	            	Timer.schedule(new Task(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							setMove();
						}
	            		
	            	}, 0.4f);
	            }
	           

	            }
	            
	            
	           
			      //Détection des boutons A et B
				          if(touchPos.x > buttonA.x && touchPos.y > buttonA.y && 
				        		  touchPos.x < buttonA.x + buttonA.width && 
				        		  touchPos.y < buttonA.y + buttonA.height)
				          {
				        	  //actionA
				          }
				     else
					      if(touchPos.x > buttonB.x && touchPos.y > buttonB.y && 
					    		  touchPos.x < buttonB.x + buttonB.width && 
					    		  touchPos.y < buttonB.y + buttonB.height)
					       {
					    	  //actionB
					       }
	            
	            
	          // bucket.x = cases.get(currentPosition[1]).get(currentPosition[0]).x;
	           // bucket.y = cases.get(currentPosition[1]).get(currentPosition[0]).y;
	           
	          
	          
	        }
	        
	        //Comme on change l'index de position de notre perso, il faut ensuite le déplacer aux coordonnées correspondantes
	        if(bucket.x < cases.get(currentPosition[1]).get(currentPosition[0]).x-2)
            {
            	bucket.x+=speed;
            }
	        else
	        	if(bucket.x > cases.get(currentPosition[1]).get(currentPosition[0]).x+2)
	        	bucket.x-=speed;
	        
	        if(bucket.y < cases.get(currentPosition[1]).get(currentPosition[0]).y-2)
            {
            	bucket.y+=speed;
            }
	        else
	        	if(bucket.y > cases.get(currentPosition[1]).get(currentPosition[0]).y+2)
	        	bucket.y-=speed;
	        
	    }
	    
	    
	    
	    @Override
	    public void resize(int width, int height) {
	    }

	    @Override
	    public void show() {
	        // start the playback of the background music
	        // when the screen is shown
	        rainMusic.play();
	    }

	    @Override
	    public void hide() {
	    }

	    @Override
	    public void pause() {
	    }

	    @Override
	    public void resume() {
	    }

	    @Override
	    public void dispose() {
	        dropImage.dispose();
	        bucketImage.dispose();
	       
	        rainMusic.dispose();
	    }
	    void setMove()
	    {
	    	  canMove = true;
	    }
	}
