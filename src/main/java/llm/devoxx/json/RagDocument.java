package llm.devoxx.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RagDocument {

    private String title;

    private String url;

    private String content;
}
