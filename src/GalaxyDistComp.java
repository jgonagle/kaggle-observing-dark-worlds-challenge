import java.util.Comparator;


public class GalaxyDistComp implements Comparator<Galaxy>
{
	private final double[] coords;
	
	public GalaxyDistComp(double[] coords)
	{
		this.coords = coords;
	}
	
	@Override
	public int compare(Galaxy galaxy1, Galaxy galaxy2) 
	{
		double galaxy1Dist = getDistance(galaxy1.getCoords());
		double galaxy2Dist = getDistance(galaxy2.getCoords());
		
		if (galaxy1Dist < galaxy2Dist)
		{
			return -1;
		}
		else if (galaxy1Dist > galaxy2Dist)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}

	public double getDistance(double[] galaxyCoords)
	{
		return Math.sqrt(Math.pow((coords[0] - galaxyCoords[0]), 2) +
						  Math.pow((coords[1] - galaxyCoords[1]), 2));
	}	
}
