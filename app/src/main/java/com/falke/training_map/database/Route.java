package com.falke.training_map.database;

public class Route {
	private int ID;
	private String BEZ;
	
	public Route()
	{
		ID=0;
		BEZ="";
	}
	public Route(int iD, String bEZ) {
		
		ID=iD;
		BEZ=bEZ;
	}

	public int getID()
	{
		return ID;
	}
	public void setID(int id)
	{
		ID=id;
	}

	public String getBEZ() {
		return BEZ;
	}
	public void setBEZ(String bEZ) {
		BEZ = bEZ;
	}
}	