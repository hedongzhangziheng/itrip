package club.zhangkang.itrip.dao.comment;
import club.zhangkang.itrip.beans.pojo.ItripComment;
import club.zhangkang.itrip.beans.vo.comment.ItripListCommentVO;
import club.zhangkang.itrip.beans.vo.comment.ItripScoreCommentVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ItripCommentMapper {

	public ItripComment getItripCommentById(@Param(value = "id") Long id)throws Exception;

	public List<ItripListCommentVO> getItripCommentListByMap(Map<String, Object> param)throws Exception;

	public Integer getItripCommentCountByMap(Map<String, Object> param)throws Exception;

	public Long insertItripComment(ItripComment itripComment)throws Exception;

	public Integer updateItripComment(ItripComment itripComment)throws Exception;

	public Integer deleteItripCommentById(@Param(value = "id") Long id)throws Exception;

	public ItripScoreCommentVO getCommentAvgScore(@Param(value = "hotelId") Long hotelId) throws Exception;
}
