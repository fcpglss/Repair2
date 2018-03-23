package constant;

/**
 * Created by hsp on 2017/4/23.
 */

public class RequestUrl {


    private static final String IP = "http://116.13.193.8";


    private static final String project = "http://192.168.31.199:8080/repairSystem/";
//  private static final String project="http://hqbx.qzhu.edu.cn/repairSystem/";

    public static final String updateApp = project + "UpdateVersion";


//    public static final String SendMyRepairMore = project + "SendMyRepairMore";

    public static final String SendMyRepairPassword = project + "myserver2/";


    public static final String JSONEMPLOYEE = project + "AdminServerUpdate";
//    public static final String URL = project + "address";


//    public static final String ANNCOUCEMENT = project + "SendAnnoucement?annoucementFirst";



    public static final String UPDATESERVER = project + "updateServer";

    //地址
    public static final String AdressAreaList = project + "address/areaList";
    public static final String AdressPlaceList = project + "address/placeList";
    public static final String AdressFliesList = project + "address/fliesList";
    public static final String AdressroomList = project + "address/roomList";

    //类型
    public static final String TypeCategoryList = project + "type/categoryList";
    public static final String TypeDetailClassList = project + "type/detailClassList";

//    public static final String UP_APPLY = project + "Upload2";//
//    public static final String GET_JSON = project + "ResponseClient";
    //    public static final String FRIST_URL = project+"FirstRequest";

//    public static final String SENDMORE_URL = project + "sendmore";
//    public static final String REFRESH_URL = project + "RefreshServer";

    //报修
    public static final String FRIST_URL = project + "applyList/FirstRequest";
    public static final String ApplyRrefresh = project +"applyList/refresh";
    public static final String ApplyLoadMore = project +"applyList/loadMore";
    public static final String ApplySearch = project + "applyList/search";
    public static final String ApplySearchMore = project + "applyList/searchMore";
    public static final String ApplyDetail = project + "applyList/detail";
    public static final String ApplyChange = project + "applyList/change";
    public static final String ApplyPassword = project + "applyList/password";
    public static final String ApplyInsert = project + "applyList/insert";
    public static final String ApplyUpdate = project + "applyList/update";
    public static final String ApplyNoImgInsert = project + "applySave/insert";
    public static final String ApplyNoImgUpdate = project + "applySave/update";
    public static final String ApplyAppraise = project + "applySave/appraise";
    //公告
    public static final String AnnouceList = project + "annouceList/annouce";
    public static final String AnnouceRefresh= project + "annouceList/refresh";
    public static final String AnnouceLoadMore= project + "annouceList/loadMore";
    //权限请求回调
    final public static int REQUEST_CODE_CAMERA = 1;
    final public static int REQUEST_CODE_SD_CARD = 2;

//


//    public static final String QUERYMYREPAIR = project + "QueryRepair";
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
