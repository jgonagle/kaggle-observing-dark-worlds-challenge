import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.*;

@SuppressWarnings("serial")
public class DrawGridField extends JPanel 
{
	private static final int PREF_W = 500;
	private static final int PREF_H = 500;
	private static final int BORDER_GAP = 30;
	private static final Color CONTOUR_COLOR = Color.blue;
	private static final Color HALO_COLOR = Color.red;
	private static double CONTOUR_LENGTH = 40;
	private static double HALO_DIAMETER = 75;
	
	private GridPoint[][] gridPoints;
	private double[][] halos;

	public DrawGridField(GridPoint[][] gridPoints, double[][] halos) 
	{
		this.gridPoints = gridPoints;
		this.halos = halos;
	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		Graphics2D pen = (Graphics2D)g;
		pen.setColor(CONTOUR_COLOR);
		pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double xScale = (getWidth() - (2 * BORDER_GAP)) / GridPoint.X_DIM;
		double yScale = (getHeight() - (2 * BORDER_GAP)) / GridPoint.Y_DIM;
		
		double[] midPoint, contour;
		double armLength, xOffset, yOffset;
		int startX, startY, finishX, finishY;
		
		for (int x = 0; x < gridPoints.length; x++) 
		{
			for (int y = 0; y < gridPoints[0].length; y++) 
			{
				midPoint = gridPoints[x][y].getCoords();
				contour = gridPoints[x][y].getContour();
				
				armLength = CONTOUR_LENGTH / 2;
				
				xOffset = armLength * Math.cos(contour[1]);
				yOffset = armLength * Math.sin(contour[1]);
				
				startX = (int) (((midPoint[0] + xOffset) * xScale) + BORDER_GAP);
				startY = (int) (((midPoint[1] + yOffset) * yScale) + BORDER_GAP);
				finishX = (int) (((midPoint[0] - xOffset) * xScale) + BORDER_GAP);
				finishY = (int) (((midPoint[1] - yOffset) * yScale) + BORDER_GAP);
				
				pen.drawLine(startX,  startY, finishX, finishY);
			}
		}
		
		pen.setColor(HALO_COLOR);
		
		int haloX, haloY, diameterX, diameterY;
		
		for (int i = 0; i < halos.length; i++)
		{
			haloX = (int) ((halos[i][0] * xScale) + BORDER_GAP);
			haloY = (int) ((halos[i][1] * yScale) + BORDER_GAP);

			diameterX = (int) (HALO_DIAMETER * xScale);
			diameterY = (int) (HALO_DIAMETER * yScale);
			
			pen.drawOval(haloX,  haloY, diameterX, diameterY);
		}
	}

	@Override
	public Dimension getPreferredSize() 
	{
		return new Dimension(PREF_W, PREF_H);
	}

	public static void newGUI(int skyNum, GridPoint[][] gridPoints, double[][] halos) 
	{
		DrawGridField mainPanel = new DrawGridField(gridPoints, halos);
		
		JFrame frame = new JFrame("ContourGrid Sky " + skyNum);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
}