package chongwm.demo.hyland.alfresco.search.pojo.json2kt;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

   
public class Entry {

   @SerializedName("isFile")
   boolean isFile;

   @SerializedName("createdByUser")
   CreatedByUser createdByUser;

   @SerializedName("modifiedAt")
   Date modifiedAt;

   @SerializedName("nodeType")
   String nodeType;

   @SerializedName("content")
   Content content;

   @SerializedName("parentId")
   String parentId;

   @SerializedName("createdAt")
   Date createdAt;

   @SerializedName("isFolder")
   boolean isFolder;

   @SerializedName("search")
   Search search;

   @SerializedName("modifiedByUser")
   ModifiedByUser modifiedByUser;

   @SerializedName("name")
   String name;

   @SerializedName("location")
   String location;

   @SerializedName("id")
   String id;

   @SerializedName("properties")
   Properties properties;


    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }
    public boolean getIsFile() {
        return isFile;
    }
    
    public void setCreatedByUser(CreatedByUser createdByUser) {
        this.createdByUser = createdByUser;
    }
    public CreatedByUser getCreatedByUser() {
        return createdByUser;
    }
    
    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    public Date getModifiedAt() {
        return modifiedAt;
    }
    
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
    public String getNodeType() {
        return nodeType;
    }
    
    public void setContent(Content content) {
        this.content = content;
    }
    public Content getContent() {
        return content;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getParentId() {
        return parentId;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }
    public boolean getIsFolder() {
        return isFolder;
    }
    
    public void setSearch(Search search) {
        this.search = search;
    }
    public Search getSearch() {
        return search;
    }
    
    public void setModifiedByUser(ModifiedByUser modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }
    public ModifiedByUser getModifiedByUser() {
        return modifiedByUser;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    public String getLocation() {
        return location;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    public Properties getProperties() {
        return properties;
    }
    
}