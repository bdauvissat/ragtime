package llm.adelean.json;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
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

}