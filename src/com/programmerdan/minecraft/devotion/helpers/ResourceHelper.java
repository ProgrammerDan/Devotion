package com.programmerdan.minecraft.devotion.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.programmerdan.minecraft.devotion.Devotion;

public class ResourceHelper {
	public static ArrayList<String> readScript(String resourcePath) {
		InputStream stream = Devotion.class.getResourceAsStream(resourcePath);
    	StringBuilder script = new StringBuilder("");
    	ArrayList<String> list = new ArrayList<String>(); 
    	
    	try {
    		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    		String line;
    		
            while ((line = reader.readLine()) != null) { 
            	if(line.endsWith(";")) {
            		script.append(line.substring(0, line.length() - 1));
            		list.add(script.toString());
            		script.delete(0, script.length());
            	}
            	else {
                	script.append(line);
                	script.append("\n");
            	}
            }
            
            if(script.length() > 0) {
            	list.add(script.toString());
            }
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return list;
	}
}
