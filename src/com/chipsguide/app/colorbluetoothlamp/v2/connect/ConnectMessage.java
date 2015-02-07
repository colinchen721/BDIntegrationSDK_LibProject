package com.chipsguide.app.colorbluetoothlamp.v2.connect;

public class ConnectMessage {
	
	public static final int TYPE_ITEM = 0;
	public static final int TYPE_SEPARATOR = 1;
	
	private int type ;
	private String bluetoothname;
	private String address;
	
	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	public String getBluetoothname() 
	{
		return bluetoothname;
	}
	public void setBluetoothname(String bluetoothname)
	{
		this.bluetoothname = bluetoothname;
	}
	public String getAddress()
	{
		return address;
	}
	public void setAddress(String address)
	{
		this.address = address;
	}
	
}
