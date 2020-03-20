package club.zhangkang.itrip.dao.orderlinkuser;
import club.zhangkang.itrip.beans.pojo.ItripOrderLinkUser;
import club.zhangkang.itrip.beans.vo.order.ItripOrderLinkUserVo;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ItripOrderLinkUserMapper {

	public ItripOrderLinkUser getItripOrderLinkUserById(@Param(value = "id") Long id)throws Exception;

	public List<ItripOrderLinkUserVo>	getItripOrderLinkUserListByMap(Map<String, Object> param)throws Exception;

	public Integer getItripOrderLinkUserCountByMap(Map<String, Object> param)throws Exception;

	public Integer insertItripOrderLinkUser(ItripOrderLinkUser itripOrderLinkUser)throws Exception;

	public Integer updateItripOrderLinkUser(ItripOrderLinkUser itripOrderLinkUser)throws Exception;

	public Integer deleteItripOrderLinkUserById(@Param(value = "id") Long id)throws Exception;

	public Integer deleteItripOrderLinkUserByOrderId(@Param(value = "orderId") Long orderId)throws Exception;

	public List<Long> getItripOrderLinkUserIdsByOrder() throws Exception;

	//判断是否有未支付订单与要删除的联系人关联，有的话返回这个人的名字
	public String judgeDelUserLinkUser(@Param(value = "linkUserId") Long linkUserId) throws Exception;

}
