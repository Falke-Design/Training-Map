package com.falke.training_map.database;

public class Point {
	private int ID;
	private String LAT;
	private String LNG;
	private int ROUTE_ID;


	public Point()
	{
		ID=0;
		LAT="";
		LNG="";
		ROUTE_ID=0;
	}
	public Point(int iD, String lAT, String lNG, int rOUTE_ID) {

		ID=iD;
		LAT=lAT;
		LNG=lNG;
		ROUTE_ID=rOUTE_ID;
	}

	public int getID()
	{
		return ID;
	}
	public void setID(int id)
	{
		ID=id;
	}

	public String getLAT()
	{
		return LAT;
	}
	public void setLAT(String lat)
	{
		LAT=lat;
	}

	public String getLNG()
	{
		return LNG;
	}
	public void setLNG(String lng)
	{
		LNG=lng;
	}

	public int getROUTE_ID()
	{
		return ROUTE_ID;
	}
	public void setROUTE_ID(int route_id)
	{
		ROUTE_ID=route_id;
	}
}	