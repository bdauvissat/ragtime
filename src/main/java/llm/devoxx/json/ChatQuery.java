package llm.devoxx.json;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatQuery {

    String topic;

    int nbLines;

    String language;
}
