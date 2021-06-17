package person.clp.stationeryback.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jerry
 */
@Getter
@Setter
public class Result<T> {

    private Integer code;
    private boolean success;
    private String message;
    private T data;

    public Result() {
        this(true);
    }

    public Result(boolean success) {
        this.success = true;
        this.success = success;
    }

    public Result(T data, String message, boolean success, Integer code) {
        this.data = data;
        this.code = code;
        this.success = success;
        this.message = message;
    }
}
