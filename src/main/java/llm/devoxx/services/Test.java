package llm.devoxx.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface Test {

    @SystemMessage("Answer in French.")
    @UserMessage("""
    Write a poem about {topic}. The poem should be {lines} lines long.
""")
    String poemWriting(String topic, int lines);

    @SystemMessage("Answer in {language}")
    String joke(@UserMessage String message, String language);

}
