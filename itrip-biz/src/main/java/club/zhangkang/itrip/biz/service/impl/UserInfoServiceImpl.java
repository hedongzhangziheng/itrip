package club.zhangkang.itrip.biz.service.impl;

import club.zhangkang.itrip.beans.pojo.ItripUser;
import club.zhangkang.itrip.beans.pojo.ItripUserLinkUser;
import club.zhangkang.itrip.beans.vo.userinfo.ItripAddUserLinkUserVO;
import club.zhangkang.itrip.beans.vo.userinfo.ItripModifyUserLinkUserVO;
import club.zhangkang.itrip.beans.vo.userinfo.ItripSearchUserLinkUserVO;
import club.zhangkang.itrip.biz.service.UserInfoService;
import club.zhangkang.itrip.dao.orderlinkuser.ItripOrderLinkUserMapper;
import club.zhangkang.itrip.dao.userlinkuser.ItripUserLinkUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private ItripUserLinkUserMapper userLinkUserMapper;

    @Resource
    private ItripOrderLinkUserMapper orderLinkUserMapper;

    @Override
    public boolean addUserLinkUser(ItripAddUserLinkUserVO vo) throws Exception {
        ItripUserLinkUser userLinkUser = new ItripUserLinkUser();
        BeanUtils.copyProperties(vo, userLinkUser);
        userLinkUser.setCreationDate(new Date());
        userLinkUser.setCreatedBy(userLinkUser.getUserId());
        Integer linkUser = userLinkUserMapper.insertItripUserLinkUser(userLinkUser);
        if (linkUser > 0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public String judgeDelUserLinkUser(Long[] ids) throws Exception {
        for (int i = 0; i < ids.length; i++){
            String linkUser = orderLinkUserMapper.judgeDelUserLinkUser(ids[i]);
            if (linkUser != null){
                return linkUser;
            }
        }
        return null;
    }

    @Override
    public boolean delUserLinkUser(Long[] ids) throws Exception {
        Integer del = userLinkUserMapper.deleteItripUserLinkUserByIds(ids);
        if (del > 0){
            return true;
        }
        return false;
    }

    @Override
    public List<ItripUserLinkUser> queryUserLinkUser(ItripSearchUserLinkUserVO vo, ItripUser user) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("linkUserName", vo.getLinkUserName());
        return userLinkUserMapper.getItripUserLinkUserListByMap(map);
    }

    @Override
    public boolean modifyUserLinkUser(ItripModifyUserLinkUserVO vo) throws Exception {
        ItripUserLinkUser user = new ItripUserLinkUser();
        BeanUtils.copyProperties(vo, user);
        user.setModifiedBy(vo.getUserId());
        user.setModifyDate(new Date());
        Integer integer = userLinkUserMapper.updateItripUserLinkUser(user);
        if (integer > 0){
            return true;
        }
        return false;
    }
}
