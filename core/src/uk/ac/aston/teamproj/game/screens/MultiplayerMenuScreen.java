package uk.ac.aston.teamproj.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.net.MPClient;
import uk.ac.aston.teamproj.game.scenes.SoundManager;

/**
 * @author Suleman
 * @since 5.1.1
 * @date 14/12/2020
 */

public class MultiplayerMenuScreen implements Screen {

	private MainGame game;
	private Viewport viewport;
	private Stage stage;
	private TextureAtlas buttonsAtlas; //the sprite-sheet containing all buttons
	private Skin skin; //skin for buttons
	private ImageButton[] buttons;

	public MultiplayerMenuScreen(MainGame game) {
		this.game = game;
		viewport = new FitViewport(MainGame.V_WIDTH/6, MainGame.V_HEIGHT/6, new OrthographicCamera());
		stage = new Stage(viewport, ((MainGame) game).batch);

		buttonsAtlas = new TextureAtlas("buttons/new_buttons.pack");
		skin = new Skin(buttonsAtlas);
		buttons = new ImageButton[3];

		initializeButtons();		
		populateTable();
	}

	private void initializeButtons() {
		ImageButtonStyle style;

		// Create Button
		style = new ImageButtonStyle();
		style.up = skin.getDrawable("create_inactive");  //set default image
		style.over = skin.getDrawable("create_active");  //set image for mouse over

		ImageButton createBtn = new ImageButton(style);
		createBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	//Sets to playScreen

            	 //plays button pop sound

            	Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
                SoundManager.playSound(sound);
            	System.out.println("Create");
            	MultiplayerMenuScreen.this.dispose();
            	game.setScreen(new CreateScreen(game));
            	return true;
            }
		});

		// Create Button
		style = new ImageButtonStyle();
		style.up = skin.getDrawable("join_inactive");  //set default image
		style.over = skin.getDrawable("join_active");  //set image for mouse over

		ImageButton joinBtn = new ImageButton(style);
		joinBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	//Sets to playScreen

            	 //plays button pop sound

            	Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
            	SoundManager.playSound(sound);
            	System.out.println("Join");
            	MultiplayerMenuScreen.this.dispose();
            	game.setScreen(new JoinScreen(game));
            	return true;
            }
		});


		//Go Back Button
		style = new ImageButtonStyle();
		style.up = skin.getDrawable("back_inactive");  //set default image
		style.over = skin.getDrawable("back_active");  //set image for mouse over

		ImageButton backBtn = new ImageButton(style);
		backBtn.addListener(new InputListener() {
	            @Override
	            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	            	Sound sound = Gdx.audio.newSound(Gdx.files.internal("pop.mp3"));
	            	SoundManager.playSound(sound);
	            	System.out.println("Back");
	            	MultiplayerMenuScreen.this.dispose();
	            	game.setScreen(new MainMenuScreen(game));
	            	return true;
	            }
		});

		buttons[0] = createBtn;
		buttons[1] = joinBtn;
		buttons[2] = backBtn;
	}

	private void populateTable() {
		Table table = new Table();
		table.top();
		table.setFillParent(true);

		//draw the background
		Texture background = new Texture("buttons/main_menu_bg.jpg");
		table.background(new TextureRegionDrawable(new TextureRegion(background)));



		//add contents to table
		table.row();


		//table.add(singleBtn).height(17.5f).width(100).pad(4).padLeft(200).padTop(50);
		//draw all buttons
		ImageButton singleBtn = buttons[0];
		table.add(singleBtn).height(22f).width(120).pad(4).padLeft(200).padTop(60);
		table.row();
		for (int i = 1; i < buttons.length; i++) {
			ImageButton button = buttons[i];
			table.add(button).height(22f).width(120).pad(4).padLeft(200);
			table.row();
		}

		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
    public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,  0,  0 , 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.draw();
		stage.act(delta);
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
		buttonsAtlas.dispose();
		skin.dispose();
		stage.dispose();
	}
}
