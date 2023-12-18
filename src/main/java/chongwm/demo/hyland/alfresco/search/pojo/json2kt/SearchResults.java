package chongwm.demo.hyland.alfresco.search.pojo.json2kt;


import com.google.gson.annotations.SerializedName;

   
public class SearchResults 
{
   @SerializedName("list")
   SearchResultsList searchResultsList;


    public void setList(SearchResultsList searchResultsList) {
        this.searchResultsList = searchResultsList;
    }
    public SearchResultsList getList() {
        return this.searchResultsList;
    }
    
}