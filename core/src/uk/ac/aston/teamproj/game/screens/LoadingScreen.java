package uk.ac.aston.teamproj.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import uk.ac.aston.teamproj.game.MainGame;

public class LoadingScreen implements Screen {
	
    private MainGame mGame;
    private Batch batch;
    private BitmapFont bf_loadProgress;
    private long startTime;
    private ShapeRenderer mShapeRenderer;

    private int clientID;
    private String mapPath;
    private Viewport viewport;
    private Texture background;
    
    public LoadingScreen(Game game, int clientID, String mapPath) {
        mGame = (MainGame) game;
        batch = mGame.batch;
        bf_loadProgress = new BitmapFont();
        mShapeRenderer = new ShapeRenderer();
        
        this.clientID = clientID;
        this.mapPath = mapPath;
        
        viewport = new FitViewport(MainGame.V_WIDTH/6, MainGame.V_HEIGHT/6, new OrthographicCamera());
        background = new Texture("buttons/multiplayer_menu_bg.jpg");   
        
        startTime = System.currentTimeMillis();
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(background, 0 , 0, MainGame.V_WIDTH/6, MainGame.V_HEIGHT/6);
        batch.end();
                
        float elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0f;
        float progress = elapsedTime * 10 * 100;
        // Width of progress bar on screen relevant to Screen width
        float progressBarWidth = (MainGame.V_WIDTH/6 / 100) * progress;

        batch.setProjectionMatrix(batch.getProjectionMatrix());
        batch.begin();
        bf_loadProgress.draw(batch, "Loading " + Math.round(progress) + "%", 10, 40);
        batch.end();
        
        mShapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        mShapeRenderer.begin(ShapeType.Filled);
        mShapeRenderer.setColor(Color.valueOf("#e0be24"));
        mShapeRenderer.rect(0, 10, progressBarWidth , 10);
        mShapeRenderer.end();
        if (progress >= 100)
            moveToPlayScreen();
    }

    /**
     * Move to play screen after progress reaches 100%
     */
    private void moveToPlayScreen() {
        mGame.setScreen(new PlayScreen(mGame, clientID, mapPath));
        dispose();
    }

    @Override
    public void resize(int width, int height) {
		viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub    	
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        bf_loadProgress.dispose();
        mShapeRenderer.dispose();
    }
}