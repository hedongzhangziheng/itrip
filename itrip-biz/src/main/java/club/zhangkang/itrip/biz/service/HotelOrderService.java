package club.zhangkang.itrip.biz.service;

import club.zhangkang.itrip.beans.pojo.ItripHotelOrder;
import club.zhangkang.itrip.beans.pojo.ItripUser;
import club.zhangkang.itrip.beans.vo.order.*;
import club.zhangkang.itrip.common.Page;

import java.util.Map;

public interface HotelOrderService {
    //这个方法其实可以撤掉，因为其逻辑跟getPreorderInfo(ValidateRoomStoreVO vo)类似
    boolean validateRoomStore(ValidateRoomStoreVO validateRoomStoreVO) throws Exception;
    ItripPersonalHotelOrderVO getPersonalOrderInfo(Long orderId) throws Exception;
    ItripPersonalOrderRoomVO getPersonalOrderRoomInfo(Long orderId) throws Exception;
    Page<ItripListHotelOrderVO> getPersonalOrderList(ItripSearchOrderVO vo, ItripUser user) throws Exception;
    PreAddOrderVO getPreorderInfo(ValidateRoomStoreVO vo) throws Exception;
    Map<String, Object> querySuccessOrderInfo(Long id) throws Exception;
    Map<String, Object> addHotelOrder(ItripAddHotelOrderVO vo, String token) throws Exception;
    ItripHotelOrder queryOrderById(Long orderId) throws Exception;
    boolean validatePayType(ItripModifyHotelOrderVO vo) throws Exception;
    boolean updateOrderStatusAndPaytype(ItripModifyHotelOrderVO vo, ItripUser user) throws Exception;
    void flushOrderStatus(String type) throws Exception;
}
