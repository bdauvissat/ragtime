package llm.devoxx.services;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import llm.devoxx.json.Answer;
import llm.devoxx.json.CompleteAnswer;
import llm.devoxx.json.Question;
import llm.devoxx.util.Constants;
import llm.devoxx.util.Tools;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class QuestionService {

    @Inject
    Tools tools;

    @Inject
    EmbeddingModel embeddingModel;

    public CompleteAnswer processQuestion(Question question) {

        EmbeddingStore<TextSegment> store = tools.getStore();

        LanguageModel languageModel = tools.createLanguageModel();

        Embedding queryEmbedded = embeddingModel.embed(question.getQuestion()).content();

        EmbeddingSearchResult<TextSegment> docs = store.search(EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedded)
                .build());

        List<EmbeddingMatch<TextSegment>> relevant = docs.matches();

        relevant.sort((o1, o2) -> o2.score().compareTo(o1.score()));

        List<Answer> answers = new ArrayList<>();

        StringBuilder rep = new StringBuilder(question.getQuestion());

        log.info("Generating answer for question: \"{}\"", question.getQuestion());

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

}
