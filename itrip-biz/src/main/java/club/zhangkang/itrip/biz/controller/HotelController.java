package club.zhangkang.itrip.biz.controller;

import club.zhangkang.itrip.beans.dto.Dto;
import club.zhangkang.itrip.beans.vo.ItripAreaDicVO;
import club.zhangkang.itrip.beans.vo.ItripImageVO;
import club.zhangkang.itrip.beans.vo.ItripLabelDicVO;
import club.zhangkang.itrip.beans.vo.hotel.HotelVideoDescVO;
import club.zhangkang.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import club.zhangkang.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import club.zhangkang.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;
import club.zhangkang.itrip.biz.service.HotelService;
import club.zhangkang.itrip.common.DtoUtil;
import club.zhangkang.itrip.common.EmptyUtils;
import club.zhangkang.itrip.common.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "酒店controller", tags = "酒店操作接口")
@Controller
@RequestMapping("/api/hotel")
public class HotelController {

    @Resource
    private HotelService hotelService;

    //3.1.1
    @ApiOperation(value = "根据城市查询商圈", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/querytradearea/{cityId}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<List<ItripAreaDicVO>> queryTradeArea(@ApiParam(name = "cityId", value = "城市id", required = true)
                                                        @PathVariable Long cityId){
        if (EmptyUtils.isEmpty(cityId)){
            return DtoUtil.returnFail("城市id不能为空", ErrorCode.BIZ_CITYID_NOTNULL);
        }
        try {
            List<ItripAreaDicVO> tradeArea = hotelService.queryTradeArea(cityId);
            return DtoUtil.returnDataSuccess(tradeArea);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.1.2
    @ApiOperation(value = "根据酒店ID查询酒店图片", httpMethod = "GET", response = Dto.class, notes = "type=0")
    @RequestMapping(value = "/getimg/{targetId}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<List<ItripImageVO>> getImg(@ApiParam(name = "targetId", value = "酒店id", required = true)
                                              @PathVariable String targetId){
        if (EmptyUtils.isEmpty(targetId)){
            return DtoUtil.returnFail("酒店id不能为空", ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            List<ItripImageVO> imageVOS = hotelService.getImg(Long.parseLong(targetId));
            if (imageVOS.size() == 0){
                return DtoUtil.returnFail("获取酒店图片失败", ErrorCode.BIZ_GET_PIC_FAILED);
            }
            return DtoUtil.returnDataSuccess(imageVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.1.3
    @ApiOperation(value = "根据酒店id查询酒店特色、商圈、酒店名称（视频文字描述）", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/getvideodesc/{hotelId}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<HotelVideoDescVO> getVideoDesc(@ApiParam(name = "hotelId", value = "酒店id", required = true)
                                                  @PathVariable String hotelId){
        if (EmptyUtils.isEmpty(hotelId)){
            return DtoUtil.returnFail("酒店id不能为空", ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            HotelVideoDescVO hotelVideoDescVO = hotelService.getVideoDesc(Long.parseLong(hotelId));
            return DtoUtil.returnDataSuccess(hotelVideoDescVO);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_GET_HOTEL_DESC_FAILED);
        }
    }

    //3.1.4
    @ApiOperation(value = "根据酒店id查询酒店设施", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/queryhotelfacilities/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<String> queryHotelFacilities(@ApiParam(name = "id", value = "酒店id", required = true)
                                                                      @PathVariable Long id){
        if (EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("酒店id不能为空", ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            ItripSearchFacilitiesHotelVO searchFacilitiesHotelVO = hotelService.queryHotelFacilities(id);
            return DtoUtil.returnDataSuccess(searchFacilitiesHotelVO.getFacilities());
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.1.5
    @ApiOperation(value = "获取酒店特色", httpMethod = "GET", response = Dto.class, notes = "用于查询页列表")
    @RequestMapping(value = "/queryhotelfeature", method = RequestMethod.GET)
    @ResponseBody
    public Dto<List<ItripLabelDicVO>> queryHotelFeature(){
        try {
            List<ItripLabelDicVO> hotelFeature = hotelService.queryHotelFeature();
            return DtoUtil.returnDataSuccess(hotelFeature);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.1.6
    @ApiOperation(value = "查询国内、国外的热门城市", httpMethod = "GET", response = Dto.class, notes = "1:国内 2:国外")
    @RequestMapping(value = "/queryhotcity/{type}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<List<ItripAreaDicVO>> queryHotCity(@ApiParam(name = "type", value = "城市类型，1表示国内，2表示国外", required = true)
                                                      @PathVariable Integer type){
        if (EmptyUtils.isEmpty(type)){
            return DtoUtil.returnFail("城市类型不能为空", ErrorCode.BIZ_CITY_TYPE_NOTNULL);
        }
        try {
            List<ItripAreaDicVO> hotCity = hotelService.queryHotCity(type);
            return DtoUtil.returnDataSuccess(hotCity);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.1.7
    @ApiOperation(value = "根据酒店id查询酒店特色和介绍", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/queryhoteldetails/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<List<ItripSearchDetailsHotelVO>> queryHotelDetails(@ApiParam(name = "id", value = "酒店id", required = true)
                                                                      @PathVariable Long id){
        if (EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("酒店id不能为空", ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            List<ItripSearchDetailsHotelVO> searchDetailsHotelVOS = hotelService.queryHotelDetails(id);
            return DtoUtil.returnDataSuccess(searchDetailsHotelVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.1.8
    @ApiOperation(value = "根据酒店id查询酒店政策", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/queryhotelpolicy/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<String> queryHotelPolicy(@ApiParam(name = "id", value = "酒店id", required = true)
                                                              @PathVariable Long id){
        if (EmptyUtils.isEmpty(id)){
            return DtoUtil.returnFail("酒店id不能为空", ErrorCode.BIZ_HOTELID_NOTNULL);
        }
        try {
            ItripSearchPolicyHotelVO policyHotelVO = hotelService.queryHotelPolicy(id);
            return DtoUtil.returnDataSuccess(policyHotelVO.getHotelPolicy());
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

}
