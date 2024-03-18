package llm.devoxx.json;

public class QuestionRequest {
    private Question question;
    private String index;

    public QuestionRequest(Question question, String index) {
        this.question = question;
        this.index = index;
    }

    public QuestionRequest() {
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
