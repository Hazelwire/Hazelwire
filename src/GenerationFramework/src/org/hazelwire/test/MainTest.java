package org.hazelwire.test;

import org.hazelwire.main.FileName;
import org.hazelwire.main.Generator;



public class MainTest {

	public static void main(String[] args) throws Exception
	{
		FileName bla = new FileName("output/test.ova",Generator.getInstance().getFileSeperator(),'.');
		bla.setFileName("KANKER");
	}
}