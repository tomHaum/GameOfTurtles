package gameOfTurtles;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;

public class GameOfTurtles{
	static List<Turtle> enemy = new ArrayList<Turtle>(); // List of enemies
	static List<Turtle> deadEnemy = new ArrayList<Turtle>(); // List of dead enemies
																
	static List<Projectile> bulletLive = new ArrayList<Projectile>();
	static List<Projectile> bulletIdle = new ArrayList<Projectile>();

	static List<SpreadShot> shotGunShot = new ArrayList<SpreadShot>();
	static List<SpreadShot> shotGunShotIdle = new ArrayList<SpreadShot>();
	
	static GameCanvas canvas; // imported canvas for the player
	static double xPos, yPos; // position of the player
	static double hasteMultiplier; //how much faster things go while under haste
	static int ticks, numberOfEnemies, hp, energy, enemyTravelDistance, maxBullets,
	scatterTime, energyRegenTime, maxEnergy, shootCooldown, currentShotDelay,
	shotGunCooldown, currentShotGunDelay, points,
	pointsPerKill, playerMoveDistance, hasteDuration, remainingHasteDuration;
	
	static boolean godMode = false;
	static boolean hasteActive, haste, paused;
	static File gunShot, teleSound, endScream, shotGunSound, splatSound;
	// number of iterations of the while loop,

	// total number of enemies spawned
	// hp: Hitpoints
	//energy: Player resource to use special abilities
	// enemyTravelDistance: How far each enemy travels each tick 
	//scatterTime: Number of while loop iterations until next scatter
	//energyRegenTime: While Loop Iterations it takes to regen energy
	//maxEnergy: Maximum energy you can have
	//shootDelay: Number of iterations before you can take another shot;
	//a cooldown for shooting.
	//currentShotDelay: Loop iterations until you can make the next shot.
	
	//hasteActive: The haste ability has been activated
	//haste: Haste is currently in effect on the player

	public static void main(String[] args) {
		initializeFields();
		initializePlayer(canvas.player);
		initializeHitPointsCounter(canvas.hitPointsTurtle);
		initializeEnergy(canvas.energyTurtle);
		
		while (hp > 0) {
			//main loop for game; each iteration is one 'tick'
			if(!paused){
				ticks += 1; // adds one tick for each iteration
				gainPoints(1);
				if(haste){
					canvas.player.forward(playerMoveDistance*hasteMultiplier);
				}
				else{
					canvas.player.forward(playerMoveDistance);
				}
				xPos = canvas.player.getX();
				yPos = canvas.player.getY();
	
				tick(5, enemyTravelDistance);
				
				if (ticks % scatterTime == 0) {
					scatter();
					enemyTravelDistance += 1;
				}
				
				if (ticks % energyRegenTime == 0){
					gainEnergy(1);
				}
				checkBoundryConditions();
			}
			// tick(5,Math.ceil((double) ticks / 100)); //second parameter:
			// Speeds up as time goes on
			// canvas.player.zoom(-500, -500, 500, 500);
			// System.out.println(enemy.size());
			//System.out.println(currentShotDelay);
		}
		playSound(endScream);
		System.out.println("Thanks for playing!");
		System.out.println("Final Score: " + ticks);
		
		
		
	}
	public static void initializeFields(){
		gunShot = getFile("/resources/gun.wav");
		teleSound = getFile("/resources/tele.wav");
		endScream = getFile("/resources/zilla4.wav");
		shotGunSound = getFile("/resources/shotGun.wav");
		splatSound = getFile("/resources/splat.wav");
		canvas = new GameCanvas();
		
		xPos = 0;
		yPos = 0;
		ticks = 0;
		
		
		scatterTime = 150;
		pointsPerKill = 15;
		
		numberOfEnemies = 0;
		enemyTravelDistance = 1;
		playerMoveDistance = 10;		
		
		energyRegenTime = 25;
		maxEnergy = 10;
		
		maxBullets = 5;
		
		shootCooldown = 10;
		shotGunCooldown = 100;
		currentShotDelay = 0;
		currentShotGunDelay = 0;
		
		hasteActive = true;
		hasteMultiplier = 3.0;
		remainingHasteDuration = 0;
		
		paused = true;
		
		//for haste ability

		SpreadShot p1 = new SpreadShot(0, 0, 0, 10, 45, 1, 20);
		SpreadShot p2 = new SpreadShot(0, 0, 0, 10, 45, 1, 20);
		p1.kill();
		p2.kill();
		shotGunShot.add(p1);
		shotGunShot.add(p2);
	}
	@SuppressWarnings("static-access")
	public static void initializePlayer(Turtle t) {
		t.up();
		t.hide();
		t.speed(.0001);

		// draws boundaries
		t.setPosition(-500, -500);
		t.down();
		t.setPosition(-500, 450);
		t.setPosition(500, 450);
		t.setPosition(500, -500);
		t.setPosition(-500, -500);
		t.up();
		t.home();
		// finished boundaries drawn

		t.show();
		t.speed(50);
		t.shape("circle");
		t.bgcolor("yellow");
		t.fillColor("pink");
		//t.zoom(-500, -500, 500, 500); //Before energy bar zoom
		t.zoom(-500, -550, 500, 500); //Post energy bar zoom
		
		t.onKey("moveRight", "d");
		t.onKey("moveLeft", "a");
		t.onKey("moveUp", "w");
		t.onKey("moveDown", "s");
		t.onKey("teleport", "q");
		t.onKey("shoot", "e");
		t.onKey("shotGun","r");
		t.onKey("pause", "p");
		t.onKey("pause", "space");
      //t.onKey("restart","r");
		
		t.onKey("hasteMax","h");
		
		
	}

	public static void initializeHitPointsCounter(Turtle t) {
		t.speed(.0001);
		t.up();
		t.shape("circle");
		t.fillColor("red");
		t.outlineColor("pink");
		for (int i = 0; i < 10; i++) {
			t.setPosition((100 * i) - 450, 475);
			t.dot("red");
		}
		t.setDirection(180);
		hp = 10;
	}
	
	public static void initializeEnergy(Turtle t) {
		t.speed(.0001);
		t.up();
		t.shape("circle");
		t.fillColor("purple");
		t.outlineColor("pink");
		for (int i = 0; i < 11; i++) {
			t.setPosition((100 * i) - 550, -525);
			t.dot("purple");
		}
		t.setPosition(-550, -525);
		t.setDirection(0);
		energy = 0;
	}

	public void moveRight() {
		canvas.player.setDirection(0);
		// canvas.player.forward(10);
		// positionReport();
	}

	public void moveLeft() {
		canvas.player.setDirection(180);
		// canvas.player.forward(10);
		// positionReport();
	}

	public void moveUp() {
		canvas.player.setDirection(90);
		// canvas.player.forward(10);
		// positionReport();
	}

	public void moveDown() {
		canvas.player.setDirection(270);
		// canvas.player.forward(10);
		// positionReport();
	}

	//methods for teleport that have different strengths
	@SuppressWarnings("static-access")
	public static void teleport() {
		//original teleport method
		if(energy >= 3){
			double canvasX = Turtle.canvasX(canvas.player.mouseX());
			double canvasY = Turtle.canvasY(canvas.player.mouseY());
			//System.out.println("mouseX: " + canvasX);
			//System.out.println("mouseY: " + canvasY);
			if (-500 < canvasX && canvasX < 500 && -500 < canvasY && canvasY < 450) {
				canvas.player.setPosition(canvasX, canvasY);
				System.out.println("Wooosh");
				playSound(teleSound);
				loseEnergy(3);
			}
		}
	}
	
	public static void hasteMin(){
		metaHaste(3, 25);
	}
	
	public static void hasteMax(){
		metaHaste(1, 50);
	}

	public static void shoot() {
		//System.out.println("currentShotDelay: " + currentShotDelay);
		if(currentShotDelay == 0 && !paused){
			double canvasX = Turtle.canvasX(canvas.player.mouseX());
			double canvasY = Turtle.canvasY(canvas.player.mouseY());
			double playerX = canvas.player.getX();
			double playerY = canvas.player.getY();
			double deltaX = canvasX - playerX;
			double deltaY = canvasY - playerY;
			double direction = Math.atan2(deltaY, deltaX);
			direction *= 57.2957795;
			
			if (!bulletIdle.isEmpty()) {
				Projectile t = bulletIdle.get(0);
				
				bulletIdle.remove(0);
				t.set(playerX, playerY,direction, 10, 20);
				bulletLive.add(t);
				if(haste){
					currentShotDelay = (int) (shootCooldown/(hasteMultiplier*hasteMultiplier));
				}
				else{
					currentShotDelay = shootCooldown;
				}
				playSound(gunShot);
			} else if (bulletLive.size() < maxBullets) {
				Projectile t = new Projectile(playerX, playerY,direction, 10 , 5);
				bulletLive.add(t);
				if(haste){
					currentShotDelay = (int) (shootCooldown/hasteMultiplier);
				}
				else{
					currentShotDelay = shootCooldown;
				}
				playSound(gunShot);
			}
		}
	}

	public static void shotGun() throws IOException{
		ProcessBuilder q = new ProcessBuilder("help");
		q.start();
		if(currentShotGunDelay == 0 && !paused){
			double canvasX = Turtle.canvasX(canvas.player.mouseX());
			double canvasY = Turtle.canvasY(canvas.player.mouseY());
			double playerX = canvas.player.getX();
			double playerY = canvas.player.getY();
			double deltaX = canvasX - playerX;
			double deltaY = canvasY - playerY;
			double direction = Math.atan2(deltaY, deltaX);
			direction *= 57.2957795;
	
			if (!shotGunShotIdle.isEmpty()) {
				SpreadShot t = shotGunShotIdle.get(0);
				
				shotGunShotIdle.remove(0);
				t.set(playerX, playerY, direction, 10, 45, 40, 20);
				
				shotGunShot.add(t);
				currentShotGunDelay = shotGunCooldown;
				if(haste){
					currentShotGunDelay = (int) (shotGunCooldown/(hasteMultiplier*hasteMultiplier));
				}
				else{
					currentShotGunDelay = shotGunCooldown;
				}
				playSound(shotGunSound);
			} else if (shotGunShot.size() < maxBullets) {
				SpreadShot p = new SpreadShot(playerX, playerY, direction, 10, 45, 40, 20);
				shotGunShot.add(p);
				currentShotGunDelay = shotGunCooldown;
				playSound(shotGunSound);
			}
		}
		
	}

	public static void restart(){
		numberOfEnemies = 0;
		enemyTravelDistance = 1;
		xPos = 0;
		yPos = 0;
		ticks = 0;
		enemy.clear();
		initializeHitPointsCounter(canvas.hitPointsTurtle);
		canvas.player.setPosition(0,0,0);
	}

	public static void pause(){
		paused = !paused;
	}

	public static void scatter() {
		//randomly scatters enemies, away from player
		double playerPosX = canvas.player.getX(); // player x position
		double playerPosY = canvas.player.getY(); // player y position
		double x, y;
		for (int i = 0; i < enemy.size(); i++) {
			Turtle t = enemy.get(i);
	
			do {
				x = Math.random() * 1000 - 500;
			} while (Math.abs(playerPosX - x) < 100);
			do {
				y = Math.random() * 950 - 500;
			} while (Math.abs(playerPosY - y) < 100);
	
			t.setPosition(x, y);
		}
	}

	public static void tick(int spawnDelay, double movementDistance)
	/*
	 * spawnDelay is an argument that describes the amount of while loop
	 * iterations that occur /before a new turtle spawns
	 */
	{
		if (ticks % spawnDelay == 0) {
			// spawns based on spawnDelay
			spawn(1, 30);
		}
		bulletMove();
		shotGunMove();
		checkEnemyPlayerCollision();
		checkEnemyBulletCollision();
		checkEnemyShotgunCollision();
		enemyMove(movementDistance);
		
		shotDelayTick(1);
		shotGunDelayTick(1);
		if(hasteActive){
			hasteTickDown(1);
		}
	}
	
	public static void spawn(int count, int maxEnemies) {
		double playerPosX = canvas.player.getX(); // player x position
		double playerPosY = canvas.player.getY(); // player y position
		double x, y;
		// count: Number of Enemies Spawned
		// maxEnemies: Max number of enemies that can exist in a game.
		for (int i = 0; i < count; i++) {
			if (numberOfEnemies <= maxEnemies) {
	
				// block of code that generates new turtle
				/*
				 * double degree = (Math.random() * Math.PI * 2); /double radius
				 * = (Math.random() * 50) + 50; / /double y = Math.sin(degree) *
				 * radius; /double x = Math.cos(degree) * radius;
				 */
				// selects random spawning position within bounds, not near
				// turtle
				do {
					x = Math.random() * 1000 - 500;
				} while (Math.abs(playerPosX - x) < 100);
				do {
					y = Math.random() * 950 - 500;
				} while (Math.abs(playerPosY - y) < 100);
	
				Turtle t = new Turtle(x, y);
				t.speed(.0001);
				t.up();
				turnTo(t, canvas.player);
	
				enemy.add(t);
				numberOfEnemies += 1;
			} else if (deadEnemy.size() > 0) {
				// block of code that recycles dead turtles
				Turtle t = deadEnemy.get(0);
				deadEnemy.remove(0);
	
				/*
				 * double degree = (Math.random() * Math.PI * 2); /double radius
				 * = (Math.random() * 50) + 50; / /double y = Math.sin(degree) *
				 * radius; /double x = Math.cos(degree) * radius;
				 */
	
				do {
					x = Math.random() * 1000 - 500;
				} while (Math.abs(playerPosX - x) < 100);
				do {
					y = Math.random() * 950 - 500;
				} while (Math.abs(playerPosY - y) < 100);
	
				t.setPosition(x, y);
				t.show();
				turnTo(t, canvas.player);
	
				enemy.add(t);
			}
		}
	}
	
	public static void teleportMeta(int energyCost, double distance) {
		//Meta teleport method
		//Energy Cost: How much energy ability costs
		//distance: How far you can teleport
		if(energy >= energyCost && !paused){
			double canvasX = Turtle.canvasX(canvas.player.mouseX());
			double canvasY = Turtle.canvasY(canvas.player.mouseY());
			//System.out.println("mouseX: " + canvasX);
			//System.out.println("mouseY: " + canvasY);
			if (-500 < canvasX && canvasX < 500 && -500 < canvasY && canvasY < 450
					 && distancePlayerMouse() <= distance) {
				canvas.player.setPosition(canvasX, canvasY);
				System.out.println("Wooosh");
				playSound(teleSound);
				loseEnergy(energyCost);
			}
		}
	}
	
	public static void teleport1(){
		//level 1 teleport
		//Cost: 3 energy
		//Distance: 150 units
		teleportMeta(3, 150);
	}
	
	public static void teleport2(){
		//level 2 teleport
		//Cost: 3 2nergy
		//Distance: 250 units
		teleportMeta(3, 250);
	}
	
	public static void teleport3(){
		//level 3 teleport
		//Cost: 3 energy
		//Distance: 400 units
		teleportMeta(3, 400);
	}
	
	public static void teleport4(){
		//level 4 teleport
		//Cost: 2 energy
		//Distance: 450 units
		teleportMeta(2, 450);
	}

	public static void metaHaste(int energyCost, int duration){
		if(energy >= energyCost && !paused){
			haste = true;
			canvas.player.fillColor("blue");
			remainingHasteDuration = duration;
			loseEnergy(energyCost);
		}
	}
	
		
	public static void gainEnergy(int amount){
		//gain [amount] energy
		for(int i = 0; i < amount; i++){
			if(energy < maxEnergy){
				canvas.energyTurtle.forward(100);
				energy += 1;
			}
		}
	}
	
	public static void loseEnergy(int amount){
		//lose [amount] energy
		for(int i = 0; i < amount; i++){
			if(energy > 0){
				canvas.energyTurtle.backward(100);
				energy -= 1;
			}
		}
	}
	
	public static void gainHealth(int amount){
		//gain [amount] health
		for(int i = 0; i < amount; i++){
			if(hp < 10){
				hp += 1;
				canvas.hitPointsTurtle.backward(100);
			}
		}
	}
	
	public static void loseHealth(int amount){
		//lose [amount] health
		for(int i = 0; i < amount; i++)
			if(hp > 0){
				hp -= 1;
				canvas.hitPointsTurtle.forward(100);
			}		
	}
	
	public static void gainPoints(int amount){
		points += amount;
	}
	
	public static boolean checkCollision(Turtle a, Turtle b, double radius) {
		return radius > b.distance(a.getX(), a.getY());
	}

	public static void checkBoundryConditions() {
		// method that makes sure turtle does not go out of bounds
		double x = canvas.player.getX();
		double y = canvas.player.getY();
	
		if (x > 490) {
			canvas.player.setPosition(480, y);
			x = 490;
		}
		if (x < -490) {
			canvas.player.setPosition(-480, y);
			x = -490;
		}
	
		if (y > 430) {
			canvas.player.setPosition(x, 430);
		}
		if (y < -490) {
			canvas.player.setPosition(x, -480);
		}
	}
	
	public static void checkEnemyPlayerCollision(){
		//checks for collisions between player and any enemy
		//used on each tick 
		for (int i = 0; i < enemy.size(); i++) {
			Turtle t = enemy.get(i);
			if (checkCollision(canvas.player, t, 30)) {
				// System.out.println("removed");
				deadEnemy.add(enemy.get(i));
				enemy.remove(i).hide();
				if (!godMode) {
					loseHealth(1);
				}
				// System.out.println(enemy.size());
				i--;
			}		
		}
	}
	
	public static void checkEnemyBulletCollision(){
		//checks for collisions between enemies and bullets
		//used each loop iteration
		for (int i = 0; i < enemy.size(); i++) {
			Turtle t = enemy.get(i);
			for(int j = 0; j < bulletLive.size(); j++){
				if (checkCollision(bulletLive.get(j).getTurtle(),
					t,20)) {
						deadEnemy.add(enemy.get(i));
						enemy.remove(i).hide();
						gainPoints(pointsPerKill);
						
						bulletIdle.add(bulletLive.remove(j).kill());
						playSound(splatSound);
				}
			}		
		}
	}
	
	public static void checkEnemyShotgunCollision(){
		for (int i = 0; i < enemy.size(); i++) {
			if(i < 0)return;
			Turtle t = enemy.get(i);
			for(int j = 0; j < shotGunShot.size(); j++){
				Projectile[] sub = shotGunShot.get(j).sub();
				if(shotGunShot.get(j).getTicks() < 1){
					shotGunShotIdle.add(shotGunShot.remove(j).kill());
					continue;
				}
				for(int k = 0; k < sub.length; k++){	
					Projectile curr = sub[k];
					if (checkCollision(curr.getTurtle(),t,20)) {
							t.hide();
							deadEnemy.add(t);
							enemy.remove(t);													
							i--;
							playSound(splatSound);
							break;
					}
				}
			}		
		}
	}
	
	public static void enemyMove(double movementDistance){
		for (Turtle t : enemy) {
			turnTo(t, canvas.player);
			t.forward(movementDistance);
		}
	}
	
	public static void bulletMove(){
		for (int i = 0; i < bulletLive.size(); i++) {
			Projectile t = bulletLive.get(i);
			if (t.getTicks() < 1) {
				bulletIdle.add(t);
				bulletLive.remove(i).kill();
			}
			t.step();
		}
	}
	
	public static void shotGunMove(){
		for (int i = 0; i < shotGunShot.size(); i++) {
			SpreadShot t = shotGunShot.get(i);
			if (t.getTicks() < 1) {
				shotGunShotIdle.add(t);
				shotGunShot.remove(i).kill();
			}
			t.step();
		}
	}
	
	public static void shotDelayTick(int amount){
		//counts down the current active cooldown for shots
		//by "amount" ticks
		//base: 1 cooldown tick reduced per loop iteration
		for(int i = 0; i < amount; i++){
			if(currentShotDelay > 0){
				currentShotDelay -= 1;
			}else{
				return;
			}
		}
	}
	
	public static void shotGunDelayTick(int amount){
		//counts down the current active cooldown for shots
		//by "amount" ticks
		//base: 1 cooldown tick reduced per loop iteration
		for(int i = 0; i < amount; i++){
			if(currentShotGunDelay > 0){
				currentShotGunDelay -= 1;
			}else{
				return;
			}
		}
	}
	
	public static void hasteTickDown(int amount){
		//ticks down haste duration
		for(int i = 0; i < amount; i++){
			if(remainingHasteDuration > 0){
				remainingHasteDuration -= 1;
			}
		}
		if(remainingHasteDuration == 0){
			haste = false;
			canvas.player.fillColor("pink");
		}
	}
	
	
	
	/**
	 * returns file objects that are stored in the class path.
	 * AKA files that are stored inside the src folder.
	 * ex: src/resources/gun.wav
	 * src is omited when using this method
	 */
	public static File getFile(String path){        
		File f = new File(GameOfTurtles.class.getResource(path).toString());
		System.out.println(f.exists());
		String s = f.getAbsolutePath().split("file")[1];
		s = s.substring(2);
		System.out.println(s);
		return new File(s);
					
	}
	
	/**
	 * plays .wav files that are stored within the program
	 * @param f
	 */
	public static void playSound(File f){
		 try
		 {
			 Clip sound = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
			
			 sound.open(AudioSystem.getAudioInputStream(f));
			 sound.start();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}
	
	public void positionReport() {
		xPos = canvas.player.getX();
		yPos = canvas.player.getY();
		System.out.println("X: " + xPos);
		System.out.println("Y: " + yPos);
	}

	public static double distancePlayerMouse(){
		//returns distance between player turtle and the mouse
		double canvasX = Turtle.canvasX(canvas.player.mouseX());
		double canvasY = Turtle.canvasY(canvas.player.mouseY());
		double playerX = canvas.player.getX();
		double playerY = canvas.player.getY();
		
		double deltaX = canvasX - playerX;
		double deltaXSquared = deltaX * deltaX;
		double deltaY = canvasY - playerY;
		double deltaYSquared = deltaY * deltaY;
		
		double distance = Math.pow(deltaXSquared + deltaYSquared, .5);
		return distance;
	}
	
	public static void turnTo(Turtle a, Turtle b) {
		a.face(b.getX(), b.getY());
	}
	

}