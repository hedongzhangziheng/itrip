package club.zhangkang.itrip.biz.service.impl;

import club.zhangkang.itrip.beans.pojo.ItripAreaDic;
import club.zhangkang.itrip.beans.pojo.ItripHotel;
import club.zhangkang.itrip.beans.pojo.ItripLabelDic;
import club.zhangkang.itrip.beans.vo.ItripAreaDicVO;
import club.zhangkang.itrip.beans.vo.ItripImageVO;
import club.zhangkang.itrip.beans.vo.ItripLabelDicVO;
import club.zhangkang.itrip.beans.vo.hotel.HotelVideoDescVO;
import club.zhangkang.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import club.zhangkang.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import club.zhangkang.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;
import club.zhangkang.itrip.biz.service.HotelService;
import club.zhangkang.itrip.dao.areadic.ItripAreaDicMapper;
import club.zhangkang.itrip.dao.hotel.ItripHotelMapper;
import club.zhangkang.itrip.dao.hotelfeature.ItripHotelFeatureMapper;
import club.zhangkang.itrip.dao.image.ItripImageMapper;
import club.zhangkang.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelServiceImpl implements HotelService {

    @Resource
    private ItripAreaDicMapper areaDicMapper;

    @Resource
    private ItripHotelMapper hotelMapper;

    @Resource
    private ItripImageMapper imageMapper;

    @Resource
    private ItripLabelDicMapper labelDicMapper;

    @Override
    public List<ItripAreaDicVO> queryTradeArea(Long cityId) throws Exception {
        List<ItripAreaDicVO> areaDicVOS = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("isActivated", 1);
        map.put("parent", cityId);
        map.put("isTradingArea", 1);
        List<ItripAreaDic> areaDicListByMap = areaDicMapper.getItripAreaDicListByMap(map);
        for(ItripAreaDic areaDic : areaDicListByMap){
            ItripAreaDicVO vo = new ItripAreaDicVO();
            BeanUtils.copyProperties(areaDic, vo);
            areaDicVOS.add(vo);
        }
        return areaDicVOS;
    }

    @Override
    public List<ItripImageVO> getImg(Long targetId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("type", 0);
        map.put("targetId", targetId);
        return imageMapper.getItripImageListByMap(map);
    }

    @Override
    public HotelVideoDescVO getVideoDesc(Long hotelId) throws Exception {
        HotelVideoDescVO hotelVideoDescVO = new HotelVideoDescVO();
        List<String> feature = new ArrayList<>();
        List<String> areaDics = new ArrayList<>();
        //特色
        List<ItripLabelDic> hotelFeatureByHotelId = hotelMapper.getHotelFeatureByHotelId(hotelId);
        for (ItripLabelDic labelDic : hotelFeatureByHotelId){
            feature.add(labelDic.getName());
        }
        hotelVideoDescVO.setHotelFeatureList(feature);
        //商圈
        List<ItripAreaDic> hotelAreaByHotelId = hotelMapper.getHotelAreaByHotelId(hotelId);
        for (ItripAreaDic areaDic : hotelAreaByHotelId){
            areaDics.add(areaDic.getName());
        }
        hotelVideoDescVO.setTradingAreaNameList(areaDics);
        //名称
        ItripHotel itripHotel = hotelMapper.getItripHotelById(hotelId);
        hotelVideoDescVO.setHotelName(itripHotel.getHotelName());

        return hotelVideoDescVO;
    }

    @Override
    public ItripSearchFacilitiesHotelVO queryHotelFacilities(Long id) throws Exception {
        return hotelMapper.getItripHotelFacilitiesById(id);
    }

    @Override
    public List<ItripLabelDicVO> queryHotelFeature() throws Exception {
        return labelDicMapper.getItripLabelDicByParentId(16l);
    }

    @Override
    public List<ItripAreaDicVO> queryHotCity(Integer type) throws Exception {
        List<ItripAreaDicVO> hotCity = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("isActivated", 1);
        map.put("isHot", 1);
        map.put("isChina", type);
        List<ItripAreaDic> itripAreaDicListByMap = areaDicMapper.getItripAreaDicListByMap(map);
        for (ItripAreaDic areaDic : itripAreaDicListByMap){
            ItripAreaDicVO vo = new ItripAreaDicVO();
            BeanUtils.copyProperties(areaDic, vo);
            hotCity.add(vo);
        }
        return hotCity;
    }

    @Override
    public List<ItripSearchDetailsHotelVO> queryHotelDetails(Long id) throws Exception {
        List<ItripSearchDetailsHotelVO> searchDetailsHotelVOS = new ArrayList<>();
        List<ItripLabelDic> hotelFeatureByHotelId = hotelMapper.getHotelFeatureByHotelId(id);
        for (ItripLabelDic labelDic : hotelFeatureByHotelId){
            ItripSearchDetailsHotelVO vo = new ItripSearchDetailsHotelVO();
            BeanUtils.copyProperties(labelDic, vo);
            searchDetailsHotelVOS.add(vo);
        }
        return searchDetailsHotelVOS;
    }

    @Override
    public ItripSearchPolicyHotelVO queryHotelPolicy(Long id) throws Exception {
        return hotelMapper.queryHotelPolicy(id);
    }

}
