/**
 * 
 */
package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

/**
 * @author ysenyuta
 *
 */
public class GooglePlacesWrapper {

	/**
	 * @param args - first element of args is the file name in the same directory as this executable
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, MalformedURLException {
		
		String inputFile = "input.txt";
		String outputFile = "output.txt";
		// see if we're overriding default filenames
		if (args.length < 1) {
			System.out.println("No filenames specified as parameters; defaulting to input "
					+ "filename " + inputFile + " and output filename " + outputFile);
		}
		else if (args.length == 1) {
			inputFile = args[0];
			System.out.println("Input filename specified: " + inputFile + " and defaulting "
					+ "to " + outputFile);
		}
		else {
			inputFile = args[0];
			outputFile = args[1];
			System.out.println("Input filename specified: " + inputFile + " and output filename specified: " + outputFile);
		}
		
		// get urls from input file
		LinkedList<String> list = urlFromFile(inputFile);
		// set up API
		GooglePlaces client = new GooglePlaces(GoogleAPIKey.getKey());
		// set up output file
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		
		// perform the tagging with google results
		tag(list, writer, client);
		
		writer.close();
	}
	
	private static LinkedList<String> urlFromFile(String filename)
    {
    	LinkedList<String> list = new LinkedList<String>();
    	
    	FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		while (scan.hasNextLine()) {
			list.add(scan.nextLine().toLowerCase());
		}
		try {
			scan.close();
			file.close();
		} catch (IOException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}
		
		return list;
    }
    
	private static void tag(LinkedList<String> list, PrintWriter writer, GooglePlaces client) throws MalformedURLException {
		
	    for (String url : list)
        {
	    	System.out.println("Trying url: " + url);
	    	
	        String h;
	        
	        try{
	        	h = new URL(url).getHost();
	        }
	        catch (MalformedURLException e) {
	        	String protocol = "http";
	        	if (!url.contains(protocol)) {
	        		h = new URL(protocol + "://" + url).getHost();
	        	}
	        	else throw e;
	        }
	        
	        writer.print(url);
	    	
	    	// query the places by the host of the url
        	List<Place> places = client.getPlacesByQuery(h, 1);
        	
        	if (!places.isEmpty())
        	{
        		// use the top result
	        	Place top = (Place) places.get(0);
	        	// write the address of the place
	        	String address = top.getAddress();
	        	
	        	// can perform checks here such as:
	        	//if ((!address.isEmpty() && address.contains("Canada")) || address.isEmpty())
	        	{
		        	String name = top.getName();
		        	writer.print("\t" + name);
		        	
		        	writer.print("\t" + address);
		        	
		        	writer.print("\t");
		        	Iterator<String> it = top.getTypes().iterator();
					while (it.hasNext())
					{
						writer.print(it.next());
						if (it.hasNext())
							writer.print(", ");
						
					}
					
					writer.print("\t" + top.getLatitude() + "\t" + top.getLongitude());
	        	}	        	
        	}
	        	
        	writer.print("\n");
        	writer.flush();
	        	
	    }
	}

}
