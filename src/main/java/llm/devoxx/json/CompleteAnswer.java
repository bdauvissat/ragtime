package llm.devoxx.json;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CompleteAnswer {

    private String generatedAnswer;

    private List<Answer> relevantDocuments;

    public CompleteAnswer(String generatedAnswer, List<Answer> relevantDocuments) {
        this.generatedAnswer = generatedAnswer;
        this.relevantDocuments = relevantDocuments;
    }

}
