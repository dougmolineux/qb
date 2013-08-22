package com.restlink.qb;

import java.io.IOException;
import java.io.InputStream;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;
import org.andengine.util.HorizontalAlign;
import android.graphics.Typeface;
import android.util.FloatMath;
import android.util.Log;

public class FieldGoal extends SimpleBaseGameActivity implements IOnSceneTouchListener {

	/*
	 * Version 0.0.1 Last updated 1/7/2013
	 * 
	 * TODO: - Refactor the original positioning of the players to be seperate
	 * from the "make*" functions, so that we can reuse it after each down -
	 * Somehow figure out what x coordinates line up with yardage points This
	 * will be necessary when figuring out how to place the first yard line 10
	 * yds and can tell where the touchdown line is, and other horizontal
	 * positioning logic - Figure out Y coordinates that line up with the
	 * sidelines, (can tell if player runs out of bounds - Add functionality to
	 * see if a player sprite currently has the ball - Show Touchdown if player
	 * passes touchdown line with the ball - Add a score to the top left (using
	 * Text class) - Add a timer that runs down, while ball is in play - Add a
	 * defensive team - Give the defense the capability to tackle and intercept
	 * - Add a splash screen, menu
	 */

	// declare camera and it's width and height
	private static int CAMERA_WIDTH = 480;
	private static int CAMERA_HEIGHT = 800;

	// set the max velocity of x and y (of the camera)
	// only useful if we want to use a smooth camera
	// private static int MAX_VELOCITY_X = 120;
	// private static int MAX_VELOCITY_Y = 20;

	private static int BALL_WIDTH = 16;
	private static int BALL_HEIGHT = 28;

	// set the number of players;
	private static int NUMER_OF_OLINEMEN = 3;
	private static int NUMER_OF_WIDE_RECEIVERS = 2;

	// declare the camera, smoothcamera is great for camera movement, normal
	// camera is good for no camera movement
	// private SmoothCamera camera;
	private Camera camera;

	// create the team
	public qbTeam team1;

	// players texture regions
	public ITextureRegion qbTextureRegion;
	public BitmapTextureAtlas qbTextureAtlas;
	public ITextureRegion wrTextureRegion;
	public BitmapTextureAtlas wrTextureAtlas;

	public ITextureRegion[] playersTextureRegion = new ITextureRegion[NUMER_OF_OLINEMEN];
	public BitmapTextureAtlas[] playersTextureAtlas = new BitmapTextureAtlas[NUMER_OF_OLINEMEN];

	public ITextureRegion[] wrsTextureRegion = new ITextureRegion[NUMER_OF_WIDE_RECEIVERS];
	public BitmapTextureAtlas[] wrsTextureAtlas = new BitmapTextureAtlas[NUMER_OF_WIDE_RECEIVERS];

	// background texture region
	private ITextureRegion mBackgroundTextureRegion;

	// set player data
	private float playerX = 0;
	private float playerY = 0;
	private float yOffset = 173;
	private Sprite qbSprite;
	private String playerSpritePath = "playerSprite.png";

	// offensive linemen
	private Sprite[] olineman = new Sprite[NUMER_OF_OLINEMEN];
	private PhysicsHandler[] olinemenPhysicsHandler = new PhysicsHandler[NUMER_OF_OLINEMEN];

	// single wide receiver
	private Sprite wrSprite;
	private Entity wrEntity;
	private PhysicsHandler wrPhysicsHandler;

	// array of wide receivers
	private Sprite[] wideReceivers = new Sprite[NUMER_OF_WIDE_RECEIVERS];
	private PhysicsHandler[] wrsPhysicsHandler = new PhysicsHandler[NUMER_OF_WIDE_RECEIVERS];

	// set ball data
	private Sprite ball;
	private PhysicsHandler ballPhysicsHandler;
	private float beingThrown = 0;
	private Entity ballEntity;
	protected ITextureRegion ballTextureRegion;
	protected BitmapTextureAtlas ballTextureAtlas;

	// set highlighter info
	private Sprite highlighter;
	protected ITextureRegion highlighterTextureRegion;
	protected BitmapTextureAtlas highlighterTextureAtlas;
	private PhysicsHandler highlighterPhysicsHandler;
	private Entity highlighterEntity;
	private float highlighterOffset = 11;

	// set the firstdown line data
	private Sprite fdline;
	protected ITextureRegion fdlineTextureRegion;
	protected BitmapTextureAtlas fdlineTextureAtlas;
	private PhysicsHandler fdlinePhysicsHandler;
	// set game data
	private float hiking = 0;

	private Font mFont;
	private Text centerText;

	Scene scene;
	private Entity qbPhyicsHandler;

	@Override
	protected void onCreateResources() {

		// Set the directory that contains all the images
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		// load players
		loadQB(playerSpritePath);
		loadWR(playerSpritePath);

		// load offensive linemen
		for (int i = 0; i < NUMER_OF_OLINEMEN; i++)
			loadLinebackers(playerSpritePath, i);

		// load wide receivers
		for (int i = 0; i < NUMER_OF_WIDE_RECEIVERS; i++)
			loadWideReceivers(playerSpritePath, i);

		// load the ball and background images
		loadBall();
		loadBackground();
		loadHighlighter();
		loadFirstdownLine();

		// set up font data
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50, Color.WHITE.hashCode());
		this.mFont.load();

	}

	@Override
	protected Scene onCreateScene() {

		// create new scene and a team
		scene = new Scene();
		//team1 = new qbTeam();

		// set the background
		Sprite backgroundSprite = new Sprite(0, 0, this.mBackgroundTextureRegion, getVertexBufferObjectManager());
		scene.attachChild(backgroundSprite);

		playerX = (CAMERA_WIDTH - qbTextureRegion.getWidth()) / 2;
		playerY = ((CAMERA_HEIGHT - qbTextureRegion.getHeight()) / 2) + yOffset;

		// make the first down line
		makeFdline(scene, 0, playerY - 100);

		// attach a player to the scene
		makeQB(scene, playerX, playerY);

		// make the 2nd player, move the to the left a bit
		makeWR(scene, playerX - 100, playerY);

		// make the linebackers
		for (int i = 0; i < NUMER_OF_OLINEMEN; i++)
			makeLinebackers(scene, playerX - 50, playerY - 50, i, 50);

		// make the wide receivers
		for (int i = 0; i < NUMER_OF_WIDE_RECEIVERS; i++)
			makeWideReceivers(scene, playerX + 100, playerY, i, 50);

		// make a ball and attach
		makeBall(scene);

		// make the highlighter
		makeHighlighter(scene, playerX - highlighterOffset, playerY - highlighterOffset);

		// set the touch listener to the field and return
		scene.setOnSceneTouchListener(this);

		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		centerText = new Text(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, this.mFont, "QB 2013", new TextOptions(HorizontalAlign.CENTER), vertexBufferObjectManager);
		centerText.setColor(new Color(1, 0, 0));
		centerText.setPosition(CAMERA_WIDTH / 2 - centerText.getWidth() / 2, (float) (CAMERA_HEIGHT * 0.75));
		scene.attachChild(centerText);

		return scene;
	}

	public void placePlayers() {

		/*
		 * TODO: move each player on the team to the correct coordinates (use
		 * above as reference)
		 */
	}

	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {

		if (pSceneTouchEvent.isActionDown()) {

			if (hiking == 0) {
				return hike();
			} else {
				return throwBall(pSceneTouchEvent);
			}

		} else
			return false;
	}

	private boolean hike() {

		if (hiking == 1)
			return false;

		// centerText.setVisible(false);
		centerText.setText("HIKE");
		centerText.setPosition(CAMERA_WIDTH / 2 - centerText.getWidth() / 2, CAMERA_HEIGHT / 2);

		movePlayer(100, wrEntity, wrSprite);

		wrRun();

		hiking = 1;
		return true;

	}

	private void wrRun() {

		// TODO make the wide receivers run

	}

	private void movePlayer(float distance, Entity playerEntity, final Sprite playerSprite) {

		// set the from and to coordinates, calculate the distance from player
		// to touch event
		float from_X = playerSprite.getX();
		float from_Y = playerSprite.getY();
		float to_X = from_X;
		float to_Y = from_Y - distance;
		float dx = to_X - from_X;
		float dy = to_Y - from_Y;
		float speed = 200f;
		float distToTouch = (float) FloatMath.sqrt((dx) * (dx) + (dy) * (dy));
		float time = distToTouch / speed;

		// make move modifier
		MoveModifier myMovemod = new MoveModifier(time, from_X, to_X, from_Y, to_Y);

		// make sequence mod with move modifier
		SequenceEntityModifier modifier = new SequenceEntityModifier(myMovemod) {

			@Override
			protected void onModifierFinished(IEntity pItem) {
				// animation finished
				centerText.setVisible(false);
				super.onModifierFinished(pItem);
			}

			@Override
			public float onUpdate(float pSecondsElapsed, IEntity pItem) {
				// move highlighter to follow player
				highlighter.setPosition(playerSprite.getX() - highlighterOffset, playerSprite.getY() - highlighterOffset);
				return super.onUpdate(pSecondsElapsed, pItem);
			}
		};

		// apply modifier
		playerEntity.registerEntityModifier(modifier);

	}

	private boolean throwBall(TouchEvent event) {

		// if the ball is mid-air don't throw
		if (beingThrown == 1)
			return false;

		// mark the ball as being thrown
		beingThrown = 1;

		// show the ball as it's thrown (it's hidden when it reaches touch xy)
		ballEntity.setVisible(true);
		this.highlighterEntity.setVisible(false);

		// get the touch event x,y as well as the player's xy
		final float from_X = this.qbSprite.getX();
		final float from_Y = this.qbSprite.getY();
		final float to_X = event.getX();
		final float to_Y = event.getY();
		float dx = to_X - from_X;
		float dy = to_Y - from_Y;

		double Radius = Math.atan2(dy, dx);

		ball.setRotation(MathUtils.radToDeg((float) Radius) + 90);
		qbSprite.setRotation(MathUtils.radToDeg((float) Radius) + 90);

		// calculate distance so that speed of ball is constantly 400f
		final float speed = 300f;
		final float distance = (float) FloatMath.sqrt((dx) * (dx) + (dy) * (dy));
		final float time = distance / speed;

		final MoveModifier moveMod = new MoveModifier(time, from_X, to_X, from_Y, to_Y);

		final SequenceEntityModifier modifier = new SequenceEntityModifier(moveMod) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				ballEntity.setVisible(false);
				highlighterEntity.setVisible(true);
				beingThrown = 0;

				// check if the player caught the ball
				if (checkForCollision(ball, wrSprite)) {
					// highlight reciever and run
					// highlighter.setPosition(playerSprite.getX() -
					// highlighterOffset, playerSprite.getY() -
					// highlighterOffset);
					runForTouchdown();
				} else {
					returnToFormation();
				}
			}

		};

		// apply modifier
		ballEntity.registerEntityModifier(modifier);

		return true;

	}

	private void returnToFormation() {
		team1.returnToFormation();
	}

	private boolean checkForCollision(Sprite sp1, Sprite sp2) {
		// set the sprites x y's, width and height
		float sp1x = sp1.getX();
		float sp1y = sp1.getY();
		float sp2x = sp2.getX();
		float sp2y = sp2.getY();
		float sp2h = sp2.getHeight();
		float sp2w = sp2.getWidth();

		if ((sp1x > sp2x && sp1x < (sp2x + sp2w)) && (sp1y > sp2y && sp1y < (sp2y + sp2h)))
			return true;
		else
			return false;
	}

	private void runForTouchdown() {
		movePlayer(400, wrEntity, wrSprite);
	}

	private void makeBall(Scene scene) {
		this.ball = new Sprite(-40, -40, this.ballTextureRegion, this.getVertexBufferObjectManager());
		this.ball.setCullingEnabled(true);
		this.ballPhysicsHandler = new PhysicsHandler(this.ball);
		this.ball.registerUpdateHandler(this.ballPhysicsHandler);
		this.ballEntity = (Entity) this.ballPhysicsHandler.getEntity();
		scene.attachChild(this.ball);
	}

	private void makeQB(Scene scene, float posX, float posY) {
		this.qbSprite = new Sprite(posX, posY, this.qbTextureRegion, this.getVertexBufferObjectManager());
		final PhysicsHandler physicsHandler = new PhysicsHandler(this.qbSprite);
		this.qbSprite.registerUpdateHandler(physicsHandler);
		this.qbPhyicsHandler = (Entity) physicsHandler.getEntity();
		scene.attachChild(this.qbSprite);
		//team1.addPlayer(this.qbSprite, this.qbPhyicsHandler, posX, posY);
	}

	private void makeWR(Scene scene, float posX, float posY) {
		this.wrSprite = new Sprite(posX, posY, this.wrTextureRegion, this.getVertexBufferObjectManager());
		this.wrPhysicsHandler = new PhysicsHandler(this.wrSprite);
		this.wrSprite.registerUpdateHandler(this.wrPhysicsHandler);
		this.wrEntity = (Entity) this.wrPhysicsHandler.getEntity();
		scene.attachChild(this.wrSprite);
		//team1.addPlayer(this.wrSprite, this.wrEntity, posX, posY);
	}

	private void makeLinebackers(Scene scene, float posX, float posY, int i, int spaceBetween) {
		float currentX = posX + (spaceBetween * i);
		this.olineman[i] = new Sprite(currentX, posY, this.playersTextureRegion[i], this.getVertexBufferObjectManager());
		this.olinemenPhysicsHandler[i] = new PhysicsHandler(this.olineman[i]);
		this.olineman[i].registerUpdateHandler(this.olinemenPhysicsHandler[i]);
		scene.attachChild(this.olineman[i]);
		//team1.addPlayer(this.olineman[i], (Entity) this.olinemenPhysicsHandler[i].getEntity(), currentX, posY);
	}

	private void makeWideReceivers(Scene scene, float posX, float posY, int i, int spaceBetween) {
		float currentX = posX + (spaceBetween * i);
		this.wideReceivers[i] = new Sprite(currentX, posY, this.wrsTextureRegion[i], this.getVertexBufferObjectManager());
		this.wrsPhysicsHandler[i] = new PhysicsHandler(this.wideReceivers[i]);
		this.wideReceivers[i].registerUpdateHandler(this.wrsPhysicsHandler[i]);
		scene.attachChild(this.wideReceivers[i]);
		//team1.addPlayer(this.wideReceivers[i], (Entity) this.wrsPhysicsHandler[i].getEntity(), currentX, posY);
	}

	private void makeHighlighter(Scene scene, float posX, float posY) {
		this.highlighter = new Sprite(posX, posY, this.highlighterTextureRegion, this.getVertexBufferObjectManager());
		this.highlighterPhysicsHandler = new PhysicsHandler(this.highlighter);
		this.highlighter.registerUpdateHandler(this.highlighterPhysicsHandler);
		this.highlighterEntity = (Entity) this.highlighterPhysicsHandler.getEntity();
		scene.attachChild(this.highlighter);
	}

	private void makeFdline(Scene scene, float posX, float posY) {
		this.fdline = new Sprite(posX, posY, this.fdlineTextureRegion, this.getVertexBufferObjectManager());
		this.fdlinePhysicsHandler = new PhysicsHandler(this.fdline);
		this.fdline.registerUpdateHandler(this.fdlinePhysicsHandler);
		scene.attachChild(this.fdline);
	}

	private void loadQB(String imageResource) {
		this.qbTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.qbTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.qbTextureAtlas, this, imageResource, 0, 0);
		this.qbTextureAtlas.load();
	}

	private void loadWR(String imageResource) {
		this.wrTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.wrTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.wrTextureAtlas, this, imageResource, 0, 0);
		this.wrTextureAtlas.load();
	}

	private void loadLinebackers(String imageResource, int i) {
		this.playersTextureAtlas[i] = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.playersTextureRegion[i] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.playersTextureAtlas[i], this, imageResource, 0, 0);
		this.playersTextureAtlas[i].load();
	}

	private void loadWideReceivers(String imageResource, int i) {
		this.wrsTextureAtlas[i] = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.wrsTextureRegion[i] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.wrsTextureAtlas[i], this, imageResource, 0, 0);
		this.wrsTextureAtlas[i].load();
	}

	private void loadBall() {
		this.ballTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), BALL_WIDTH, BALL_HEIGHT, TextureOptions.BILINEAR);
		this.ballTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.ballTextureAtlas, this, "ball2.png", 0, 0);
		this.ballTextureAtlas.load();
	}

	private void loadFirstdownLine() {
		this.fdlineTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 480, 5, TextureOptions.BILINEAR);
		this.fdlineTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.fdlineTextureAtlas, this, "firstline.png", 0, 0);
		this.fdlineTextureAtlas.load();
	}

	private void loadHighlighter() {
		this.highlighterTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 53, 53, TextureOptions.BILINEAR);
		this.highlighterTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.highlighterTextureAtlas, this, "blueRing.png", 0, 0);
		this.highlighterTextureAtlas.load();
	}

	private void loadBackground() {
		try {
			ITexture backgroundTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
				public InputStream open() throws IOException {
					return getAssets().open("gfx/background2.png");
				}
			});
			backgroundTexture.load();
			this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture);
		} catch (IOException e) {
			Debug.e(e);
		}
	}

	public EngineOptions onCreateEngineOptions() {
		// this.camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
		// MAX_VELOCITY_X, MAX_VELOCITY_Y, 5);
		this.camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	public void myLog(String myStr) {
		Log.v("QB 2013", myStr);
	}

}
