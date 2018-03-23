package repair.com.repair;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Serializable;

import model.ResultBean;
import util.Util;

import static org.junit.Assert.*;

/**
 * Instrumentation apply_fragment, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under apply_fragment.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("repair.com.repair", appContext.getPackageName());
    }



}

 class Response<T> implements Serializable {

    private static final long serialVersionUID = 5871459408880841442L;
    //是否连接
    private boolean success;
    //错误类型
    private int errorType ;
    //错误描述
    private String errorMessage;
    //实体
    private T result=null;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Response() {
        super();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


}