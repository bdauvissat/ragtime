package llm.devoxx.json;

import java.util.List;

public class CompleteAnswer {

    private String generatedAnswer;

    private List<Answer> relevantDocuments;

    public CompleteAnswer() {
    }

    public CompleteAnswer(String generatedAnswer, List<Answer> relevantDocuments) {
        this.generatedAnswer = generatedAnswer;
        this.relevantDocuments = relevantDocuments;
    }

    public String getGeneratedAnswer() {
        return generatedAnswer;
    }

    public void setGeneratedAnswer(String generatedAnswer) {
        this.generatedAnswer = generatedAnswer;
    }

    public List<Answer> getRelevantDocuments() {
        return relevantDocuments;
    }

    public void setRelevantDocuments(List<Answer> relevantDocuments) {
        this.relevantDocuments = relevantDocuments;
    }
}
