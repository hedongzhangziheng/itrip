package club.zhangkang.itrip.auth.service.impl;

import club.zhangkang.itrip.beans.pojo.ItripUser;
import club.zhangkang.itrip.common.MD5;
import club.zhangkang.itrip.common.RedisUtil;
import club.zhangkang.itrip.common.UserAgentUtil;
import club.zhangkang.itrip.auth.service.TokenService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    @Resource
    private RedisUtil redisUtil;

    //token的保护时间
    private static final long PROTECTED_TIME = 30 * 60 * 1000;

    @Override
    public String generateToken(String userAgent, ItripUser user) throws Exception {
        //token:PC-userCode(md5)-userId-creationdate-random(6)
        StringBuilder str = new StringBuilder("token:");
        //判断属于哪种客户端
        if (UserAgentUtil.CheckAgent(userAgent)){
            str.append("MOBILE-");
        }else {
            str.append("PC-");
        }
        //userCode的加密
        str.append(MD5.getMd5(user.getUserCode(), 32) + "-");
        //userId
        str.append(user.getId() + "-");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //当前的时间
        str.append(sdf.format(new Date()) + "-");
        //6位随机数
        str.append(MD5.getMd5(userAgent, 6));
        return str.toString();
    }

    @Override
    public void saveToken(String token, ItripUser user) throws Exception {
        String json = JSONObject.toJSONString(user);
        if (token.startsWith("token:PC-")){
            //pc端设置过期时间为两小时
            redisUtil.setString(token, json, 2*60*60);
        }else {
            //移动端不过期，以后不再需要输入用户名和密码
            redisUtil.setString(token, json);
        }
    }

    @Override
    public boolean validateToken(String userAgent, String token) throws Exception {
        //判断redis中是否存在
        if (!redisUtil.hasKey(token)){
            return false;
        }
        String agentMD5 = token.split("-")[4];
        //判断token后六位和前端传过来的是否一致
        if (!MD5.getMd5(userAgent, 6).equals(agentMD5)){
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteToken(String token) throws Exception {
        return redisUtil.del(token);
    }

    @Override
    public String reloadToken(String userAgent, String token) throws Exception {
        //验证token是否有效
        if (!validateToken(userAgent, token)){
            throw new Exception("token无效！");
        }
        //计算token的受保护时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long generateTime = sdf.parse(token.split("-")[3]).getTime();
        long passTime = Calendar.getInstance().getTimeInMillis() - generateTime;
        //判断token是否在保护期内
        if (passTime < PROTECTED_TIME){
            throw new Exception("token处于保护期内，不允许置换，还剩下" + (PROTECTED_TIME - passTime)/1000 + "秒可以置换。");
        }
        //获取redis中的value值，也就是保存在数据库中的user对象
        ItripUser user = JSONObject.parseObject(redisUtil.getString(token), ItripUser.class);
        Long expire = redisUtil.getExpire(token);
        if (expire > 0 || expire == -1){
            //生成新token
            String newToken = generateToken(userAgent, user);
            //保存新token
            saveToken(newToken, user);
            //旧token设置五分钟的有效期
            redisUtil.setString(token, JSONObject.toJSONString(user), 5 * 60);
            return newToken;
        }else {
            throw new Exception("token过期，不能置换。");
        }
    }
}
