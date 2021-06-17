package person.clp.stationeryback.base;

import person.clp.stationeryback.enums.ErrorCodeEnum;

/**
 * @author Jerry
 */
public class Results {

    public static <T> Result<T> newSuccessResult(T data) {
        return new Result(data, "success", true,0);
    }

    public static <T> Result<T> newFailedResult(String message) {
        return new Result(null, message, false,-10000);
    }

    public static <T> Result<T> newFailedResult(Integer code, String message) {
        return new Result(null, message, false, code);
    }

    public static <T> Result<T> newFailedResult(ErrorCodeEnum errorCodeEnum) {
        return new Result(null, errorCodeEnum.getDescription(), false, errorCodeEnum.getCode());
    }

    public static <T> Result<T> newResult(T data, String message, boolean success,Integer code) {
        Result result = new Result();
        result.setData(data);
        result.setCode(code);
        result.setSuccess(success);
        result.setMessage(message);
        return result;
    }

}
