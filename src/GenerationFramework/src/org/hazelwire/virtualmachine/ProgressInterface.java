package org.hazelwire.virtualmachine;

/**
 * Simple interface for updating the progress of something (currently only used for VMDownloader) in the frontend without
 * connecting the backend with the frontend.
 * @author Tim Strijdhorst
 *
 */
public interface ProgressInterface
{
	public void setProgress(int progress);
	public void setFilePath(String filePath);
}
