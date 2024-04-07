package llm.devoxx.json;

public class ChatQuery {

    String topic;

    int nbLines;

    String language;


    public ChatQuery() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getNbLines() {
        return nbLines;
    }

    public void setNbLines(int nbLines) {
        this.nbLines = nbLines;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
