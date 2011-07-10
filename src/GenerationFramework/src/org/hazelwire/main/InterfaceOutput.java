package org.hazelwire.main;

/**
 * This class generalises the output structure needed by the backend. It's very basic in that it only allows to print lines but it's necessary for
 * unlinking the backend from the frontend.
 * 
 * @author Tim Strijdhorst
 *
 */
public interface InterfaceOutput
{
	public void println(String message);
	public void print(String message);
	public void setProgress(int progress);
}
