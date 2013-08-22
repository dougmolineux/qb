package com.restlink.qb;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.Sprite;

import android.util.FloatMath;

public class qbPlayer {
	
	private Entity currentEntity;
	private Sprite currentSprite;
	private qbTeam currentTeam;
	private String position;
	
	private float startingX;
	private float startingY;
	private float runPos1x;
	private float runPos1y;
	private float currentPosX;
	

	public qbPlayer(qbTeam qbTeam, Sprite playerSprite, Entity playerEntity, float startingX2, float startingY2, float runPosX, float runPosY, String pos) {
		this.currentSprite = playerSprite;
		this.startingX = startingX2;
		this.startingY = startingY2;
		this.currentEntity = playerEntity;
		this.runPos1x = runPosX;
		this.runPos1y = runPosY;
		this.currentTeam = qbTeam;
		this.position = pos;
	}
	
	public int hike() {
		if((currentSprite.getX() == this.runPos1x) && (currentSprite.getY() == this.runPos1y)) {
			return 0;
		}
		else {
			movePlayer(this.runPos1x, this.runPos1y, currentEntity, currentSprite, 200);
			return 1;
		}
	}

	public int returnToFormation() {
		if((currentSprite.getX() == this.startingX) && (currentSprite.getY() == this.startingY)) {
			currentTeam.setReturned(1);
			return 0;
		}
		else {
			movePlayer(this.startingX, this.startingY, currentEntity, currentSprite, 50);
			return 1;
		}
	}

	private void movePlayer(float toX, float toY, Entity playerEntity,
			final Sprite playerSprite, final float playerSpeed) {

		float from_X = playerSprite.getX();
		float from_Y = playerSprite.getY();
		float dx = toX - from_X;
		float dy = toY - from_Y;
		float speed = playerSpeed;
		float distToTouch = (float) FloatMath.sqrt((dx) * (dx) + (dy) * (dy));
		float time = distToTouch / speed;

		// make move modifier
		MoveModifier myMovemod = new MoveModifier(time, from_X, toX, from_Y, toY);

		// make sequence mod with move modifier
		SequenceEntityModifier modifier = new SequenceEntityModifier(myMovemod) {

			@Override
			protected void onModifierFinished(IEntity pItem) {
				if(playerSpeed == 50)
					currentTeam.setReturned(1);
				super.onModifierFinished(pItem);
			}

			@Override
			public float onUpdate(float pSecondsElapsed, IEntity pItem) {
				return super.onUpdate(pSecondsElapsed, pItem);
			}
		};

		// apply modifier
		playerEntity.registerEntityModifier(modifier);
	}

	public void runCatch() {
		movePlayer(this.runPos1y, 0, currentEntity, currentSprite, 200);
	}

}
