import java.io.*;
import java.util.List;

public class SubwayPrice{

private int length;

public SubwayPrice(){
	this.length=0;
}

public int totalLength(List route){
    
	for(int i=0; i<route.size(); i++){
		Connection conneciton = (Connection)route.get(i);
		length+=conneciton.getLength();
	}
    return length;
}

public int price(int length){
		int price=3;
		if(length<=6000){
		    price=3;
		}
		else if(length>6000&&length<=12000){
			price=4;
		}			
		else if(length>12000){
			if(length<=32000){
				price=5+1*(int)((length-12000)/10000);
			}
			if(length>32000){
				price=7+1*(int)((length-32000)/20000);
			}
			
		}
		return price;	
	}
}