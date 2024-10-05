package llm.adelean.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import llm.adelean.json.CompleteAnswer;
import llm.adelean.json.Question;
import llm.adelean.services.QuestionService;

@Path(("/question"))
public class QuestionResource {

    @Inject
    QuestionService questionService;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CompleteAnswer question(Question question) {
        return questionService.processQuestion(question);
    }


}
