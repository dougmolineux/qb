package com.restlink.qb;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;

public class qbTeam {
	
	private static MainGame main;
	private static int returned;
	private static int playerCount = 0;
	
	static ArrayList<qbPlayer> players = new ArrayList<qbPlayer>();
	
	public qbTeam(MainGame mainGame) {
		main = mainGame;
	}

	public void addPlayer(Sprite qbSprite, Entity qbEntity, float startingX, float startingY, float runPosX, float runPosY, String pos) {
		players.add(new qbPlayer(this, qbSprite, qbEntity, startingX, startingY, runPosX, runPosY, pos));
		playerCount++;
	}

	public void returnToFormation() {
		main.updateCenterText("Returning to Formation");
		for(int i = 0; i < playerCount; i++) {
			players.get(i).returnToFormation();
		}
	}

	public void hike() {
		for(int i = 0; i < playerCount; i++) {
			players.get(i).hike();
		}
	}

	public static int getReturned() {
		return returned;
	}

	public void setReturned(int returned1) {
		returned += returned1;
		main.updateCenterText("Returning to Formation "+returned+" / "+playerCount);
		if(returned == playerCount) {
			main.setHiking(0);
			main.updateCenterText("Ready for Hike");
			returned = 0;
		}
	}

	public void run() {
		main.updateCenterText("Catch!");
		for(int i = 0; i < playerCount; i++) {
			players.get(i).runCatch();
		}
	}

}
