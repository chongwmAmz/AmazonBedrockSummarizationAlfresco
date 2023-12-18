package chongwm.demo.hyland.alfresco.search.pojo.json2kt;

import com.google.gson.annotations.SerializedName;

   
public class ModifiedByUser {

   @SerializedName("id")
   String id;

   @SerializedName("displayName")
   String displayName;


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
    
}