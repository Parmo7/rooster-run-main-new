package uk.ac.aston.teamproj.game.screens;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.net.MPClient;
import uk.ac.aston.teamproj.game.net.MPServer;
import uk.ac.aston.teamproj.game.net.packet.Movement;
import uk.ac.aston.teamproj.game.scenes.Hud;
import uk.ac.aston.teamproj.game.scenes.Hud2;
import uk.ac.aston.teamproj.game.scenes.PlayerProgressBar;
import uk.ac.aston.teamproj.game.sprites.Bomb;
import uk.ac.aston.teamproj.game.sprites.Rooster;
import uk.ac.aston.teamproj.game.tools.B2WorldCreator;
import uk.ac.aston.teamproj.game.tools.WorldContactListener;

public class PlayScreen implements Screen {

	private static final int SCORE_LOC = 400 * 6; // increment score every 400 units
	private static final String DEFAULT_MAP_PATH = "map_beginner_fix";

	private MainGame game;
	private TextureAtlas atlas; // sprite sheet that wraps all images
	Texture texture;

	// Aspect ratio
	private OrthographicCamera gamecam;
	private Viewport gamePort;
	private Hud hud;
	private Hud2 hud2;

	// Tiled map variables
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;

	// Box2d variables
	private World world;
	private Box2DDebugRenderer b2dr;

	// Sprites
	public static Rooster player;
	public static Rooster player2;

	// counts the number of consecutive jumps for each rooster
	private static final int MAX_JUMPS = 2;
	private int jumpCount1 = 0;
	private int jumpCount2 = 0;

	// multiplayer
	public static int clientID;
	private HashMap<Bomb, Float> toExplode = new HashMap<>();
	
	//Stats
	
	public static int coins;
	
	private final PlayerProgressBar progressBar;
	
	public PlayScreen(MainGame game, int clientID, String mapPath) {
		this.game = game;
		PlayScreen.clientID = clientID;
		this.atlas = new TextureAtlas("new_sprite_sheet/new_chicken.pack");

		// Create a cam to follow chicken in the game world
		gamecam = new OrthographicCamera();

		// Create a FitViewport to maintain virtual aspect ratio despite screen size
		gamePort = new FitViewport(MainGame.V_WIDTH / MainGame.PPM, MainGame.V_HEIGHT / MainGame.PPM, gamecam);

		// Create our game HUD for scores /timers/level info/players in the game etc
		hud = new Hud(game.batch);
		hud2 = new Hud2(game.batch);
		progressBar = new PlayerProgressBar(game.batch, 2);

		// Load our map and setup our map renderer
		mapLoader = new TmxMapLoader();
		String correctMapPath = (mapPath != null)? mapPath : DEFAULT_MAP_PATH;
		map = mapLoader.load(correctMapPath + ".tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / MainGame.PPM);

		// Initially set our game cam to be centered correctly at the start of the map
		gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

		// Vector of gravity no grav rn, sleep objects at rest = true - box2d does not
		// calculate physics simulations on objects that are in rest.
		world = new World(new Vector2(0, -10), true);
		b2dr = new Box2DDebugRenderer();
		b2dr.setDrawBodies(false);

		new B2WorldCreator(world, map);

		// Create rooster in the world
		player = new Rooster(world, this, MPServer.playerCount.get(0));
		player2 = new Rooster(world, this, MPServer.playerCount.get(1));

		// make the world react of object collision
		if(clientID == 1) {
			world.setContactListener(new WorldContactListener(this, player));
		} else {
			world.setContactListener(new WorldContactListener(this, player2));
		}
//		Sound sound = Gdx.audio.newSound(Gdx.files.internal("game_soundtrack.mp3"));
//        sound.play(1F);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	public void handleInput(float dt) {
		// If our user is holding down mouse over camera throughout the game world.
		if (clientID == MPServer.playerCount.get(0)) {
			if (player.currentState != Rooster.State.DEAD) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && jumpCount1 < MAX_JUMPS) {

					 //plays button swoosh sound
					Sound sound = Gdx.audio.newSound(Gdx.files.internal("electric-transition-super-quick-www.mp3"));
	                sound.play(1F);


					Movement packet = new Movement();
					packet.clientID = 0;
					packet.direction = 1;
					MPClient.client.sendTCP(packet);

					jumpCount1++;
				}

				if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
					Movement packet = new Movement();
					packet.clientID = 0;
					packet.direction = 2;

					MPClient.client.sendTCP(packet);
				}

				if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
					Movement packet = new Movement();
					packet.clientID = 0;
					packet.direction = 0;

					MPClient.client.sendTCP(packet);
				}
			}
		}

		if (clientID == MPServer.playerCount.get(1)) {
			if (player2.currentState != Rooster.State.DEAD) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && jumpCount2 < MAX_JUMPS) {
					Sound sound = Gdx.audio.newSound(Gdx.files.internal("electric-transition-super-quick-www.mp3"));
	                sound.play(1F);

					Movement packet = new Movement();
					packet.clientID = 1;
					packet.direction = 1;
					MPClient.client.sendTCP(packet);

					jumpCount2++;
				}

				if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
					Movement packet = new Movement();
					packet.clientID = 1;
					packet.direction = 2;
					MPClient.client.sendTCP(packet);
				}

				if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
				{
					Movement packet = new Movement();
					packet.clientID = 1;
					packet.direction = 0;
					MPClient.client.sendTCP(packet);
				}
			}
		}

        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            System.out.println(clientID + " : rooster1 " + player.getPositionX());

            System.out.println(clientID + " : rooster2 " + player2.getPositionX());
        }

	}

	/*
	 * This is where we are going to do all the updating in the game world. First
	 * thing we check if there is any inputs happening
	 */
	public void update(float dt) {
		// Handle user input first
		handleInput(dt);

		world.step(1 / 60f, 6, 2);

		// update player based on delta time
		player.update(dt);
		player2.update(dt);

		// update score based on location
//		if (player.getPositionX() * MainGame.PPM > (hud.getScore() + 1) * SCORE_LOC) {
//			hud.updateScore();
//		}
//		if (player2.getPositionX() * MainGame.PPM > (hud2.getScore() + 1) * SCORE_LOC) {
//			hud2.updateScore();
//		}
		if (player.currentState != Rooster.State.DEAD)
			progressBar.updateProgress(new float[] {player.getPositionX(), player2.getPositionX()});

		// Everytime chicken moves we want to track him with our game cam
		if (clientID == MPServer.playerCount.get(0))
			if (player.currentState != Rooster.State.DEAD) {
				gamecam.position.x = player.getPositionX();
			}

		if (clientID == MPServer.playerCount.get(1))
			if (player2.currentState != Rooster.State.DEAD) {
				gamecam.position.x = player2.getPositionX();
			}

		// Update our gamecam with correct coordinates after changes
		gamecam.update();

		// tell our renderer to draw only what the camera sees in our game world.
		float width = gamecam.viewportWidth * gamecam.zoom;
		float height = gamecam.viewportHeight * gamecam.zoom;

		float w = width * Math.abs(gamecam.up.y) + height * Math.abs(gamecam.up.x);
		float h = height * Math.abs(gamecam.up.y) + width * Math.abs(gamecam.up.x);
		float x = gamecam.position.x - w / 2;
		float y = gamecam.position.y - h / 2;

		renderer.setView(gamecam.combined, x, y, w, h); // Only render what our game can see
//        renderer.setView(gamecam);

			//for (HashMap.Entry<Bomb, Float> entry : toExplode.entrySet()) {
			for (Iterator<HashMap.Entry<Bomb, Float>> iter = toExplode.entrySet().iterator();
					iter.hasNext();) {
				HashMap.Entry<Bomb, Float> entry = iter.next();
				Bomb bomb = entry.getKey();
				@SuppressWarnings("rawtypes")
				Animation a = bomb.getAnimation();
				float time = entry.getValue();

				if (time <= 1f) { // if the animation is still running
					time += dt;
					toExplode.put(bomb, time);
					if (time < 0.9f) {
						TextureRegion region = (TextureRegion) a.getKeyFrame(time);
						bomb.getCell().setTile(new StaticTiledMapTile(region));
					} else
						bomb.getCell().setTile(null); // last frame in animation should be empty

				} else { // else if the animation is finished
					iter.remove();
				}
			}
	}

	public void updateCoins() {
		//hud.updateCoins(10);
		progressBar.updateCoins(1);
	}

	public void updateLives() {
		//hud.updateLives();
		progressBar.updateLives();
	}

	public void updateCoinsP2() {
		hud2.updateCoins(10);
	}

	public void updateLivesP2() {
		hud2.updateLives();
	}

	@Override
	public void render(float delta) {
		// separate our update logic from render
		update(delta);

		// clear the game screen with Black
		Gdx.gl.glClearColor(0, 0, 0, 1); // Colour and alpha
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Actually clears the screen

		// render our game map
		renderer.render();

		// renderer our Box2DDebugLines
		b2dr.render(world, gamecam.combined);

		// render rooster image
		game.batch.setProjectionMatrix(gamecam.combined); // render only what the game camera can see
		game.batch.begin();
		player.draw(game.batch); // draw
		player2.draw(game.batch);
		game.batch.end();

		// Set our batch to now draw what the hud camera sees
//		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
//		hud.stage.draw();

//		game.batch.setProjectionMatrix(hud2.stage.getCamera().combined);
//		hud2.stage.draw();
		
		progressBar.draw();
		
		if (gameOver()) {
			game.setScreen(new GameOverScreen(game));
			dispose();
		} else if (gameFinished()) {
			game.setScreen(new GameFinishedScreen(game));
			dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
		/*
		 * Its important that when we change the size of our screen on the desktop that
		 * the view point gets adjusted to know what the actual screen size is
		 */
		gamePort.update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		map.dispose();
		renderer.dispose();
		world.dispose();
		b2dr.dispose();
		hud.dispose();
		hud2.dispose();
	}

	public TextureAtlas getAtlas() {
		return atlas;
	}

	// TEMP
	private boolean gameOver() {
		if (clientID == MPServer.playerCount.get(0)) {
			coins = progressBar.getCoinsCollected();
			return (player.currentState == Rooster.State.DEAD && player.getStateTimer() > 3);
		}
		if (clientID == MPServer.playerCount.get(1)) {
			coins = progressBar.getCoinsCollected();
			return (player2.currentState == Rooster.State.DEAD && player2.getStateTimer() > 3);
		}
		return false;
	}

	private boolean gameFinished() {

		if (clientID == MPServer.playerCount.get(0)) {
			return (player.currentState == Rooster.State.WON);
		}
		if (clientID == MPServer.playerCount.get(1)) {
			return (player2.currentState == Rooster.State.WON);
		}
		return false;
	}

	public void makeBombExplode(Bomb bomb) {
		float startTime = Gdx.graphics.getDeltaTime();
		toExplode.put(bomb, startTime);
	}

	public void resetJumpCount1() {
		jumpCount1 = 0;
	}

	public void resetJumpCount2() {
		jumpCount2 = 0;
	}

}
