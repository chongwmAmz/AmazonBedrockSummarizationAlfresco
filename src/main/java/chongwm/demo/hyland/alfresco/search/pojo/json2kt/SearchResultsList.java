package chongwm.demo.hyland.alfresco.search.pojo.json2kt;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class SearchResultsList {

   @SerializedName("pagination")
   Pagination pagination;

   @SerializedName("context")
   Context context;

   @SerializedName("entries")
   java.util.List<Entries> entries;


    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setContext(Context context) {
        this.context = context;
    }
    public Context getContext() {
        return context;
    }
    
    public void setEntries(List<Entries> entries) {
        this.entries = entries;
    }
    public List<Entries> getEntries() {
        return entries;
    }
    
}