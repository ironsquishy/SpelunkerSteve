package com.games.spelunkersteve.mechanics;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.games.spelunkersteve.characters.*;
import com.games.gameplay.spelunkersteve.*;

import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

public class PhysicsBody {
	/**Constants**/
	private static final FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(1.0f, 0.0f, 0.5f);
	private static final FixtureDef SCUBA_FIX = PhysicsFactory.createFixtureDef(0.5f, 0.0f, 0.5f);
	
	/**Sprite and Body objects**/
	private static Body mScubaBody;
	private static Body mPlateBody;
	

	/**
	 * @author Allen Space
	 * Description: Empty constructor.
	 * */
	public void PhysicsBody()
	{
		//do something
	}
	
	/**
	 * @author Allen Space
	 * @param pScuba ScubaDiver class sprite object.
	 * @param pPhysicsWorld PhysicsWorld object from activity.
	 * Description: Sets for the scubadiver sprite to be physics listed.
	 * 
	 * */
	public Body physicsDiver(ScubaDiver pScuba, PhysicsWorld pPhysicsWorld)
	{
		// Create the diver's physics body - dynamic.
		this.mScubaBody = PhysicsFactory.createCircleBody(pPhysicsWorld,
						pScuba, BodyType.DynamicBody, SCUBA_FIX);
				
		pPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(
						pScuba, this.mScubaBody, true, false));
		
		return this.mScubaBody;
		
				
	}
	
	/**
	 * @author Allen Space
	 * @param pPlates Plate class sprites.
	 * @parma pPhysicsWorld PhysicsWorld objecr from activity.
	 * Descirption: Connects Plate tiles to the physicsworld.
	 * */
	public Body physicsTiles(Plate pPlates, PhysicsWorld pPhysicsWorld)
	{
		
		this.mPlateBody = PhysicsFactory.createBoxBody(pPhysicsWorld, pPlates,
				BodyType.KinematicBody, WALL_FIX);

		pPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(pPlates,
				this.mPlateBody, true, false));

		pPlates.move(this.mPlateBody);
		
		return this.mPlateBody;
	}
	
	
}
