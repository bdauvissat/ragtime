package llm.devoxx.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestJson {

    private String topic;

    private int nbLines;

    private String answerLanguage;
}
