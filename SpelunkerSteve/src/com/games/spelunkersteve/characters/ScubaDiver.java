package com.games.spelunkersteve.characters;

import java.io.IOException;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class ScubaDiver extends AnimatedSprite {
	
	// Scuba diver animated sprite data members.
	private AnimatedSprite mScubaDiver;
	private final PhysicsHandler mPhysicsHandler;
	private Body mBody;
	
	// Constructor.
	public ScubaDiver(int posX, int posY, SimpleBaseGameActivity sbga, TiledTextureRegion tta) throws IOException{
		super(posX, posY, tta, sbga.getVertexBufferObjectManager());
        this.mPhysicsHandler = new PhysicsHandler(this);
        this.registerUpdateHandler(this.mPhysicsHandler);
		try {
		// Initialize character's animated sprite.
		this.mScubaDiver = new AnimatedSprite(0,0,tta,sbga.getVertexBufferObjectManager());
		} finally {
			if (this.mScubaDiver!=null)
				this.mScubaDiver.dispose();
		}
	}
	
	// Makes the scuba diver dive.
	public void dive(Body body) {
		this.mBody = body;
		body.setLinearVelocity(new Vector2(0, -5.0f));
	}
	
	/**
	 * @author Christopher Washington
	 * Description: Slows down Divers object further towards the edge of the screen.
	 * @param body Body object for physics handler.
	 * */
	public void slowDiver(Body body)
	{
		this.mBody = body;
		body.setLinearVelocity(new Vector2(-1.0f, body.getLinearVelocity().y));
	}
	
	/**
	 * @author Christopher Washington
	 * Description: Resets Steves speed on screen.
	 * @param body Body object that is need for physics handler.
	 * */
	public void setConstantSpeed(Body body)
	{
		this.mBody = body;
		body.setLinearVelocity(new Vector2(0.0f, body.getLinearVelocity().y));
	}
	
	public void diveDown()
	{	
		this.setRotation(-45);	
	}
	
	public void diveUp()
	{	
		this.setRotation(45);	
	}
	
	private float getVelocity()
	{
		if(this.mBody ==null)
		{
			return 0f;
		}else 
		{
			return this.mBody.getLinearVelocity().y;
		}
	}
	
	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		if(getVelocity() < 0)
		{
			this.diveUp();
		}else {
			this.diveDown();
		}
		
		super.onManagedUpdate(pSecondsElapsed);
	}
}
