package club.zhangkang.itrip.dao.hoteltempstore;
import club.zhangkang.itrip.beans.pojo.ItripHotelTempStore;
import club.zhangkang.itrip.beans.vo.store.StoreVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ItripHotelTempStoreMapper {

	public ItripHotelTempStore getItripHotelTempStoreById(@Param(value = "id") Long id)throws Exception;

	public List<ItripHotelTempStore>  getItripHotelTempStoreListByMap(Map<String, Object> param)throws Exception;

	public Integer getItripHotelTempStoreCountByMap(Map<String, Object> param)throws Exception;

	public Integer insertItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

	public Integer updateItripHotelTempStore(ItripHotelTempStore itripHotelTempStore)throws Exception;

	public Integer deleteItripHotelTempStoreById(@Param(value = "id") Long id)throws Exception;

	//这个方法本身不好使。我略微改动之后，有点用，但查出来的东西不能用，方向跑偏了。也可能是咱看不懂人的算法
	public List<StoreVO> queryRoomStore(Map<String, Object> param) throws Exception;

	public void flushStore(Map<String, Object> param)throws Exception;

	public Integer updateRoomStore(Map<String, Object> param)throws Exception;

	//根据hotelId和roomId以及预定的日期查询库存量（其实不需要hotelId，因为roomId是唯一的）
	public Integer getStore (Map<String, Object> param) throws Exception;
}
