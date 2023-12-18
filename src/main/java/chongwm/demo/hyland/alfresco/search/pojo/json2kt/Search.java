package chongwm.demo.hyland.alfresco.search.pojo.json2kt;

import com.google.gson.annotations.SerializedName;

   
public class Search {

   @SerializedName("score")
   double score;


    public void setScore(double score) {
        this.score = score;
    }
    public double getScore() {
        return score;
    }
    
}