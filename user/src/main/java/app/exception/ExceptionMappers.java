package app.exception;

 import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
 import jakarta.ws.rs.NotFoundException;
 import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMappers {

    @ServerExceptionMapper
    public RestResponse<ErrorMessage> mapBadRequestException(BadRequestException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, new ErrorMessage(x.getMessage()));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorMessage> mapNotFoundException(NotFoundException x) {
        return RestResponse.status(Response.Status.NOT_FOUND, new ErrorMessage(x.getMessage()));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorMessage> mapNotAuthorizedException(NotAuthorizedException x) {
        String message = x.getMessage();
        if (x.getChallenges() != null && !x.getChallenges().isEmpty() && x.getChallenges().getFirst() instanceof String error) {
            message = error;
        }
        return RestResponse.status(Response.Status.UNAUTHORIZED, new ErrorMessage(message));
    }

    public record ErrorMessage(String message) {
    }

}
