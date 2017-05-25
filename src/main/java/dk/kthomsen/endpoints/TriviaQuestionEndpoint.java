package dk.kthomsen.endpoints;

import dk.kthomsen.model.TriviaQuestion;
import dk.kthomsen.model.TriviaQuestionAccessible;
import dk.kthomsen.model.TriviaQuestionArrayAccess;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Kenneth Thomsen on 25/05/2017.
 * Project: kthomsen-quizzer
 */
@Path("/questions")
final public class TriviaQuestionEndpoint {

    final private TriviaQuestionAccessible dataAccess = new TriviaQuestionArrayAccess();
    private static final int STARTING_OFFSET = 0;
    private static final int PAGE_SIZE = 10;
    final private Date questionsUpdatedDate = new Date();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestions(@Context UriInfo uri,
                                 @QueryParam("offset") @DefaultValue("0") long offset)  {
        // Calculate the effective offset
        long datasetSize = dataAccess.getQuestionListSize();
        long start = offset;
        if (start < STARTING_OFFSET) {
            start = STARTING_OFFSET;
        }
        if (start >= datasetSize) {
            start = datasetSize;
        }

        // Setup navigation links
        Link selfLink = Link.fromUri(uri.getBaseUri() + "questions?offset={offset}")
                .rel("self").type(MediaType.APPLICATION_JSON)
                .build(offset);
        long nextOffset = (offset + PAGE_SIZE < datasetSize)
                ? offset + PAGE_SIZE : PAGE_SIZE * (datasetSize / PAGE_SIZE);
        Link nextLink = Link.fromUri(uri.getBaseUri() + "questions?offset={offset}")
                .rel("next").type(MediaType.APPLICATION_JSON)
                .build(nextOffset);
        long prevOffset = (offset - PAGE_SIZE > STARTING_OFFSET) ? offset - PAGE_SIZE : STARTING_OFFSET;
        Link prevLink = Link.fromUri(uri.getBaseUri() + "questions?offset={offset}")
                .rel("prev").type(MediaType.APPLICATION_JSON)
                .build(prevOffset);
        Link firstLink = Link.fromUri(uri.getBaseUri() + "questions?offset={offset}")
                .rel("first").type(MediaType.APPLICATION_JSON)
                .build(STARTING_OFFSET);
        Link lastLink = Link.fromUri(uri.getBaseUri() + "questions?offset={offset}")
                .rel("last").type(MediaType.APPLICATION_JSON)
                .build(PAGE_SIZE * (datasetSize / PAGE_SIZE));
        Link countLink = Link.fromUri(uri.getBaseUri() + "questions/count")
                .rel("count").type(MediaType.APPLICATION_JSON)
                .build();
        Link rndLink = Link.fromUri(uri.getBaseUri() + "questions/random")
                .rel("random").type(MediaType.APPLICATION_JSON)
                .build();

        // Get the list of questions from starting point
        List<TriviaQuestion> list = dataAccess.getQuestionList(start);
        return Response.ok(list)
                .header("question-count", datasetSize)
                .header("current-question-list-size", list.size())
                .header("offset", start)
                .links(selfLink, nextLink, prevLink, firstLink, lastLink,
                        countLink, rndLink)
                .lastModified(questionsUpdatedDate)
                .location(uri.getRequestUri())
                .build();
    }

    /**
     * Returns the total number of trivia questions in the database.
     *
     * @param uri
     * @return the question count as JSON in a HTTP response
     */
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestionCount(@Context UriInfo uri) {
        long numberOfQuestions = dataAccess.getQuestionListSize();
        return Response.ok(numberOfQuestions)
                .header("question-count", numberOfQuestions)
                .lastModified(questionsUpdatedDate)
                .location(uri.getRequestUri())
                .build();
    }

    /**
     * Returns a single question by id. If the query parameter is "random", a
     * random question is returned. If the id is not found, 404 is returned. If
     * the id cannot be parsed, 400 is returned.
     *
     * @param uri
     * @param idString either an identifier or the word "random"
     * @return the question by id as JSON in a HTTP response
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestion(@Context UriInfo uri,
                                @PathParam("id") String idString) {
        Response response;
        if (idString.trim().equalsIgnoreCase("random")) {
            TriviaQuestion question = dataAccess.getRandomQuestion();
            response = Response.ok(question)
                    .lastModified(question.getLastUpdated())
                    .location(uri.getRequestUri())
                    .build();
        } else {
            try {
                long identifier = Long.parseLong(idString);
                if (identifier >= dataAccess.getQuestionListSize()) {
                    response = Response.status(Response.Status.NOT_FOUND).build();
                } else {
                    TriviaQuestion question = dataAccess.getQuestionById(identifier);
                    response = Response.ok(question)
                            .lastModified(question.getLastUpdated())
                            .location(uri.getRequestUri())
                            .build();
                }
            } catch (NumberFormatException ne) {
                response = Response.status(Response.Status.BAD_REQUEST).build();
            }

        }
        return response;
    }

}
