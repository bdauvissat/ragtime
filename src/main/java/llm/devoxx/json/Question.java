package llm.devoxx.json;

public class Question {

    private String question;

    private boolean generateAnswer;

    public Question() {
    }

    public Question(String question, boolean generateAnswer) {
        this.question = question;
        this.generateAnswer = generateAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isGenerateAnswer() {
        return generateAnswer;
    }

    public void setGenerateAnswer(boolean generateAnswer) {
        this.generateAnswer = generateAnswer;
    }
}
