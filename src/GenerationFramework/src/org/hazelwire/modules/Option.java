package org.hazelwire.modules;

public class Option
{
	private int id;
	String name, type, value, defaultValue;
	
	public Option(String name, String type, String defaultValue)
	{
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = this.defaultValue;
	}
	
	public Option(String name, String defaultValue)
	{
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = this.defaultValue;
	}
	
	public Option()
	{
		//empty constructor, client has to make sure that all the data has been filled for usage...
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}
}
