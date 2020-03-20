package club.zhangkang.itrip.biz.service;

import club.zhangkang.itrip.beans.pojo.ItripUser;
import club.zhangkang.itrip.beans.pojo.ItripUserLinkUser;
import club.zhangkang.itrip.beans.vo.userinfo.ItripAddUserLinkUserVO;
import club.zhangkang.itrip.beans.vo.userinfo.ItripModifyUserLinkUserVO;
import club.zhangkang.itrip.beans.vo.userinfo.ItripSearchUserLinkUserVO;

import java.util.List;

public interface UserInfoService {
    boolean addUserLinkUser(ItripAddUserLinkUserVO vo) throws Exception;
    String judgeDelUserLinkUser(Long[] ids) throws Exception;
    boolean delUserLinkUser(Long[] ids) throws Exception;
    List<ItripUserLinkUser> queryUserLinkUser(ItripSearchUserLinkUserVO vo, ItripUser user) throws Exception;
    boolean modifyUserLinkUser(ItripModifyUserLinkUserVO vo) throws Exception;
}
