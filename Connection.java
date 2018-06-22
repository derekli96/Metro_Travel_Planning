public class Connection
{
	private Station station1, station2;
    private String lineName;
	private int length;
	

    public Connection(Station station1, Station station2, String lineName, int length)
    {
		this.station1 = station1;
		this.station2 = station2;
		this.lineName = lineName;
		this.length = length;
    }

    public Station getStation1()
    {   
		return station1;
    }

    public Station getStation2()
    {   
		return station2;
    }

    public String getLineName()
    {   
		return lineName;
    }
	
	public int getLength()
    {   
		return length;
    }
	
}