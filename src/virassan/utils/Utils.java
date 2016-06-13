package virassan.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Nifty Utility Stuff
 * @author Virassan
 *
 */
public class Utils {

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String loadFileAsString(String path){
		StringBuilder builder = new StringBuilder();
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line;
			while((line = br.readLine()) != null)
				builder.append(line+ "\n");
			br.close();
		}catch(IOException e){
			e.printStackTrace();
			//TODO: error message
		}
		return builder.toString();
	}
	
	public static int parseInt(String number){
		try{
			return Integer.parseInt(number);
		}catch(NumberFormatException e){
			e.printStackTrace();
			//TODO: error message
			return 0;
		}
	}
	
	/**
	 * Keeps the variable given from going below the min or above the max
	 * @param var the variable to be checked
	 * @param min the minimum that variable can be
	 * @param max the maxiumum that variable can be
	 * @return if above the max, returns the max, if below the min, returns the min, if neither it returns the variable unchanged
	 */
	public static int clamp(int var, int min, int max){
		if(var >= max)
			return var = max;
		else if(var <= min)
			return var = min;
		else
			return var;
	}

	public static float clamp(float var, float min, float max){
		if(var >= max)
			return var = max;
		else if(var <= min)
			return var = min;
		else
			return var;
	}
	
}
