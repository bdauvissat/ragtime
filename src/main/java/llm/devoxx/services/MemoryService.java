package llm.devoxx.services;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@ApplicationScoped
public class MemoryService implements Serializable {

    private ChatMemory memory;

    public void resetMemory() {
        memory = MessageWindowChatMemory.withMaxMessages(20);
    }

    public ChatMemory addQuestion(String question) {
        if (StringUtils.isNotBlank(question)) {
            memory.add(UserMessage.userMessage(question));
        }

        return memory;

    }

    public void addAnswer(AiMessage answer) {

        if (answer != null && StringUtils.isBlank(answer.text())) {
            memory.add(answer);
        }

    }

}
