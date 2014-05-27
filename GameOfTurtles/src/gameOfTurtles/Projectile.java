package gameOfTurtles;

import java.awt.Color;

public class Projectile {
	Turtle base;
	int tick;
	double speed;

	Projectile(){
		base = new Turtle();
		tick = 5;
		speed = 10;
	}
	Projectile(double x, double y, double direction, double speed, int tick){
		base = new Turtle();
		base.hide();
		base.shape("triangle");
		base.speed(.0001);
		base.fillColor(Color.BLACK);
		base.up();
		base.setPosition(x, y, direction);
		base.show();
		
		this.tick = tick;
		this.speed = speed;
	}

	Projectile kill() {
		base.hide();
		return this;
	}

	int getTicks() {
		return tick;
	}
	
	Turtle getTurtle(){
		return base;
	}

	void set(double x, double y, double direction, double speed, int tick) {
		base.setPosition(x, y, direction);
		this.tick = tick;
		this.speed = speed;
		base.show();
	}

	void step() {
		if (tick > 0) {
			base.forward(speed * 1);
			tick--;
		}
	}
}
