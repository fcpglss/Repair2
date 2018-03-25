package constant;

/**
 * Created by hsp on 2017/4/23.
 */

public class RequestUrl {


//    private static final String IP = "http://116.13.193.8";
    private static final String project="http://hqbx.qzhu.edu.cn/repairSystem/";

//    private static final String project = "http://192.168.31.199:8080/repairSystem/";


    public static final String updateApp = project + "UpdateVersion";


    //地址
    public static final String AdressAreaList = project + "address/areaList";
    public static final String AdressPlaceList = project + "address/placeList";
    public static final String AdressFliesList = project + "address/fliesList";
    public static final String AdressroomList = project + "address/roomList";

    //类型
    public static final String TypeCategoryList = project + "type/categoryList";
    public static final String TypeDetailClassList = project + "type/detailClassList";



    //报修
    public static final String ApplyHomeList = project + "applyList/home";
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

    //版本
    public static final String VersionURL = project + "version/code";
    public static final String VersionApk = project + "apks/";

    //公告
    public static final String AnnouceList = project + "annouceList/annouce";
    public static final String AnnouceRefresh= project + "annouceList/refresh";
    public static final String AnnouceLoadMore= project + "annouceList/loadMore";
    //权限请求回调
    final public static int REQUEST_CODE_CAMERA = 1;
    final public static int REQUEST_CODE_SD_CARD = 2;


}
