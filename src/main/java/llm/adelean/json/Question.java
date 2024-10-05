package llm.adelean.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    private String question;

    private boolean generateAnswer;

    private boolean newChat;

}
