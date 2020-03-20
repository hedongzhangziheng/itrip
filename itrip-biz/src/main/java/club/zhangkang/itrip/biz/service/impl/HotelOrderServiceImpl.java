package club.zhangkang.itrip.biz.service.impl;

import club.zhangkang.itrip.beans.pojo.*;
import club.zhangkang.itrip.beans.vo.order.*;
import club.zhangkang.itrip.beans.vo.store.StoreVO;
import club.zhangkang.itrip.biz.service.HotelOrderService;
import club.zhangkang.itrip.common.*;
import club.zhangkang.itrip.dao.hotel.ItripHotelMapper;
import club.zhangkang.itrip.dao.hotelorder.ItripHotelOrderMapper;
import club.zhangkang.itrip.dao.hotelroom.ItripHotelRoomMapper;
import club.zhangkang.itrip.dao.hoteltempstore.ItripHotelTempStoreMapper;
import club.zhangkang.itrip.dao.orderlinkuser.ItripOrderLinkUserMapper;
import club.zhangkang.itrip.dao.productstore.ItripProductStoreMapper;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HotelOrderServiceImpl implements HotelOrderService {

    @Resource
    private ItripHotelTempStoreMapper storeMapper;

    @Resource
    private ItripProductStoreMapper productStoreMapper;

    @Resource
    private ItripHotelOrderMapper orderMapper;

    @Resource
    private ItripHotelRoomMapper roomMapper;

    @Resource
    private SystemConfig systemConfig;

    @Resource
    private ItripHotelMapper hotelMapper;

    @Resource
    private ItripOrderLinkUserMapper linkUserMapper;

    //获取所预订时间范围内每天的库存量
    private List<StoreVO> getStores(ValidateRoomStoreVO vo) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        //其实hotelId没用，因为roomId是唯一的。不过既然前端传过来了，给个面子
        map.put("hotelId", vo.getHotelId());
        map.put("roomId", vo.getRoomId());
        //时间集合不应该包括退房时间，这里我改了原来提供的方法
        List<Date> dateList = DateUtil.getBetweenDates(vo.getCheckInDate(), vo.getCheckOutDate());
        //查询预订日期临时库存表是否有数据，没有就给插一条
        for (Date date : dateList){
            map.put("date", date);
            Integer store = storeMapper.getStore(map);
            if (store == null){
                //给临时库存表插入预订日期数据
                ItripHotelTempStore tempStore = new ItripHotelTempStore();
                tempStore.setHotelId(vo.getHotelId());
                tempStore.setRoomId(vo.getRoomId());
                tempStore.setRecordDate(date);
                tempStore.setCreationDate(new Date());
                //去原始库存表查询库存量
                Map<String, Object> map1 = new HashMap<String, Object>();
                map1.put("productType", 1);
                map1.put("productId", vo.getRoomId());
                tempStore.setStore(productStoreMapper.getStore(map1));
                Integer integer = storeMapper.insertItripHotelTempStore(tempStore);
                if (integer < 1){
                    throw new Exception("临时库存表插入库存错误");
                }
            }
        }
        map.put("startTime", vo.getCheckInDate());
        //结束时间应该是退房时间的前一天，前边的工具类计算的集合就不包括退房时间
        map.put("endTime", dateList.get(dateList.size()-1));
        return storeMapper.queryRoomStore(map);
    }

    @Override
    public boolean validateRoomStore(ValidateRoomStoreVO vo) throws Exception {
        Integer count = vo.getCount();
        //防止前端没有传值或传来错误的值，默认给1
        count = (count == null || count < 1) ? 1: count;
        List<StoreVO> storeVOS = getStores(vo);
        if (EmptyUtils.isEmpty(storeVOS)){
            throw new Exception("查询库存时出错");
        }
        //判断所预订的日期的库存数量是否满足
        for (StoreVO svo : storeVOS){
            if (svo.getStore() < count){
                return false;
            }
        }
        return true;
    }

    @Override
    public ItripPersonalHotelOrderVO getPersonalOrderInfo(Long orderId) throws Exception {
        ItripPersonalHotelOrderVO vo = new ItripPersonalHotelOrderVO();
        ItripHotelOrder itripHotelOrder = orderMapper.getItripHotelOrderById(orderId);
        if (itripHotelOrder != null){
            BeanUtils.copyProperties(itripHotelOrder, vo);
            //获取房间的支付方式，并放进返回前端的vo对象中
            ItripHotelRoom room = roomMapper.getItripHotelRoomById(itripHotelOrder.getRoomId());
            vo.setRoomPayType(room.getPayType());
            //添加订单流程对象
            if (vo.getOrderStatus() == 1){
                Object cancel = JSON.parse(systemConfig.getOrderProcessCancel());
                vo.setOrderProcess(cancel);
            }else {
                Object ok = JSON.parse(systemConfig.getOrderProcessOK());
                vo.setOrderProcess(ok);
            }
            //添加订单流程状态
            if (vo.getOrderStatus() == 0){
                vo.setProcessNode("2");
            }else if (vo.getOrderStatus() == 1){
                vo.setProcessNode("3");
            }else if (vo.getOrderStatus() == 2){
                vo.setProcessNode("4");
            }else if (vo.getOrderStatus() == 3){
                vo.setProcessNode("5");
            }else if (vo.getOrderStatus() == 4){
                vo.setProcessNode("6");
            }
            return vo;
        }
        return null;
    }

    @Override
    public ItripPersonalOrderRoomVO getPersonalOrderRoomInfo(Long orderId) throws Exception {
        return orderMapper.getItripHotelOrderRoomInfoById(orderId);
    }

    @Override
    public Page<ItripListHotelOrderVO> getPersonalOrderList(ItripSearchOrderVO vo, ItripUser user) throws Exception {
        Integer pageNo = vo.getPageNo() != null ? vo.getPageNo() : 1;
        Integer pageSize = vo.getPageSize() != null ? vo.getPageSize() : 5;
        Map<String, Object> map = new HashMap<String, Object>();
        if (vo.getOrderType() != -1) map.put("orderType", vo.getOrderType());
        if (vo.getOrderStatus() != -1) map.put("orderStatus", vo.getOrderStatus());
        map.put("startDate", vo.getStartDate());
        map.put("endDate", vo.getEndDate());
        map.put("linkUserName", vo.getLinkUserName());
        map.put("orderNo", vo.getOrderNo());
        map.put("pageSize", pageSize);
        Page<ItripListHotelOrderVO> page = new Page<ItripListHotelOrderVO>(pageNo, pageSize, orderMapper.getOrderCountByMap(map));
        map.put("beginPos", page.getBeginPos());
        map.put("userId", user.getId());
        List<ItripListHotelOrderVO> orderVOList = orderMapper.getOrderListByMap(map);
        page.setRows(orderVOList);
        return page;
    }

    @Override
    public PreAddOrderVO getPreorderInfo(ValidateRoomStoreVO vo) throws Exception {
        PreAddOrderVO preAddOrderVO = new PreAddOrderVO();
        Integer count = vo.getCount();
        //防止前端没有传值或传来错误的值，默认给1
        count = (count == null || count < 1) ? 1: count;
        vo.setCount(count);
        BeanUtils.copyProperties(vo, preAddOrderVO);
        List<StoreVO> storeVOS = getStores(vo);
        if (EmptyUtils.isEmpty(storeVOS)){
            throw new Exception("查询库存为空！");
        }
        //获取所预订时间范围内的最小库存量
        Integer min = storeVOS.get(0).getStore();
        for (StoreVO svo : storeVOS){
            if (svo.getStore() < min) min = svo.getStore();
        }
        //返回前端的库存值应该是所预订天数内最小的那个值
        preAddOrderVO.setStore(min);
        //酒店名称
        String hotelName = hotelMapper.getItripHotelById(vo.getHotelId()).getHotelName();
        preAddOrderVO.setHotelName(hotelName);
        //房间价格
        BigDecimal roomPrice = roomMapper.getItripHotelRoomById(vo.getRoomId()).getRoomPrice();
        BigDecimal price = roomPrice.multiply(BigDecimal.valueOf(count*storeVOS.size()));
        preAddOrderVO.setPrice(price);
        return preAddOrderVO;
    }

    @Override
    public Map<String, Object> querySuccessOrderInfo(Long id) throws Exception {
        ItripHotelOrder itripHotelOrder = orderMapper.getItripHotelOrderById(id);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", itripHotelOrder.getId());
        map.put("orderNo", itripHotelOrder.getOrderNo());
        map.put("payType", itripHotelOrder.getPayType());
        map.put("payAmount", itripHotelOrder.getPayAmount());
        map.put("hotelName", itripHotelOrder.getHotelName());
        map.put("roomTitle", roomMapper.getItripHotelRoomById(itripHotelOrder.getRoomId()).getRoomTitle());
        return map;
    }

    @Override
    public Map<String, Object> addHotelOrder(ItripAddHotelOrderVO vo, String token) throws Exception {
        ItripHotelOrder order = new ItripHotelOrder();
        BeanUtils.copyProperties(vo, order);

        //拆分token
        String[] tokens = token.split("-");

        //userId
        String userIdStr = tokens[2];
        Long userId = Long.parseLong(userIdStr);
        order.setUserId(userId);

        //生成订单号：机器码 +日期+（MD5）（商品IDs+毫秒数+1000000的随机数）
        StringBuilder md5String = new StringBuilder();
        md5String.append(order.getHotelId());
        md5String.append(order.getRoomId());
        md5String.append(System.currentTimeMillis());
        md5String.append(Math.random() * 1000000);
        String md5 = MD5.getMd5(md5String.toString(), 6);
        //生成订单编号
        StringBuilder orderNo = new StringBuilder();
        orderNo.append(systemConfig.getMachineCode());
        orderNo.append(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        orderNo.append(md5);
        order.setOrderNo(orderNo.toString());

        //预订天数
        List<Date> dateList = DateUtil.getBetweenDates(order.getCheckInDate(), order.getCheckOutDate());
        order.setBookingDays(dateList.size());

        //订单状态
        order.setOrderStatus(0);

        //订单金额
        BigDecimal roomPrice = roomMapper.getItripHotelRoomById(vo.getRoomId()).getRoomPrice();
        BigDecimal price = roomPrice.multiply(BigDecimal.valueOf(vo.getCount()*dateList.size()));
        order.setPayAmount(price);

        //入住人姓名
        StringBuilder linkUserName = new StringBuilder();
        List<ItripUserLinkUser> userList = vo.getLinkUser();
        for (int i = 0; i < userList.size(); i++){
            if (i == userList.size()-1){
                linkUserName.append(userList.get(i).getLinkUserName());
            }else {
                linkUserName.append(userList.get(i).getLinkUserName() + "，");
            }
        }
        order.setLinkUserName(linkUserName.toString());

        //订单生成时间
        order.setCreationDate(new Date());

        //订单创建者
        order.setCreatedBy(userId);

        //预订方式
        String bookType = tokens[0];
        if (bookType.endsWith("PC")){
            order.setBookType(0);
        }else if (bookType.endsWith("MOBILE")){
            order.setBookType(1);
        }else {
            order.setBookType(2);
        }

        //订单表插入数据
        orderMapper.insertItripHotelOrder(order);

        //订单联系人表插入数据
        ItripOrderLinkUser orderLinkUser = new ItripOrderLinkUser();
        orderLinkUser.setOrderId(order.getId());
        for (ItripUserLinkUser userLinkUser : userList){
            orderLinkUser.setLinkUserId(userLinkUser.getId());
            orderLinkUser.setLinkUserName(userLinkUser.getLinkUserName());
            orderLinkUser.setCreationDate(new Date());
            orderLinkUser.setCreatedBy(userId);
            linkUserMapper.insertItripOrderLinkUser(orderLinkUser);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", order.getId());
        map.put("orderNo", orderNo);
        return map;
    }

    @Override
    public ItripHotelOrder queryOrderById(Long orderId) throws Exception {
        return orderMapper.getItripHotelOrderById(orderId);
    }

    @Override
    public boolean validatePayType(ItripModifyHotelOrderVO vo) throws Exception {
        ItripHotelRoom room = roomMapper.getItripHotelRoomById(vo.getRoomId());
        if (room.getPayType() == 3) return true;
        if (room.getPayType() == 2 && vo.getPayType() == 3) return true;
        if (room.getPayType() == 1 && (vo.getPayType() == 1 || vo.getPayType() == 2)) return true;
        return false;
    }

    @Override
    public boolean updateOrderStatusAndPaytype(ItripModifyHotelOrderVO vo, ItripUser user) throws Exception {
        ItripHotelOrder order = new ItripHotelOrder();
        BeanUtils.copyProperties(vo, order);

        //修改userId（一般是和原来一样的）
        order.setUserId(user.getId());

        //更新入住人信息
        ItripOrderLinkUser itripOrderLinkUser = new ItripOrderLinkUser();
        itripOrderLinkUser.setOrderId(vo.getId());
        for (ItripOrderLinkUserVo linkUserVo : vo.getItripOrderLinkUserList()){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("orderId", vo.getId());
            map.put("linkUserId", linkUserVo.getLinkUserId());
            List<ItripOrderLinkUserVo> userVos = linkUserMapper.getItripOrderLinkUserListByMap(map);
            itripOrderLinkUser.setLinkUserId(linkUserVo.getLinkUserId());
            itripOrderLinkUser.setLinkUserName(linkUserVo.getLinkUserName());
            if (EmptyUtils.isEmpty(userVos)){
                itripOrderLinkUser.setCreationDate(new Date());
                itripOrderLinkUser.setCreatedBy(user.getId());
                Integer integer = linkUserMapper.insertItripOrderLinkUser(itripOrderLinkUser);
                if (integer < 1){
                    throw new Exception("新增入住人信息失败");
                }
            }else {
                itripOrderLinkUser.setModifyDate(new Date());
                itripOrderLinkUser.setModifiedBy(user.getId());
                Integer integer = linkUserMapper.updateItripOrderLinkUser(itripOrderLinkUser);
            }
        }

        //修改时间
        order.setModifyDate(new Date());
        //修改人
        order.setModifiedBy(user.getId());

        Integer integer = orderMapper.updateItripHotelOrder(order);
        return true;
    }

    @Override
    public void flushOrderStatus(String type) throws Exception {
        if ("cancel".equals(type)) orderMapper.flushCancelOrderStatus();
        if ("success".equals(type)) orderMapper.flushSuccessOrderStatus();
    }
    
}
