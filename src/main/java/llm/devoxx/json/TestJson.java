package llm.devoxx.json;

public class TestJson {

    private String topic;

    private int nbLines;

    private String answerLanguage;

    public TestJson() {
    }

    public TestJson(String topic, int nbLines, String answerLanguage) {
        this.topic = topic;
        this.nbLines = nbLines;
        this.answerLanguage = answerLanguage;
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

    public String getAnswerLanguage() {
        return answerLanguage;
    }

    public void setAnswerLanguage(String answerLanguage) {
        this.answerLanguage = answerLanguage;
    }
}
