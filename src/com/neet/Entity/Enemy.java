package com.neet.Entity;

import java.awt.Color;
import java.awt.Graphics2D;

import com.neet.Audio.JukeBox;
import com.neet.TileMap.TileMap;

public class Enemy extends MapObject {
	
	protected int health;
	protected int maxHealth;
	protected boolean dead;
	protected int damage;
	protected boolean remove;
	protected int middleHealth;
	
	protected boolean flinching;
	protected long flinchCount;
	
	public Enemy(TileMap tm) {
		super(tm);
		remove = false;
		middleHealth = maxHealth - health;
	}
	
	public boolean isDead() { return dead; }
	public boolean shouldRemove() { return remove; }
	
	public int getDamage() { return damage; }
	
	public void hit(int damage) {
		if(dead || flinching) return;
		JukeBox.play("enemyhit");
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) dead = true;
		if(dead) remove = true;
		flinching = true;
		flinchCount = 0;
	}
	
	public void update() {}
	
	public void draw(Graphics2D g){
		super.draw(g);
		
	}
	
}














