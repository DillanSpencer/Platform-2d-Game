package com.neet.Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.neet.Audio.JukeBox;
import com.neet.Main.GamePanel;
import com.neet.TileMap.TileMap;

public class Player extends MapObject{
	
	//enemy
	private ArrayList<Enemy> enemies;
	
	//player
	private int lives;
	private int health;
	private int maxHealth;
	//private int damage;
	//private int chargeDamage;
	private boolean knockback;
	private boolean flinching;
	private long flinchCount;
	//private int score;
	private boolean doubleJump;
	private boolean alreadyDoubleJump;
	private double doubleJumpStart;
	private ArrayList<EnergyParticle> energyParticles;
	private long time;
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] NUMFRAMES = {
		1,5,3,4
	};
	private final int[] FRAMEWIDTHS = {
		32,32,32,32
	};
	private final int[] FRAMEHEIGHTS = {
		32,32,32,32
	};
	private final int[] SPRITEDELAYS = {
		-1, 9, 20, 18
	};
	
	private Rectangle ar;
	private Rectangle aur;
	private Rectangle cr;
	
	//animations
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	
	// emotes
	private BufferedImage confused;
	private BufferedImage surprised;
	public static final int NONE = 0;
	public static final int CONFUSED = 1;
	public static final int SURPRISED = 2;
	private int emote = NONE;
	
	public Player(TileMap tm){
		super(tm);
		
		ar = new Rectangle(0, 0, 0, 0);
		ar.width = 30;
		ar.height = 20;
		aur = new Rectangle((int)x - 15, (int)y - 45, 30, 30);
		cr = new Rectangle(0, 0, 0, 0);
		cr.width = 32;
		cr.height = 32;
		
		width = 30;
		height = 30;
		cwidth = 16;
		cheight = 26;
		
		moveSpeed = 1.6;
		maxSpeed = 1.6;
		stopSpeed = 1.6;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		doubleJumpStart = -3;
		
		facingRight = true;
		
		lives = 3;
		health = maxHealth = 5;
		
		//load the sprites
		try {
			
			BufferedImage spritesheet = ImageIO.read(
				getClass().getResourceAsStream(
					"/Sprites/Player/KingSprites.gif"
				)
			);
			
			int count = 0;
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < NUMFRAMES.length; i++) {
				BufferedImage[] bi = new BufferedImage[NUMFRAMES[i]];
				for(int j = 0; j < NUMFRAMES[i]; j++) {
					bi[j] = spritesheet.getSubimage(
						j * FRAMEWIDTHS[i],
						count,
						FRAMEWIDTHS[i],
						FRAMEHEIGHTS[i]
					);
				}
				sprites.add(bi);
				count += FRAMEHEIGHTS[i];
			}
			
			// emotes
			spritesheet = ImageIO.read(getClass().getResourceAsStream(
				"/HUD/Emotes.gif"
			));
			confused = spritesheet.getSubimage(
				0, 0, 14, 17
			);
			surprised = spritesheet.getSubimage(
				14, 0, 14, 17
			);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		energyParticles = new ArrayList<EnergyParticle>();
		
		setAnimation(IDLE);
	}
	
	public void init(
			ArrayList<Enemy> enemies,
			ArrayList<EnergyParticle> energyParticles) {
			this.enemies = enemies;
			this.energyParticles = energyParticles;
		}
	
	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }
	
	public void setEmote(int i) {
		emote = i;
	}
	
	//public void setTeleporting(boolean b) { teleporting = b; }
	
	public void setJumping(boolean b) {
		if(knockback) return;
		if(b && !jumping && falling && !alreadyDoubleJump) {
			doubleJump = true;
		}
		jumping = b;
	}
	
	public String getTimeToString() {
		int minutes = (int) (time / 3600);
		int seconds = (int) ((time % 3600) / 60);
		return seconds < 10 ? minutes + ":0" + seconds : minutes + ":" + seconds;
	}
	public long getTime() { return time; }
	public void setTime(long t) { time = t; }
	
	public void hit(int damage) {
		if(flinching) return;
		JukeBox.play("playerhit");
		stop();
		health -= damage;
		if(health < 0) health = 0;
		flinching = true;
		flinchCount = 0;
		if(facingRight) dx = -1;
		else dx = 1;
		dy = -3;
		knockback = true;
		falling = true;
		jumping = false;
	}
	
	public void stop() {
		left = right = up = down = flinching = 
			 jumping = false;
	}
	
private void getNextPosition() {
		
		if(knockback) {
			dy += fallSpeed * 2;
			if(!falling) knockback = false;
			return;
		}
		
		double maxSpeed = this.maxSpeed;
		
		
		// movement
		if(left) {
			dx -= moveSpeed;
			if(dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}
		else if(right) {
			dx += moveSpeed;
			if(dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		else {
			if(dx > 0) {
				dx -= stopSpeed;
				if(dx < 0) {
					dx = 0;
				}
			}
			else if(dx < 0) {
				dx += stopSpeed;
				if(dx > 0) {
					dx = 0;
				}
			}
		}
		
		
		// jumping
		if(jumping && !falling) {
			//sfx.get("jump").play();
			dy = jumpStart;
			falling = true;
			JukeBox.play("playerjump");
		}
		
		if(doubleJump) {
			dy = doubleJumpStart;
			alreadyDoubleJump = true;
			doubleJump = false;
			JukeBox.play("playerjump");
			for(int i = 0; i < 6; i++) {
				energyParticles.add(
					new EnergyParticle(
						tileMap,
						x,
						y + cheight / 4,
						EnergyParticle.DOWN));
			}
		}
		
		if(!falling) alreadyDoubleJump = false;
		
		// falling
		if(falling) {
			dy += fallSpeed;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			if(dy > maxFallSpeed) dy = maxFallSpeed;
		}
		
	}

	private void setAnimation(int i) {
		currentAction = i;
		animation.setFrames(sprites.get(currentAction));
		animation.setDelay(SPRITEDELAYS[currentAction]);
		width = FRAMEWIDTHS[currentAction];
		height = FRAMEHEIGHTS[currentAction];
	}
	
	public void update(){
		time++;
		
		
		// update position
		boolean isFalling = falling;
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		if(isFalling && !falling) {
			JukeBox.play("playerlands");
		}
		if(dx == 0) x = (int)x;
		
		// check done flinching
		if(flinching) {
			flinchCount++;
			if(flinchCount > 120) {
				flinching = false;
			}
		}
		
		// energy particles
		for(int i = 0; i < energyParticles.size(); i++) {
			energyParticles.get(i).update();
			if(energyParticles.get(i).shouldRemove()) {
				energyParticles.remove(i);
				i--;
			}
		}
		
		//eneimes
		for(int i = 0; i < enemies.size(); i++) {
			
			Enemy e = enemies.get(i);
			if(!e.isDead() && intersects(e)) {
				hit(e.getDamage());
			}
			
			if(e.isDead()) {
				JukeBox.play("explode", 2000);
			}
		}
		
		 if(dy < 0) {
			if(currentAction != JUMPING) {
				setAnimation(JUMPING);
			}
		}
		else if(dy > 0) {
			if(currentAction != FALLING) {
				setAnimation(FALLING);
			}
		}
		 
		else if(left || right) {
			if(currentAction != WALKING) {
				setAnimation(WALKING);
			}
		}
		else if(currentAction != IDLE) {
			setAnimation(IDLE);
		}
		
		animation.update();
		
		if(right) facingRight = true;
		if(left) facingRight = false;
	}
	
	public void draw(Graphics2D g){
		// draw emote
				if(emote == CONFUSED) {
					g.drawImage(confused, (int)(x + xmap - cwidth / 2), (int)(y + ymap - 30), null);
				}
				else if(emote == SURPRISED) {
					g.drawImage(surprised, (int)(x + xmap - cwidth / 2), (int)(y + ymap - 30), null);
				}
				
				// draw energy particles
				for(int i = 0; i < energyParticles.size(); i++) {
					energyParticles.get(i).draw(g);
				}
				
				// flinch
				if(flinching && !knockback) {
					if(flinchCount % 10 < 5) return;
				}
				
				
				super.draw(g);
	}

	public int getLives() {
		return lives;
	}

}
