package chongwm.demo.hyland.alfresco.search.pojo.json2kt;

import com.google.gson.annotations.SerializedName;

public class Content
{

	@SerializedName("mimeType")
	String mimeType;

	@SerializedName("mimeTypeName")
	String mimeTypeName;

	@SerializedName("sizeInBytes")
	int sizeInBytes;

	@SerializedName("encoding")
	String encoding;

	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}

	public String getMimeType()
	{
		return mimeType;
	}

	public void setMimeTypeName(String mimeTypeName)
	{
		this.mimeTypeName = mimeTypeName;
	}

	public String getMimeTypeName()
	{
		return mimeTypeName;
	}

	public void setSizeInBytes(int sizeInBytes)
	{
		this.sizeInBytes = sizeInBytes;
	}

	public int getSizeInBytes()
	{
		return sizeInBytes;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public String getEncoding()
	{
		return encoding;
	}

	
	
	public final static String MIME_TEXTDoc ="text/plain";
	public final static String MIME_MSWordXDoc ="application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public final static String MIME_MSWordDoc ="application/msword";
	public final static String MIME_RFTDoc ="application/rtf";
	public final static String MIME_PDFDoc ="application/pdf";
	
	
	
	
	
}