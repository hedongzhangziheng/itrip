package club.zhangkang.itrip.biz.service.impl;

import club.zhangkang.itrip.beans.pojo.*;
import club.zhangkang.itrip.beans.vo.ItripImageVO;
import club.zhangkang.itrip.beans.vo.ItripLabelDicVO;
import club.zhangkang.itrip.beans.vo.comment.*;
import club.zhangkang.itrip.biz.service.SystemCommentService;
import club.zhangkang.itrip.common.Page;
import club.zhangkang.itrip.dao.comment.ItripCommentMapper;
import club.zhangkang.itrip.dao.hotel.ItripHotelMapper;
import club.zhangkang.itrip.dao.hotelorder.ItripHotelOrderMapper;
import club.zhangkang.itrip.dao.image.ItripImageMapper;
import club.zhangkang.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SystemCommentServiceImpl implements SystemCommentService {

    @Resource
    private ItripLabelDicMapper labelDicMapper;

    @Resource
    private ItripImageMapper imageMapper;

    @Resource
    private ItripCommentMapper commentMapper;

    @Resource
    private ItripHotelOrderMapper orderMapper;

    @Resource
    private ItripHotelMapper hotelMapper;

    @Override
    public List<ItripLabelDicVO> getTravelType() throws Exception{
        return labelDicMapper.getItripLabelDicByParentId(107l);
    }

    @Override
    public List<ItripImageVO> getImg(Long targetId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("targetId", targetId);
        return imageMapper.getItripImageListByMap(map);
    }

    @Override
    public boolean add(ItripAddCommentVO vo, ItripUser user) throws Exception {
        boolean flag = true;
        Long userId = user.getId();
        ItripComment comment = new ItripComment();
        BeanUtils.copyProperties(vo, comment);
        comment.setUserId(userId);
        Integer score = Math.round((vo.getFacilitiesScore() + vo.getHygieneScore() + vo.getPositionScore() + vo.getServiceScore())*1.0f/4);
        comment.setScore(score);
        comment.setCreationDate(new Date());
        comment.setCreatedBy(userId);
        //保存评论
        Long comment1 = commentMapper.insertItripComment(comment);
        if (comment1 < 1){
            flag = false;
        }
        //保存图片地址
        /*Map<String, Object> map = new HashMap<>();
        map.put("orderId", comment.getOrderId());
        Long id = commentMapper.getItripCommentListByMap(map).get(0).getId();*/
        Long id = comment.getId();
        if (vo.getIsHavingImg() == 1){
            int img = 0;
            ItripImage[] images = vo.getItripImages();
            for (int i = 0; i < images.length; i++){
                images[i].setTargetId(id);
                images[i].setType("2");
                images[i].setCreationDate(new Date());
                images[i].setCreatedBy(userId);
                images[i].setPosition(i + 1);
                Integer image = imageMapper.insertItripImage(images[i]);
                img += image;
            }
            if (img < 1){
                flag = false;
            }
        }
        //变更订单状态 -> 已评论
        /*ItripHotelOrder order = new ItripHotelOrder();
        order.setId(comment.getOrderId());
        order.setOrderStatus(4);
        order.setModifyDate(comment.getCreationDate());
        order.setModifiedBy(comment.getCreatedBy());
        Integer order1 = orderMapper.updateItripHotelOrder(order);*/
        Integer order1 = orderMapper.updateHotelOrderStatus(comment.getOrderId(), userId);
        if (order1 < 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Page<ItripListCommentVO> getCommentList(ItripSearchCommentVO vo) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("hotelId", vo.getHotelId());
        if (vo.getIsHavingImg() != -1) map.put("isHavingImg", vo.getIsHavingImg());
        if (vo.getIsOk() != -1) map.put("isOk", vo.getIsOk());
        Integer total = commentMapper.getItripCommentCountByMap(map);
        int pageNo = vo.getPageNo() == null ? 1 : vo.getPageNo();
        int pageSize = vo.getPageSize() == null ? 5 : vo.getPageSize();
        Page<ItripListCommentVO> page = new Page<>(pageNo, pageSize, total);
        map.put("pageSize", vo.getPageSize());
        map.put("beginPos", page.getBeginPos());
        List<ItripListCommentVO> list = commentMapper.getItripCommentListByMap(map);
        page.setRows(list);
        return page;
    }

    @Override
    public ItripHotelDescVO getHotelDesc(Long hotelId) throws Exception {
        ItripHotel itripHotel = hotelMapper.getItripHotelById(hotelId);
        ItripHotelDescVO vo = new ItripHotelDescVO();
        BeanUtils.copyProperties(itripHotel, vo);
        return vo;
    }

    @Override
    public ItripScoreCommentVO getHotelScore(Long hotelId) throws Exception {
        return commentMapper.getCommentAvgScore(hotelId);
    }

    @Override
    public Map<String, Integer> getCount(Long hotelId) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        Map<String, Object> condition = new HashMap<>();
        condition.put("hotelId", hotelId);
        //总评论数
        map.put("allcomment", commentMapper.getItripCommentCountByMap(condition));
        //有图片评论数
        condition.put("isHavingImg", 1);
        map.put("havingimg", commentMapper.getItripCommentCountByMap(condition));
        //有待改善评论数
        condition.remove("isHavingImg");
        condition.put("isOk", 0);
        map.put("improve", commentMapper.getItripCommentCountByMap(condition));
        //值得推荐评论数
        condition.put("isOk", 1);
        map.put("isok", commentMapper.getItripCommentCountByMap(condition));
        return map;
    }

}
