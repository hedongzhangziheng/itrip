package club.zhangkang.itrip.search.service;

import club.zhangkang.itrip.beans.vo.hotel.SearchHotelVO;
import club.zhangkang.itrip.common.Page;
import club.zhangkang.itrip.search.beans.ItripHotelVO;

import java.util.List;

public interface SearchService {
    Page<ItripHotelVO> queryHotelPage(SearchHotelVO vo, Integer pageNo, Integer pageSize) throws Exception;
    List<ItripHotelVO> queryHotCity(Integer cityId, Integer count) throws Exception;
}
