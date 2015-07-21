import java.util.ArrayList;
import java.util.Arrays;

public class GridPoint
{
	public static final double X_DIM = 4200;
	public static final double Y_DIM = 4200;
	
	private double[] coords;
	private double[] meanEllip;
	private double[] stdDevEllip;
	private double[] andersonDarlingStat;
	private double[] contour;					//(magnitude, direction)
	private double averageDistance;
	
	public GridPoint(){}
	
	public void setCoords(double[] coords)
	{
		this.coords = coords;
	}
	
	public void determineLocalEllipticity(Galaxy[] galaxies, int numCloseGalaxies)
	{
		meanEllip = new double[2];
		stdDevEllip = new double[2];
		andersonDarlingStat = new double[2];
		contour = new double[2];

		Galaxy[] rankedGalaxies = new Galaxy[galaxies.length];
		
		for (int i = 0; i < rankedGalaxies.length; i++)
		{
			rankedGalaxies[i] = galaxies[i];
		}
		
		GalaxyDistComp galaxyComparator= new GalaxyDistComp(coords);
		
		Arrays.sort(rankedGalaxies, galaxyComparator);
		
		double distance;
		
		for (int i = 0; i < numCloseGalaxies; i++)
		{
			meanEllip[0] += rankedGalaxies[i].getEllipticities()[0];
			meanEllip[1] += rankedGalaxies[i].getEllipticities()[1];
			
			distance = galaxyComparator.getDistance(rankedGalaxies[i].getCoords());
			
			averageDistance += distance;
		}
		
		meanEllip[0] /= numCloseGalaxies;
		meanEllip[1] /= numCloseGalaxies;
		
		averageDistance /= numCloseGalaxies;
		
		double ellipticity = Math.sqrt(Math.pow(meanEllip[0], 2) + Math.pow(meanEllip[1], 2));
		contour[0] = (1 + ellipticity) / (1 - ellipticity);
		contour[1] = Math.atan2(meanEllip[1], meanEllip[0]) / 2;
			
		if (numCloseGalaxies > 1)
		{
			//setup standardized order statistics
			double[][] stdOrdEllip = new double[2][numCloseGalaxies];
			
			double[] galaxyEllip = new double[2];
			
			for (int i = 0; i < numCloseGalaxies; i++)
			{
				galaxyEllip = rankedGalaxies[i].getEllipticities();
				
				stdOrdEllip[0][i] = galaxyEllip[0];
				stdOrdEllip[1][i] = galaxyEllip[1];
				
				stdDevEllip[0] += Math.pow((galaxyEllip[0] - meanEllip[0]), 2);
				stdDevEllip[1] += Math.pow((galaxyEllip[1] - meanEllip[1]), 2);
			}
			
			stdDevEllip[0] = Math.sqrt(stdDevEllip[0] / (numCloseGalaxies - 1));
			stdDevEllip[1] = Math.sqrt(stdDevEllip[1] / (numCloseGalaxies - 1));
			
			Arrays.sort(stdOrdEllip[0]);
			Arrays.sort(stdOrdEllip[1]);
			
			for (int i = 0; i < numCloseGalaxies; i++)
			{
				stdOrdEllip[0][i] = (galaxyEllip[0] - meanEllip[0]) / stdDevEllip[0];
				stdOrdEllip[1][i] = (galaxyEllip[1] - meanEllip[1]) / stdDevEllip[1];
			}
			
			andersonDarlingStat[0] = findAndersonDarlingStat(stdOrdEllip[0]);
			andersonDarlingStat[1] = findAndersonDarlingStat(stdOrdEllip[1]);
		}
	}
	
	public double[] getCoords()
	{
		return coords;
	}
	
	public double[] getEllipticities()
	{
		return meanEllip;
	}
	
	public double[] getAndersonDarlingStat()
	{
		return andersonDarlingStat;
	}
	
	public double[] getContour()
	{
		return contour;
	}
	
	public double getAverageDistance()
	{
		return averageDistance;
	}
	
	public static GridPoint[][] getRectGridSky(int approxGridPoints)
	{
		double gridDensity = Math.sqrt(approxGridPoints / (X_DIM * Y_DIM));
	
		int xPoints = Math.max(2, ((int) Math.ceil(X_DIM * gridDensity)));
		int yPoints = Math.max(2, ((int) Math.ceil(Y_DIM * gridDensity)));
		
		double xStep = X_DIM / (xPoints - 1);
		double yStep = Y_DIM / (yPoints - 1);
	
		GridPoint[][] gridPoints = new GridPoint[xPoints][yPoints];
	
		for (int x = 0; x < xPoints; x++)
		{
			for (int y = 0; y < yPoints; y++)
			{
				gridPoints[x][y] = new GridPoint();
		
				gridPoints[x][y].setCoords(new double[]{(x * xStep), (y * yStep)});
			}
		}
	
		return gridPoints;
	}
	
	//calculate normal cdf using marsaglia's taylor series expansion
	private static double normalCDF(double z)
	{
		double sum = 0;
		double prevTaylorTerm = z;
		
		for (int i = 3; prevTaylorTerm != 0; i += 2)
		{
			sum += prevTaylorTerm;
			prevTaylorTerm *= Math.pow(z, 2) / i;
		}
		
		return (.5 + (normalPDF(z) * sum));
	}
	
	//calculate normal cdf using marsaglia's taylor series expansion
	private static double normalPDF(double z)
	{
		return (Math.exp((- Math.pow(z, 2)) / 2) / Math.sqrt(2 * Math.PI));
	}
	
	//takes in array of standardized ordered statistics
	private static double findAndersonDarlingStat(double[] dist)
	{
		int n = dist.length;
		double sum = 0;
		
		for (int i = 0; i < n; i++)
		{
			sum += (((2 * (i + 1)) - 1) * Math.log(normalCDF(dist[i]))) +
				   (((2 * (n - (i + 1))) + 1) * Math.log(1 - normalCDF(dist[i])));
		}
		
		return Math.sqrt(-n - (sum / n));
	}
}