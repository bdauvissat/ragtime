package llm.devoxx.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import llm.devoxx.json.TestJson;
import llm.devoxx.services.Test;

@Path("/test")
public class TestResource {

    @Inject
    Test test;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String poem(TestJson query) {
        return test.poemWriting(query.getTopic(), query.getNbLines());
    }

    @POST
    @Path("/joke")
    @Produces(MediaType.TEXT_PLAIN)
    public String frenchJoke(TestJson query) {
        return test.joke(query.getTopic(), query.getAnswerLanguage());
    }


}
