package gameOfTurtles;

import java.util.Scanner;

public class GameCanvas {
	Turtle player, hitPointsTurtle,energyTurtle;

	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		char flag;
		do{
			System.out.println("starting a game");
			GameOfTurtles.run(10,10);
			System.out.println("play again?");
			flag = in.next().toLowerCase().charAt(0);
		}while(flag != 'n');
	}
	public GameCanvas(){
		player = new Turtle();
		hitPointsTurtle = new Turtle();
		energyTurtle = new Turtle();
	}

}
