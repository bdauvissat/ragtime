package llm.devoxx.json;

import dev.langchain4j.data.document.Metadata;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class Answer {

    private String text;

    private double score;

    private Map<String, String> metadata;

    public Answer(String text, double score, Metadata metadata) {
        this.text = text;
        this.score = score;
        setMetadata(metadata);
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata.asMap();
    }
}
