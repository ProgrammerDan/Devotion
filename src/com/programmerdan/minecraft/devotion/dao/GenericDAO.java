package com.programmerdan.minecraft.devotion.dao;

import java.util.List;

public interface GenericDAO<T extends Flyweight> {
	public void insert(T val);
	public List<T> findAll();
}
