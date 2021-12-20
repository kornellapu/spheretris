package gui;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import basic.Position;
import indicator.TriangleIndicator;
import logic.GameLogic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Dimension;

/**	Ez jeleníti meg magát a játékot, a középpontot, a lerakott blokkokat és az éppen esõ blokkot a játék logika alapján
 * */
public class PlayFieldComponent extends JComponent {
	
	/**	A szérializálás miatt verzió szám
	 * */
	private static final long serialVersionUID = 1L;

	/** Referencia a játék logikára
	 * */
	final GameLogic gl;
	
	/**	Egy blokknak a szélességének/magasságának a száma, pixeleket jelent 
	 * */
	final int blockSizePx = 15;
	/**	A blokkok közötti pixelek száma
	 * */
	final int offSetPx = 2;
	/**	A megjelenített elem nagysága
	 * */
	Dimension dim;
	
	/**	Referencia egy képet tartalmazó mezõre, amire a játék kirajzolása fog történni
	 * 	Eltárolásra kerül, hogy ne kelljen létrehozni, újat minden kirajzolásnál
	 * */
	BufferedImage canvas;
	
	/**	Konstruktor
	 * 	Létrehozza és beállítja a komponens méretét, ehhez fog igazodni a többi ablak is
	 * 	A kirajzolásra használt BufferedImage-et is inicializálja
	 * 
	 * 	@param gl Referencia a játék logikára
	 * */
	public PlayFieldComponent(GameLogic gl) {
		super();
		this.gl = gl;
		dim = new Dimension(gl.gameWidth*blockSizePx+((gl.gameWidth-1)*offSetPx), gl.gameHeight*blockSizePx+((gl.gameHeight-1)*offSetPx));
		canvas = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
		
	}
	
	/**	A komponens preferált méretének gettere
	 * 	A java ablakkezelõ függvényei miatt szükséges
	 * */
	public Dimension getMinimumSize() { 
		return dim;
	}
	
	/**	A komponens maximum méretének gettere
	 * 	A java ablakkezelõ függvényei miatt szükséges
	 * */
	public Dimension getMaximumSize() { 
		return dim;
	}
	
	/**	A komponens minimum méretének gettere
	 * 	A java ablakkezelõ függvényei miatt szükséges
	 * */
	public Dimension getPreferredSize() { 
		return dim;
	}
	
	/** Letisztítja a képernyõt és kirajzolja az érintkezés indikátort, majd az új blokkokat, a játék aktuális állása szerint, utána pedig a gravitációs indikátort
	 * 	Csak a játékmezõn iterál végig, amiben el vannak tárolva a blokkok
	 * */
	public void paint(Graphics grx) {
		Graphics cg = canvas.getGraphics();
		cg.setColor(Color.black);
		cg.fillRect(0,0,dim.width, dim.height);
		
		/*
		Random rnd = new Random(System.currentTimeMillis());
		for(int x = 0; x < rowCount; x++) {
			for(int y = 0; y < colCount; y++) {
				int r = rnd.nextInt(256);
				int g = rnd.nextInt(256);
				int b = rnd.nextInt(256);
				//System.out.println(" XY:(" + x + ", " + y + "), RGB:(" + r + ", " + g + ", " + b + ")" );
				cg.setColor( new Color( r, g, b));
				if(x == 20 && y == 20) {
					cg.setColor( new Color(255, 255, 255) );
				}
				cg.fillRect(x*(blockSizePx+offSetPx), y*(blockSizePx+offSetPx), blockSizePx, blockSizePx);
			}
		}
		*/
		
		drawIndicator(cg);
		
		for(int x = 0; x < gl.gameWidth; x++) {
			for( int y = 0; y < gl.gameHeight; y++) {
				if(gl.isFilled(x,y)) {
					cg.setColor( gl.getBlockColorAt(x,y) );
					cg.fillRect(x*(blockSizePx+offSetPx), y*(blockSizePx+offSetPx), blockSizePx, blockSizePx);
				}
			}
		}
		
		drawGravityIndicator(cg);
		
		grx.drawImage(canvas, 0, 0, null);
	}
	
	/**	Kirajzol egy adott méretû üres négyzetet az x,y koordináátn a lineWidth vonalvastagsággal
	 * 	Ez a kiralyzolás pont a blokkokk közötti részbe fog esni
	 * 	Ha nincs ilyen blokkok közötti távolság nem fog kirajzolni semmit
	 * 
	 * 	@param cg			A grafikus változó amire rajzol
	 * 	@param x			Az x koordináta (bal felsõ sarok)
	 * 	@param y			Az ykoordináta (jobb felsõ sarok)
	 * 	@param size			A négyzet méretenek külseje
	 * 	@param lineWidth	A vonal vastagsága amivel kirajzolásra kerül
	 * */
	void drawSquare(Graphics cg, int x, int y, int size, int lineWidth) {
		for(int i=0; i<lineWidth; i++) {
			//fenti vízszintes
			cg.drawLine(x+i, y+i, x+size+lineWidth-i -2, y+i);
			//jobb függõleges
			cg.drawLine(x+size+lineWidth-i-1, y+i, x+size+lineWidth-i-1, y+lineWidth+size-i-2);
			//lenti vízszintes
			cg.drawLine(x+i+1, y+lineWidth+size-i-1, x+size+lineWidth-i-1, y+lineWidth+size-i-1);
			//bal függõleges
			cg.drawLine(x+i, y+i+1, x+i, y+lineWidth+size-i-1);
		}
	}
	
	/**	Kiszámolja a helyét hová kell rajzolni az indikátort, milyen alpha értékkel
	 * 	Majd meghívja a kirajzolást a kiszámolt adatokkal
	 * 	Végül csökkenti az élettartamának az értékét
	 * 	
	 * 	@param cg Amilyen grafikus felületre a kirajzolás történik
	 * */
	void drawIndicator(Graphics cg) {
		int MID_X = gl.gameWidth/2;
		int MID_Y = gl.gameHeight/2;
		
		int inBound  = gl.getShapeIndicator().getInnerDist();
		int outBound = gl.getShapeIndicator().getOuterDist()+1;
		
		for(int i = inBound; i < outBound; i++ ) {
			
			int alpha = gl.getShapeIndicator().getOpac(i-inBound);
			
			cg.setColor( new Color(255,255,255, alpha) );
			//Külsõ
			int x = (MID_X - i)*(blockSizePx+offSetPx)-offSetPx;
			int y = (MID_Y - i)*(blockSizePx+offSetPx)-offSetPx;
			int size = (i*2 + 1) * (blockSizePx+offSetPx);
			
			drawSquare(cg, x, y, size, offSetPx);
		}
		
		gl.getShapeIndicator().decreaseLife();
	}
	
	/**	A gravitációs indikátor kirajzolása a képernyõre
	 * 	A háromszög elkészítése a megfelelõ pozició és irány segítségével	
	 * 
	 * 	Megjegyzés: Az indikátort a játéklogika beállítja a megfelelõ pozícióba, ezért nem kell beállítani a rajzolás elõtt	
	 * 
	 * 	@param cg A grafikus felület amire kirajzolja
	 * */
	void drawGravityIndicator(Graphics cg){
		TriangleIndicator gi = gl.getGravityIndicator();
		cg.setColor( gi.getColor() );
		
		Polygon triangle = getPolygon(gi.getPos(), gi.getDir());
		cg.fillPolygon(triangle);
	}
	
	/**	Elkészít egy kirajzolható háromszöget a mefelelõ pozícióra és irányba
	 * 	Tartalmazza a háromszög leírását
	 * 
	 * 	@param pos A háromszög blokk hálóban értelmezett pozíciója
	 * 	@param dir A háromszög csúcsának iránya
	 * 	@return Egy három csúcsból álló polygont ad vissza (háromszöget)
	 * */
	Polygon getPolygon(Position pos, GameLogic.Dir dir) {
		//blokk távolság
		//[b]lock [d]istance
		int bd = blockSizePx + offSetPx;
		
		pos.x -= gl.getOffScreenBorder();
		pos.y -= gl.getOffScreenBorder();
		
		int[] xArr = {bd*pos.x, bd*pos.x, bd*pos.x};
		int[] yArr = {bd*pos.y, bd*pos.y, bd*pos.y};
		int cnt = 3;
		
		double sqr2 = 1.4142;
		int diagSize = (int) Math.round((double)blockSizePx / sqr2);
		
		//System.out.println("Diag size: " + diagSize);
		
		switch(dir) {
		case UP:
			xArr[0] += 0;
			yArr[0] += blockSizePx/2;
			
			xArr[1] += blockSizePx/2;
			yArr[1] += 0;
			
			xArr[2] += blockSizePx;
			yArr[2] += blockSizePx/2;
			break;
			
		case RIGHT_UP:
			//xArr[0] += 0;
			yArr[0] += blockSizePx - diagSize;
			
			xArr[1] += diagSize;
			yArr[1] += blockSizePx - diagSize;
			
			xArr[2] += diagSize;
			yArr[2] += blockSizePx;
			break;
			
		case RIGHT:
			xArr[0] += blockSizePx/2;
			yArr[0] += 0;
			
			xArr[1] += blockSizePx;
			yArr[1] += blockSizePx/2;
			
			xArr[2] += blockSizePx/2;
			yArr[2] += blockSizePx;
			break;
			
		case RIGHT_DOWN:
			//xArr[0] += 0;
			yArr[0] += diagSize;
			
			xArr[1] += diagSize;
			//yArr[1] += 0;
			
			xArr[2] += diagSize;
			yArr[2] += diagSize;
			break;
			
		case DOWN:
			xArr[0] += 0;
			yArr[0] += blockSizePx/2;
			
			xArr[1] += blockSizePx;
			yArr[1] += blockSizePx/2;
			
			xArr[2] += blockSizePx/2;
			yArr[2] += blockSizePx;
			break;
			
		case LEFT_DOWN:
			xArr[0] += blockSizePx - diagSize;
			//yArr[0] += 0;
			
			xArr[1] += blockSizePx;
			yArr[1] += diagSize;
			
			xArr[2] += blockSizePx - diagSize;
			yArr[2] += diagSize;
			break;
			
		case LEFT:
			xArr[0] += blockSizePx/2;
			//yArr[0] -= 1;
			
			xArr[1] += blockSizePx/2;
			yArr[1] += blockSizePx;
			
			//xArr[2] += 0;
			yArr[2] += blockSizePx/2;			
			break;
			
		case LEFT_UP:
			xArr[0] += blockSizePx - diagSize;
			yArr[0] += blockSizePx - diagSize;
			
			xArr[1] += blockSizePx;
			yArr[1] += blockSizePx - diagSize;
			
			xArr[2] += blockSizePx - diagSize;
			yArr[2] += blockSizePx;
			break;
		default:
			break;

		}
		
		return new Polygon(xArr, yArr, cnt);
	}
	

}
