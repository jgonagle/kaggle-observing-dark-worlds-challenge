public class Galaxy 
{
	private double[] coords;
	private double[] ellipticities;
	
	public Galaxy(){}
	
	public void setCoords(double[] coords)
	{
		this.coords = coords;
	}
	
	public void setEllipticities(double[] ellipticities)
	{
		this.ellipticities = ellipticities;
	}
	
	public double[] getCoords()
	{
		return coords;
	}
	
	public double[] getEllipticities()
	{
		return ellipticities;
	}
}
