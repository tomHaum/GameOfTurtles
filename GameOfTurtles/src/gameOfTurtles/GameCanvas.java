package gameOfTurtles;

public class GameCanvas {
	Turtle player, hitPointsTurtle,energyTurtle;

	public static void main(String[] args){
		GameOfTurtles.main(null);
		System.out.println("Game 2 begin");
		GameOfTurtles.main(null);
	}
	public GameCanvas(){
		player = new Turtle();
		hitPointsTurtle = new Turtle();
		energyTurtle = new Turtle();
	}

}
