package model;

import java.io.Serializable;

/**
 * Created by hsp on 2017/3/13.
 */

public class Response implements Serializable{

    private static final long serialVersionUID=1324648431L;

    private boolean error;
    private int errorType;
    private String errorMessage;

    private boolean isEnd;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ResultBean getResultBean() {
        return resultBean;
    }

    public void setResultBean(ResultBean resultBean) {
        this.resultBean = resultBean;
    }

    private ResultBean resultBean;

    public Response() {
    }
}
