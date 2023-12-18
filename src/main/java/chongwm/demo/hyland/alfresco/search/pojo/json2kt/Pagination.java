package chongwm.demo.hyland.alfresco.search.pojo.json2kt;

import com.google.gson.annotations.SerializedName;

   
public class Pagination {

   @SerializedName("count")
   int count;

   @SerializedName("hasMoreItems")
   boolean hasMoreItems;

   @SerializedName("totalItems")
   int totalItems;

   @SerializedName("skipCount")
   int skipCount;

   @SerializedName("maxItems")
   int maxItems;


    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }
    
    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }
    public boolean getHasMoreItems() {
        return hasMoreItems;
    }
    
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
    public int getTotalItems() {
        return totalItems;
    }
    
    public void setSkipCount(int skipCount) {
        this.skipCount = skipCount;
    }
    public int getSkipCount() {
        return skipCount;
    }
    
    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }
    public int getMaxItems() {
        return maxItems;
    }
    
}