package club.zhangkang.itrip.trade.service;

import club.zhangkang.itrip.trade.bean.AlipayBean;

public interface PayService {
    String alipay(AlipayBean alipayBean) throws Exception;
    //下面的方法并没有用到
    String query(String out_trade_no, String trade_no) throws Exception;
    String refund(String out_trade_no, String trade_no, String refund_amount, String refund_reason, String out_request_no) throws Exception;
    String refundQuery(String out_trade_no, String trade_no, String out_request_no) throws Exception;
    String close(String out_trade_no, String trade_no) throws Exception;
    String billDownload(String bill_date) throws Exception;
}
