package llm.devoxx.json;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RagDocument {

    private String title;

    private String url;
    
    @SerializedName(value = "body")
    private List<String> content;
    
    @SerializedName(value = "publishing_date")
    private String datetime;
    
    private List<String> keywords;
    
    public RagDocument(String title, String url, List<String> content) {
        this.title = title;
        this.url = url;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatetime() {
        return datetime;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
}
