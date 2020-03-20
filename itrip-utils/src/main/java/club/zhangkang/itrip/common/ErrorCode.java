package club.zhangkang.itrip.common;

/**
 * 系统错误编码，根据业务定义如下
 * <br/>
 * 酒店主业务biz：1开头（10000）<br/>
 * 评论：10001 ——10100<br/>
 * 酒店详情：10101 ——10200<br/>
 * 订单：10201 ——10400<br/>
 * 搜索search：2开头（20000）<br/>
 * 认证auth：3开头（30000）<br/>
 * 支付trade：4开头（40000）<br/>
 * @author hduser
 *
 */
public class ErrorCode {

	/*认证模块错误码-start*/
	public final static String AUTH_UNKNOWN="30000";
	public final static String AUTH_USER_ALREADY_EXISTS="30001";//用户已存在
	public final static String AUTH_AUTHENTICATION_FAILED="30002";//认证失败
	public final static String AUTH_PARAMETER_ERROR="30003";//用户名密码参数错误，为空
	public final static String AUTH_ACTIVATE_FAILED="30004";//邮件注册，激活失败
	public final static String AUTH_REPLACEMENT_FAILED="30005";//置换token失败
	public final static String AUTH_TOKEN_INVALID="30006";//token无效
	public static final String AUTH_ILLEGAL_USERCODE = "30007";//非法的用户名
	/*认证模块错误码-end*/

	//查询模块
	public static final String SEARCH_UNKNOWN = "20001";//系统异常,获取失败
	public static final String SEARCH_DESTINATION_NOTNULL = "20002";//目的地不能为空
	public static final String SEARCH_CITYID_NOTFOUND = "20004";//城市id不能为空

	//biz模块开始
	//hotel
	public static final String BIZ_CITY_TYPE_NOTNULL = "10201";//城市类型不能为空
	public static final String BIZ_CITYID_NOTNULL = "10203";//城市id不能为空
	public static final String BIZ_UNKNOWN = "10204";//系统异常,获取失败
	public static final String BIZ_GET_PIC_FAILED = "100212";//获取酒店图片失败
	public static final String BIZ_HOTELID_NOTNULL = "100213";//酒店id不能为空
	public static final String BIZ_GET_HOTEL_DESC_FAILED = "100214";//获取酒店视频文字描述失败

	//hotelroom
	public static final String BIZ_GET_HOTEL_ROOM_IMG_FAILED = "100301";//获取酒店房型图片失败
	public static final String BIZ_HOTEL_ROOM_ID_NOTNULL = "100302";//酒店房型id不能为空
	public static final String BIZ_HOTEL_ID_NOTNULL = "100303";//酒店id不能为空,酒店入住及退房时间不能为空,入住时间不能大于退房时间
	public static final String BIZ_HOTEL_ROOM_UNKNOWN = "100304";//系统异常
	public static final String BIZ_GET_HOTEL_ROOM_BED_FAILED = "100305";//获取酒店房间床型失败

	//comment
	public static final String BIZ_TOKEN_INVALID = "100000";//token失效，请重新登录
	public static final String BIZ_GET_ITRIP_TYPE_FAILED = "100019";//获取旅游类型列表错误
	public static final String BIZ_GET_COMMENT_IMAGE_FAILED = "100012";//获取评论图片失败
	public static final String BIZ_COMMENTID_NOTNULL = "100013";//评论id不能为空
	public static final String BIZ_ADD_COMMENT_FAILED = "100003";//新增评论失败
	public static final String BIZ_COMMENT_NOTNULL = "100004";//不能提交空，请填写评论信息
	public static final String BIZ_ADD_COMMENT_ORDERID_NOTNULL = "100005";//新增评论，订单ID不能为空
	public static final String BIZ_UPLOAD_FAILED = "100006";//文件上传失败
	public static final String BIZ_UPLOAD_NUMBER_ERROR = "100007";//上传的文件数不正确，必须是大于1小于等于4
	public static final String BIZ_NOT_UPLOAD_TYPE = "100008";//请求的内容不是上传文件的类型
	public static final String BIZ_SIZE_OVER = "100009";//文件大小超限
	public static final String BIZ_PIC_NOT_EXIST = "100010";//文件不存在，删除失败
	public static final String BIZ_GET_COMMENTLIST_FAILED = "100020";//获取评论列表错误
	public static final String BIZ_GET_COMMENT_HOTEL_DESC_FAILED = "100021";//获取酒店相关信息错误
	public static final String BIZ_GET_COMMENT_SCORE_FAILED = "100001";//获取评分失败
	public static final String BIZ_COMMENT_HOTELID_NOTNULL = "100002";//hotelId不能为空
	public static final String BIZ_GET_ALLCOMMENT_COUNT_FAILED = "100014";//获取酒店总评论数失败
	public static final String BIZ_GET_COMMENT_COUNT_HAVINGIMG_FAILED = "100015";//获取酒店有图片评论数失败
	public static final String BIZ_GET_COMMENT_COUNT_IMPROVE_FAILED = "100016";//获取酒店有待改善评论数失败
	public static final String BIZ_GET_COMMENT_COUNT_ISOK_FAILED = "100017";//获取酒店值得推荐评论数失败
	public static final String BIZ_COMMENT_COUNT_HOTELID_NOTNULL = "100018";//参数hotelId为空

	//userInfo
	public static final String BIZ_ADD_USER_INFO_FAILED = "100411";//新增常用联系人失败
	public static final String BIZ_ADD_USER_INFO_NOTNULL = "100412";//不能提交空，请填写常用联系人信息
	public static final String BIZ_HAVE_SOME_USER_INFO_LINK_ORDER = "100431";//所选的常用联系人中有与某条待支付的订单关联的项，无法删除
	public static final String BIZ_DEL_USER_INFO_FAILED = "100432";//删除常用联系人失败
	public static final String BIZ_DEL_USER_INFO_NOTNULL = "100433";//请选择要删除的常用联系人
	public static final String BIZ_GET_USER_INFO_FAILED = "100401";//获取常用联系人信息失败
	public static final String BIZ_MODIFY_USER_INFO_FAILED = "100421";//修改常用联系人失败
	public static final String BIZ_MODIFY_USER_INFO_NOTNULL = "100422";//不能提交空，请填写常用联系人信息

	//order
	public static final String BIZ_ORDER_VALIDATE_ROOM_STORE_HOTEL_ID_NOTNULL = "100515";//hotelId不能为空
	public static final String BIZ_ORDER_VALIDATE_ROOM_STORE_ROOM_ID_NOTNULL = "100516";//roomId不能为空
	public static final String BIZ_ORDER_VALIDATE_ROOM_STORE_UNKNOWN = "100517";//系统异常
	public static final String BIZ_GET_ORDER_INFO_ORDER_ID_NOTNULL = "100525";//请传递参数：orderId
	public static final String BIZ_ORDER_INFO_ISNULL = "100526";//没有相关订单信息
	public static final String BIZ_GET_ORDER_INFO_FAILED = "100527";//获取个人订单信息错误
	public static final String BIZ_GET_ORDER_ROOM_INFO_ORDER_ID_NOTNULL = "100529";//请传递参数：orderId
	public static final String BIZ_ORDER_ROOM_INFO_ISNULL = "100530";//没有相关订单房型信息
	public static final String BIZ_GET_ORDER_ROOM_INFO_FAILED = "100531";//获取个人订单房型信息错误
	public static final String BIZ_GET_ORDER_LIST_TYPE_NOTNULL = "100501";//请传递参数：orderType
	public static final String BIZ_ORDER_LIST_STATUS_NOTNULL = "100502";//请传递参数：orderStatus
	public static final String BIZ_GET_ORDER_LIST_FAILED = "100503";//获取个人订单列表错误
	public static final String BIZ_SCAN_ISNULL = "100535";//没有查询到相应记录
	public static final String BIZ_SCAN_UNKNOWN = "100536";//系统异常
	public static final String BIZ_GET_PREORDER_HOTEL_ID_NOTNULL = "100510";//hotelId不能为空
	public static final String BIZ_GET_PREORDER_ROOM_ID_NOTNULL = "100511";//roomId不能为空
	public static final String BIZ_GET_PREORDER_ROOM_ISNULL = "100512";//暂时无房
	public static final String BIZ_GET_PREORDER_UNKNOWN = "100513";//系统异常
	public static final String BIZ_GET_ORDER_INFO_ID_NOTNULL = "100519";//id不能为空
	public static final String BIZ_GET_ORDER_DATA_FAILED = "100520";//获取数据失败
	public static final String BIZ_ADD_ORDER_FAILED = "100505";//生成订单失败
	public static final String BIZ_ADD_ORDER_NOTNULL = "100506";//不能提交空，请填写订单信息
	public static final String BIZ_ADD_ORDER_STORE_ISNULL = "100507";//库存不足
	public static final String BIZ_GET_ORDER_INFO_ISNULL = "100533";//没有查询到相应订单
	public static final String BIZ_GET_ORDER_INFO_UNKNOWN = "100534";//系统异常
	public static final String BIZ_UPDATE_NOT_SUPPORT = "100521";//对不起，此房间不支持线下支付
	public static final String BIZ_UPDATE_ORDER_FAILED = "100522";//修改订单失败
	public static final String BIZ_UPDATE_ORDER_NOTNULL = "100523";//不能提交空，请填写订单信息
	//biz模块结束


}
