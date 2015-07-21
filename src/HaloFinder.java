import java.io.*;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

@SuppressWarnings("unused")
public class HaloFinder 
{
	public static void main(String[] args)
	{
		final int NUM_GRID_POINTS = 1000;
		final int NUM_CLOSE_GALAXIES = 20;

		GridPoint[][] gridPoints = GridPoint.getRectGridSky(NUM_GRID_POINTS);
		double xGridSize = gridPoints.length;
		double yGridSize = gridPoints[0].length;
		
		ArrayList<Sky> skies = getSkysFromFile();
		int numSkies = skies.size();
		
		System.out.println("Number of Skies: " + numSkies);

		Sky sky;
		Galaxy[] galaxies;
		
		for (int i = 105; i < 106; i++)
		{
			sky = skies.get(i);
			galaxies = sky.getGalaxies();
			
			for (int x = 0; x < xGridSize; x++)
			{
				for (int y = 0; y < yGridSize; y++)
				{
					gridPoints[x][y].determineLocalEllipticity(galaxies, NUM_CLOSE_GALAXIES);
				}
			}

			plotSkyCountourGrid((i + 1), gridPoints, sky.getHalos());
		}
	}
	
	public static void plotSkyCountourGrid(int skyId, GridPoint[][] skyGrid, double[][] skyHalos)
	{
		final int skyNum = skyId;
		final GridPoint[][] gridPoints = skyGrid;
		final double[][] halos = skyHalos;
		
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				DrawGridField.newGUI(skyNum, gridPoints, halos);
			}
		});
	}
	
	private static double[] findTotalSignal(Sky someSky, double[] darkMatterCoords)
	{
		double[] totalSignal = new double[2];
		double[] galaxySignal = new double[2];
		
		Galaxy[] galaxies = someSky.getGalaxies();
		
		for (int i = 0; i < someSky.getNumGalaxies(); i++)
		{
			galaxySignal = findRelativeEllip(galaxies[i].getCoords(), galaxies[i].getEllipticities(), 
											 darkMatterCoords);
			
			totalSignal[0] += galaxySignal[0];
			totalSignal[1] += galaxySignal[1];
		}
		
		return totalSignal;
	}
	
	private static double[] findRelativeEllip(double[] galaxyCoords, double[] galaxyEllip, 
												double[] darkMatterCoords)
	{
		double doubleAngle = 2 * Math.atan((galaxyCoords[1] - darkMatterCoords[1]) / (galaxyCoords[0] - darkMatterCoords[0]));
		
		double tangEllip = -((galaxyEllip[0] * Math.cos(doubleAngle)) + (galaxyEllip[1] * Math.sin(doubleAngle)));
		double crossEllip = (galaxyEllip[1] * Math.cos(doubleAngle)) - (galaxyEllip[0] * Math.sin(doubleAngle));
		
		return (new double[]{tangEllip, crossEllip});
	}
	
	private static BufferedReader getReader(String filename) throws FileNotFoundException
	{
		FileInputStream fstream = new FileInputStream(filename);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		return br;
	}
	
	private static ArrayList<Galaxy> getSkyGalaxiesFromFile(int skyNum)
	{
		String trainingSkyFilename = "/home/jgonagle/Synced_Documents/Projects/Kaggle_Observing_Dark_Worlds/data/Train_Skies/Training_Sky" +
				 			 skyNum + ".csv";
		
		try 
		{
			BufferedReader trainingSkyFileReader = getReader(trainingSkyFilename);
			
			String strLine;
			String[] strContents;
			
			trainingSkyFileReader.readLine();
			
			ArrayList<Galaxy> galaxies = new ArrayList<Galaxy>();
			Galaxy galaxy;
			
			while ((strLine = trainingSkyFileReader.readLine()) != null)   
			{
				strContents = ((String[]) strLine.split(","));
				
				galaxy = new Galaxy();
				galaxy.setCoords(new double[]{Double.valueOf(strContents[1]), 
					    					   Double.valueOf(strContents[2])});
				galaxy.setEllipticities(new double[]{Double.valueOf(strContents[3]), 
													  Double.valueOf(strContents[4])});
				
				galaxies.add(galaxy);
			}

			return galaxies;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static ArrayList<Sky> getSkysFromFile()
	{
		String trainingHalosFilename = "/home/jgonagle/Synced_Documents/Projects/Kaggle_Observing_Dark_Worlds/data/Training_halos.csv";
		
		try 
		{
			BufferedReader trainingHalosFileReader = getReader(trainingHalosFilename);
			
			String strLine;
			String[] strContents;
			
			trainingHalosFileReader.readLine();
			
			ArrayList<Sky> skies = new ArrayList<Sky>();
			int skyCount = 0;
			Sky sky;
			
			while ((strLine = trainingHalosFileReader.readLine()) != null)   
			{
				strContents = ((String[]) strLine.split(","));
				
				sky = new Sky();
				
				sky.setNumHalos(Integer.valueOf(strContents[1]));				
				sky.setReference(new double[]{Double.valueOf(strContents[2]),
											   Double.valueOf(strContents[3])});
				sky.setGalaxies(getSkyGalaxiesFromFile(skyCount + 1));
				
				for (int i = 0; i < sky.getNumHalos(); i++)
				{
					sky.setHalo(i, (new double[]{Double.valueOf(strContents[i*2+4]),
							   				  	  Double.valueOf(strContents[i*2+5])}));
				}
				
				skies.add(sky);
				skyCount++;
			}
			
			return skies;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
