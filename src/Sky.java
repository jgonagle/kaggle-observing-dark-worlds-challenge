import java.util.ArrayList;


public class Sky 
{
	private int numHalos;
	private int numGalaxies;
	
	private double[][] halos;	
	private Galaxy[] galaxies;

	private double[] refCoords;
	
	public Sky(){}
	
	public void setNumHalos(int num)
	{
		numHalos = num;
		halos = new double[numHalos][2];
	}
	
	public void setHalo(int haloNum, double[] coords)
	{
		halos[haloNum] = coords;
	}
	
	public void setGalaxies(ArrayList<Galaxy> galaxyList)
	{
		numGalaxies = galaxyList.size();
		galaxies = new Galaxy[numGalaxies];
		
		for (int i = 0; i < numGalaxies; i++)
		{
			galaxies[i] = galaxyList.get(i);
		}
	}
	
	public void setReference(double[] refCoords)
	{
		this.refCoords = refCoords;
	}
	
	public int getNumHalos()
	{
		return numHalos;
	}
	
	public int getNumGalaxies()
	{
		return numGalaxies;
	}
	
	public double[][] getHalos()
	{
		return halos;
	}
	
	public Galaxy[] getGalaxies()
	{
		return galaxies;
	}
	
	public double[] getRefCoords()
	{
		return refCoords;
	}
}
