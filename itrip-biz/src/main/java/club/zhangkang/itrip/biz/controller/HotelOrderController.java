package club.zhangkang.itrip.biz.controller;

import club.zhangkang.itrip.beans.dto.Dto;
import club.zhangkang.itrip.beans.pojo.ItripHotelOrder;
import club.zhangkang.itrip.beans.pojo.ItripUser;
import club.zhangkang.itrip.beans.vo.order.*;
import club.zhangkang.itrip.biz.service.HotelOrderService;
import club.zhangkang.itrip.common.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(value = "订单controller", tags = "订单信息操作接口")
@Controller
@RequestMapping("/api/hotelorder")
public class HotelOrderController {

    @Resource
    private ValidationToken validateToken;

    @Resource
    private HotelOrderService orderService;

    //3.2.1
    @ApiOperation(value = "修改订房日期，验证是否有房", httpMethod = "POST", response = Dto.class, notes = "")
    @RequestMapping(value = "/validateroomstore", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto<Map<String, Boolean>> validateRoomStore(HttpServletRequest request,
                                      @ApiParam(name = "validateRoomStoreVO", value = "validateRoomStoreVO", required = true)
                                      @RequestBody ValidateRoomStoreVO validateRoomStoreVO){
        if (EmptyUtils.isEmpty(validateRoomStoreVO.getHotelId())){
            return DtoUtil.returnFail("hotelId不能为空", ErrorCode.BIZ_ORDER_VALIDATE_ROOM_STORE_HOTEL_ID_NOTNULL);
        }
        if (EmptyUtils.isEmpty(validateRoomStoreVO.getRoomId())){
            return DtoUtil.returnFail("roomId不能为空", ErrorCode.BIZ_ORDER_VALIDATE_ROOM_STORE_ROOM_ID_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            Map<String, Boolean> map = new HashMap<>();
            if (orderService.validateRoomStore(validateRoomStoreVO)){
                map.put("flag", true);
                return DtoUtil.returnSuccess("所预定日期有该房型", map);
            }else {
                map.put("flag", false);
                return DtoUtil.returnSuccess("所预定日期没有该房型", map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_ORDER_VALIDATE_ROOM_STORE_UNKNOWN);
        }
    }

    //3.2.2
    @ApiOperation(value = "根据订单ID查看个人订单详情", httpMethod = "GET", response = Dto.class,
            notes = "订单状态(orderStatus)（0：待支付 1:已取消 2:支付成功 3:已消费 4：已点评）：\n" +
            "\n" +
            "订单流程：\n" +
            "\n" +
            "订单状态(0：待支付 2:支付成功 3:已消费 4:已点评)的流程：{\"1\":\"订单提交\",\"2\":\"订单支付\",\"3\":\"支付成功\",\"4\":\"入住\",\"5\":\"订单点评\",\"6\":\"订单完成\"}\n" +
            "\n" +
            "订单状态(1:已取消)的流程：{\"1\":\"订单提交\",\"2\":\"订单支付\",\"3\":\"订单取消\"}\n" +
            "\n" +
            "支持支付类型(roomPayType)：{\"1\":\"在线付\",\"2\":\"线下付\",\"3\":\"不限\"}")
    @RequestMapping(value = "/getpersonalorderinfo/{orderId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<ItripPersonalHotelOrderVO> getPersonalOrderInfo(HttpServletRequest request,
                                                               @ApiParam(name = "orderId", value = "订单id", required = true)
                                                               @PathVariable String orderId){
        if (EmptyUtils.isEmpty(orderId)){
            return DtoUtil.returnFail("请传递参数：orderId", ErrorCode.BIZ_GET_ORDER_INFO_ORDER_ID_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            ItripPersonalHotelOrderVO orderInfo = orderService.getPersonalOrderInfo(Long.parseLong(orderId));
            if (EmptyUtils.isEmpty(orderInfo)){
                return DtoUtil.returnFail("没有相关订单信息", ErrorCode.BIZ_ORDER_INFO_ISNULL);
            }else {
                return DtoUtil.returnDataSuccess(orderInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取个人订单信息错误", ErrorCode.BIZ_GET_ORDER_INFO_FAILED);
        }
    }

    //3.2.3
    @ApiOperation(value = "根据订单ID查看个人订单详情-房型相关信息", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/getpersonalorderroominfo/{orderId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<ItripPersonalOrderRoomVO> getPersonalOrderRoomInfo(HttpServletRequest request,
                               @ApiParam(name = "orderId", value = "订单id", required = true)
                               @PathVariable String orderId){
        if (EmptyUtils.isEmpty(orderId)){
            return DtoUtil.returnFail("请传递参数：orderId", ErrorCode.BIZ_GET_ORDER_ROOM_INFO_ORDER_ID_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            ItripPersonalOrderRoomVO orderRoomInfo = orderService.getPersonalOrderRoomInfo(Long.parseLong(orderId));
            if (EmptyUtils.isEmpty(orderRoomInfo)){
                return DtoUtil.returnFail("没有相关订单房型信息", ErrorCode.BIZ_ORDER_ROOM_INFO_ISNULL);
            }else {
                return DtoUtil.returnDataSuccess(orderRoomInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取个人订单房型信息错误", ErrorCode.BIZ_GET_ORDER_ROOM_INFO_FAILED);
        }
    }

    //3.2.4
    @ApiOperation(value = "根据条件查询个人订单列表，并分页显示", httpMethod = "POST", response = Dto.class,
            notes = "订单类型(orderType)（-1：全部订单 0:旅游订单 1:酒店订单 2：机票订单）：\n" +
            "\n" +
            "订单状态(orderStatus)（0：待支付 1:已取消 2:支付成功 3:已消费 4：已点评）：\n" +
            "\n" +
            "对于页面tab条件：\n" +
            "\n" +
            "全部订单（orderStatus：-1）\n" +
            "\n" +
            "未出行（orderStatus：2）\n" +
            "\n" +
            "待付款（orderStatus：0）\n" +
            "\n" +
            "待评论（orderStatus：3）\n" +
            "\n" +
            "已取消（orderStatus：1）")
    @RequestMapping(value = "/getpersonalorderlist", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto<Page<ItripListHotelOrderVO>> getPersonalOrderList(HttpServletRequest request,
                                                                 @ApiParam(name = "itripSearchOrderVO", value = "itripSearchOrderVO", required = true)
                                                                 @RequestBody ItripSearchOrderVO itripSearchOrderVO){
        if (EmptyUtils.isEmpty(itripSearchOrderVO.getOrderType())){
            return DtoUtil.returnFail("请传递参数：orderType", ErrorCode.BIZ_GET_ORDER_LIST_TYPE_NOTNULL);
        }
        if (EmptyUtils.isEmpty(itripSearchOrderVO.getOrderStatus())){
            return DtoUtil.returnFail("请传递参数：orderStatus", ErrorCode.BIZ_ORDER_LIST_STATUS_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            ItripUser user = validateToken.getCurrentUser(token);
            Page<ItripListHotelOrderVO> page = orderService.getPersonalOrderList(itripSearchOrderVO, user);
            return DtoUtil.returnDataSuccess(page);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取个人订单列表错误", ErrorCode.BIZ_GET_ORDER_LIST_FAILED);
        }
    }

    //3.2.5
    @ApiOperation(value = "扫描中间表,执行库存更新操作", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/scanTradeEnd", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto scanTradeEnd(HttpServletRequest request){
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_SCAN_UNKNOWN);
        }
        return null;
    }

    //3.2.6
    @ApiOperation(value = "生成订单前,获取预订信息", httpMethod = "POST", response = Dto.class, notes = "")
    @RequestMapping(value = "/getpreorderinfo", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto<PreAddOrderVO> getPreorderInfo(HttpServletRequest request,
                               @ApiParam(name = "validateRoomStoreVO", value = "validateRoomStoreVO", required = true)
                               @RequestBody ValidateRoomStoreVO validateRoomStoreVO){
        if (EmptyUtils.isEmpty(validateRoomStoreVO.getHotelId())){
            return DtoUtil.returnFail("hotelId不能为空", ErrorCode.BIZ_GET_PREORDER_HOTEL_ID_NOTNULL);
        }
        if (EmptyUtils.isEmpty(validateRoomStoreVO.getRoomId())){
            return DtoUtil.returnFail("roomId不能为空", ErrorCode.BIZ_GET_PREORDER_ROOM_ID_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            PreAddOrderVO preorderInfo = orderService.getPreorderInfo(validateRoomStoreVO);
            if (preorderInfo.getStore() < preorderInfo.getCount()){
                return DtoUtil.returnFail("暂时无房。所预订时间段的房间数为"+preorderInfo.getStore(), ErrorCode.BIZ_GET_PREORDER_ROOM_ISNULL);
            }
            return DtoUtil.returnDataSuccess(preorderInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_GET_PREORDER_UNKNOWN);
        }
    }

    //3.2.7
    @ApiOperation(value = "支付成功后查询订单信息", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/querysuccessorderinfo/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<Map<String, Object>> querySuccessOrderInfo(HttpServletRequest request,
                                                          @ApiParam(name = "id", value = "订单id", required = true)
                                                          @PathVariable Long id){
        if (EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("id不能为空", ErrorCode.BIZ_GET_ORDER_INFO_ID_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            Map<String, Object> map = orderService.querySuccessOrderInfo(id);
            return DtoUtil.returnDataSuccess(map);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取数据失败", ErrorCode.BIZ_GET_ORDER_DATA_FAILED);
        }
    }

    //3.2.8
    @ApiOperation(value = "生成订单", httpMethod = "POST", response = Dto.class, notes = "")
    @RequestMapping(value = "/addhotelorder", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto<Map<String, Object>> addHotelOrder(HttpServletRequest request,
                               @ApiParam(name = "itripAddHotelOrderVO", value = "itripAddHotelOrderVO", required = true)
                               @RequestBody ItripAddHotelOrderVO itripAddHotelOrderVO){
        if (EmptyUtils.isEmpty(itripAddHotelOrderVO)){
            return DtoUtil.returnFail("不能提交空，请填写订单信息", ErrorCode.BIZ_ADD_ORDER_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            ValidateRoomStoreVO vo = new ValidateRoomStoreVO();
            BeanUtils.copyProperties(itripAddHotelOrderVO, vo);
            if (!orderService.validateRoomStore(vo)){
                return DtoUtil.returnFail("库存不足", ErrorCode.BIZ_ADD_ORDER_STORE_ISNULL);
            }
            Map<String, Object> map = orderService.addHotelOrder(itripAddHotelOrderVO, token);
            return DtoUtil.returnSuccess("成功生成订单" ,map);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("生成订单失败", ErrorCode.BIZ_ADD_ORDER_FAILED);
        }
    }

    //3.2.9
    @ApiOperation(value = "根据订单ID获取订单信息", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/queryOrderById/{orderId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<ItripHotelOrder> queryOrderById(HttpServletRequest request,
                                               @ApiParam(name = "orderId", value = "订单id", required = true)
                                               @PathVariable Long orderId){
        if (EmptyUtils.isEmpty(orderId)){
            return DtoUtil.returnFail("orderId不能为空", ErrorCode.BIZ_GET_ORDER_INFO_ORDER_ID_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            ItripHotelOrder order = orderService.queryOrderById(orderId);
            if (EmptyUtils.isEmpty(order)){
                return DtoUtil.returnFail("没有查询到相应订单", ErrorCode.BIZ_GET_ORDER_INFO_ISNULL);
            }
            return DtoUtil.returnDataSuccess(order);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常", ErrorCode.BIZ_GET_ORDER_INFO_UNKNOWN);
        }
    }

    //3.2.10
    @ApiOperation(value = "修改订单的支付方式和状态", httpMethod = "POST", response = Dto.class, notes = "")
    @RequestMapping(value = "/updateorderstatusandpaytype", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto updateOrderStatusAndPaytype(HttpServletRequest request,
                               @ApiParam(name = "itripModifyHotelOrderVO", value = "itripModifyHotelOrderVO", required = true)
                               @RequestBody ItripModifyHotelOrderVO itripModifyHotelOrderVO){
        if (EmptyUtils.isEmpty(itripModifyHotelOrderVO)){
            return DtoUtil.returnFail("不能提交空，请填写订单信息", ErrorCode.BIZ_UPDATE_ORDER_NOTNULL);
        }
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            if (!orderService.validatePayType(itripModifyHotelOrderVO)){
                if (itripModifyHotelOrderVO.getPayType() == 3){
                    return DtoUtil.returnFail("对不起，此房间不支持线下支付", ErrorCode.BIZ_UPDATE_NOT_SUPPORT);
                }else {
                    return DtoUtil.returnFail("对不起，此房间不支持线上支付", ErrorCode.BIZ_UPDATE_NOT_SUPPORT);
                }
            }
            if (orderService.updateOrderStatusAndPaytype(itripModifyHotelOrderVO, validateToken.getCurrentUser(token))){
                return DtoUtil.returnSuccess("修改订单成功");
            }else {
                return DtoUtil.returnFail("修改订单失败", ErrorCode.BIZ_UPDATE_ORDER_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_ORDER_VALIDATE_ROOM_STORE_UNKNOWN);
        }
    }

    //刷新取消订单。也就是未在规定时间内完成支付时，刷新订单状态成已取消（0->1）
    @Scheduled(cron = "0 0/10 * * * ?")
    public void flushCancelOrderStatus(){
        try {
            orderService.flushOrderStatus("cancel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //刷新消费完成。也就是退房之后修改订单的状态为已消费（2->3）
    @Scheduled(cron = "0 0/30 * * * ?")
    public void flushSuccessOrderStatus(){
        try {
            orderService.flushOrderStatus("success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
