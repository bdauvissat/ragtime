package llm.devoxx.services;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import llm.devoxx.json.Answer;
import llm.devoxx.json.CompleteAnswer;
import llm.devoxx.json.Question;
import llm.devoxx.util.Constants;
import llm.devoxx.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class QuestionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionService.class);
    @Inject
    Tools tools;

    @Inject
    MemoryService memoryService;

    @Inject
    EmbeddingModel embeddingModel;

    ChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);

    public CompleteAnswer processQuestion(Question question) {

        EmbeddingStore<TextSegment> store = tools.getStore();

        LanguageModel languageModel = tools.createLanguageModel();

        Embedding queryEmbedded = embeddingModel.embed(question.getQuestion()).content();

        List<EmbeddingMatch<TextSegment>> relevant = store.findRelevant(queryEmbedded,3, 0.55);

        List<Answer> answers = new ArrayList<>();

        StringBuilder rep = new StringBuilder(question.getQuestion());
        LOGGER.info("Generating answer for question: \"{}\"", question.getQuestion());
        for (var rel : relevant) {
            answers.add(new Answer(rel.embedded().text(), rel.score(), rel.embedded().metadata()));

            rep.append(System.lineSeparator());
            rep.append(rel.embedded().text());
        }

        if (question.isGenerateAnswer()) {
            Response<String> generatedAnswer = languageModel.generate(rep.toString());
            return new CompleteAnswer(generatedAnswer.content(), answers);
        }

        return new CompleteAnswer(Constants.EMPTY_STRING, answers);
    }
    public CompleteAnswer chatWithDocs(Question question) {

        EmbeddingStore<TextSegment> store = tools.getStore();

        ChatLanguageModel chatModel = tools.createChatModel();

        Embedding queryEmbedded = embeddingModel.embed(question.getQuestion()).content();

        List<EmbeddingMatch<TextSegment>> relevant = store.findRelevant(queryEmbedded,3, 0.55);

        List<Answer> answers = new ArrayList<>();
        LOGGER.info("Generating chat answer for question: \"{}\"", question.getQuestion());
        for (var rel : relevant) {
            answers.add(new Answer(rel.embedded().text(), rel.score(), rel.embedded().metadata()));

        }

        PromptTemplate promptTemplate = PromptTemplate.from(
                "Answer the following question :\n"
                        + "\n"
                        + "Question:\n"
                        + "{{question}}\n"
                        + "\n"
                        + "Base your answer on the following information:\n"
                        + "{{information}}"
        );

        String information = relevant.stream().map(r -> r.embedded().text()).collect(Collectors.joining("\n\n"));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("question", question.getQuestion());
        parameters.put("information", information);

        Prompt prompt = promptTemplate.apply(parameters);

        AiMessage message = chatModel.generate(prompt.toUserMessage()).content();

        return new CompleteAnswer(message.text(), answers);

    }
//    public CompleteAnswer chatWithDocsAndMemory(Question question) {
//
//        EmbeddingStore<TextSegment> store = tools.getStore();
//
//        ChatLanguageModel chatModel = tools.createChatModel();
//
//        Embedding queryEmbedded = embeddingModel.embed(question.getQuestion()).content();
//
//        List<EmbeddingMatch<TextSegment>> relevant = store.findRelevant(queryEmbedded,3, 0.55);
//
//        PromptTemplate promptTemplate = PromptTemplate.from(
//                "Answer the following question :\n"
//                        + "\n"
//                        + "Question:\n"
//                        + "{{question}}\n"
//                        + "\n"
//                        + "Base your answer on the following information:\n"
//                        + "{{information}}"
//        );
//
//        String information = relevant.stream().map(r -> r.embedded().text()).collect(Collectors.joining("\n\n"));
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("question", question.getQuestion());
//        parameters.put("information", information);
//
//        Prompt prompt = promptTemplate.apply(parameters);
//
//        QueryTransformer queryTransformer = new CompressingQueryTransformer(chatModel);
//        Query query = Query.from(prompt.text(), new Metadata(UserMessage.from(prompt.text()), 1, memory.messages()));
//        queryTransformer.transform(query);
//
//        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(store)
//                .embeddingModel(embeddingModel)
//                .maxResults(3)
//                .minScore(0.55)
//                .build();
//
//        ContentInjector contentInjector = DefaultContentInjector.builder()
//                .metadataKeysToInclude(List.of("title", "index", "url"))
//                .build();
//
//        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
//                .queryTransformer(queryTransformer)
//                .contentRetriever(contentRetriever)
//                .contentInjector(contentInjector)
//                .build();
//
//        DocumentChat toto = AiServices.builder(DocumentChat.class)
//                .chatLanguageModel(chatModel)
//                .retrievalAugmentor(retrievalAugmentor)
//                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
//                .build();
//
//        String reponse = toto.answer(question.getQuestion());
//
//        List<Answer> answers = new ArrayList<>();
//        List<ChatMessage> messages = new ArrayList<>();
//        LOGGER.info("Generating chat answer for question: \"{}\"", question.getQuestion());
//        for (var rel : relevant) {
//            answers.add(new Answer(rel.embedded().text(), rel.score(), rel.embedded().metadata()));
//            messages.add(AiMessage.from(rel.embedded().text()));
//
//        }
//
//        if (question.isNewChat()) {
//            memoryService.resetMemory();
//        }
//
//        ChatMemory memory = memoryService.addQuestion(question.getQuestion());
//
//        Response<AiMessage> response = chatModel.generate(memory.messages());
//
//        memoryService.addAnswer(response.content());
//
//        return new CompleteAnswer(response.content().text(), answers);
//
//    }

}
