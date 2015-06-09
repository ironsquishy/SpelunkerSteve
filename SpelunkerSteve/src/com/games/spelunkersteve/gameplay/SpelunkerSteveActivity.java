package com.games.spelunkersteve.gameplay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.color.Color;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;

import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.games.spelunkersteve.characters.DrawableSprites;
import com.games.spelunkersteve.characters.Fish;
import com.games.spelunkersteve.characters.ScubaDiver;

public class SpelunkerSteveActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener {
		
	/** Camera **/
	private static final int CAMERA_WIDTH  = 640;
	private static final int CAMERA_HEIGHT = 480;
	private static final int CAMERA_CENTER_X = CAMERA_WIDTH/2;
	private static final int CAMERA_CENTER_Y = CAMERA_HEIGHT/2;
	
	
	/** Sprite sheets **/
	private static final int SCUBA_DIVER_SHEET_COLS = 2;
	private static final int SCUBA_DIVER_SHEET_ROWS = 2;
	
	/** Scenes **/
	//private Scene mSplashScene;
	private Scene mGameScene;
	
	
	private BitmapTextureAtlas mTextureAtlasScubaDiver;
	private BitmapTextureAtlas mTextureAtlasAutoParallax;
	
	private ITextureRegion mAutoParallaxLayerBack;
	private ITextureRegion mAutoParallaxLayerMid;
	private ITextureRegion mAutoParallaxLayerFront;
	
	// Parallax background.
	private ParallaxEntity mParallaxEntity;
	private Sprite mCaveCeiling, mCaveFloor;
	
	//Tiled textture regions for animated objects.
	private TiledTextureRegion mTextureRegionScubaDiver;
	
	
	//Animated objects.
	private ScubaDiver mScubaDiver;
	private Body mDiverBody;
	
	//private DrawableSprites sprites;
	
	// Physics.
	private PhysicsWorld mPhysicsWorld;
	
	//Top and bottom borders.
	private static  Line hitMeTop;
	private static  Line hitMeBottom;
	
//	Camera camera;	

	/*************************************************
	 ************** Inherited methods ****************
	 *************************************************/
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		// Create a new camera object for the scene.
		Camera camera = new Camera(0,0,CAMERA_WIDTH,CAMERA_HEIGHT);
		// Return an engine object - full screen,fixed landscape, scaling scheme, width and height of scene.
		EngineOptions engineOptions = new EngineOptions(true,ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),camera);
		// Set dithering to remove horizontal lines.
		engineOptions.getRenderOptions().setDithering(true);
		
		return engineOptions;
		}
	

	@Override
	protected void onCreateResources() throws IOException {
		
		// Set the graphics assets base directory.
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        
        // Parallax background assets.
        this.mTextureAtlasAutoParallax = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
        this.mAutoParallaxLayerBack  = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTextureAtlasAutoParallax, this.getAssets(), "temp_water.png", 0, 188);
        this.mAutoParallaxLayerMid   = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTextureAtlasAutoParallax, this, "temp_ceiling.png", 0, 669);
        this.mAutoParallaxLayerFront = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTextureAtlasAutoParallax, this, "temp_floor.png", 0, 0);
        this.mTextureAtlasAutoParallax.load(); 
        
        // Diver assets.
        this.mTextureAtlasScubaDiver = new BitmapTextureAtlas(this.getTextureManager(), 1300, 1390, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mTextureRegionScubaDiver = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mTextureAtlasScubaDiver, this, "scuba_diver_boy.png", 0, 0, SCUBA_DIVER_SHEET_COLS, SCUBA_DIVER_SHEET_ROWS);
        this.mTextureAtlasScubaDiver.load();
        
        // Cave obstacle assets.
        
	}

	@Override
	protected Scene onCreateScene() {
		// Register FPS logger.
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		// Scene
		this.mGameScene   = new Scene();
		
		// Set the screen touch listener.
		mGameScene.setOnSceneTouchListener(this);
		
		// Build the scene.
		setBackground();
		
		//draw sprites.
		drawDiver();
		
		//Set simple collisions.
		drawBorderLine();	
		sceneUpdateHandle();
		
		return mGameScene;
	}
	
	//User input handler.
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {
			// When the screen is touched, make the diver dive.
			
			mScubaDiver.dive(mDiverBody);
			
		}
		
		return false;
	}
		
	/*************************************************
	 ********** Scene helper methods.*****************
	 *************************************************/
	
	// Builds the parallax background effects.
	private void setBackground() {
		// Create a parallax background object.
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0,0,0,5);
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();		
		
		// Initialize the sprite members that will have collision detection.
		mCaveCeiling = new Sprite(this.mAutoParallaxLayerMid.getWidth()/2, this.mAutoParallaxLayerBack.getHeight()-this.mAutoParallaxLayerMid.getHeight()/2, this.mAutoParallaxLayerMid, vertexBufferObjectManager);
		mCaveFloor = new Sprite(this.mAutoParallaxLayerFront.getWidth()/2, this.mAutoParallaxLayerFront.getHeight()/2, this.mAutoParallaxLayerFront, vertexBufferObjectManager);
		
		// Assemble the parallax background.
		mParallaxEntity = new ParallaxEntity(-10.0f, this.mCaveCeiling);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(this.mAutoParallaxLayerBack.getWidth()/2, this.mAutoParallaxLayerBack.getHeight()/2, this.mAutoParallaxLayerBack, vertexBufferObjectManager)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5.0f, this.mCaveFloor));
		autoParallaxBackground.attachParallaxEntity(mParallaxEntity);
		
		// Set the background.
		this.mGameScene.setBackground(autoParallaxBackground);	
	}
	
	// Builds the animated diver sprite.
	private void drawDiver() {
		// Initialize a diver object.
		try {
			this.mScubaDiver = new ScubaDiver(CAMERA_CENTER_X/4,CAMERA_CENTER_Y, this, mTextureAtlasScubaDiver, mTextureRegionScubaDiver);
			this.mScubaDiver.setSize(100, 80);
			this.mScubaDiver.animate(200);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Attach the diver to the scene.
		this.mGameScene.attachChild(this.mScubaDiver);
		
		setPhysics();
	}
	
	
	//Draws top and bottom line for border collision.
	public void drawBorderLine()
	{
		hitMeTop    = new Line(0,CAMERA_HEIGHT-this.mAutoParallaxLayerMid.getHeight()/2,CAMERA_WIDTH, CAMERA_HEIGHT-this.mAutoParallaxLayerMid.getHeight()/2, 10, this.getVertexBufferObjectManager());
		hitMeBottom = new Line(0,this.mAutoParallaxLayerFront.getHeight()/2,CAMERA_WIDTH, this.mAutoParallaxLayerFront.getHeight()/2, 10, this.getVertexBufferObjectManager());
		hitMeTop.setColor(Color.TRANSPARENT);
		hitMeBottom.setColor(Color.TRANSPARENT);
		mGameScene.attachChild(hitMeTop);
		mGameScene.attachChild(hitMeBottom);
	}
	
	
	 // Sets up the physics environment.
	private void setPhysics() {
		// Create a new phsyics world.
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_MARS),false);
		// Register the update handler.
		this.mGameScene.registerUpdateHandler(this.mPhysicsWorld);
		
		// Create the fixture definition - how the diver body will respond to impacts against other physics bodies.
		final FixtureDef SCUBA_FIX = PhysicsFactory.createFixtureDef(0.0f,0.0f,0.0f);
		
		// Create the diver's physics body - dynamic.
		mDiverBody = PhysicsFactory.createCircleBody(mPhysicsWorld, mScubaDiver, BodyType.DynamicBody, SCUBA_FIX);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mScubaDiver, mDiverBody, true, false));
		
		FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f);
		
		// Top and bottom limits for the diver .
		Line top_border    = new Line(0,CAMERA_HEIGHT+10,CAMERA_WIDTH, CAMERA_HEIGHT+1, 1, this.getVertexBufferObjectManager());
		Line bottom_border = new Line(0, 0,CAMERA_WIDTH, 0, 0, this.getVertexBufferObjectManager());

		PhysicsFactory.createLineBody(mPhysicsWorld, top_border, WALL_FIX);
		PhysicsFactory.createLineBody(mPhysicsWorld, bottom_border, WALL_FIX);
		this.mGameScene.attachChild(top_border);
		this.mGameScene.attachChild(bottom_border);
		
	}
	
	
	
	//check if steve hit top or bottom.
	public void sceneUpdateHandle()
	{
		mGameScene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset(){}
		
			@Override
			public void onUpdate(final float pSecondsElpased) {
				
				if(mScubaDiver.collidesWith(hitMeTop)) {
					hitMeTop.setColor(Color.RED);
				}else {
					hitMeTop.setColor(Color.TRANSPARENT);
				}
				if(mScubaDiver.collidesWith(hitMeBottom)) {
					hitMeBottom.setColor(Color.RED);
				}else {
					hitMeBottom.setColor(Color.TRANSPARENT);
				}
			}
		});

	}
	

}

/*
 * 
 	private BitmapTextureAtlas mTextureAtlasFish;
 	private TiledTextureRegion mTextureRegionFish;
 	private Fish littleFishy;
	private Body mFishyBody;
 		//Fish Texture generator for onCreateResource.
        //this.mTextureAtlasFish = new BitmapTextureAtlas(this.getTextureManager(), 1300, 1390, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        //this.mTextureRegionFish = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mTextureAtlasFish, this, "fish.png", 0, 0, 2, 2);
        //this.mTextureAtlasFish.load();
 public void drawFish(int location)
	{			
		// Initialize a diver object.
				try {
					this.littleFishy = new Fish(CAMERA_WIDTH, location, this, mTextureAtlasFish, mTextureRegionFish);
					Log.i(TAG_FISH, "Fish object decleration: ");
					Log.i(TAG_FISH, "Init fish at: " + location);
					this.littleFishy.setSize(80, 80);
				} catch (IOException e) {
					e.printStackTrace();
				}
				// Attach the fish to the scene.
				
				this.mGameScene.attachChild(littleFishy);
				Log.i(TAG_FISH, "Attched enabled to Fish object... ");
				
				setFishPhysics();
				Log.i(TAG_FISH, "Physics set for Fish object... ");
	}
	
//Update on for fish object.
	public void updateFishScene() 
	{
		mGameScene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset(){}
		
			@Override
			public void onUpdate(final float pSecondsElpased) {
				deltaTime += pSecondsElpased;
				
				if(deltaTime > 5.0f && deltaTime < 10.f){
					
					littleFishy.moveFishRight(mFishyBody);
					
				}else if (deltaTime > 10.f){
					
					littleFishy.moveFishLeft(mFishyBody);
				
				}else{
					
					littleFishy.moveFishLeft(mFishyBody);
				}
			}
		});
	}
	
	public void setFishPhysics()
	{
		// Create a new phsyics world.
				this.mPhysicsWorld = new PhysicsWorld(new Vector2(0,0),false);
				
				// Register the update handler.
				this.mGameScene.registerUpdateHandler(this.mPhysicsWorld);
				 
				// Create the fixture definition - how the diver body will respond to impacts against other physics bodies.
				final FixtureDef FISH_FIX = PhysicsFactory.createFixtureDef(0.0f,0.0f,0.0f);
			
				//Create for fish object physics.
				mFishyBody = PhysicsFactory.createCircleBody(mPhysicsWorld, littleFishy, BodyType.DynamicBody, FISH_FIX);
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(littleFishy, mFishyBody, true, false));

	}
	
	public void setFishPhysics()
	{
		// Create a new phsyics world.
				this.mPhysicsWorld = new PhysicsWorld(new Vector2(0,0),false);
				
				// Register the update handler.
				this.mGameScene.registerUpdateHandler(this.mPhysicsWorld);
				 
				// Create the fixture definition - how the diver body will respond to impacts against other physics bodies.
				final FixtureDef FISH_FIX = PhysicsFactory.createFixtureDef(0.0f,0.0f,0.0f);
			
				//Create for fish object physics.
				mFishyBody = PhysicsFactory.createCircleBody(mPhysicsWorld, littleFishy, BodyType.DynamicBody, FISH_FIX);
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(littleFishy, mFishyBody, true, false));

	}
	
	
	*/

