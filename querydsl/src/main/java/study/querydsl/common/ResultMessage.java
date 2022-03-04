package study.querydsl.common;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ResultMessage {
    private Object data;
    private HttpStatus statusCode;
    private String message;

    public final static ResultMessage of(final Object data, final HttpStatus statusCode, final String message) {
        return ResultMessage.builder()
                .data(data)
                .statusCode(statusCode)
                .message(message)
                .build();
    }

    public final static ResultMessage of(final Object data, final HttpStatus statusCode) {
        return ResultMessage.builder()
                .data(data)
                .statusCode(statusCode)
                .build();
    }
}
