package model;

import java.io.Serializable;

/**
 * Created by hsp on 2017/3/13.
 */

public class ResponseAdmin implements Serializable {

    private static final long serialVersionUID = 2L;

    private boolean error;
    private int errorType; //1为失败；
    private String errorMessage;
    private boolean isEnd;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    private Admin admin;

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

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }


}
