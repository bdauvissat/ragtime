package llm.devoxx.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface ChatService {

    @SystemMessage("Answer in {language}")
    String answer(@UserMessage String message, String language);

    @SystemMessage("Answer in French.")
    @UserMessage("""
    Write a poem about {topic}. The poem should be {lines} lines long.
""")
    String poemWriting(String topic, int lines);

}
