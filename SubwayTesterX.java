import java.util.List;
import java.io.File;

public class SubwayTesterX
{
	public static void main(String[] args)
    {
		if(args.length != 3)
		{
			System.err.println("Usage: SubwayTester [SubwayMapFile] [startStation] [endStation]");
			System.exit(-1);
		}

		try
		{
			SubwayLoader loader = new SubwayLoader();
			Subway objectville = loader.loadFromFile(new File(args[0]));
            int status=1;
			
			if(!objectville.hasStation(args[1]))
			{
				System.err.println(args[1] + " is not a station in Objectville.");
				System.exit(-1);
			}
			else if (!objectville.hasStation(args[2]))
			{
				System.err.println(args[2] + " is not a station in Objectville.");
			System.exit(-1);
			}

			SubwayPrinter printer = new SubwayPrinter(System.out);
			
			List route1 = objectville.getDirections_LeastStation(args[1], args[2]);
			status=1;
			printer.printDirections(route1,status);
			
			List route2 = objectville.getDirections_LeastKm(args[1], args[2]);
			status=2;
			printer.printDirections(route2,status);
			
			List route3 = objectville.getDirections_LeastTransfer(args[1], args[2]);
		    status=3;
			printer.printDirections(route3,status);
			

		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}    
    }
}