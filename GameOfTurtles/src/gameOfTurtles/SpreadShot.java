package gameOfTurtles;

public class SpreadShot {
	Projectile[] subMunition;
	int ticks;
	public SpreadShot(double x, double y, double direction, double speed, double spread, int tick, int count){
		subMunition = new Projectile[count];
		this.ticks = tick;
		double subD = spread / count;
		double start = direction  - (spread / 2.0);
		for(int i = 0; i < count; i ++){
			double d = start + (i * subD);
			subMunition[i] = new Projectile(x,y,d,speed,tick);	
		}
	}
	public void set(double x, double y, double direction, double speed, double spread, int tick, int count){
		this.ticks = tick;
		double subD = spread / (subMunition.length * 1.0);	
		double start = direction  - (spread / 2.0);
		
		for(int i = 0; i < subMunition.length; i ++){
			double d = (start) + (i * subD);
			
			subMunition[i].set(x, y, d, speed, tick);	
		}
	}
	public int getTicks(){
		return ticks;
	}
	public void step(){
		for(Projectile p: subMunition){
			p.step();
		}
		ticks--;
	}
	public Projectile[] sub(){
		return subMunition;
	}
	public SpreadShot kill(){
		for(Projectile p: subMunition){
			p.kill();
		}
		return this;
	}
}
