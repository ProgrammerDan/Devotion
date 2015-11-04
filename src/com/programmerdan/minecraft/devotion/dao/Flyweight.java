package com.programmerdan.minecraft.devotion.dao;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Placeholder interface to manage Flyweight DAO insertion stubs. Since this project is (mostly) one direction
 * IO, this interface simply allows easy cataloguing and potentially future expansion.
 *
 * @author ProgrammerDan <programmerdan@gmail.com>
 * @since 1.0
 */
public interface Flyweight {

	public void serialize(OutputStream os);

	//public static Flyweight deserialize(InputStream os);
}
