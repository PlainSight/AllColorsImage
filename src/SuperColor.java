import java.awt.Color;

public class SuperColor {
	int x;
	int y;
	
	int r;
	int g;
	int b;
	
	float[] hsb = new float[3];
	
	int rr;
	int gg;
	int bb;
	
	Quadtree whereIAm;
	
	Boolean isAlive = true;
	
	public SuperColor(int red, int green, int blue, int xx, int yy) {
		r = red;
		g = green;
		b = blue;
		x = xx;
		y = yy;
		
		Color.RGBtoHSB(r, g, b, hsb);
		
		rr = (int) (hsb[0] * 255);
		gg = (int) (hsb[1] * 255);
		bb = (int) (hsb[2] * 255);
	}
	
	public SuperColor(int red, int green, int blue) {
		r = red;
		g = green;
		b = blue;
		
		Color.RGBtoHSB(r, g, b, hsb);
		
		rr = (int) (hsb[0] * 255);
		gg = (int) (hsb[1] * 255);
		bb = (int) (hsb[2] * 255);
	}
	
	public void destruct() {
		isAlive = false;
		
		if(whereIAm != null) {			
			whereIAm.remove(this);
		}
	}
	
	public static int getDist(SuperColor color1, SuperColor color2) {
		int rd = color1.rr - color2.rr;
		rd -= rd > 128 ? 256 : 0;
		rd += rd < -128 ? 256 : 0;
		
		int gd = color1.gg - color2.gg;
		int bd = color1.bb - color2.bb;
		
		
		
		return (rd*rd) + (gd*gd) + (bd*bd);
		// float hd = (((color1.hsb[0] - color2.hsb[0]) + 0.5f) % 1f) - 0.5f;
		// float sd = color1.hsb[1] - color2.hsb[1];
		// float bd = color1.hsb[2] - color2.hsb[2];
		
		// return (hd*hd) + (sd*sd) + (bd*bd);
	}
	
}
