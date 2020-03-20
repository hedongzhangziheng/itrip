package club.zhangkang.itrip.auth.service.impl;

import club.zhangkang.itrip.auth.service.SmsService;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendSms(String toPhone, String templateId, String[] datas) throws Exception {
        HashMap<String, Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        restAPI.init("app.cloopen.com", "8883");
        // 初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883.
        restAPI.setAccount("8a216da8701eb7c101703936295b0a31", "824cd9f1f9eb4ca0b35d66a47ecdf08f");
        // 初始化主账号名称和主账号令牌，登陆云通讯网站后，可在控制首页中看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN。
        restAPI.setAppId("8a216da8701eb7c10170393629b90a37");
        // 请使用管理控制台中已创建应用的APPID。
        result = restAPI.sendTemplateSMS(toPhone,templateId ,datas);
        if("000000".equals(result.get("statusCode"))){
            System.out.println("短信发送成功！");
        }else{
            throw new Exception("短信发送失败！");
        }
    }

}
