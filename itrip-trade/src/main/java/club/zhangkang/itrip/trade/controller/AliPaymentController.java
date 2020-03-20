package club.zhangkang.itrip.trade.controller;

import club.zhangkang.itrip.beans.dto.Dto;
import club.zhangkang.itrip.beans.pojo.ItripHotelOrder;
import club.zhangkang.itrip.trade.bean.AlipayBean;
import club.zhangkang.itrip.trade.bean.AlipayConfig;
import club.zhangkang.itrip.trade.service.OrderService;
import club.zhangkang.itrip.trade.service.PayService;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;

@Api(value = "支付controller", tags = "支付操作接口")
@Controller
@RequestMapping("/api")
public class AliPaymentController {

    @Resource
    private OrderService orderService;

    @Resource
    private PayService payService;

    @Resource
    private AlipayConfig alipayConfig;

    @ApiOperation(value = "预支付操作接口", httpMethod = "GET", response = String.class, notes = "支付前最后确认信息,返回的是个视图")
    @RequestMapping(value = "/prepay/{orderNo}", method = RequestMethod.GET)
    public String prePay(@ApiParam(name = "orderNo", value = "订单号", required = true) @PathVariable String orderNo, Model model) {
        try {
            ItripHotelOrder order = orderService.loadItripHotelOrder(orderNo);
            model.addAttribute("orderNo", orderNo);
            model.addAttribute("hotelName", order.getHotelName());
            model.addAttribute("payAmount", order.getPayAmount());
            model.addAttribute("roomId", order.getRoomId());
            model.addAttribute("count", order.getCount());
            return "pay";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "notfound";
    }

    @ApiOperation(value = "支付操作接口", httpMethod = "POST", response = String.class, notes = "跳转到支付宝支付的页面")
    @RequestMapping(value = "/pay", method = RequestMethod.POST, produces = "text/html;charset=utf-8")
    @ResponseBody
    public String pay(@ApiParam(name = "bean", value = "alipayBean", required = true) AlipayBean bean) {
        try {
            return payService.alipay(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ApiOperation(value = "异步通知接口", httpMethod = "POST", response = String.class, notes = "支付结果以异步通知的结果为准，所以对订单的操作在此接口进行。此接口返回视图，告诉客户支付是否成功")
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public String trackPaymentStatus(HttpServletRequest request) throws Exception {
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType()); //调用SDK验证签名
        if (signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            if (orderService.processed(out_trade_no)) {
                //支付宝交易号
                String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //交易状态
                String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
                if (trade_status.equals("TRADE_FINISHED")) {
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序
                    //payType为1表示支付方式为支付宝
                    orderService.paySuccess(out_trade_no, 1, trade_no);
                    //注意：
                    //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                    return "success";
                } else if (trade_status.equals("TRADE_SUCCESS")) {
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序
                    orderService.paySuccess(out_trade_no, 1, trade_no);
                    //注意：
                    //付款完成后，支付宝系统发送该交易状态通知
                    return "success";
                } else {
                    //不是完成，也不是成功，就表示是客户取消了订单，则此订单作废
                    orderService.payFailed(out_trade_no, 1, trade_no);
                    return "failure";
                }
            }/*else {
                return "failure";
            }*/
        }/*else{
            return "failure";
        }*/
        return "failure";
    }

    //同步请求
    @ApiOperation(value = "同步通知操作接口", httpMethod = "GET", response = void.class, notes = "同步通知后跳转页面")
    @RequestMapping(value = "/return", method = RequestMethod.GET)
    public void success(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType()); //调用SDK验证签名
        if (signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            Long id = orderService.loadItripHotelOrder(out_trade_no).getId();
            response.sendRedirect(String.format(alipayConfig.getPaymentSuccessUrl(), out_trade_no, id));
        } else {
            response.sendRedirect(String.format(alipayConfig.getPaymentFailureUrl()));
        }
    }

    //以下方法都没有用到，随便写的
    @ApiOperation(value = "查询支付结果操作接口", httpMethod = "GET", response = String.class, notes = "查询某订单支付结果")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ResponseBody
    public String query(HttpServletRequest request) {
        //商户订单号，商户网站订单系统中唯一订单号
        try {
            String out_trade_no = new String(request.getParameter("WIDTQout_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            String trade_no = new String(request.getParameter("WIDTQtrade_no").getBytes("ISO-8859-1"), "UTF-8");
            return payService.query(out_trade_no, trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //退款。因为这个项目没有设计退款，所以没有对订单做任何操作。
    //退款应该是需要单独设计一个字段的。酒店都有押金，所以正常消费也会有退款，而已支付但实际没住也有退款，所以不应该跟现有的订单状态用一个字段表示
    @ApiOperation(value = "退款操作接口", httpMethod = "POST", response = String.class,
            notes = "当交易发生之后一段时间内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付款退还给买家，支付宝将在收到退款请求并且验证成功之后，按照退款规则将支付款按原路退到买家帐号上。" +
                    " 交易超过约定时间（签约时设置的可退款时间）的订单无法进行退款 支付宝退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。" +
                    "一笔退款失败后重新提交，要采用原来的退款单号。总退款金额不能超过用户实际支付金额")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    @ResponseBody
    public String refund(HttpServletRequest request) {
        try {
            //商户订单号，商户网站订单系统中唯一订单号
            String out_trade_no = new String(request.getParameter("WIDTRout_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("WIDTRtrade_no").getBytes("ISO-8859-1"), "UTF-8");
            //需要退款的金额，该金额不能大于订单金额，必填
            String refund_amount = new String(request.getParameter("WIDTRrefund_amount").getBytes("ISO-8859-1"), "UTF-8");
            //退款的原因说明
            String refund_reason = new String(request.getParameter("WIDTRrefund_reason").getBytes("ISO-8859-1"), "UTF-8");
            //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
            String out_request_no = new String(request.getParameter("WIDTRout_request_no").getBytes("ISO-8859-1"), "UTF-8");
            return payService.refund(out_trade_no, trade_no, refund_amount, refund_reason, out_request_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ApiOperation(value = "退款查询操作接口", httpMethod = "GET", response = String.class, notes = "查询退款信息")
    @RequestMapping(value = "/refundQuery", method = RequestMethod.GET)
    @ResponseBody
    public String refundQuery(HttpServletRequest request) {
        try {
            //商户订单号，商户网站订单系统中唯一订单号
            String out_trade_no = new String(request.getParameter("WIDRQout_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("WIDRQtrade_no").getBytes("ISO-8859-1"), "UTF-8");
            //请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号，必填
            String out_request_no = new String(request.getParameter("WIDRQout_request_no").getBytes("ISO-8859-1"), "UTF-8");
            return payService.refundQuery(out_trade_no, trade_no, out_request_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ApiOperation(value = "关闭交易操作接口", httpMethod = "GET", response = String.class, notes = "通常交易关闭是通过 alipay.trade.page.pay 中的超时时间来控制，而通过此接口可以提前关闭指定交易。成功关闭交易后该交易不可支付。")
    @RequestMapping(value = "/close", method = RequestMethod.GET)
    @ResponseBody
    public String close(HttpServletRequest request) {
        try {
            //商户订单号，商户网站订单系统中唯一订单号
            String out_trade_no = new String(request.getParameter("WIDTCout_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("WIDTCtrade_no").getBytes("ISO-8859-1"), "UTF-8");
            return payService.close(out_trade_no, trade_no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ApiOperation(value = "下载账单", httpMethod = "GET", response = Dto.class,
            notes = "账单时间：日账单格式为yyyy-MM-dd，最早可下载2016年1月1日开始的日账单；\n" +
            "月账单格式为yyyy-MM，最早可下载2016年1月开始的月账单。")
    @RequestMapping(value = "/billDownload", method = RequestMethod.GET)
    public void billDownload(@ApiParam(name = "bill_date", value = "账单日期", required = true) String bill_date) {
        try {
            String json = payService.billDownload(bill_date);
            JSONObject download = JSONObject.parseObject(json);
            //将接口返回的对账单下载地址传入urlStr
            //账单下载地址链接，获取连接后30秒后未下载，链接地址失效。
            String urlStr = download.getString("bill_download_url");
            //指定希望保存的文件路径
            String filePath = "E:/temp";
            URL url = null;
            HttpURLConnection httpUrlConnection = null;
            InputStream fis = null;
            FileOutputStream fos = null;
            try {
                url = new URL(urlStr);
                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setConnectTimeout(5 * 1000);
                httpUrlConnection.setDoInput(true);
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setRequestMethod("GET");
                httpUrlConnection.setRequestProperty("Charsert", "UTF-8");
                httpUrlConnection.connect();
                fis = httpUrlConnection.getInputStream();
                byte[] temp = new byte[1024];
                int b;
                fos = new FileOutputStream(new File(filePath));
                while ((b = fis.read(temp)) != -1) {
                    fos.write(temp, 0, b);
                    fos.flush();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (fos != null) fos.close();
                    if (httpUrlConnection != null) httpUrlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
