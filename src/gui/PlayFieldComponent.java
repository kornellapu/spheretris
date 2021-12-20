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

/**	Ez jelen�ti meg mag�t a j�t�kot, a k�z�ppontot, a lerakott blokkokat �s az �ppen es� blokkot a j�t�k logika alapj�n
 * */
public class PlayFieldComponent extends JComponent {
	
	/**	A sz�rializ�l�s miatt verzi� sz�m
	 * */
	private static final long serialVersionUID = 1L;

	/** Referencia a j�t�k logik�ra
	 * */
	final GameLogic gl;
	
	/**	Egy blokknak a sz�less�g�nek/magass�g�nak a sz�ma, pixeleket jelent 
	 * */
	final int blockSizePx = 15;
	/**	A blokkok k�z�tti pixelek sz�ma
	 * */
	final int offSetPx = 2;
	/**	A megjelen�tett elem nagys�ga
	 * */
	Dimension dim;
	
	/**	Referencia egy k�pet tartalmaz� mez�re, amire a j�t�k kirajzol�sa fog t�rt�nni
	 * 	Elt�rol�sra ker�l, hogy ne kelljen l�trehozni, �jat minden kirajzol�sn�l
	 * */
	BufferedImage canvas;
	
	/**	Konstruktor
	 * 	L�trehozza �s be�ll�tja a komponens m�ret�t, ehhez fog igazodni a t�bbi ablak is
	 * 	A kirajzol�sra haszn�lt BufferedImage-et is inicializ�lja
	 * 
	 * 	@param gl Referencia a j�t�k logik�ra
	 * */
	public PlayFieldComponent(GameLogic gl) {
		super();
		this.gl = gl;
		dim = new Dimension(gl.gameWidth*blockSizePx+((gl.gameWidth-1)*offSetPx), gl.gameHeight*blockSizePx+((gl.gameHeight-1)*offSetPx));
		canvas = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
		
	}
	
	/**	A komponens prefer�lt m�ret�nek gettere
	 * 	A java ablakkezel� f�ggv�nyei miatt sz�ks�ges
	 * */
	public Dimension getMinimumSize() { 
		return dim;
	}
	
	/**	A komponens maximum m�ret�nek gettere
	 * 	A java ablakkezel� f�ggv�nyei miatt sz�ks�ges
	 * */
	public Dimension getMaximumSize() { 
		return dim;
	}
	
	/**	A komponens minimum m�ret�nek gettere
	 * 	A java ablakkezel� f�ggv�nyei miatt sz�ks�ges
	 * */
	public Dimension getPreferredSize() { 
		return dim;
	}
	
	/** Letiszt�tja a k�perny�t �s kirajzolja az �rintkez�s indik�tort, majd az �j blokkokat, a j�t�k aktu�lis �ll�sa szerint, ut�na pedig a gravit�ci�s indik�tort
	 * 	Csak a j�t�kmez�n iter�l v�gig, amiben el vannak t�rolva a blokkok
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
	
	/**	Kirajzol egy adott m�ret� �res n�gyzetet az x,y koordin��tn a lineWidth vonalvastags�ggal
	 * 	Ez a kiralyzol�s pont a blokkokk k�z�tti r�szbe fog esni
	 * 	Ha nincs ilyen blokkok k�z�tti t�vols�g nem fog kirajzolni semmit
	 * 
	 * 	@param cg			A grafikus v�ltoz� amire rajzol
	 * 	@param x			Az x koordin�ta (bal fels� sarok)
	 * 	@param y			Az ykoordin�ta (jobb fels� sarok)
	 * 	@param size			A n�gyzet m�retenek k�lseje
	 * 	@param lineWidth	A vonal vastags�ga amivel kirajzol�sra ker�l
	 * */
	void drawSquare(Graphics cg, int x, int y, int size, int lineWidth) {
		for(int i=0; i<lineWidth; i++) {
			//fenti v�zszintes
			cg.drawLine(x+i, y+i, x+size+lineWidth-i -2, y+i);
			//jobb f�gg�leges
			cg.drawLine(x+size+lineWidth-i-1, y+i, x+size+lineWidth-i-1, y+lineWidth+size-i-2);
			//lenti v�zszintes
			cg.drawLine(x+i+1, y+lineWidth+size-i-1, x+size+lineWidth-i-1, y+lineWidth+size-i-1);
			//bal f�gg�leges
			cg.drawLine(x+i, y+i+1, x+i, y+lineWidth+size-i-1);
		}
	}
	
	/**	Kisz�molja a hely�t hov� kell rajzolni az indik�tort, milyen alpha �rt�kkel
	 * 	Majd megh�vja a kirajzol�st a kisz�molt adatokkal
	 * 	V�g�l cs�kkenti az �lettartam�nak az �rt�k�t
	 * 	
	 * 	@param cg Amilyen grafikus fel�letre a kirajzol�s t�rt�nik
	 * */
	void drawIndicator(Graphics cg) {
		int MID_X = gl.gameWidth/2;
		int MID_Y = gl.gameHeight/2;
		
		int inBound  = gl.getShapeIndicator().getInnerDist();
		int outBound = gl.getShapeIndicator().getOuterDist()+1;
		
		for(int i = inBound; i < outBound; i++ ) {
			
			int alpha = gl.getShapeIndicator().getOpac(i-inBound);
			
			cg.setColor( new Color(255,255,255, alpha) );
			//K�ls�
			int x = (MID_X - i)*(blockSizePx+offSetPx)-offSetPx;
			int y = (MID_Y - i)*(blockSizePx+offSetPx)-offSetPx;
			int size = (i*2 + 1) * (blockSizePx+offSetPx);
			
			drawSquare(cg, x, y, size, offSetPx);
		}
		
		gl.getShapeIndicator().decreaseLife();
	}
	
	/**	A gravit�ci�s indik�tor kirajzol�sa a k�perny�re
	 * 	A h�romsz�g elk�sz�t�se a megfelel� pozici� �s ir�ny seg�ts�g�vel	
	 * 
	 * 	Megjegyz�s: Az indik�tort a j�t�klogika be�ll�tja a megfelel� poz�ci�ba, ez�rt nem kell be�ll�tani a rajzol�s el�tt	
	 * 
	 * 	@param cg A grafikus fel�let amire kirajzolja
	 * */
	void drawGravityIndicator(Graphics cg){
		TriangleIndicator gi = gl.getGravityIndicator();
		cg.setColor( gi.getColor() );
		
		Polygon triangle = getPolygon(gi.getPos(), gi.getDir());
		cg.fillPolygon(triangle);
	}
	
	/**	Elk�sz�t egy kirajzolhat� h�romsz�get a mefelel� poz�ci�ra �s ir�nyba
	 * 	Tartalmazza a h�romsz�g le�r�s�t
	 * 
	 * 	@param pos A h�romsz�g blokk h�l�ban �rtelmezett poz�ci�ja
	 * 	@param dir A h�romsz�g cs�cs�nak ir�nya
	 * 	@return Egy h�rom cs�csb�l �ll� polygont ad vissza (h�romsz�get)
	 * */
	Polygon getPolygon(Position pos, GameLogic.Dir dir) {
		//blokk t�vols�g
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
