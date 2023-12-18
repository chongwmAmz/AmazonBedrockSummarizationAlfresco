package chongwm.demo.hyland.alfresco.search.pojo.json2kt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;

public class TestJig
{
	public static void main (String args[]) throws IOException
	{
        Gson gson = new Gson();

        
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\chongwm\\eclipse-workspace\\summarization\\src\\main\\java\\org\\chongwm\\hyland\\alfresco\\search\\pojo\\json2kt\\sampleSearchResultsList.json"));
        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonString.append(line);
        }
        reader.close();
        
        
        
        SearchResults sr = gson.fromJson(jsonString.toString(), SearchResults.class);
        SearchResultsList list=  sr.getList();
        System.out.println(list.entries.size());
	}

}
