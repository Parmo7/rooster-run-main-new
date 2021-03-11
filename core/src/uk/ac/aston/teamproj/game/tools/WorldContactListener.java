package uk.ac.aston.teamproj.game.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import uk.ac.aston.teamproj.game.MainGame;
import uk.ac.aston.teamproj.game.net.MPServer;
import uk.ac.aston.teamproj.game.screens.PlayScreen;
import uk.ac.aston.teamproj.game.sprites.Bomb;
import uk.ac.aston.teamproj.game.sprites.Brick;
import uk.ac.aston.teamproj.game.sprites.Coin;
import uk.ac.aston.teamproj.game.sprites.EndPlane;
import uk.ac.aston.teamproj.game.sprites.InteractiveTileObject;
import uk.ac.aston.teamproj.game.sprites.Lightning;
import uk.ac.aston.teamproj.game.sprites.Mud;
import uk.ac.aston.teamproj.game.sprites.Rooster;


public class WorldContactListener implements ContactListener {

	private PlayScreen playScreen;
	private Rooster player;
	
	public WorldContactListener(PlayScreen playScreen, Rooster player) {
		this.playScreen = playScreen;
		this.player = player;
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
		if (PlayScreen.clientID == player.getID()) {
			
			// check if rooster is colliding with the ground
			if (fixA.getUserData() == "legs" || fixB.getUserData() == "legs") {
				Fixture beak = (fixA.getUserData() == "legs")? fixA : fixB;
				Fixture object = (beak == fixA)? fixB : fixA; //the other object
				
				//check if other object is an interactive one
				if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
					playScreen.resetJumpCount1();
				}
			}
			
			switch (cDef) {
				case (MainGame.ROOSTER_BIT | MainGame.BOMB_BIT):
					PlayScreen.player.bombHit();
				
					Fixture bombFixture = (fixA.getFilterData().categoryBits == MainGame.BOMB_BIT) ? fixA : fixB;
					Bomb bomb = ((Bomb) bombFixture.getUserData());
					
					bomb.onHit();
					playScreen.makeBombExplode(bomb);
					playScreen.updateLives();
					break;
	
				case (MainGame.ROOSTER_BIT | MainGame.BRICK_BIT):
					MPServer.impulse = MPServer.DEFAULT_SPEED;
	
					Fixture brickFixture = (fixA.getFilterData().categoryBits == MainGame.BRICK_BIT) ? fixA : fixB;
					((Brick) brickFixture.getUserData()).onHit();
					break;
	
				case (MainGame.ROOSTER_BIT | MainGame.COIN_BIT):
					
					playScreen.updateCoins();
	
					Fixture coinFixture = (fixA.getFilterData().categoryBits == MainGame.COIN_BIT) ? fixA : fixB;
					((Coin) coinFixture.getUserData()).onHit();
					
					break;
	
				case (MainGame.ROOSTER_BIT | MainGame.LIGHTNING_BIT):
					MPServer.impulse = MPServer.FAST_SPEED;
				
					Fixture lightningFixture = (fixA.getFilterData().categoryBits == MainGame.LIGHTNING_BIT) ? fixA : fixB;
					((Lightning) lightningFixture.getUserData()).onHit();
					break;
					
				case (MainGame.ROOSTER_BIT | MainGame.PLANE_BIT):
					PlayScreen.player.onFinish();
					
					Fixture PlaneFixture = (fixA.getFilterData().categoryBits == MainGame.PLANE_BIT)? fixA : fixB;
					((EndPlane) PlaneFixture.getUserData()).onHit();
					break;
				
				case (MainGame.ROOSTER_BIT | MainGame.MUD_BIT):
					MPServer.impulse = MPServer.SLOW_SPEED;
	
					Fixture mudFixture = (fixA.getFilterData().categoryBits == MainGame.MUD_BIT) ? fixA : fixB;
					((Mud) mudFixture.getUserData()).onHit();
					break;
	
				default:
					break;
			}
			
		}
		
		if (PlayScreen.clientID == player.getID()) {
			
			// check if rooster is colliding with the ground
			if (fixA.getUserData() == "legs" || fixB.getUserData() == "legs") {
				Fixture beak = (fixA.getUserData() == "legs")? fixA : fixB;
				Fixture object = (beak == fixA)? fixB : fixA; //the other object
				
				//check if other object is an interactive one
				if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
					playScreen.resetJumpCount2();
				}
			}
			
			switch (cDef) {
				case (MainGame.ROOSTER_BIT2 | MainGame.BOMB_BIT):
					PlayScreen.player2.bombHit();
					Fixture bombFixture = (fixA.getFilterData().categoryBits == MainGame.BOMB_BIT) ? fixA : fixB;
					Bomb bomb = ((Bomb) bombFixture.getUserData());
					bomb.onHit();
					playScreen.makeBombExplode(bomb);
					//playScreen.updateLivesP2();
					break;
	
				case (MainGame.ROOSTER_BIT2 | MainGame.BRICK_BIT):
					Fixture brickFixture = (fixA.getFilterData().categoryBits == MainGame.BRICK_BIT) ? fixA : fixB;
					((Brick) brickFixture.getUserData()).onHit();
					
					MPServer.impulse2 = MPServer.DEFAULT_SPEED;
					
					break;
	
				case (MainGame.ROOSTER_BIT2 | MainGame.COIN_BIT):
					Fixture coinFixture = (fixA.getFilterData().categoryBits == MainGame.COIN_BIT) ? fixA : fixB;
					((Coin) coinFixture.getUserData()).onHit();
					
					//playScreen.updateCoinsP2();
					
					break;
	
				case (MainGame.ROOSTER_BIT2 | MainGame.LIGHTNING_BIT):
					MPServer.impulse2 = MPServer.FAST_SPEED;
				
					Fixture lightningFixture = (fixA.getFilterData().categoryBits == MainGame.LIGHTNING_BIT) ? fixA : fixB;
					((Lightning) lightningFixture.getUserData()).onHit();
					break;
					
				case (MainGame.ROOSTER_BIT2 | MainGame.PLANE_BIT):
					Fixture PlaneFixture = (fixA.getFilterData().categoryBits == MainGame.PLANE_BIT)? fixA : fixB;
					((EndPlane) PlaneFixture.getUserData()).onHit();
					
					PlayScreen.player2.onFinish();
					
					break;
	
				case (MainGame.ROOSTER_BIT2 | MainGame.MUD_BIT):
					MPServer.impulse2 = MPServer.SLOW_SPEED;
	
					Fixture mudFixture = (fixA.getFilterData().categoryBits == MainGame.MUD_BIT) ? fixA : fixB;
					((Mud) mudFixture.getUserData()).onHit();
					break;
	
				default:
					break;
			}
		}
	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
