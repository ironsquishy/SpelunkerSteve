package com.games.spelunkersteve.characters;

import java.io.IOException;

import android.util.Log;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Fish extends AnimatedSprite {
	
	private static final float VELOCITY = 5.0f;
	private AnimatedSprite mfish;
	private final PhysicsHandler mPhysicsHandler;
	private Body mBody;
	private static final String TAG = "Fish. ";

	public Fish(int posX, int posY, SimpleBaseGameActivity sbga, BitmapTextureAtlas bta, TiledTextureRegion tta) throws IOException{
		super(posX, posY, tta, sbga.getVertexBufferObjectManager());
        this.mPhysicsHandler = new PhysicsHandler(this);
        this.registerUpdateHandler(this.mPhysicsHandler);
		try {
		// Initialize character's animated sprite.
		this.mfish = new AnimatedSprite(0,0,tta,sbga.getVertexBufferObjectManager());
		} finally {
			if (this.mfish!=null)
				this.mfish.dispose();
		}
		
	}
	
	
	
	
	public void moveFishLeft(Body body)
	{	
		this.mBody = body;
		this.setFlippedHorizontal(false);
		body.setLinearVelocity(new Vector2(-2.0f, 0));
	}
	
	public void moveFishRight(Body body)
	{
		this.mBody = body;
		this.setFlippedHorizontal(true);
		body.setLinearVelocity(new Vector2(1.0f, 0));
	}
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	

}
