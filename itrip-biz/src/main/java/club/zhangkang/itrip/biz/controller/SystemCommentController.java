package club.zhangkang.itrip.biz.controller;

import club.zhangkang.itrip.beans.dto.Dto;
import club.zhangkang.itrip.beans.pojo.ItripUser;
import club.zhangkang.itrip.beans.vo.ItripImageVO;
import club.zhangkang.itrip.beans.vo.ItripLabelDicVO;
import club.zhangkang.itrip.beans.vo.comment.*;
import club.zhangkang.itrip.biz.service.SystemCommentService;
import club.zhangkang.itrip.common.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Api(value = "评论controller", tags = "评论操作接口")
@Controller
@RequestMapping("/api/comment")
public class SystemCommentController {

    @Resource
    private SystemCommentService commentService;

    @Resource
    private ValidationToken validateToken;

    @Resource
    private SystemConfig systemConfig;

    //3.4.1
    @ApiOperation(value = "查询出游类型列表", httpMethod = "GET", response = Dto.class, notes = "")
    @RequestMapping(value = "/gettraveltype", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<List<ItripLabelDicVO>> getTravelType(){
        try {
            List<ItripLabelDicVO> labelDicVOS = commentService.getTravelType();
            if (EmptyUtils.isEmpty(labelDicVOS)){
                return DtoUtil.returnFail("获取旅游类型列表错误", ErrorCode.BIZ_GET_ITRIP_TYPE_FAILED);
            }
            return DtoUtil.returnDataSuccess(labelDicVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.4.2
    @ApiOperation(value = "根据评论id查询评论照片", httpMethod = "GET", response = Dto.class, notes = "type=2；总体评分、位置评分、设施评分、服务评分、卫生评分")
    @RequestMapping(value = "/getimg/{targetId}", method = RequestMethod.GET)
    @ResponseBody
    public Dto<List<ItripImageVO>> getImg(@ApiParam(name = "targetId", value = "评论id", required = true)
                                              @PathVariable String targetId){
        if (EmptyUtils.isEmpty(targetId)){
            return DtoUtil.returnFail("评论id不能为空", ErrorCode.BIZ_COMMENTID_NOTNULL);
        }
        try {
            List<ItripImageVO> imageVOS = commentService.getImg(Long.parseLong(targetId));
            return DtoUtil.returnDataSuccess(imageVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取评论图片失败", ErrorCode.BIZ_GET_COMMENT_IMAGE_FAILED);
        }
    }

    //3.4.3
    @ApiOperation(value = "新增评论接口", httpMethod = "POST", response = Dto.class, notes = "新增评论信息<p style=‘color:red’>注意：若有评论图片，需要传图片路径")
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto add(HttpServletRequest request,
                   @ApiParam(name = "itripAddCommentVO", value = "itripAddCommentVO", required = true)
                   @RequestBody ItripAddCommentVO itripAddCommentVO){
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            if (EmptyUtils.isEmpty(itripAddCommentVO)){
                return DtoUtil.returnFail("不能提交空，请填写评论信息", ErrorCode.BIZ_COMMENT_NOTNULL);
            }
            if (EmptyUtils.isEmpty(itripAddCommentVO.getOrderId())){
                return DtoUtil.returnFail("新增评论，订单ID不能为空", ErrorCode.BIZ_ADD_COMMENT_ORDERID_NOTNULL);
            }
            //Long userId = Long.parseLong(token.split("-")[2]);
            ItripUser user = validateToken.getCurrentUser(token);
            if (commentService.add(itripAddCommentVO, user)){
                return DtoUtil.returnSuccess("新增评论成功");
            }else {
                return DtoUtil.returnFail("新增评论失败", ErrorCode.BIZ_ADD_COMMENT_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.4.4
    @ApiOperation(value = "图片删除接口", httpMethod = "POST", response = Dto.class, notes = "删除传递图片名称")
    @RequestMapping(value = "/delpic", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto delpic(HttpServletRequest request,
                      @ApiParam(name = "imgName", value = "imgName", required = true) String imgName){
        String userAgent = request.getHeader("user-agent");
        String token = request.getHeader("token");
        try {
            if (!validateToken.validateToken(userAgent, token)){
                return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
            }
            //获取文件在服务器上的物理路径
            String fullPath = systemConfig.getFileUploadPathString() + imgName;
            File file = new File(fullPath);
            if (file.exists()){
                file.delete();
                return DtoUtil.returnSuccess("图片删除成功");
            }else {
                return DtoUtil.returnFail("文件不存在，删除失败", ErrorCode.BIZ_PIC_NOT_EXIST);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
        }
    }

    //3.4.5
    @ApiOperation(value = "图片上传接口", httpMethod = "POST", response = Dto.class, notes = "上传评论图片，最多支持4张图片同时上传，格式为：jpg、jpeg、png，大小不超过5M<p style=‘color:red’>注意：input file 中的name不可重复 e:g : file1 、 file2 、 fileN")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto<List<String>> upload(HttpServletRequest request){
        List<String> result = new ArrayList<>();
        List<String> hasError = new ArrayList<>();
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        if (multipartResolver.isMultipart(request)){
            //处理文件上传
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            String userAgent = request.getHeader("user-agent");
            String token = request.getHeader("token");
            try {
                if (!validateToken.validateToken(userAgent, token)){
                    return DtoUtil.returnFail("token失效，请重新登录", ErrorCode.BIZ_TOKEN_INVALID);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail(e.getMessage(), ErrorCode.BIZ_UNKNOWN);
            }
            int fileCount;
            try {
                fileCount = multipartHttpServletRequest.getMultiFileMap().size();
            }catch (Exception e){
                return DtoUtil.returnFail("文件大小超限", ErrorCode.BIZ_SIZE_OVER);
            }
            if (fileCount <= 4){
                //读文件
                //获取文件名，文件类型
                //验证文件类型
                //保存文件：不用文件原名，用户id-系统时间毫秒数-随机数.后缀
                //存到服务器指定目录
                Iterator<String> names = multipartHttpServletRequest.getFileNames();
                ItripUser user = validateToken.getCurrentUser(token);
                while (names.hasNext()){
                    String originalFilename = null;
                    try {
                        MultipartFile file = multipartHttpServletRequest.getFile(names.next());
                        if (file != null){
                            originalFilename = file.getOriginalFilename();
                            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                            if (!originalFilename.equals("") && (suffix.equals(".jpg") || suffix.equals(".jpeg") || suffix.equals(".png")) ){
                                //文件名
                                String fileName = user.getId() + "-" + System.currentTimeMillis() + "-"
                                        + (int)(Math.random()*100000) + suffix;
                                //完整的保存路径
                                //File.separator  目录分隔符  /
                                //File.pathSeparator  路径分隔符  ;
                                String fullPath = systemConfig.getFileUploadPathString() + fileName;
                                file.transferTo(new File(fullPath));
                                result.add(systemConfig.getVisitImgUrlString() + fileName);
                            }else {
                                hasError.add(originalFilename + "不是规定的文件类型");
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        hasError.add(originalFilename + "上传失败");
                    }
                }
                if (hasError.isEmpty()){
                    return DtoUtil.returnDataSuccess(result);
                }else {
                    return DtoUtil.returnSuccess(hasError.toString(), result);
                }
            }else {
                return DtoUtil.returnFail("上传的文件数不正确，必须是大于1小于等于4", ErrorCode.BIZ_UPLOAD_NUMBER_ERROR);
            }
        }else {
            return DtoUtil.returnFail("请求的内容不是上传文件的类型", ErrorCode.BIZ_NOT_UPLOAD_TYPE);
        }
    }

    //3.4.6
    @ApiOperation(value = "根据评论类型查询评论列表，并分页显示", httpMethod = "POST", response = Dto.class, notes = "")
    @RequestMapping(value = "/getcommentlist", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto<Page<ItripListCommentVO>> getCommentList(@ApiParam(name = "itripSearchCommentVO", value = "itripSearchCommentVO", required = true)
                                                            @RequestBody ItripSearchCommentVO itripSearchCommentVO){
        if (EmptyUtils.isEmpty(itripSearchCommentVO)){
            return DtoUtil.returnFail("搜索条件不能为空", ErrorCode.BIZ_GET_COMMENTLIST_FAILED);
        }
        try {
            Page<ItripListCommentVO> commentVOList = commentService.getCommentList(itripSearchCommentVO);
            return DtoUtil.returnDataSuccess(commentVOList);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取评论列表错误", ErrorCode.BIZ_GET_COMMENTLIST_FAILED);
        }
    }

    //3.4.7
    @ApiOperation(value = "新增评论信息页面内获取酒店相关信息", httpMethod = "GET", response = Dto.class, notes = "酒店名称、酒店星级")
    @RequestMapping(value = "/gethoteldesc/{hotelId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<ItripHotelDescVO> getHotelDesc(@ApiParam(name = "hotelId", value = "酒店id", required = true)
                                                  @PathVariable String hotelId){
        if (EmptyUtils.isEmpty(hotelId)){
            return DtoUtil.returnFail("酒店id不能为空", ErrorCode.BIZ_COMMENT_HOTELID_NOTNULL);
        }
        try {
            ItripHotelDescVO hotelDescVO = commentService.getHotelDesc(Long.parseLong(hotelId));
            return DtoUtil.returnDataSuccess(hotelDescVO);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取酒店相关信息错误", ErrorCode.BIZ_GET_COMMENT_HOTEL_DESC_FAILED);
        }
    }

    //3.4.8
    @ApiOperation(value = "据酒店id查询酒店平均分", httpMethod = "GET", response = Dto.class, notes = "总体评分、位置评分、设施评分、服务评分、卫生评分")
    @RequestMapping(value = "/gethotelscore/{hotelId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<ItripScoreCommentVO> getHotelScore(@ApiParam(name = "hotelId", value = "酒店id", required = true)
                                                      @PathVariable String hotelId){
        if (EmptyUtils.isEmpty(hotelId)){
            return DtoUtil.returnFail("酒店id不能为空", ErrorCode.BIZ_COMMENT_HOTELID_NOTNULL);
        }
        try {
            ItripScoreCommentVO scoreCommentVO = commentService.getHotelScore(Long.parseLong(hotelId));
            return DtoUtil.returnDataSuccess(scoreCommentVO);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取评分失败", ErrorCode.BIZ_GET_COMMENT_SCORE_FAILED);
        }
    }

    //3.4.9
    @ApiOperation(value = "根据酒店id查询各类评论数量", httpMethod = "GET", response = Dto.class, notes = "全部评论、值得推荐、有待改善、有图片")
    @RequestMapping(value = "/getcount/{hotelId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<Map<String, Integer>> getCount(@ApiParam(name = "hotelId", value = "酒店id", required = true)
                                                  @PathVariable String hotelId){
        if (EmptyUtils.isEmpty(hotelId)){
            return DtoUtil.returnFail("参数hotelId为空", ErrorCode.BIZ_COMMENT_COUNT_HOTELID_NOTNULL);
        }
        try {
            Map<String, Integer> map = commentService.getCount(Long.parseLong(hotelId));
            Set<String> set = map.keySet();
            if (!set.contains("allcomment")){
                return DtoUtil.returnFail("获取酒店总评论数失败", ErrorCode.BIZ_GET_ALLCOMMENT_COUNT_FAILED);
            }
            if (!set.contains("havingimg")){
                return DtoUtil.returnFail("获取酒店有图片评论数失败", ErrorCode.BIZ_GET_COMMENT_COUNT_HAVINGIMG_FAILED);
            }
            if (!set.contains("improve")){
                return DtoUtil.returnFail("获取酒店有待改善评论数失败", ErrorCode.BIZ_GET_COMMENT_COUNT_IMPROVE_FAILED);
            }
            if (!set.contains("isok")){
                return DtoUtil.returnFail("获取酒店值得推荐评论数失败", ErrorCode.BIZ_GET_COMMENT_COUNT_ISOK_FAILED);
            }
            return DtoUtil.returnDataSuccess(map);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("未知异常", ErrorCode.BIZ_UNKNOWN);
        }
    }


}
