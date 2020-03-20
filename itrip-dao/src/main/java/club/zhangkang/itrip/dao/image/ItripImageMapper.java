package club.zhangkang.itrip.dao.image;
import club.zhangkang.itrip.beans.pojo.ItripImage;
import club.zhangkang.itrip.beans.vo.ItripImageVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ItripImageMapper {

	public ItripImage getItripImageById(@Param(value = "id") Long id)throws Exception;

	public List<ItripImageVO> getItripImageListByMap(Map<String, Object> param)throws Exception;

	public Integer getItripImageCountByMap(Map<String, Object> param)throws Exception;

	public Integer insertItripImage(ItripImage itripImage)throws Exception;

	public Integer updateItripImage(ItripImage itripImage)throws Exception;

	public Integer deleteItripImageById(@Param(value = "id") Long id)throws Exception;

}
