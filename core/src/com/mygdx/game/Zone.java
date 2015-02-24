package com.mygdx.game;


import java.util.ArrayList;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Zone {

	//Ces listes contiendront tous les 'objets' (décors, escaliers...) qui seront ajoutés dans la zone
	ArrayList<Texture> images = new ArrayList<Texture>();
	ArrayList<Rectangle> boxes = new ArrayList<Rectangle>();
	
	//les trois compansants essentiels pour une zone
	Texture floorImage;
	Texture wallImage;
	Texture backgroundImage;
	
		Rectangle floor;
		Rectangle wall ;
		Rectangle background;
		
		//Récupération des variables utiles
		MyGdxGame game;
		OrthographicCamera camera;
		int cameraWidth;
		int cameraHeight;
		
		//Contient la découpe de la zone
		ArrayList<ArrayList<Vector2>> cases = new ArrayList<ArrayList<Vector2>>();
		
		float zoneWidth;
		float zoneHeight;
		
		//nb de cases adapté pour taille de l'écran et delai de 0.4, pas utilisés
		int nbCasesWidth = 10;
		int nbCasesHeight = 6;
	
	public Zone(MyGdxGame game, OrthographicCamera camera,int cameraWidth, int cameraHeight)
	{
		
		this.game = game;
		this.camera = camera;
		this.cameraWidth = cameraWidth;
		this.cameraHeight = cameraHeight;
		
		/*
		floorImage = new Texture(Gdx.files.internal("floor.png"));
		wallImage =  new Texture(Gdx.files.internal("wall.png"));
		*/
		/*
		  zoneWidth=cameraWidth;
		 
		zoneHeight=cameraHeight;
		*/
		/*
		floor = new Rectangle(0,0,zoneWidth, zoneHeight);
		wall = new Rectangle(0,  zoneHeight,zoneWidth, cameraHeight/4);	
		*/
		
	}
	
	
	public void setWalkArea(Texture image,Rectangle box, int nbCasesWidth, int nbCasesHeight)
	{
		floorImage = image;
		floor = box;
		zoneWidth = box.width;
		zoneHeight = box.height;
		this.nbCasesWidth = nbCasesWidth;
		this.nbCasesHeight = nbCasesHeight;
	}

	public void setWall(Texture wallImage,int height)
	{
		this.wallImage = wallImage;
		Rectangle wall = new Rectangle(this.floor.x,this.floor.y+this.floor.height, this.floor.width,height);
		this.wall = wall;
	}
	
	//Pour ajouter des éléments. les coordonnées de la box sont en fait l'index de la case visée. La taille définit le nombre de cases à remplir
	public void addElement(Texture image, Rectangle box)
	{
		images.add(image);
		int x =(int)box.x;
		int y = (int)box.y;
		int width = (int) box.width;
		int height = (int) box.height;
		box.x = cases.get(y).get(x).x;
		box.y = cases.get(y).get(x).y;
		
		box.width =box.width*(cases.get(2).get(2).x - cases.get(1).get(1).x);
		box.height =box.height*(cases.get(2).get(2).y - cases.get(1).get(1).y);
		
		boxes.add(box);
	}
	
	//découpe de la zone suivant les paramètres déjà rentrés
	public ArrayList<ArrayList<Vector2>> getPoints()
	{
		ArrayList<Vector2> ligne = new ArrayList<Vector2>();
		
		float xBegin = 0;//zoneWidth/nbCasesWidth/2;
		float yBegin =0;// zoneHeight/nbCasesHeight/2;
		
		
		float y=yBegin;
		while(y <= zoneHeight)
		{
			ligne=new ArrayList<Vector2>();
		float x=xBegin;
		System.out.println("Largeur: "+ zoneWidth);
		while(x < zoneWidth)
		{
			ligne.add(new Vector2(x,y));
			x+= zoneWidth/nbCasesWidth;
			System.out.println(x);
			
		}
		cases.add(ligne);
		y+= zoneHeight/nbCasesHeight;
		
		
		
		
		}
		
		return cases;
	}
	
	//Méthode dessinant tous les éléments de la zone, à appeler pour dessiner une zone...
	public void draw()
	{
	
		
		 game.batch.draw(floorImage, floor.x, floor.y, floor.width, floor.height);
		 game.batch.draw(wallImage, wall.x, wall.y, wall.width, wall.height);	 
		 
		 
		 if(images.size() >0 && boxes.size() >0)
		 {
			 try
			 {
				 if(images.size() != boxes.size())
				 {
					 throw new Exception("Il n'y a pas le même nombre d'images / rectangle !");
				 }
				 for(int i=0;i < images.size();i++)
				 {
					 game.batch.draw(images.get(i),boxes.get(i).x, boxes.get(i).y, boxes.get(i).width, boxes.get(i).height);
				 }
			  }
			 catch(Exception e)
			 {
				 System.out.println(e.getMessage());
			 }
		 }
		 
		
	}
	
	
	
}
