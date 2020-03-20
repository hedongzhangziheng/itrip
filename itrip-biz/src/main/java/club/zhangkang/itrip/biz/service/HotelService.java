package club.zhangkang.itrip.biz.service;

import club.zhangkang.itrip.beans.vo.ItripAreaDicVO;
import club.zhangkang.itrip.beans.vo.ItripImageVO;
import club.zhangkang.itrip.beans.vo.ItripLabelDicVO;
import club.zhangkang.itrip.beans.vo.hotel.HotelVideoDescVO;
import club.zhangkang.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import club.zhangkang.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import club.zhangkang.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;

import java.util.List;

public interface HotelService {
    List<ItripAreaDicVO> queryTradeArea(Long cityId) throws Exception;
    List<ItripImageVO> getImg(Long targetId) throws Exception;
    HotelVideoDescVO getVideoDesc(Long hotelId) throws Exception;
    ItripSearchFacilitiesHotelVO queryHotelFacilities(Long id) throws Exception;
    List<ItripLabelDicVO> queryHotelFeature() throws Exception;
    List<ItripAreaDicVO> queryHotCity(Integer type) throws Exception;
    List<ItripSearchDetailsHotelVO> queryHotelDetails(Long id) throws Exception;
    ItripSearchPolicyHotelVO queryHotelPolicy(Long id) throws Exception;
}
