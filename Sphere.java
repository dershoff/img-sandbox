package de_test;

import net.imglib2.Point;

public class Sphere {
	private int x;
	private int y;
	private int z;
	private int r;
	private boolean drawn;
	
	public Sphere( int x, int y, int z, int r ) {
		this.x = x;
		this.y = y;	
		this.z = z;
		this.r = r;
		this.drawn = false;
	}
	
	public boolean isDrawn() {
		return this.drawn;
	}
	public void setDrawn() {
		this.drawn = true;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public int getZ() {
		return this.z;
	}
	public int getR() {
		return this.r;
	}
	public String toString() {
		return "" + this.x + ", " + this.y + " " + this.z + " " + this.r; 
	}
}
