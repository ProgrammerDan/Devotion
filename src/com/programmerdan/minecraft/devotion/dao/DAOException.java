package com.programmerdan.minecraft.devotion.dao;

import java.io.IOException;

public class DAOException extends RuntimeException {

	private static final long serialVersionUID = 2122426584787399467L;

	public DAOException(String message) {
		super(message);
	}

	public DAOException(String string, IOException e) {
		super(string, e);
	}

}
