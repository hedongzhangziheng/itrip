package club.zhangkang.itrip.trade.service.Impl;

import club.zhangkang.itrip.trade.bean.AlipayBean;
import club.zhangkang.itrip.trade.bean.AlipayConfig;
import club.zhangkang.itrip.trade.service.PayService;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AlipayServiceImpl implements PayService {

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private AlipayClient alipayClient;

    @Override
    public String alipay(AlipayBean alipayBean) throws Exception {
        /*AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getUrl(),//支付宝网关
                alipayConfig.getAppID(),//appid
                alipayConfig.getRsaPrivateKey(),//商户私钥
                "json",
                alipayConfig.getCharset(),//字符编码格式
                alipayConfig.getAlipayPublicKey(),//支付宝公钥
               alipayConfig.getSignType()//签名方式
        );*/

        //2、设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        //页面跳转同步通知页面路径
        alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
        // 服务器异步通知页面路径
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());
        //封装参数
        alipayRequest.setBizContent(JSON.toJSONString(alipayBean));
        //3、请求支付宝进行付款，并获取支付结果
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        System.out.println(result);
        return result;//返回付款信息
    }

    @Override
    public String query(String out_trade_no, String trade_no) throws Exception {
        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","+"\"trade_no\":\""+ trade_no +"\"}");
        //请求
        String result = alipayClient.execute(alipayRequest).getBody();
        return result;
    }

    @Override
    public String refund(String out_trade_no, String trade_no, String refund_amount, String refund_reason, String out_request_no) throws Exception {
        //设置请求参数
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"trade_no\":\""+ trade_no +"\","
                + "\"refund_amount\":\""+ refund_amount +"\","
                + "\"refund_reason\":\""+ refund_reason +"\","
                + "\"out_request_no\":\""+ out_request_no +"\"}");
        //请求
        String result = alipayClient.execute(alipayRequest).getBody();
        return result;
    }

    @Override
    public String refundQuery(String out_trade_no, String trade_no, String out_request_no) throws Exception {
        //设置请求参数
        AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                +"\"trade_no\":\""+ trade_no +"\","
                +"\"out_request_no\":\""+ out_request_no +"\"}");
        //请求
        String result = alipayClient.execute(alipayRequest).getBody();
        return result;
    }

    @Override
    public String close(String out_trade_no, String trade_no) throws Exception {
        //设置请求参数
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," +"\"trade_no\":\""+ trade_no +"\"}");
        //请求
        String result = alipayClient.execute(alipayRequest).getBody();
        return result;
    }

    @Override
    public String billDownload(String bill_date) throws Exception {
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();//创建API对应的request类
        //设置业务参数
        //账单类型，商户通过接口或商户经开放平台授权后其所属服务商通过接口可以获取以下账单类型：trade、signcustomer；
        //trade指商户基于支付宝交易收单的业务账单；signcustomer是指基于商户支付宝余额收入及支出等资金变动的帐务账单。
        //显然一般是不需要signcustomer类型的账单的
        request.setBizContent("{" +
                        "\"bill_type\":\"trade\"," +
                        "\"bill_date\":\""+ bill_date +"\"}");
        String result = alipayClient.execute(request).getBody();
        return result;
    }
}
