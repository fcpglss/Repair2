package repair.com.repair;

import org.junit.Test;

import util.AESUtil;


/**
 * Example local unit apply_fragment, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
      String encode = AESUtil.encode("陈哲");
        System.out.println(encode);
        System.out.print(AESUtil.decode(encode));
    }


}