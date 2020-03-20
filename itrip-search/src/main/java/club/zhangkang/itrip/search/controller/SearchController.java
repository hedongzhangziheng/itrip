package club.zhangkang.itrip.search.controller;

import club.zhangkang.itrip.beans.dto.Dto;
import club.zhangkang.itrip.beans.vo.hotel.SearchHotCityVO;
import club.zhangkang.itrip.beans.vo.hotel.SearchHotelVO;
import club.zhangkang.itrip.common.DtoUtil;
import club.zhangkang.itrip.common.EmptyUtils;
import club.zhangkang.itrip.common.ErrorCode;
import club.zhangkang.itrip.common.Page;
import club.zhangkang.itrip.search.beans.ItripHotelVO;
import club.zhangkang.itrip.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "搜索controller", tags = "搜索操作接口")
@Controller
@RequestMapping("/api/hotellist")
public class SearchController {

    @Resource
    private SearchService searchService;

    @ApiOperation(value = "根据热门城市查询酒店", httpMethod = "POST", response = Dto.class, notes = "")
    @RequestMapping(value="/searchItripHotelListByHotCity", method = RequestMethod.POST)
    @ResponseBody
    public Dto<ItripHotelVO> queryHotCity(@ApiParam(name = "vo", value = "搜索热门城市酒店VO", required = true) @RequestBody SearchHotCityVO vo){
        if(vo.getCityId() == null){
            return DtoUtil.returnFail("城市id不能为空", ErrorCode.SEARCH_CITYID_NOTFOUND);
        }else{
            List<ItripHotelVO> list = null;
            try {
                list = searchService.queryHotCity(vo.getCityId(),vo.getCount());
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail(e.getMessage(), ErrorCode.SEARCH_UNKNOWN);
            }
            return DtoUtil.returnDataSuccess(list);
        }
    }

    @ApiOperation(value = "查询酒店分页", httpMethod = "POST", response = Dto.class, notes = "用于查询酒店列表")
    @RequestMapping(value="/searchItripHotelPage", method = RequestMethod.POST)
    @ResponseBody
    public Dto<ItripHotelVO> queryHotel(@ApiParam(name = "vo", value = "搜索酒店VO", required = true) @RequestBody SearchHotelVO vo){
        if(EmptyUtils.isEmpty(vo) || EmptyUtils.isEmpty(vo.getDestination())){
            return DtoUtil.returnFail("目的地不能为空",ErrorCode.SEARCH_DESTINATION_NOTNULL);
        }
        Page<ItripHotelVO> page = null;
        try {
            page = searchService.queryHotelPage(vo,vo.getPageNo(),vo.getPageSize());
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.SEARCH_UNKNOWN);
        }
        return DtoUtil.returnDataSuccess(page);
    }
}
