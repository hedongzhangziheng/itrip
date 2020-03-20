package club.zhangkang.itrip.biz.service.impl;

import club.zhangkang.itrip.beans.vo.ItripImageVO;
import club.zhangkang.itrip.beans.vo.ItripLabelDicVO;
import club.zhangkang.itrip.beans.vo.hotelroom.ItripHotelRoomVO;
import club.zhangkang.itrip.beans.vo.hotelroom.SearchHotelRoomVO;
import club.zhangkang.itrip.biz.service.HotelRoomService;
import club.zhangkang.itrip.common.DateUtil;
import club.zhangkang.itrip.dao.hotelroom.ItripHotelRoomMapper;
import club.zhangkang.itrip.dao.image.ItripImageMapper;
import club.zhangkang.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class HotelRoomServiceImpl implements HotelRoomService {

    @Resource
    private ItripImageMapper imageMapper;

    @Resource
    private ItripLabelDicMapper labelDicMapper;

    @Resource
    private ItripHotelRoomMapper hotelRoomMapper;

    @Override
    public List<ItripImageVO> getImg(Long targetId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        map.put("targetId", targetId);
        return imageMapper.getItripImageListByMap(map);
    }

    @Override
    public List<ItripLabelDicVO> queryHotelRoomBed() throws Exception {
        return labelDicMapper.getItripLabelDicByParentId(1l);
    }

    @Override
    public List<List<ItripHotelRoomVO>> queryHotelRoomByHotel(SearchHotelRoomVO vo) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Date startDate = vo.getStartDate();
        Date endDate = vo.getEndDate();
        List<Date> dateList = DateUtil.getBetweenDates(startDate, endDate);
        map.put("hotelId", vo.getHotelId());
        map.put("timesList", dateList);
        if (vo.getRoomBedTypeId() != null) map.put("roomBedTypeId", vo.getRoomBedTypeId());
        if (vo.getPayType() != null) map.put("payType", vo.getPayType());
        if (vo.getIsBook() != null) map.put("isBook", vo.getIsBook());
        if (vo.getIsHavingBreakfast() != null) map.put("isHavingBreakfast", vo.getIsHavingBreakfast());
        if (vo.getIsTimelyResponse() != null) map.put("isTimelyResponse", vo.getIsTimelyResponse());
        if (vo.getIsCancel() != null) map.put("isCancel", vo.getIsCancel());
        List<ItripHotelRoomVO> list = hotelRoomMapper.getItripHotelRoomListByMap(map);
        List<List<ItripHotelRoomVO>> vos = new ArrayList<>();
        for (ItripHotelRoomVO v : list){
            List<ItripHotelRoomVO> li = new ArrayList<>();
            li.add(v);
            vos.add(li);
        }
        return vos;
    }
}
