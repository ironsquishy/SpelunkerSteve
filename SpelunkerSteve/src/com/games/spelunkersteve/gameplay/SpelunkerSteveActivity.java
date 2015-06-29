package com.games.spelunkersteve.gameplay;

import java.io.IOException;
import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
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
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.color.Color;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.games.spelunkersteve.characters.Plate;
import com.games.spelunkersteve.characters.ScubaDiver;
import com.games.spelunkersteve.mechanics.*;

public class SpelunkerSteveActivity extends SimpleBaseGameActivity implements
		IOnSceneTouchListener {

	/** Camera **/
	private static final int CAMERA_WIDTH = 640;
	private static final int CAMERA_HEIGHT = 480;
	private static final int CAMERA_CENTER_X = CAMERA_WIDTH / 2;
	private static final int CAMERA_CENTER_Y = CAMERA_HEIGHT / 2;
	
	private static final FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(1.0f, 0.0f, 0.5f);
	private static final FixtureDef SCUBA_FIX = PhysicsFactory.createFixtureDef(0.5f, 0.0f, 0.5f);
	
	private Scene mGameScene;
	
	private Camera mCamera;
	
	private static ResourceManager mResourceManager;
	
	// Parallax background.
	private ParallaxEntity mParallaxEntity;
	private Sprite mCaveCeiling, mCaveFloor;

	// Other Sprites.
	private Plate plates;
	private Body plateBody;

	// Animated objects.
	private ScubaDiver mScubaDiver;
	private Body mDiverBody;

	// private DrawableSprites sprites;

	// Physics.
	private PhysicsWorld mPhysicsWorld;
	private PhysicsBody mPhysicSet;

	// Top and bottom borders.
	private static Line hitMeTop;
	private static Line hitMeBottom;
	private static Line DiverRightBorder;

	// Cave tiles
	private static Plate[][] tiles;
	private static int[][] binaryLattice;
	private static int[][][] locationLattice;

	

	/*************************************************
	 ************** Inherited methods ****************
	 *************************************************/

	@Override
	public EngineOptions onCreateEngineOptions() {

		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		// Return an engine object - full screen,fixed landscape, scaling
		// scheme, width and height of scene.
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);

		// Set dithering to remove horizontal lines.
		engineOptions.getRenderOptions().setDithering(true);

		return engineOptions;
	}

	@Override
	protected void onCreateResources() throws IOException {
		
		ResourceManager.getInstance().loadGameTextures(mEngine, this);
		
		
		Lattice lattice = new Lattice(CAMERA_HEIGHT / 80, CAMERA_WIDTH / 80);
		lattice.generateLattice();
		binaryLattice = lattice.getBinaryLattice();
		locationLattice = lattice.getLocationLattice(CAMERA_HEIGHT, CAMERA_WIDTH);
		
		mPhysicSet = new PhysicsBody();
		
		
		Log.i("SCENE", "Load resources success.");
	}

	/**
	 * @author Allen Space
	 * @brief Code has been changed signficantly since initial given source
	 *        code.
	 * */
	@Override
	protected Scene onCreateScene() {

		// Register FPS logger.
		this.mEngine.registerUpdateHandler(new FPSLogger());

		// Scene
		this.mGameScene = new Scene();
		
		setBackground();
		
		mGameScene.setOnSceneTouchListener(this);

		this.setWorldPhysics();
		
		this.setForeground();
		
		this.drawBorderLine();
		
		this.drawDiver();
		
		this.sceneUpdateHandle();

		Log.i("SCENE", "Load Scene complete....");
		
		return mGameScene;
	}

	/**
	 * @author Sebastian Babb
	 * @brief OnTouch method handler.
	 * */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			// When the screen is touched, make the diver dive.

			mScubaDiver.dive(mDiverBody);

		}
		return false;
	}

	/*************************************************
	 ********** Scene helper methods.*****************
	 *************************************************/

	/**
	 * @author Allen Space
	 * @brief Given thet name set the Parallax background which must be called
	 *        in onCreatScene method.
	 * */
	private void setBackground() {
		// Create a parallax background object.
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(
				0, 0, 0, 5);

		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(
						ResourceManager.mAutoParallaxLayerBack.getWidth() / 2,
						ResourceManager.mAutoParallaxLayerBack.getHeight() / 2,
						ResourceManager.mAutoParallaxLayerBack, vertexBufferObjectManager)));			

		this.mGameScene.setBackground(autoParallaxBackground);
		
		Log.i("SCENE", "Draw background complete.");
	}

	/**
	 * @author Allen Space
	 * @brief Sets up the foreground. Initialize and draws TexturePlates
	 *        objects.
	 * 
	 * */
	private void setForeground() {
		
		drawCaveTiles();
		
		Log.i("SCENE", "Draw foreground complete.");
	}

	/**
	 * @author Sebastian Babb
	 * @brief Method to draw the diver for onCreateScene method.
	 * 
	 * */
	private void drawDiver() {
		// Initialize a diver object.
		try {
			this.mScubaDiver = new ScubaDiver(CAMERA_CENTER_X / 4, CAMERA_CENTER_Y, this, ResourceManager.mTextureRegionScubaDiver);
			this.mScubaDiver.setSize(64, 64);
			this.mScubaDiver.animate(150);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Attach the diver to the scene
		
		mDiverBody = mPhysicSet.physicsDiver(mScubaDiver, mPhysicsWorld);
		
		this.mGameScene.attachChild(this.mScubaDiver);
		
		Log.i("SCENE", "Draw diver completed.");
	}

	/**
	 * @author Allen Space, Sebastian Babb
	 * @brief Draws wireframe like borders and bounds.
	 * */
	public void drawBorderLine() {

		hitMeTop = new Line(0, CAMERA_HEIGHT- 50, CAMERA_WIDTH, CAMERA_HEIGHT - 50, 10, this.getVertexBufferObjectManager());

		hitMeBottom = new Line(0, 50, CAMERA_WIDTH, 50, 10,this.getVertexBufferObjectManager());

		DiverRightBorder = new Line(150, CAMERA_HEIGHT, 150, 0, 0, this.getVertexBufferObjectManager());

		DiverRightBorder.setColor(Color.GREEN);

		hitMeTop.setColor(Color.GREEN);
		hitMeBottom.setColor(Color.GREEN);

		mGameScene.attachChild(hitMeTop);
		mGameScene.attachChild(hitMeBottom);
		mGameScene.attachChild(DiverRightBorder);
		
		PhysicsFactory.createLineBody(mPhysicsWorld, hitMeTop, WALL_FIX);
		PhysicsFactory.createLineBody(mPhysicsWorld, hitMeBottom, WALL_FIX);
		PhysicsFactory.createLineBody(mPhysicsWorld, DiverRightBorder, WALL_FIX);
		
		Log.i("SCENE", "Draw borderline complete.");
	}
	
	
	/**
	 * @author Christopher Washington.
	 * */
	public void drawCaveTiles() {
		
		
		tiles = new Plate[locationLattice.length][locationLattice[0].length];
		for (int i = 0; i < locationLattice.length; i++) {
			for (int j = 0; j < locationLattice[0].length; j++) {
				if (binaryLattice[i][j] == 1) {
					tiles[i][j] = new Plate(
							locationLattice[i][j][1] + 32,
							locationLattice[i][j][0] + 32, 64, 64,
							ResourceManager.mTextureRegionPlates,
							this.getVertexBufferObjectManager());
					
							//plateBody =	mPhysicSet.physicsTiles(tiles[i][j], mPhysicsWorld);
					
					mGameScene.attachChild(tiles[i][j]);
				}
			}
		}
		
		Log.i("SCENE", "DrawCave tiles complete.");
	}

	/**
	 * @author Allen Space, Sebastian Babb
	 * @brief Setting world physics and divers physics. In future must need
	 *        diveded so diver physics is set in different method.
	 * */
	private void setWorldPhysics() {

		// Create a new phsyics world.
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0,
				SensorManager.GRAVITY_MARS), false);

		// Register the update handler.
		this.mGameScene.registerUpdateHandler(this.mPhysicsWorld);
		
		Log.i("SCENE", "Set physics world complete.");

	}
	
	

	/**
	 * @author Allen Space, Sebastian Babb
	 * @brief Handles updates on each frame.
	 * */
	public void sceneUpdateHandle() {
		mGameScene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() {
			}

			@Override
			public void onUpdate(final float pSecondsElpased) {
				
				if(!mCamera.isEntityVisible(mScubaDiver))
				{
					finish();
				}
				
				if (mScubaDiver.collidesWith(hitMeTop)) {
					hitMeTop.setColor(Color.RED);
					mScubaDiver.slowDiver(mDiverBody);
				} else {
					hitMeTop.setColor(Color.GREEN);
					mScubaDiver.setConstantSpeed(mDiverBody);
				}
				if (mScubaDiver.collidesWith(hitMeBottom)) {
					mScubaDiver.slowDiver(mDiverBody);
					hitMeBottom.setColor(Color.RED);
				} else {
					mScubaDiver.setConstantSpeed(mDiverBody);
					hitMeBottom.setColor(Color.GREEN);
				}

				if (mScubaDiver.collidesWith(DiverRightBorder)) {
					DiverRightBorder.setColor(Color.RED);
					mScubaDiver.slowDiver(mDiverBody);
				} else {
					DiverRightBorder.setColor(Color.GREEN);
					mScubaDiver.setConstantSpeed(mDiverBody);
				}
			}
		});

	}
	
	
	
	/**
	 * @author Allen Space
	 * */
	@Override
	protected void onDestroy()
	{
	    super.onDestroy();
	        
	    if (this.isGameLoaded())
	    {
	        System.exit(0);    
	    }
	}
}


