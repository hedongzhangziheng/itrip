package club.zhangkang.itrip.auth.controller;

import club.zhangkang.itrip.beans.dto.Dto;
import club.zhangkang.itrip.beans.vo.ItripTokenVO;
import club.zhangkang.itrip.common.DtoUtil;
import club.zhangkang.itrip.common.ErrorCode;
import club.zhangkang.itrip.auth.service.TokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

@Api(value = "tokenController", tags = "token操作接口")
@Controller
@RequestMapping("/api")
public class TokenController {

    @Resource
    private TokenService tokenService;

    @ApiOperation(value = "置换token", httpMethod = "POST", response = Dto.class, notes = "token时效两小时，当还剩下10分钟到期时，自动调用该方法置换token")
    @RequestMapping(value = "/retoken", method = RequestMethod.POST)
    @ResponseBody
    public Dto reloadToken(HttpServletRequest request){
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            String newToken = tokenService.reloadToken(userAgent, token);
            ItripTokenVO vo = new ItripTokenVO(newToken,
                    Calendar.getInstance().getTimeInMillis() + 2 * 60 * 60 *1000,
                    Calendar.getInstance().getTimeInMillis());
            return DtoUtil.returnDataSuccess(vo);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
        }
    }

}
