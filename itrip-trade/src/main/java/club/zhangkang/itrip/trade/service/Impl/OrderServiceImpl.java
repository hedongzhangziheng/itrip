package club.zhangkang.itrip.trade.service.Impl;

import club.zhangkang.itrip.beans.pojo.ItripHotelOrder;
import club.zhangkang.itrip.common.EmptyUtils;
import club.zhangkang.itrip.dao.hotelorder.ItripHotelOrderMapper;
import club.zhangkang.itrip.trade.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Resource
    private ItripHotelOrderMapper orderMapper;

    @Override
    public ItripHotelOrder loadItripHotelOrder(String orderNo) throws Exception {
        Map map = new HashMap();
        map.put("orderNo",orderNo);
        List<ItripHotelOrder> list = orderMapper.getItripHotelOrderListByMap(map);
        if(list.size() == 1){
            return list.get(0);
        }
        return null;
    }

    @Override
    public void paySuccess(String orderNo, int payType, String tradeNo) throws Exception {
        //表示客户支付成功
        ItripHotelOrder order = loadItripHotelOrder(orderNo);
        order.setOrderStatus(2);
        order.setPayType(payType);
        order.setTradeNo(tradeNo);
        order.setModifyDate(new Date());
        orderMapper.updateItripHotelOrder(order);

    }

    @Override
    public void payFailed(String orderNo, int payType, String tradeNo) throws Exception {
        //代表客户取消订单
        ItripHotelOrder order = loadItripHotelOrder(orderNo);
        order.setOrderStatus(1);
        order.setPayType(payType);
        order.setTradeNo(tradeNo);
        order.setModifyDate(new Date());
        orderMapper.updateItripHotelOrder(order);

    }

    @Override
    public boolean processed(String orderNo) throws Exception {
        //该方法判断该订单是否已与某个支付宝订单关联，如果已关联返回false，没有返回true
        ItripHotelOrder order = loadItripHotelOrder(orderNo);
        return EmptyUtils.isEmpty(order.getTradeNo());
    }

}
