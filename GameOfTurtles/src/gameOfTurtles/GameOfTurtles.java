package gameOfTurtles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameOfTurtles {
	static List<Turtle> enemy = new ArrayList<Turtle>(); // List of enemies
	static List<Turtle> deadEnemy = new ArrayList<Turtle>(); // List of dead enemies
																
	static List<Projectile> bulletLive = new ArrayList<Projectile>();
	static List<Projectile> bulletIdle = new ArrayList<Projectile>();

	static GameCanvas canvas; // imported canvas for the player
	static double xPos, yPos; // position of the player
	static int ticks, numberOfEnemies, hp, enemyTravelDistance, maxBullets;
	static boolean godMode = false;
	// number of iterationsof the while loop,

	// total number of enemies spawned
	// hp: Hitpoints
	// enemyTravelDistance: How far each enemy travels each tick

	public static void main(String[] args) {
		System.out.println("/resources/pistol_gun_shot.mp3");
		canvas = new GameCanvas();
		xPos = 0;
		yPos = 0;
		ticks = 0;
		numberOfEnemies = 0;
		enemyTravelDistance = 1;
		maxBullets = 5;
		hp = 10;

		initializePlayer(canvas.player);
		initializeHitPointsCounter(canvas.hitPointsTurtle);
		while (hp > 0) {
			ticks += 1; // adds one tick for each iteration
			canvas.player.forward(10);
			xPos = canvas.player.getX();
			yPos = canvas.player.getY();

			tick(5, enemyTravelDistance);
			if (ticks % 150 == 0) {
				scatter();
				enemyTravelDistance += 1;
			}
			// tick(5,Math.ceil((double) ticks / 100)); //second parameter:
			// Speeds up as time goes on
			checkBoundryConditions();
			// canvas.player.zoom(-500, -500, 500, 500);
			// System.out.println(enemy.size());
		}
		System.out.println("Thanks for playing!");
		System.out.println("Final Score: " + ticks);
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
		t.zoom(-500, -500, 500, 500);
		t.onKey("moveRight", "d");
		t.onKey("moveLeft", "a");
		t.onKey("moveUp", "w");
		t.onKey("moveDown", "s");
		t.onKey("teleport", "q");
		t.onKey("shoot", "e");
//		t.onKey("restart","r");
		
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

	public void positionReport() {
		xPos = canvas.player.getX();
		yPos = canvas.player.getY();
		System.out.println("X: " + xPos);
		System.out.println("Y: " + yPos);
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
	public static void scatter() {
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

	public static void shoot() {
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
		} else if (bulletLive.size() < maxBullets) {
			Projectile t = new Projectile(playerX, playerY,direction, 10 , 5);
			bulletLive.add(t);
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

	@SuppressWarnings("static-access")
	public static void teleport() {
		// broken method to teleport
	
		double canvasX = Turtle.canvasX(canvas.player.mouseX());
		double canvasY = Turtle.canvasY(canvas.player.mouseY());
		System.out.println("mouseX: " + canvasX);
		System.out.println("mouseY: " + canvasY);
		if (-500 < canvasX && canvasX < 500 && -500 < canvasY && canvasY < 450) {
			canvas.player.setPosition(canvasX, canvasY);
			System.out.println("Wooosh");
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
		for (int i = 0; i < bulletLive.size(); i++) {
			Projectile t = bulletLive.get(i);
			if (t.getTicks() < 1) {
				bulletIdle.add(t);
				bulletLive.remove(i).kill();
			}
			t.step();

		}
	
		for (int i = 0; i < enemy.size(); i++) {
			Turtle t = enemy.get(i);
			for(int j = 0; j < bulletLive.size(); j++){
				if (checkCollision(bulletLive
						.get(j)
						.getTurtle(),
						t,
						20)) {
						deadEnemy.add(enemy.get(i));
						enemy.remove(i).hide();
						
						bulletIdle.add(bulletLive.remove(j).kill());
						System.out.println("HIT");
				}
			}
			
		
			if (checkCollision(canvas.player, t, 30)) {
				// System.out.println("removed");
				deadEnemy.add(enemy.get(i));
				enemy.remove(i).hide();
				if (!godMode) {
					hp -= 1;
					canvas.hitPointsTurtle.forward(100);
				}
				// System.out.println(enemy.size());

				i--;
				
			}
			

		}
		for (Turtle t : enemy) {
			turnTo(t, canvas.player);
			t.forward(movementDistance);
		}

	}

	public static void turnTo(Turtle a, Turtle b) {
		a.face(b.getX(), b.getY());
	}
	
	public File getFile(String path){        
		String imgURL = getClass().getResource(path).toString();
		if (imgURL != null) {
			return new File(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}	

	}
		
	
}
