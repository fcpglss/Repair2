package constant;

/**
 * Created by hsp on 2017/4/23.
 */

public class RequestUrl {


   // private static final String project="http://192.168.43.128:8888/repairSystem/";

//    private static final  String IP = "http://116.10.16.84";
//    private static final  String IP = "http://1711p20y55.iask.in";
//    private static final  String IP = "http://huangshipeng.in.8866.org";
//    private static final  String IP = "http://huangshipeng.f3322.net";
//       private static final  String IP = "http://10.0.11.244";
   private static final  String IP = "http://116.13.193.8";

       // private static final String duankou = ":8080";
    private static final String duankou = ":8888";


    private static final String project="http://192.168.31.177:8080/repairSystem/";
  // private static final String project="http://hqbx.qzhu.edu.cn/repairSystem/";

 public  static final String updateApp = project+"UpdateVersion";

    //发送邮件的ip 上面为我们的 下面是他们的
//    public static final String TEXT_EMAIL_URL = project+"AdminUpdate";
  //  public static final String TEXT_EMAIL_URL=IP+":80"+"/SendMessage/SendEMail.ashx";


    public static final String TEXT_EMAIL_URL=IP+":80"+"/AdminIndex/paifa.ashx";


    public static final String QUERYMYREPAIR = project+"QueryRepair";

    public static final String SendMyRepairMore = project+"SendMyRepairMore";

    public static final String SendMyRepairPassword = project+"myserver2/";



    public static final String JSONEMPLOYEE = project+"AdminServerUpdate";
    public static final String URL = project+"address";
    public static final String AdMINUPDATE = project+"AdminUpdate";

    public static final String JSONFIRST = project+"AdminServerApply";
    public static final String ADMINLIST_SENDMORE = project+"SendAdminListMore";
    public static final String ADMIN_EMAIL_CHECK = project+"AdminEmailCheck";

    public static final String ANNCOUCEMENT = project+"SendAnnoucement?annoucementFirst";

    public static final String ANNCOUCEMENTMORE = project+"SendAnnoucement";

    public static final String UPDATESERVER = project+"updateServer";

    public static final String LOGIN = project+"AdminLogin";


    public static final String JSON_URL = project+"address";
    public static final String UP_APPLY = project+"Upload2";//
    public static final String GET_JSON = project+"ResponseClient";
    public static final String FRIST_URL = project+"FirstRequest";
    public static final String SENDMORE_URL = project+"sendmore";
    public static final String REFRESH_URL=project+"RefreshServer";





    //权限请求回调
    final public static int REQUEST_CODE_CAMERA = 1;
    final public static int REQUEST_CODE_SD_CARD = 2;

//


//    public static final String QUERYMYREPAIR = project+"QueryRepair";
//
//    public static final String SendMyRepairMore = project+"SendMyRepairMore";
//    public static final String SendMyRepairPassword = project+""
//
//    public static final String AdMINUPDATE = project+"AdminUpdate";
//    public static String JSONEMPLOYEE = project+"AdminServerUpdate";
//    public static final String URL=project+"address";
//    public static final String ADMIN_SUBMIT_EMAIL=project+"AdminUpdate";
//
//
//      public static final String JSONFIRST = project+"AdminServerApply";
//      public static final String ADMIN_EMAIL_CHECK = project+"AdminEmailCheck";
//
//     public static final String ADMINLIST_SENDMORE = project+"SendAdminListMore";
//
//
//
//    public final static String  ANNCOUCEMENT=project+"SendAnnoucement?annoucementFirst";
//
//    public final static String  ANNCOUCEMENTMORE="http://192.43.128.201:8888/myserver2/SendAnnoucement";
//       public static final String UPDATESERVER=project+"updateServer";
//
//      public static final String URL=project+"servlet/action";
//
//
//        public static final String LOGIN=project+"AdminLogin";
//
//        public static final String FRIST_URL=project+"FirstRequest";
//    public static final String JSON_URL = project+"address";
//    public static final String UP_APPLY=project+"Upload2";//
//    public static final String GET_JSON=project+"ResponseClient";
//    public static final String SENDMORE_URL = project+"sendmore";
}
