package dk.kthomsen;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Date;

/**
 * Hello world!
 *
 */
@ApplicationPath("/trivia")
@Path("")
public class TriviaApp extends Application
{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDirectory(@Context UriInfo uri) {
        Link selfLink = Link.fromUri(uri.getBaseUri())
                .rel("self").type(MediaType.APPLICATION_JSON)
                .build();

        Link questionsLink = Link.fromUri(uri.getBaseUri() + "questions")
                .rel("questions").type(MediaType.APPLICATION_JSON)
                .build();

        return Response.ok()
                .lastModified(new Date())
                .location(uri.getRequestUri())
                .links(selfLink,questionsLink)
                .build();

    }

}
