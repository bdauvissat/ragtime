package llm.adelean.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import llm.adelean.json.ChatQuery;
import llm.adelean.services.ChatService;

@Path("/chat")
public class ChatResource {

    @Inject
    ChatService chatService;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String poem(ChatQuery chatQuery) {
        return chatService.poemWriting(chatQuery.getTopic(), chatQuery.getNbLines());
    }

    @POST
    @Path("/joke")
    @Produces(MediaType.TEXT_PLAIN)
    public String joke(ChatQuery chatQuery) {
        return chatService.answer(chatQuery.getTopic(), chatQuery.getLanguage());
    }

}
