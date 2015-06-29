package com.games.spelunkersteve.mechanics;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class ResourceManager {

	// ResourceManager Singleton instance
	private static ResourceManager INSTANCE;
	
	private static final int SCUBA_DIVER_SHEET_COLS = 2;
	private static final int SCUBA_DIVER_SHEET_ROWS = 2;
	private static final int TILE_TEXTURE_COLS = 3;
	private static final int TILE_TEXTURE_ROWS = 2;
	
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private BitmapTextureAtlas mTextureAtlasAutoParallax;
	
	public static ITextureRegion mGameBackgroundTextureRegion;
	public static ITextureRegion mMenuBackgroundTextureRegion;
	public static ITextureRegion mAutoParallaxLayerBack;
	public static ITextureRegion mAutoParallaxLayerMid;
	public static ITextureRegion mAutoParallaxLayerFront;
	
	public static TiledTextureRegion mTextureRegionScubaDiver;
	public static TiledTextureRegion mTextureRegionPlates;

	public Sound mSound;

	public Font	mFont;

	ResourceManager(){
		// not needed
	}

	public synchronized static ResourceManager getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ResourceManager();
		}
		return INSTANCE;
	}

	/* Each scene within a game should have a loadTextures method as well
	 * as an accompanying unloadTextures method. This way, we can display
	 * a loading image during scene swapping, unload the first scene's textures
	 * then load the next scenes textures.
	 */
	public synchronized void loadGameTextures(Engine pEngine, Context pContext){
		// Set our game assets folder in "assets/gfx/game/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
		
		this.mTextureAtlasAutoParallax = new BitmapTextureAtlas(pEngine.getTextureManager(), 2048, 2048, TextureOptions.DEFAULT);
		
		/**Background Resources**/
		mAutoParallaxLayerBack = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTextureAtlasAutoParallax, pContext, "underwater.png", 0, 0);

		mTextureRegionPlates = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, pContext, "plates.png", 0, 0, 3, 2);

		mTextureRegionScubaDiver = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mBitmapTextureAtlas, pContext, "scuba_diver_boy.png", 0, 100, SCUBA_DIVER_SHEET_COLS, SCUBA_DIVER_SHEET_ROWS);
	
		Log.i("RESOURCE", "LayerBack is: " + mAutoParallaxLayerBack);
		
		this.mBitmapTextureAtlas.load();
		this.mTextureAtlasAutoParallax.load();
		
		
		/*
		try {
			mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
			mBitmapTextureAtlas.load();
			
			mTextureAtlasAutoParallax.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,1,1));
			mTextureAtlasAutoParallax.load();
			
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		*/
		
		Log.i("RESOURCE", "Game texture load complete.");
	}
	
	/* All textures should have a method call for unloading once
	 * they're no longer needed; ie. a level transition. */
	public synchronized void unloadGameTextures(){
		// call unload to remove the corresponding texture atlas from memory
		mBitmapTextureAtlas.unload();
		
		mTextureAtlasAutoParallax.unload();
		
		// ... Continue to unload all textures related to the 'Game' scene
		
		// Once all textures have been unloaded, attempt to invoke the Garbage Collector
		System.gc();
	}
	
	/* Similar to the loadGameTextures(...) method, except this method will be
	 * used to load a different scene's textures
	 */
	public synchronized void loadMenuTextures(Engine pEngine, Context pContext){
		// Set our menu assets folder in "assets/gfx/menu/"
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		
		BuildableBitmapTextureAtlas mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(pEngine.getTextureManager() ,800 , 480);
		
		mMenuBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, pContext, "menu_background.png");
		
		try {
			mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 1));
			mBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
	}
	
	// Once again, this method is similar to the 'Game' scene's for unloading
	public synchronized void unloadMenuTextures(){
		// call unload to remove the corresponding texture atlas from memory
		BuildableBitmapTextureAtlas mBitmapTextureAtlas = (BuildableBitmapTextureAtlas) mMenuBackgroundTextureRegion.getTexture();
		mBitmapTextureAtlas.unload();
		
		// ... Continue to unload all textures related to the 'Game' scene
		
		// Once all textures have been unloaded, attempt to invoke the Garbage Collector
		System.gc();
	}
	
	/* As with textures, we can create methods to load sound/music objects
	 * for different scene's within our games.
	 */
	public synchronized void loadSounds(Engine pEngine, Context pContext){
		// Set the SoundFactory's base path
		SoundFactory.setAssetBasePath("sounds/");
		 try {
			 // Create mSound object via SoundFactory class
			 mSound	= SoundFactory.createSoundFromAsset(pEngine.getSoundManager(), pContext, "sound.mp3");			 
		 } catch (final IOException e) {
             Log.v("Sounds Load","Exception:" + e.getMessage());
		 }
	}	
	
	/* In some cases, we may only load one set of sounds throughout
	 * our entire game's life-cycle. If that's the case, we may not
	 * need to include an unloadSounds() method. Of course, this all
	 * depends on how much variance we have in terms of sound
	 */
	public synchronized void unloadSounds(){
		// we call the release() method on sounds to remove them from memory
		if(!mSound.isReleased())mSound.release();
	}
	
	/* Lastly, we've got the loadFonts method which, once again,
	 * tends to only need to be loaded once as Font's are generally 
	 * used across an entire game, from menu to shop to game-play.
	 */
	public synchronized void loadFonts(Engine pEngine){
		FontFactory.setAssetBasePath("fonts/");
		
		// Create mFont object via FontFactory class
		mFont = FontFactory.create(pEngine.getFontManager(), pEngine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),  32f, true, org.andengine.util.adt.color.Color.WHITE_ABGR_PACKED_INT);

		mFont.load();
	}
	
	/* If an unloadFonts() method is necessary, we can provide one
	 */
	public synchronized void unloadFonts(){
		// Similar to textures, we can call unload() to destroy font resources
		mFont.unload();
	}
}
