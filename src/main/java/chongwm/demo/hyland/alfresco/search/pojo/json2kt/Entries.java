package chongwm.demo.hyland.alfresco.search.pojo.json2kt;

import com.google.gson.annotations.SerializedName;

   
public class Entries {

   @SerializedName("entry")
   Entry entry;


    public void setEntry(Entry entry) {
        this.entry = entry;
    }
    public Entry getEntry() {
        return entry;
    }
    
}