/**
 * @author Allen Space
 * @brief: Java Class for tile sprites known as plates in Spelunker Steve.
 * 
 * */


package com.games.spelunkersteve.characters;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * @author Allen Space
 * @brief Plate Texture class that extends tileSprite from andengine.
 * 
 * */
public class PlateTexture extends TiledSprite {
	
	private Sprite texturePlates;
	private final PhysicsHandler mPhysicsHandler;
	private Body mBody;
	
	
	/**
	 * Description: Constructor which extends TiledSprite class.
	 * @author Allen Space
	 * */
	public PlateTexture(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager) {
			super(pX, pY, pWidth, pHeight, pTiledTextureRegion,
				vertexBufferObjectManager);
			
		// TODO Auto-generated constructor stub
		  this.mPhysicsHandler = new PhysicsHandler(this);
		  
		  this.registerUpdateHandler(this.mPhysicsHandler);
		  this.texturePlates = new Sprite(0,0,pTiledTextureRegion, vertexBufferObjectManager);

		}
	
	/**
	 * @author Allen Space
	 * @brief Moves the plate across the screen.
	 * */
	public void move(Body body) {
		
		Log.i("TexturePlate", "Inside Call for move().....");
		
		this.mBody = body;
		
		body.setLinearVelocity(new Vector2(-2.0f, 0));
		
		Log.i("TexturePlate", "Set Movement.....");
	}
			
}


