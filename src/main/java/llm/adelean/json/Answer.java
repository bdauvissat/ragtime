package llm.adelean.json;

import dev.langchain4j.data.document.Metadata;

import java.util.Map;

public class Answer {

    private String text;

    private double score;

    private Map<String, String> metadata;

    public Answer(String text, double score, Metadata metadata) {
        this.text = text;
        this.score = score;
        setMetadata(metadata);
    }

    public Answer() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata.asMap();
    }
}
