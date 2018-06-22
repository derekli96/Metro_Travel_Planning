import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

//有一个问题：尽管通过覆盖equals和hashcode方法，使得同名称的Station是相等的，List中也可以这样使用（List.contains方法）。
//但是Map中的key却认为同名的Station是不相等的。所以将所有Map中的key改为StationName（字符串）。
//这样可以实现正确的搜索路径功能，不够要求用户使用时输入站点名称时大小写要精确匹配（SubwayTester的输入参数）。

public class Subway
{
	private List stations;
    private List connections;
    private Map network;  //to store each station, and a list of all the stations that it connects to.
    
    public Subway()
    {
		this.stations = new LinkedList();
		this.connections = new LinkedList();
		this.network = new HashMap();
    }

    public void addStation(String stationName)
    {   
		if(!this.hasStation(stationName))
		{
			Station station = new Station(stationName);
			stations.add(station);
		}
    }

    //Check if a station is already in the subway's stations List.
    public boolean hasStation(String stationName)
    {   
		return stations.contains(new Station(stationName));
    }

    public void addConnection(String station1Name, String station2Name, String lineName, int length)
    {   
		if((this.hasStation(station1Name)) && (this.hasStation(station2Name)))
		{
			Station station1 = new Station(station1Name);
			Station station2 = new Station(station2Name);
			Connection connection = new Connection(station1, station2, lineName, length);
			connections.add(connection);

            //Subways run in two directions, we need to add two connections.
			connections.add(new Connection(station2, station1, lineName, length)); 

			//When adding connections, we need to update the Map of stations.
			addToNetwork(station1Name, station2);
			addToNetwork(station2Name, station1);            
		}
		else
		{
			throw new RuntimeException("Invalid connection!");
		}
    }

    private void addToNetwork(String station1Name, Station station2)
    {   
		//Our Map has each station as its keys. The value for that station is a List containing all the stations that the key station connects to.  
		if(network.containsKey(station1Name))
		{
			List connectionStations = (List)network.get(station1Name);
			if(!connectionStations.contains(station2))
			{
				connectionStations.add(station2);    
			}
		}
		else
		{
			List connectionStations = new LinkedList();
			connectionStations.add(station2);
			network.put(station1Name, connectionStations);      
		}
    }

    public Map getMap()
    {   
		return network;
    }

    //Check if a particular connection exists, given the two station names and the line name for that connection.
    public boolean hasConnection(String station1Name, String station2Name, String lineName)
    {
		Station station1 = new Station(station1Name);
		Station station2 = new Station(station2Name);

		for(Iterator i = connections.iterator(); i.hasNext();)
		{
			Connection connection = (Connection)i.next();
			if(connection.getLineName().equalsIgnoreCase(lineName))
			{
				if((connection.getStation1().equals(station1)) && (connection.getStation2().equals(station2)))
				{
					return true;
				}
			}
		}
	
		return false;        
    }
	
	public boolean hasConnection(String station1Name, String station2Name)
    {
		Station station1 = new Station(station1Name);
		Station station2 = new Station(station2Name);

		for(Iterator i = connections.iterator(); i.hasNext();)
		{
			Connection connection = (Connection)i.next();
			if((connection.getStation1().equals(station1)) && (connection.getStation2().equals(station2)))
				{
					return true;
				}
		}
	
		return false;        
    }

    //Check if a particular network exists, given the key station name and the related value List.
    public boolean hasNetwork(String stationName, List valueStations)
    {
		if(network.keySet().contains(stationName))
		{
			List connectingStations = (List)network.get(stationName);
			if(connectingStations.size() == valueStations.size())
			{
				for(Iterator k = valueStations.iterator(); k.hasNext();)
				{
					if(!connectingStations.contains(k.next()))
					{
						return false;
					}
				}
		
				return true;
			}
		}
	
		return false;
    }

    public List getDirections_LeastStation(String startStationName, String endStationName)//求经过站点数最少的路径
    {   
		//To verify the starting and ending stations both exist.
		if(!this.hasStation(startStationName) || !this.hasStation(endStationName))
		{
			throw new RuntimeException("Stations entered do not exist on this subway.");
		}

        //This method is based on a well-known bit of code called Dijkstra's algorithm, which figures out the shortest path between two nodes on a graph.
		Station start = new Station(startStationName);
		Station end = new Station(endStationName);
		List route = new LinkedList();
		List reachableStations = new LinkedList();  
		List unreachStations=new LinkedList();
		Map previousStations = new HashMap(); 
		int totalLength=0;

        //This first part of the code handles the case when the end station is just one connection away from the starting station.
		List neighbors = (List)network.get(startStationName);  

		for(Iterator i = neighbors.iterator(); i.hasNext();)
		{
			Station station = (Station)i.next();
			if(station.equals(end))
			{
				route.add(getConnection(start, end));
				return route;
			}
			else
			{
				//reachableStations.add(station); 
				previousStations.put(station.getName(), start);
				Connection connection=getConnection(start, station);
			}
		}

		List nextStations = new LinkedList();
		nextStations.addAll(neighbors);
		Station currentStation = start;
		reachableStations.add(currentStation); 
		unreachStations.addAll(stations);
		unreachStations.remove(start);
	    int minLength=100000;
		Station chosenStation;

		//These loops begin to iterate through each set of stations reachable by the starting station, 
		//and tries to find the least number of stations possible to connect the starting point and the destination.
		searchLoop:
		for(int i=1; i<stations.size(); i++)
		{
			List tmpNextStations = new LinkedList();

			for(Iterator j = nextStations.iterator(); j.hasNext();)
			{
				Station station = (Station)j.next();
				reachableStations.add(station);                
				currentStation = station;
				List currentNeighbors = (List)network.get(currentStation.getName());

				for(Iterator k = currentNeighbors.iterator(); k.hasNext();)
				{
					Station neighbor = (Station)k.next();
					if (!reachableStations.contains(neighbor))
					{
						if(neighbor.equals(end))
						{
							reachableStations.add(neighbor);
							previousStations.put(neighbor.getName(), currentStation);
							break searchLoop;
						}
						else
						{
							reachableStations.add(neighbor);
							tmpNextStations.add(neighbor);
							previousStations.put(neighbor.getName(), currentStation);
						}
					}
				}
			}

			nextStations = tmpNextStations;
		}
				

		//We've found the path by now.
		boolean keepLooping = true;
		Station keyStation = end;
		Station station;

		//Once we've got a path, we just "unwind" the path, and create a List of connections to get from the starting station to the destination station.
		while(keepLooping)
		{
			station = (Station)previousStations.get(keyStation.getName());
			route.add(0, getConnection(station, keyStation));
			if(start.equals(station))
			{
				keepLooping = false;
			}
			keyStation = station;
		}

		return route;
    }  

    public List getDirections_LeastKm(String startStationName, String endStationName)//求实际距离最短的路径
    {   
		//To verify the starting and ending stations both exist.
		if(!this.hasStation(startStationName) || !this.hasStation(endStationName))
		{
			throw new RuntimeException("Stations entered do not exist on this subway.");
		}

		Station start = new Station(startStationName);
		Station end = new Station(endStationName);
		List route = new LinkedList();
		List reachableStations = new LinkedList();  
		List unreachStations=new LinkedList();
		Map previousStations = new HashMap(); 
	    Map destLength=new HashMap();
		
		//Initialize the dest[]
		for(Iterator i = stations.iterator(); i.hasNext();)
		{
			Station station = (Station)i.next();
			destLength.put(station.getName(),100000);
		}

        //Judge whether the destination is one station away from the start
		//Initialize previousStations
		List neighbors = (List)network.get(startStationName);  
		for(Iterator i = neighbors.iterator(); i.hasNext();)
		{
			Station station = (Station)i.next();
			if(station.equals(end))
			{
				route.add(getConnection(start, end));
				return route;
			}
			else
			{
				previousStations.put(station.getName(), start);
				Connection connection=getConnection(start, station);
			}
		}

		List nextStations = new LinkedList();
		Station currentStation = start;
		reachableStations.add(currentStation); 
		unreachStations.addAll(stations);
		unreachStations.remove(start);
	    int minLength=100000;
		Station chosenStation;
		
		//Input the initial value to dest[] for iteration
		for(Iterator i = unreachStations.iterator(); i.hasNext();){
			Station station=(Station)i.next();
			if(hasConnection(start.getName(),station.getName())){
				Connection connect = (Connection) getConnection(start,station);
				destLength.remove(station.getName());
				destLength.put(station.getName(),connect.getLength());
			}			
		}
		
		//Dijkstra
		while(!unreachStations.isEmpty()){
			Iterator temp=unreachStations.iterator();
			chosenStation=(Station)temp.next();
			minLength=(int)destLength.get(chosenStation.getName());
			for(Iterator i = unreachStations.iterator(); i.hasNext();){
			    Station station=(Station)i.next();
				int length=(int)destLength.get(station.getName());
				if(length<minLength){
					minLength=length;
					chosenStation=station;
				}				
			}
			if(chosenStation.equals(end)){
					break;
			}
			else{
				reachableStations.add(chosenStation);
				unreachStations.remove(chosenStation);
				for(Iterator j = unreachStations.iterator(); j.hasNext();){
					Station unStation=(Station)j.next();
					if(hasConnection(chosenStation.getName(),unStation.getName())){
						Connection tempConnection=(Connection)getConnection(chosenStation,unStation);
						if((int)destLength.get(unStation.getName())>(int)destLength.get(chosenStation.getName())+tempConnection.getLength()){
							destLength.remove(unStation.getName());
							int newLength=(int)destLength.get(chosenStation.getName())+tempConnection.getLength();
							destLength.put(unStation.getName(),newLength);
							previousStations.remove(unStation);
							previousStations.put(unStation.getName(), chosenStation);
						}
					}
				}
			}
		}
		

		//We've found the path by now.
		boolean keepLooping = true;
		Station keyStation = end;
		Station station;

		//Once we've got a path, we just "unwind" the path, and create a List of connections to get from the starting station to the destination station.
		while(keepLooping)
		{
			station = (Station)previousStations.get(keyStation.getName());
			route.add(0, getConnection(station, keyStation));
			if(start.equals(station))
			{
				keepLooping = false;
			}
			keyStation = station;
		}

		return route;
    }

	public List getDirections_LeastTransfer(String startStationName, String endStationName)//求换乘次数最少的路径
    {   
		//To verify the starting and ending stations both exist.
		if(!this.hasStation(startStationName) || !this.hasStation(endStationName))
		{
			throw new RuntimeException("Stations entered do not exist on this subway.");
		}

        //This method is based on a well-known bit of code called Dijkstra's algorithm, which figures out the shortest path between two nodes on a graph.
		Station start = new Station(startStationName);
		Station end = new Station(endStationName);
		List finalRoute = new LinkedList();
		List route = new LinkedList(); //Record searched route
		int transferTime= 100; 
		List startList = new LinkedList();
		startList.add(start);
		route.add(startList);
		
		while(!route.isEmpty()){
			List updateRoute = new LinkedList();
			for (Iterator i = route.iterator(); i.hasNext();){
				List currentRoute = (List)i.next();
				Station lastStation = (Station)currentRoute.get(currentRoute.size()-1);
				List neighbors = (List)network.get(lastStation.getName());
				for (Iterator j = neighbors.iterator(); j.hasNext();){
					Station station = (Station)j.next();
					List newRoute = new LinkedList(currentRoute);
					if(!newRoute.contains(station)){
						newRoute.add(station);
						if(newRoute.contains(end)){
							int currentTransfer = transferTimes(newRoute);
							if(currentTransfer < transferTime){
								transferTime = currentTransfer;
								finalRoute = new LinkedList(newRoute);
							}
						}
						else{
							int currentTransfer = transferTimes(newRoute);
							if(currentTransfer < transferTime){
								updateRoute.add(newRoute);
							}
						}
					}	
				}
			}
			route = new LinkedList(updateRoute);
		}
		
		//We've found the path by now.
		Station keyStation = end;
		Station station;
		//Once we've got a path, we just "unwind" the path, and create a List of connections to get from the starting station to the destination station.
			for (int i = finalRoute.size()-1; i>=0;)
			{
				station = (Station)finalRoute.get(i);
				while(getConnection(keyStation, station) == null)
				{
					i--;
					station = (Station)finalRoute.get(i);
				}
				route.add(0, getConnection(station, keyStation));
				keyStation = station;
				i--;
			}

		return route;
	}
        /*
        //This first part of the code handles the case when the end station is just one connection away from the starting station.
		List neighbors = (List)network.get(startStationName);  

		for(Iterator i = neighbors.iterator(); i.hasNext();)
		{
			Station station = (Station)i.next();
			if(station.equals(end))
			{
				route.add(getConnection(start, end));
				return route;
			}
		}

		List nextStations = new LinkedList();
		nextStations.addAll(neighbors);
		Station currentStation = start;
		reachableStations.add(currentStation); 
		unreachStations.addAll(stations);
		unreachStations.remove(start);
	    int minTransfer=1000;
		int transferIndex;
		Station chosenStation;

		for(Iterator i = unreachStations.iterator(); i.hasNext();){
			Station station=(Station)i.next();
			if(hasConnection(start.getName(),station.getName())){
                station.setTransfer(0);
				List previousList=new LinkedList();
				previousList.add(start);
				previousStations.put(station.getName(),previousList);
			}
			else{
				station.setLength(1000);
				List previousList=new LinkedList();
				previousStations.put(station.getName(),previousList);
			}
			
		}

		while(!unreachStations.isEmpty()){
			for(Iterator i = unreachStations.iterator(); i.hasNext();){
			    Station station=(Station)i.next();
				minTransfer=1000;
				if(station.getTransfer()<minTransfer){
					minTransfer=station.getTransfer();
				}				
			}
	        System.out.println(minTransfer);
			List currentMinStation=new LinkedList();
			
			for(Iterator i = unreachStations.iterator(); i.hasNext();){
			    Station station=(Station)i.next();
				if(station.getTransfer()==minTransfer){
					currentMinStation.add(station);
				}				
			}
		if(reachableStations.contains(end)){
			    break;
		    }
		else{
			for(Iterator k =currentMinStation.iterator(); k.hasNext();){
				chosenStation=(Station)k.next();
				for(Iterator j = unreachStations.iterator(); j.hasNext();){
					transferIndex=1;
					Station unStation=(Station)j.next();
					if(hasConnection(chosenStation.getName(),unStation.getName())){
						Connection newConnect=(Connection)getConnection(chosenStation,unStation);
						String newLineName=newConnect.getLineName();
						List previous=(List)previousStations.get(chosenStation.getName());
						if(!previous.isEmpty()){
							for(Iterator m =previous.iterator(); m.hasNext();){
								Station mStation=(Station)m.next();
								Connection mConnect=(Connection)getConnection(chosenStation,mStation);
								if(newLineName.equals(mConnect.getLineName())){
									transferIndex=0;
								}
							}								
						}
						else{
							transferIndex=0;
						}
						if(unStation.getTransfer()>=(chosenStation.getTransfer()+transferIndex)){
							unStation.setTransfer(chosenStation.getTransfer()+transferIndex);
							if(unStation.getTransfer()>(chosenStation.getTransfer()+transferIndex)){
								previousStations.remove(unStation.getName());
								List mList=new LinkedList();
								mList.add(chosenStation);
								previousStations.put(unStation.getName(),mList);
							}
							else{
								List mList=(List)previousStations.get(unStation.getName());
								mList.add(chosenStation);
							}
						}
					}
				}
			}
				for(Iterator k =currentMinStation.iterator(); k.hasNext();){
					unreachStations.remove((Station)k.next());
				}
				reachableStations.addAll(currentMinStation);
			}
		}

		//We've found the path by now.
		boolean keepLooping = true;
		boolean findSameLine=false;
		Station keyStation = end;
		List stationList=new LinkedList();
		String preLine="";
        List routeList=new LinkedList();
		
		routeList.add(end);

		stationList = (List)previousStations.get(keyStation.getName());
		Iterator y =stationList.iterator(); y.hasNext();
		Station station123=(Station)y.next();

		while(keepLooping)
		{
			stationList = (List)previousStations.get(keyStation.getName());
			Station station=end;
			if(preLine.equals("")){
				Iterator i =stationList.iterator(); i.hasNext();
				station=(Station)i.next();
				preLine=(getConnection(keyStation,station)).getLineName();
			}
			else{
				for(Iterator k =stationList.iterator(); k.hasNext();){
				    Station kStation=(Station)k.next();
					if((getConnection(keyStation,kStation)).getLineName().equals(preLine)&&(!routeList.contains(kStation))){
						findSameLine=true;
						station=kStation;
					}
					else{
						boolean judge=false;
						Iterator i =stationList.iterator(); i.hasNext();
						while(!judge){
							station=(Station)i.next();
							if(!routeList.contains(station)){
								preLine=(getConnection(keyStation,station)).getLineName();
						        findSameLine=false;
							    judge=true;
							}
						}

					}
				
			    }
			}
            System.out.println(station.getName());
			routeList.add(station);
			route.add(0, getConnection(station, keyStation));
			if(start.equals(station))
			{
				keepLooping = false;
			}
			keyStation = station;
		}

		return route;
    }
	*/
	public int transferTimes(List route)
	{
		int count = 0;
		Station station1, station2, station3;
		for (int i = 0; i < route.size()-2; i++)
		{
			station1 = (Station)route.get(i);
			station2 = (Station)route.get(i+1);
			station3 = (Station)route.get(i+2);
			if(!getConnection(station1, station2).getLineName().equalsIgnoreCase(getConnection(station2, station3).getLineName()))
				count++;
		}
		return count;
	}
	
    //This is a utility method that takes two stations, and looks for a connection between them (on any line).
    private Connection getConnection(Station station1, Station station2)
    {
		
		for(Iterator i = connections.iterator(); i.hasNext();)
		{
			Connection connection = (Connection)i.next();
			Station one = connection.getStation1();
			Station two = connection.getStation2(); 
			if((station1.equals(one)) && (station2.equals(two)))
			{
				return connection;
			}
		}
		
		return null;
    }
}