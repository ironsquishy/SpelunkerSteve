package com.games.spelunkersteve.characters;

import java.io.IOException;

import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.games.spelunkersteve.gameplay.SpelunkerSteveActivity;

public class DrawableSprites extends SpelunkerSteveActivity {
	
	
	private static final int CAMERA_CENTER_X = 320;
	private static final int CAMERA_CENTER_Y = 240;

	public DrawableSprites() {
		// TODO Auto-generated constructor stub
	}
	
	public Scene drawFish(Fish fish, Scene scene, BitmapTextureAtlas fishAtlas, TiledTextureRegion fishRegion)
	{
		// Initialize a diver object.
		try {
			
			fish = new Fish(CAMERA_CENTER_X,CAMERA_CENTER_Y, this, fishAtlas, fishRegion);
			fish.setSize(80, 80);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Attach the fish to the scene.
		scene.attachChild(fish);
		
		return scene;
	}

}
