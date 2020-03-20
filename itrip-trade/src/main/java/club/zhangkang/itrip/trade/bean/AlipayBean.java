package club.zhangkang.itrip.trade.bean;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AlipayBean {
    //商户订单号，必填
    private String out_trade_no;
    //订单名称，必填
    private String subject;
    //付款金额，必填
    private String total_amount;
    //商品描述，可空
    private String body;
    //超时时间参数
    //该笔订单允许的最晚付款时间，逾期将关闭交易。
    //取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
    //该参数数值不接受小数点， 如 1.5h，可转换为 90m
    private String timeout_express="30m";
    //销售产品码，与支付宝签约的产品码名称。
    //注：目前仅支持FAST_INSTANT_TRADE_PAY
    private String product_code="FAST_INSTANT_TRADE_PAY";
}
