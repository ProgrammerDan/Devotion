package com.programmerdan.minecraft.devotion.dao;

public interface GenericDAO<T extends Flyweight> {
	public T findLast();
	public T findAndRemoveLast();
	public void insert(T val);
	public void removeLast();
}
