package llm.devoxx.services;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface Test {

    public String chat(@UserMessage String message);

}
