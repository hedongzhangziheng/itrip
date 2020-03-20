package club.zhangkang.itrip.biz.service;

import club.zhangkang.itrip.beans.vo.ItripImageVO;
import club.zhangkang.itrip.beans.vo.ItripLabelDicVO;
import club.zhangkang.itrip.beans.vo.hotelroom.ItripHotelRoomVO;
import club.zhangkang.itrip.beans.vo.hotelroom.SearchHotelRoomVO;

import java.util.List;

public interface HotelRoomService {
    List<ItripImageVO> getImg(Long targetId) throws Exception;
    List<ItripLabelDicVO> queryHotelRoomBed() throws Exception;
    List<List<ItripHotelRoomVO>> queryHotelRoomByHotel(SearchHotelRoomVO vo) throws Exception;
}
