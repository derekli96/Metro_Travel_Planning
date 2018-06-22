import java.io.*;

public class SubwayLoader
{
	private Subway subway;

    public SubwayLoader()
    {
		this.subway = new Subway();
    }

    public Subway loadFromFile(File subwayFile) throws IOException
    {
		BufferedReader reader = new BufferedReader(new FileReader(subwayFile));

		loadStations(subway, reader);

		String lineName = reader.readLine();
		while((lineName != null) && (lineName.length() > 0))
		{
			loadLine(subway, reader, lineName);
			lineName = reader.readLine();
		}
	
		return subway;          
    }

    private void loadStations(Subway subway, BufferedReader reader) throws IOException
    {   
		String currentStation;
		currentStation = reader.readLine();

		while(currentStation.length() > 0)
		{
			subway.addStation(currentStation);
			currentStation = reader.readLine();
		}
    }

    private void loadLine(Subway subway, BufferedReader reader, String lineName) throws IOException
    {   
		String station1Name, station2Name, tempLength;
		int length=0;
		station1Name = reader.readLine();
		tempLength = reader.readLine();
		station2Name = reader.readLine();
		while((tempLength!=null)  &&(!tempLength.equals(""))&&(station2Name.length() > 0))
		{
			length=Integer.parseInt(tempLength);
			subway.addConnection(station1Name, station2Name, lineName, length);
			station1Name = station2Name;
			tempLength = reader.readLine();
			
			if(((tempLength!=null)&&!(tempLength.equals(""))))
			{
				station2Name = reader.readLine();
			}		
		}
    } 
}