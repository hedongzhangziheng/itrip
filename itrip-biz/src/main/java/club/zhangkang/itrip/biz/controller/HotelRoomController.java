package club.zhangkang.itrip.biz.controller;

import club.zhangkang.itrip.beans.dto.Dto;
import club.zhangkang.itrip.beans.vo.ItripAreaDicVO;
import club.zhangkang.itrip.beans.vo.ItripImageVO;
import club.zhangkang.itrip.beans.vo.ItripLabelDicVO;
import club.zhangkang.itrip.beans.vo.hotelroom.ItripHotelRoomVO;
import club.zhangkang.itrip.beans.vo.hotelroom.SearchHotelRoomVO;
import club.zhangkang.itrip.biz.service.HotelRoomService;
import club.zhangkang.itrip.common.DtoUtil;
import club.zhangkang.itrip.common.EmptyUtils;
import club.zhangkang.itrip.common.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "酒店房间controller", tags = "酒店房间操作接口")
@Controller
@RequestMapping("/api/hotelroom")
public class HotelRoomController {

    @Resource
    private HotelRoomService hotelRoomService;

    //3.3.1
    @ApiOperation(value = "根据酒店房型ID查询酒店房型图片", httpMethod = "GET", response = Dto.class, notes = "type=1")
    @RequestMapping(value = "/getimg/{targetId}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<List<ItripImageVO>> getImg(@ApiParam(name = "targetId", value = "酒店房型id", required = true)
                                              @PathVariable String targetId){
        if (EmptyUtils.isEmpty(targetId)){
            return DtoUtil.returnFail("酒店房型id不能为空", ErrorCode.BIZ_HOTEL_ROOM_ID_NOTNULL);
        }
        try {
            List<ItripImageVO> imageVOS = hotelRoomService.getImg(Long.parseLong(targetId));
            if (EmptyUtils.isEmpty(imageVOS)){
                return DtoUtil.returnFail("获取酒店房型图片失败", ErrorCode.BIZ_GET_HOTEL_ROOM_IMG_FAILED);
            }
            return DtoUtil.returnDataSuccess(imageVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_HOTEL_ROOM_UNKNOWN);
        }
    }

    //3.3.2
    @ApiOperation(value = "查询酒店床型列表", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/queryhotelroombed", method = RequestMethod.GET)
    @ResponseBody
    public Dto<List<ItripLabelDicVO>> queryHotelRoomBed(){
        try {
            List<ItripLabelDicVO> labelDicVOS = hotelRoomService.queryHotelRoomBed();
            if (EmptyUtils.isEmpty(labelDicVOS)){
                return DtoUtil.returnFail("获取酒店房间床型失败", ErrorCode.BIZ_GET_HOTEL_ROOM_BED_FAILED);
            }
            return DtoUtil.returnDataSuccess(labelDicVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_HOTEL_ROOM_UNKNOWN);
        }
    }

    //3.3.3
    @ApiOperation(value = "查询酒店房间列表", httpMethod = "POST", response = Dto.class, notes = "")
    @RequestMapping(value = "/queryhotelroombyhotel", method = RequestMethod.POST)
    @ResponseBody
    public Dto<List<List<ItripHotelRoomVO>>> queryHotelRoomByHotel(@ApiParam(name = "vo", value = "查询酒店房间的VO", required = true)
                                                                 @RequestBody SearchHotelRoomVO vo){
        if (EmptyUtils.isEmpty(vo.getHotelId())){
            return DtoUtil.returnFail("酒店id不能为空", ErrorCode.BIZ_HOTEL_ID_NOTNULL);
        }else if (EmptyUtils.isEmpty(vo.getStartDate())){
            return DtoUtil.returnFail("入住时间不能为空", ErrorCode.BIZ_HOTEL_ID_NOTNULL);
        }else if (EmptyUtils.isEmpty(vo.getEndDate())){
            return DtoUtil.returnFail("退房时间不能为空", ErrorCode.BIZ_HOTEL_ID_NOTNULL);
        }else if (vo.getStartDate().getTime() > vo.getEndDate().getTime()){
            return DtoUtil.returnFail("入住时间不能大于退房时间", ErrorCode.BIZ_HOTEL_ID_NOTNULL);
        }
        try {
            List<List<ItripHotelRoomVO>> hotelRoomVOS = hotelRoomService.queryHotelRoomByHotel(vo);
            return DtoUtil.returnDataSuccess(hotelRoomVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_HOTEL_ROOM_UNKNOWN);
        }
    }
}
