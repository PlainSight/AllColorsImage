import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;


public class Main {
	
	int REDBITDEPTH = 8;
	int GREENBITDEPTH = 8;
	int BLUEBITDEPTH = 8;
	
	int width = 4096;
	int height = 4096;
	int topcount = 0;
	
	SuperColor[][] pixilValues;
		
	Quadtree edgeColors = new Quadtree(256, 256, 256, 128, 128, 128, null);
	
	public static void main(String[] args) {
		new Main(args);
	}
	
	int nextColor = 0;
	int sourceColors = 0;
	SuperColor[] colorSource;
	
	BufferedImage sourceImage;
	
	public Main(String[] args) {
		
		if(args.length == 0) {
			return;
		}
		
		if(args.length > 0) {
			try {
			File inputfile = new File(args[0]);
		    sourceImage = ImageIO.read(inputfile);
			} catch (Exception e) {}
		}
		
		if(args.length > 3) {
			REDBITDEPTH = Integer.parseInt(args[1]);
			GREENBITDEPTH = Integer.parseInt(args[2]);
			BLUEBITDEPTH = Integer.parseInt(args[3]);
		}
		
		width = sourceImage.getWidth();
		height = sourceImage.getHeight();
		
		System.out.println("Creating Image with dimensions: " + width + " x " + height);
		System.out.println("With " + REDBITDEPTH + " " + GREENBITDEPTH + " " + BLUEBITDEPTH + " bit colours");
		
		pixilValues = new SuperColor[width][height];
		topcount = width*height;
		int sizeOfSource = (int) Math.pow(2, REDBITDEPTH) * (int) Math.pow(2, GREENBITDEPTH) * (int) Math.pow(2, BLUEBITDEPTH);
		colorSource = new SuperColor[sizeOfSource];
		
		long starttime = System.currentTimeMillis();
		
		int redinc = 256 / ((int) Math.pow(2, REDBITDEPTH));
		int greeninc = 256 / ((int) Math.pow(2, GREENBITDEPTH));
		int blueinc = 256 / ((int) Math.pow(2, BLUEBITDEPTH));
		
		int sourceColors = 0;
		for(int r = 0; r < 256; r += redinc) {
			for(int g = 0; g < 256; g += greeninc) {
				for(int b = 0; b < 256; b += blueinc) {
					colorSource[sourceColors++] = new SuperColor(r, g, b);
				}
			}
		}
				
		long aftercolortime = System.currentTimeMillis();
		
		
		//generate colors of original picture
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int rgb = sourceImage.getRGB(x, y);
				int red = (rgb & 0x00FF0000) >> 16;
				int green = (rgb & 0x0000FF00) >> 8;
				int blue = rgb & 0x000000FF;
				edgeColors.add(new SuperColor(red, green, blue, x, y));
			}
		}
		
		System.out.println("Source Image pixils: " + edgeColors.size);
		
		//place first pixil
		SuperColor poppedColour;
		
		for(int counter = 0; counter < topcount; counter++) {
			poppedColour = getNextColor();

			SuperColor closestNeighbour = edgeColors.findNearest(poppedColour, null);
									
			setPixil(closestNeighbour, poppedColour);
			
			closestNeighbour.destruct();
			
			
			if(counter % (topcount / 100) == 0) {
				System.out.print("\r" + (counter / (topcount / 100)) + "%\t" + (System.currentTimeMillis() - aftercolortime)/1000 + "s");
			}
			
//			if(counter % 10000 == 0) {
//				render(""+counter);
//			}
		}
		
		long finishtime = System.currentTimeMillis();
		System.out.print("\r100%\n");
		System.out.println("Start up time: " + (aftercolortime - starttime) + "ms");
		System.out.println("Calculation time: " + (finishtime - aftercolortime) + "ms");

		render(""+topcount);
		
	}
	
	private void setPixil(SuperColor source, SuperColor finisher) {
		pixilValues[source.x][source.y] = finisher;
	}
	
	private SuperColor getNextColor() {
		int indexToGet = nextColor + ((int) (Math.random() * (colorSource.length - nextColor)));
		
		SuperColor result = colorSource[indexToGet];
		
		colorSource[indexToGet] = colorSource[nextColor];
		
		nextColor++;
		
		return result;
	}
	
	private void render(String label) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		
		for(int drawx = 0; drawx < width; drawx++) {			
			for(int drawy = 0; drawy < height; drawy++) {
				SuperColor elem = getPixil(drawx, drawy);
				if(elem != null) {
					g2.setColor(new Color(elem.r, elem.g, elem.b));
				} else {
					g2.setColor(Color.black);
				}
				g2.fillRect(drawx, drawy, 1, 1);
			}			
		}
		
		
		try {
		    // retrieve image
		    File outputfile = new File(label + " painting " + (new Date()).toString().replace(':', '-') + ".png");
		    ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	private SuperColor getPixil(int x, int y) {
		return pixilValues[x][y];
	}
}
