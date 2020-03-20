package club.zhangkang.itrip.biz.controller;

import club.zhangkang.itrip.beans.dto.Dto;
import club.zhangkang.itrip.beans.pojo.ItripUser;
import club.zhangkang.itrip.beans.pojo.ItripUserLinkUser;
import club.zhangkang.itrip.beans.vo.userinfo.ItripAddUserLinkUserVO;
import club.zhangkang.itrip.beans.vo.userinfo.ItripModifyUserLinkUserVO;
import club.zhangkang.itrip.beans.vo.userinfo.ItripSearchUserLinkUserVO;
import club.zhangkang.itrip.biz.service.UserInfoService;
import club.zhangkang.itrip.common.DtoUtil;
import club.zhangkang.itrip.common.EmptyUtils;
import club.zhangkang.itrip.common.ErrorCode;
import club.zhangkang.itrip.common.ValidationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "联系人controller", tags = "联系人信息操作接口")
@Controller
@RequestMapping("/api/userinfo")
public class UserInfoController {

    @Resource
    private ValidationToken validateToken;

    @Resource
    private UserInfoService userInfoService;

    //3.5.1
    @ApiOperation(value = "新增常用联系人接口", httpMethod = "POST", response = Dto.class, notes = "新增常用联系人信息")
    @RequestMapping(value = "/adduserlinkuser", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto addUserLinkUser(HttpServletRequest request,
            @ApiParam(name = "itripAddUserLinkUserVO", value = "itripAddUserLinkUserVO", required = true)
            @RequestBody ItripAddUserLinkUserVO itripAddUserLinkUserVO){
        if (EmptyUtils.isEmpty(itripAddUserLinkUserVO)){
            return DtoUtil.returnFail("不能提交空，请填写常用联系人信息", ErrorCode.BIZ_ADD_USER_INFO_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            ItripUser user = validateToken.getCurrentUser(token);
            itripAddUserLinkUserVO.setUserId(user.getId());
            if (userInfoService.addUserLinkUser(itripAddUserLinkUserVO)){
                return DtoUtil.returnSuccess("新增常用联系人成功");
            }else {
                return DtoUtil.returnFail("新增常用联系人失败", ErrorCode.BIZ_ADD_USER_INFO_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.5.2
    @ApiOperation(value = "删除常用联系人接口", httpMethod = "GET", response = Dto.class, notes = "删除常用联系人信息")
    @RequestMapping(value = "/deluserlinkuser", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto delUserLinkUser(HttpServletRequest request,
            @ApiParam(name = "ids", value = "ids", required = true) Long... ids){
        if (EmptyUtils.isEmpty(ids)){
            return DtoUtil.returnFail("请选择要删除的常用联系人", ErrorCode.BIZ_DEL_USER_INFO_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            String linkUser = userInfoService.judgeDelUserLinkUser(ids);
            if (linkUser == null){
                if (userInfoService.delUserLinkUser(ids)){
                    return DtoUtil.returnSuccess("删除常用联系人成功");
                }else {
                    return DtoUtil.returnFail("删除常用联系人失败", ErrorCode.BIZ_DEL_USER_INFO_FAILED);
                }
            }else {
                return DtoUtil.returnFail("所选的常用联系人中" + linkUser + "有与某条待支付的订单关联的项，无法删除", ErrorCode.BIZ_HAVE_SOME_USER_INFO_LINK_ORDER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.5.3
    @ApiOperation(value = "查询常用联系人接口", httpMethod = "POST", response = Dto.class, notes = "可根据联系人姓名进行模糊查询。若不根据联系人姓名进行查询，不输入参数即可 | 若根据联系人姓名进行查询，须进行相应的入参，比如：{\"linkUserName\":\"张三\"}")
    @RequestMapping(value = "/queryuserlinkuser", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto<List<ItripUserLinkUser>> queryUserLinkUser(HttpServletRequest request,
                                                    @ApiParam(name = "itripSearchUserLinkUserVO", value = "itripSearchUserLinkUserVO", required = false)
                                                    @RequestBody ItripSearchUserLinkUserVO itripSearchUserLinkUserVO){
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            ItripUser user = validateToken.getCurrentUser(token);
            List<ItripUserLinkUser> linkUsers = userInfoService.queryUserLinkUser(itripSearchUserLinkUserVO, user);
            if (EmptyUtils.isEmpty(linkUsers)){
                return DtoUtil.returnFail("获取常用联系人信息失败", ErrorCode.BIZ_GET_USER_INFO_FAILED);
            }else {
                return DtoUtil.returnDataSuccess(linkUsers);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.5.4
    @ApiOperation(value = "修改常用联系人接口", httpMethod = "POST", response = Dto.class, notes = "修改常用联系人信息")
    @RequestMapping(value = "/modifyuserlinkuser", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto modifyUserLinkUser(HttpServletRequest request,
                                 @ApiParam(name = "itripModifyUserLinkUserVO", value = "itripModifyUserLinkUserVO", required = true)
                                 @RequestBody ItripModifyUserLinkUserVO itripModifyUserLinkUserVO){
        if (EmptyUtils.isEmpty(itripModifyUserLinkUserVO)){
            return DtoUtil.returnFail("不能提交空，请填写常用联系人信息", ErrorCode.BIZ_MODIFY_USER_INFO_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            if (userInfoService.modifyUserLinkUser(itripModifyUserLinkUserVO)){
                return DtoUtil.returnSuccess("修改常用联系人信息成功");
            }else {
                return DtoUtil.returnFail("修改常用联系人失败", ErrorCode.BIZ_MODIFY_USER_INFO_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

}
