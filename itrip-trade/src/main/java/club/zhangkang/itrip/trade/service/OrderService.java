package club.zhangkang.itrip.trade.service;

import club.zhangkang.itrip.beans.pojo.ItripHotelOrder;

public interface OrderService {
    ItripHotelOrder loadItripHotelOrder(String orderNo) throws Exception;
    void paySuccess(String orderNo, int payType, String tradeNo) throws Exception;
    void payFailed(String orderNo, int payType, String tradeNo) throws Exception;
    boolean processed(String orderNo) throws Exception;

}
