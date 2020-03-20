package club.zhangkang.itrip.biz.service;

import club.zhangkang.itrip.beans.pojo.ItripUser;
import club.zhangkang.itrip.beans.vo.ItripImageVO;
import club.zhangkang.itrip.beans.vo.ItripLabelDicVO;
import club.zhangkang.itrip.beans.vo.comment.*;
import club.zhangkang.itrip.common.Page;

import java.util.List;
import java.util.Map;

public interface SystemCommentService {
    List<ItripLabelDicVO> getTravelType() throws Exception;
    List<ItripImageVO> getImg(Long targetId) throws Exception;
    boolean add(ItripAddCommentVO itripAddCommentVO, ItripUser user) throws Exception;
    Page<ItripListCommentVO> getCommentList(ItripSearchCommentVO itripSearchCommentVO) throws Exception;
    ItripHotelDescVO getHotelDesc(Long hotelId) throws Exception;
    ItripScoreCommentVO getHotelScore(Long hotelId) throws Exception;
    Map<String, Integer> getCount(Long hotelId) throws Exception;
}
