package com.programmerdan.minecraft.devotion.helpers;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.programmerdan.minecraft.devotion.Devotion;

public class ResourceHelper {
	public static String readTextFile(String resourcePath) {
    	File file = new File(Devotion.class.getClassLoader().getResource(resourcePath).getFile());
    	StringBuilder result = new StringBuilder("");
    	
    	try (Scanner scanner = new Scanner(file)) {

    		while (scanner.hasNextLine()) {
    			String line = scanner.nextLine();
    			result.append(line).append("\n");
    		}

    		scanner.close();

    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return result.toString();
	}
}
