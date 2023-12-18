package chongwm.demo.hyland.alfresco.search.pojo.json2kt;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Properties
{

	@SerializedName("crestBedrock:generateSummary")
	boolean crestBedrock_generateSummary;

	@SerializedName("crestBedrock:summaryTime")
	Date crestBedrock_summaryTime;

	@SerializedName("crestBedrock:prompt")
	String crestBedrock_prompt;

	@SerializedName("cm:versionType")
	String cm_versionType;

	@SerializedName("cm:versionLabel")
	String cm_versionLabel;

	@SerializedName("crestBedrock:fm")
	String crestBedrock_fm;

	@SerializedName("crestBedrock:summary")
	String crestBedrock_summary;

	@SerializedName("crestBedrock:responseLength")
	int crestBedrock_responseLength;
	
	@SerializedName("crestBedrock:temperature")
	float crestBedrock_temperature;

	public void setCrestBedrock_generateSummary(boolean crestBedrock_generateSummary)
	{
		this.crestBedrock_generateSummary = crestBedrock_generateSummary;
	}

	public boolean getCrestBedrock_generateSummary()
	{
		return crestBedrock_generateSummary;
	}

	public void setCrestBedrock_summaryTime(Date crestBedrock_summaryTime)
	{
		this.crestBedrock_summaryTime = crestBedrock_summaryTime;
	}

	public Date getCrestBedrock_summaryTime()
	{
		return crestBedrock_summaryTime;
	}

	
	public void setCrestBedrock_summary(String crestBedrock_summmary)
	{
		this.crestBedrock_summary = crestBedrock_summmary;
	}

	public String getCrestBedrock_summary()
	{
		return crestBedrock_summary;
	}	
	
	
	
	
	
	public void setCrestBedrock_prompt(String crestBedrock_prompt)
	{
		this.crestBedrock_prompt = crestBedrock_prompt;
	}

	public String getCrestBedrock_prompt()
	{
		return crestBedrock_prompt;
	}

	public void setCm_versionType(String cm_versionType)
	{
		this.cm_versionType = cm_versionType;
	}

	public String getCm_versionType()
	{
		return cm_versionType;
	}

	public void setCm_versionLabel(String cm_versionLabel)
	{
		this.cm_versionLabel = cm_versionLabel;
	}

	public String getCm_versionLabel()
	{
		return cm_versionLabel;
	}

	public void setCrestBedrock_fm(String crestBedrock_fm)
	{
		this.crestBedrock_fm = crestBedrock_fm;
	}

	public String getCrestBedrock_fm()
	{
		return crestBedrock_fm;
	}

	public void setCrestBedrock_responseLength(int crestBedrock_responseLength)
	{
		this.crestBedrock_responseLength = crestBedrock_responseLength;
	}

	public int getCrestBedrock_responseLength()
	{
		return crestBedrock_responseLength;
	}
	
	public void setCrestBedrock_temperature(int crestBedrock_temperature)
	{
		this.crestBedrock_temperature = crestBedrock_temperature;
	}

	public float getCrestBedrock_temperature()
	{
		return crestBedrock_temperature;
	}
	

}