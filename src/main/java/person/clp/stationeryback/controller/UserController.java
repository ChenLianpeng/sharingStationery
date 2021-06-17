package person.clp.stationeryback.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import person.clp.stationeryback.base.Result;
import person.clp.stationeryback.base.Results;
import person.clp.stationeryback.entity.FaceSearchResDto;
import person.clp.stationeryback.entity.FaceUserInfo;
import person.clp.stationeryback.entity.ProcessInfo;
import person.clp.stationeryback.entity.User;
import person.clp.stationeryback.enums.ErrorCodeEnum;
import person.clp.stationeryback.mapper.UserMapper;
import person.clp.stationeryback.service.FaceEngineService;
import person.clp.stationeryback.utils.Md5Util;
import cn.hutool.core.codec.Base64;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@RestController
public class UserController {
    @Autowired
    UserMapper userMapper;
    @Resource
    private FaceEngineService faceEngineService;

    //用户注册功能
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Map<String, Object> register(@RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam String academe,
                                        @RequestParam String realname,
                                        @RequestParam String studentNumber,
                                        @RequestParam("file") String file,
                                        @RequestParam("groupId") Integer groupId
    ) throws InterruptedException {
        System.out.println("后端执行注册功能");
        System.out.println(file);
        System.out.println(groupId);
        Map<String, Object> map = new HashMap<String, Object>();
        // 验证用户名是否已经注册
        System.out.println("---->"+username);
        User exsitUser = userMapper.selectByName(username);
        if (exsitUser != null) {
            map.put("msg", "该用户名已存在!");
            return map;
        }


        byte[] decode = Base64.decode(base64Process(file));
        ImageInfo imageInfo = ImageFactory.getRGBData(decode);
        //人脸特征获取
        byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
        if (bytes == null) {
            map.put("msg", "人脸获取失败！");
            return map;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(new Date());
        User user = new User();
        user.setUsername(username);
        user.setPassword(Md5Util.MD5(password));
        user.setStudentNumber(studentNumber);
        user.setAcademe(academe);
        user.setRealname(realname);
        user.setGroupId(groupId);
        user.setFaceFeature(bytes);
        user.setFaceId(RandomUtil.randomString(10));
        int count = userMapper.insert(user);
        System.out.println(count);
        if (count != 1) {
            map.put("msg", "注册失败");
            return map;
        }
        map.put("msg", "注册成功");
        return map;
    }


    //人脸登录功能
    @RequestMapping(value = "/faceLogin",method = RequestMethod.POST)
    public String faceLogin(@RequestParam("username") String username,
                                              @RequestParam("file") String file,
                                              @RequestParam("groupId") Integer groupId
                                     ) throws IOException, InterruptedException, ExecutionException {
        System.out.println("前端传过来的登录GroupId："+groupId);
        System.out.println("前端传过来的file："+file);
        byte[] decode = Base64.decode(base64Process(file));
        BufferedImage bufImage = ImageIO.read(new ByteArrayInputStream(decode));
        ImageInfo imageInfo = ImageFactory.bufferedImage2ImageInfo(bufImage);
        //人脸特征获取
        byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
        if (bytes == null) {
            return JSON.toJSONString(Results.newFailedResult(ErrorCodeEnum.NO_FACE_DETECTED));
        }
        //人脸比对，获取比对结果
        List<FaceUserInfo> userFaceInfoList = faceEngineService.compareFaceFeature(bytes, groupId);
        System.out.println("///////:"+userFaceInfoList);
        if (CollectionUtil.isNotEmpty(userFaceInfoList)) {
            User faceUserInfo = userFaceInfoList.get(0);
            FaceSearchResDto faceSearchResDto = new FaceSearchResDto();
            BeanUtil.copyProperties(faceUserInfo, faceSearchResDto);
            List<ProcessInfo> processInfoList = faceEngineService.process(imageInfo);
            if (CollectionUtil.isNotEmpty(processInfoList)) {
                //人脸检测
                List<FaceInfo> faceInfoList = faceEngineService.detectFaces(imageInfo);
                int left = faceInfoList.get(0).getRect().getLeft();
                int top = faceInfoList.get(0).getRect().getTop();
                int width = faceInfoList.get(0).getRect().getRight() - left;
                int height = faceInfoList.get(0).getRect().getBottom() - top;
                Graphics2D graphics2D = bufImage.createGraphics();
                graphics2D.setColor(Color.RED);//红色
                BasicStroke stroke = new BasicStroke(5f);
                graphics2D.setStroke(stroke);
                graphics2D.drawRect(left, top, width, height);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufImage, "jpg", outputStream);
                byte[] bytes1 = outputStream.toByteArray();
                faceSearchResDto.setImage("data:image/jpeg;base64," + Base64Utils.encodeToString(bytes1));
                faceSearchResDto.setAge(processInfoList.get(0).getAge());
                faceSearchResDto.setGender(processInfoList.get(0).getGender().equals(1) ? "女" : "男");
            }
            System.out.println("输出结果："+JSON.toJSONString(Results.newSuccessResult(faceSearchResDto)));
            return JSON.toJSONString(Results.newSuccessResult(faceSearchResDto));
        }
        return JSON.toJSONString(Results.newFailedResult(ErrorCodeEnum.FACE_DOES_NOT_MATCH));
    }

    //用户登录功能
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password,
                                     HttpSession session,
                                     HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        System.out.println("前端传过来的用户名："+username);
        User exsitUser = userMapper.selectByName(username);
        if (exsitUser == null) {
            map.put("msg", "该用户未注册");
            map.put("code","400");
            return map;
        }
        if (!exsitUser.getPassword().equals(Md5Util.MD5(password))) {
            map.put("msg", "密码错误,请重新输入");
            map.put("code","800");
            return map;
        }
        session.setAttribute("loginUser", exsitUser);
        map.put("msg", "登录成功");
        map.put("exsitUser",exsitUser.getUsername());
        return map;
    }


    private String base64Process(String base64Str) {
        if (!StringUtils.isEmpty(base64Str)) {
            String photoBase64 = base64Str.substring(0, 30).toLowerCase();
            int indexOf = photoBase64.indexOf("base64,");
            if (indexOf > 0) {
                base64Str = base64Str.substring(indexOf + 7);
            }
            return base64Str;
        } else {
            return "";
        }
    }
}
