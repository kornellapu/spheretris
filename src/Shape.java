import java.awt.Color;

/** Az alakzat ami blokkokból áll, színe van és pozícója.
 * */
public class Shape {
	
	public static enum Type {O, L, J, Z, S, T, I}
	
	Position pos;
	Color color;
	Type type;
	
	Block[][] blocks;
	int width;
	int height;
	
	Shape(Type shape){
		pos = new Position();
		color = null;
		type = shape;
		initBlocks(shape);
	}
	
	Shape(Shape shape){
		pos = new Position(shape.pos);
		color = null;
		type = shape.type;
		width = shape.width;
		height = shape.height;
		blocks = new Block[width][height];
		for(int i=0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				blocks[i][j] = new Block(false);
				if(shape.blocks[i][j].full)
					blocks[i][j].full = true;
			}
		}
	}
	
	Type getType() {
		return type;
	}
	
	void setPosition(Position ppos) {
		pos.x = ppos.x;
		pos.y = ppos.y;
	}
	
	Position getPosition() {
		return pos;
	}
	
	Block[][] getBlocks(){
		return blocks;
	}
	
	boolean getFullAt(int x, int y) {
		return blocks[x][y].full;
	}
	
	int getBlocksWidth() {
		return width;
	}
	
	int getBlocksHeight() {
		return height;
	}
	
	boolean isPresentAt(Position ppos) {
		if(pos.x <= ppos.x && ppos.x < pos.x+width && pos.y <= ppos.y && ppos.y < pos.y+height) {
			//Ide kell hogy azt adja vissza, hogy van-e ott blokk vagy sem ha üres akkor false, ha teli akkor true
			return blocks[ppos.x-pos.x][ppos.y-pos.y].full;
		}
		return false;
	}
	
	public String toString() {
		return "" + type.name();
	}
	
	public Position getSpawnOffset(int spawnPoint){
		Position o = new Position();
		switch(type) {
		case O:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -2;
				break;
			case 1:
				o.x = 1;
				o.y = -2;
				break;
			case 2:
				o.x = 1;
				o.y = -1;
				break;
			case 3:
				o.x = -1;
				o.y = -1;
				break;
			case 4:
				o.y = 1;
				break;
			case 5:
				o.x = -2;
				o.y = 1;
				break;
			case 6:
				o.x = -2;
				break;
			case 7:
				o.x = -2;
				o.y = -2;
				break;
			}
			break;
		case Z:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -3;
				break;
			case 1:
				o.y = -2;
				break;
			case 2:
				o.x = 1;
				o.y = -2;
				break;
			case 3:
				o.x = 1;
				break;
			case 4:
				o.x = -1;
				break;
			case 5:
				o.x = -2;
				o.y = -1;
				break;
			case 6:
				o.x = -3;
				o.y = -1;
				break;
			case 7:
				o.x = -3;
				o.y = -3;
				break;
			}
			break;
		case S:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -3;
				break;
			case 1:
				o.x = 1;
				o.y = -3;
				break;
			case 2:
				o.x = 1;
				o.y = -1;
				break;
			case 3:
				o.y = -1;
				break;
			case 4:
				o.x = -1;
				break;
			case 5:
				o.x = -3;
				break;
			case 6:
				o.x = -3;
				o.y = -2;
				break;
			case 7:
				o.x = -2;
				o.y = -2;
				break;
			}
			break;
		case L:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -3;
				break;
			case 1:
				o.y = -3;
				break;
			case 2:
				o.y = -1;
				break;
			case 3:
				o.y = 1;
				break;
			case 4:
				o.x = -1;
				o.y = 1;
				break;
			case 5:
				o.x = -2;
				o.y = -1;
				break;
			case 6:
				o.x = -3;
				o.y = -1;
				break;
			case 7:
				o.x = -3;
				o.y = -3;
				break;
			}
			break;
		case J:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -3;
				break;
			case 1:
				o.x = 1;
				o.y = -3;
				break;
			case 2:
				o.x = 1;
				o.y = -1;
				break;
			case 3:
				o.y = -1;
				break;
			case 4:
				o.x = -1;
				o.y = 1;
				break;
			case 5:
				o.x = -2;
				o.y = 1;
				break;
			case 6:
				o.x = -2;
				o.y = -1;
				break;
			case 7:
				o.x = -2;
				o.y = -3;
				break;
			}
			break;
		case T:
			switch(spawnPoint) {
			case 0:
				o.x = -1;
				o.y = -2;
				break;
			case 1:
				o.x = 1;
				o.y = -2;
				break;
			case 2:
				o.x = 1;
				o.y = -1;
				break;
			case 3:
				break;
			case 4:
				o.x = -1;
				o.y = 1;
				break;
			case 5:
				o.x = -2;
				break;
			case 6:
				o.x = -3;
				o.y = -1;
				break;
			case 7:
				o.x = -3;
				o.y = -2;
				break;
			}
			break;
		case I:
			switch(spawnPoint) {
			case 0:
				o.x = -2;
				o.y = -4;
				break;
			case 1:
				o.x = -1;
				o.y = -4;
				break;
			case 2:
				o.x = -1;
				o.y = -2;
				break;
			case 3:
				o.x = -1;
				o.y = 1;
				break;
			case 4:
				o.x = -2;
				o.y = 1;
				break;
			case 5:
				o.x = -3;
				o.y = 1;
				break;
			case 6:
				o.x = -3;
				o.y = -1;
				break;
			case 7:
				o.x = -3;
				o.y = -4;
				break;
			}
			break;
		}
		
		return o;
	}
	
	public void rotate() {
		Block temp;
		switch(type) {
		case L:/*
			temp = blocks[0][0];
			blocks[0][0] = blocks[2][0];
			blocks[2][0] = blocks[2][2];
			blocks[2][2] = blocks[0][2];
			blocks[0][2] = temp;
			temp = blocks[1][0];
			blocks[1][0] = blocks[2][1];
			blocks[2][1] = blocks[1][2];
			blocks[1][2] = blocks[0][1];
			blocks[0][1] = temp;
			break;
			*/
		case J:
		case T:
			temp = blocks[0][0];
			blocks[0][0] = blocks[0][2];
			blocks[0][2] = blocks[2][2];
			blocks[2][2] = blocks[2][0];
			blocks[2][0] = temp;
			temp = blocks[1][0];
			blocks[1][0] = blocks[0][1];
			blocks[0][1] = blocks[1][2];
			blocks[1][2] = blocks[2][1];
			blocks[2][1] = temp;
			break;
		case S:
			temp = blocks[1][0];
			blocks[1][0] = blocks[1][2];
			blocks[1][2] = temp;
			temp = blocks[2][2];
			blocks[2][2] = blocks[0][2];
			blocks[0][2] = temp;
			break;
		case Z:
			temp = blocks[2][1];
			blocks[2][1] = blocks[0][1];
			blocks[0][1] = temp;
			temp = blocks[2][0];
			blocks[2][0] = blocks[2][2];
			blocks[2][2] = temp;
			break;
		case I:
			temp = blocks[2][0];
			blocks[2][0] = blocks[3][1];
			blocks[3][1] = temp;
			temp = blocks[1][1];
			blocks[1][1] = blocks[2][2];
			blocks[2][2] = temp;
			temp = blocks[0][1];
			blocks[0][1] = blocks[2][3];
			blocks[2][3] = temp;
			break;
		default:
		}
	}
	
	private void initBlocks(Type shape) {
		switch(shape) {
		case O:
			width = 2;
			height = 2;
			color = new Color(255, 255, 0);		//yellow
			blocks = new Block[width][height];
			blocks[0][0] = new Block(color);
			blocks[0][1] = new Block(color);
			blocks[1][0] = new Block(color);
			blocks[1][1] = new Block(color);
			break;
		case L:
			width = 3;
			height = 3;
			color = new Color(255, 120, 0);		//orange
			blocks = new Block[width][height];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(false);
			blocks[0][2] = new Block(false);
			blocks[1][0] = new Block(color);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(color);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(false);
			blocks[2][2] = new Block(color);
			break;
		case J:
			width = 3;
			height = 3;
			color = new Color(0, 0, 205);		//blue
			blocks = new Block[width][height];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(false);
			blocks[0][2] = new Block(color);
			blocks[1][0] = new Block(color);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(color);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(false);
			blocks[2][2] = new Block(false);
			break;
		case Z:
			width = 3;
			height = 3;
			color = new Color(255, 0, 0);		//red
			blocks = new Block[width][height];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(color);
			blocks[0][2] = new Block(false);
			blocks[1][0] = new Block(false);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(color);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(false);
			blocks[2][2] = new Block(color);
			break;
		case S:
			width = 3;
			height = 3;
			color = new Color(0, 255, 0);		//green
			blocks = new Block[width][height];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(false);
			blocks[0][2] = new Block(color);
			blocks[1][0] = new Block(false);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(color);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(color);
			blocks[2][2] = new Block(false);
			break;
		case T:
			width = 3;
			height = 3;
			color = new Color(205, 0, 205);		//magenta
			blocks = new Block[width][height];
			blocks[0][0] = new Block(false);
			blocks[0][1] = new Block(color);
			blocks[0][2] = new Block(false);
			blocks[1][0] = new Block(color);
			blocks[1][1] = new Block(color);
			blocks[1][2] = new Block(false);
			blocks[2][0] = new Block(false);
			blocks[2][1] = new Block(color);
			blocks[2][2] = new Block(false);
			break;
		case I:
			width = 4;
			height = 4;
			color = new Color(0, 255, 255);		//cyan
			blocks = new Block[width][height];
			for(int i=0; i < 4; i++) {
				for(int j=0; j<4; j++) {
					blocks[i][j] = new Block(false);
				}
			}
			blocks[2][0] = new Block(color);
			blocks[2][1] = new Block(color);
			blocks[2][2] = new Block(color);
			blocks[2][3] = new Block(color);
			break;
		default:
			//TODO ShapeNotFoundException
			break;
		}
	}
}
