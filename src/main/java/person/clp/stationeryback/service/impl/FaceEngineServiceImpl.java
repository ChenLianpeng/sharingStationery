package person.clp.stationeryback.service.impl;
import	java.math.BigDecimal;

import cn.hutool.core.collection.CollectionUtil;
import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.toolkit.ImageInfo;
import com.google.common.collect.Lists;
import person.clp.stationeryback.entity.FaceUserInfo;
import person.clp.stationeryback.entity.ProcessInfo;
import person.clp.stationeryback.factory.FaceEngineFactory;

import person.clp.stationeryback.mapper.UserMapper;
import person.clp.stationeryback.service.FaceEngineService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Jerry
 */
@Service("faceEngineService")
@Slf4j
public class FaceEngineServiceImpl implements FaceEngineService {

    /**
     *  sdk库路径
     */
    @Value("${config.arcface-sdk.sdk-lib-path}")
    public String sdkLibPath;

    /**
     *  appId
     */
    @Value("${config.arcface-sdk.app-id}")
    public String appId;

    /**
     *  sdkKey
     */
    @Value("${config.arcface-sdk.sdk-key}")
    public String sdkKey;

    /**
     *  threadPoolSize
     */
    @Value("${config.arcface-sdk.thread-pool-size}")
    public Integer threadPoolSize;

    /**
     *  面部合格率
     */
    private Integer passRate = 80;

    private ExecutorService executorService;

    @Resource
    private UserMapper userMapper;

    /**
     * 人脸引擎对象池
     */

    private GenericObjectPool<FaceEngine> faceEngineObjectPool;

    /**
     *  初始化配置
     */
    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(threadPoolSize);
        poolConfig.setMaxTotal(threadPoolSize);
        poolConfig.setMinIdle(threadPoolSize);
        poolConfig.setLifo(false);

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);

        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);

        faceEngineObjectPool = new GenericObjectPool(new FaceEngineFactory(sdkLibPath, appId, sdkKey, engineConfiguration), poolConfig);//底层库算法对象池
    }


    private int plusHundred(Float value) {
        BigDecimal target = new BigDecimal(value);
        BigDecimal hundred = new BigDecimal(100f);
        return target.multiply(hundred).intValue();
    }


    /**
     *  检测人脸
     * @param imageInfo
     * @return
     */
    @Override
    public List<FaceInfo> detectFaces(ImageInfo imageInfo) {

        FaceEngine faceEngine = null;

        try {
            //获取引擎对象
            faceEngine = faceEngineObjectPool.borrowObject();

            //人脸检测得到人脸列表
            List<FaceInfo> faceInfoList = new ArrayList<>();

            //人脸检测
            faceEngine.detectFaces(imageInfo.getImageData(),imageInfo.getWidth(),
                    imageInfo.getHeight(),imageInfo.getImageFormat(),faceInfoList);

            return faceInfoList;
        } catch (Exception e) {
            log.error("【检测人脸】获取失败 e={}",e);
        } finally {
            if (faceEngine != null) {
                //释放引擎对象
                faceEngineObjectPool.returnObject(faceEngine);
            }
        }
        return null;
    }


    /**
     *  处理人脸
     * @param imageInfo
     * @return
     */
    @Override
    public List<ProcessInfo> process(ImageInfo imageInfo) {

        FaceEngine faceEngine = null;

        try {
            //获取引擎对象
            faceEngine = faceEngineObjectPool.borrowObject();

            //人脸检测得到人脸列表
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();

            //人脸检测
            faceEngine.detectFaces(imageInfo.getImageData(),imageInfo.getWidth(),
                    imageInfo.getHeight(),imageInfo.getImageFormat(),faceInfoList);

            int processResult = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(),
                    imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList,
                    FunctionConfiguration.builder().supportAge(true).supportGender(true).build());

            List<ProcessInfo> processInfoList = Lists.newLinkedList();

            //性别提取
            List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
            int genderCode = faceEngine.getGender(genderInfoList);

            //年龄提取
            List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
            int ageCode = faceEngine.getAge(ageInfoList);

            for (int i = 0; i < genderInfoList.size(); i++) {
                ProcessInfo processInfo = new ProcessInfo();
                processInfo.setGender(genderInfoList.get(i).getGender());
                processInfo.setAge(ageInfoList.get(i).getAge());
                processInfoList.add(processInfo);
            }
            return processInfoList;
        } catch (Exception e) {
            log.error("【处理人脸】出现异常 e={}",e);
        } finally {
            if (faceEngine != null) {
                //释放引擎对象
                faceEngineObjectPool.returnObject(faceEngine);
            }
        }
        return null;

    }

    /**
     *  提取人脸特征
     * @param imageInfo
     * @return
     * @throws InterruptedException
     */
    @Override
    public byte[] extractFaceFeature(ImageInfo imageInfo) throws InterruptedException {
        FaceEngine faceEngine = null;
        try {
            //获取引擎对象
            faceEngine = faceEngineObjectPool.borrowObject();

            //人脸检测得到人脸列表
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();

            //人脸检测
            int i = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(),
                    imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);

            if (CollectionUtil.isNotEmpty(faceInfoList)) {
                FaceFeature faceFeature = new FaceFeature();
                //提取人脸特征
                faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);

                return faceFeature.getFeatureData();
            }
        } catch (Exception e) {
            log.error("【提取人脸特征】出现异常 e={}", e);
        } finally {
            if (faceEngine != null) {
                //释放引擎对象
                faceEngineObjectPool.returnObject(faceEngine);
            }
        }
        return null;
    }


    /**
     *  比较人脸特征
     * @param faceFeature   脸部特征
     * @param groupId       分组id
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public List<FaceUserInfo> compareFaceFeature(byte[] faceFeature, Integer groupId) throws InterruptedException, ExecutionException {
        System.out.println("输入进来的："+faceFeature);
        System.out.println("输入进来的："+groupId);
        //识别到的人脸列表
        List<FaceUserInfo> resultFaceInfoList = Lists.newLinkedList();
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(faceFeature);
        //从数据库中取出人脸
        List<FaceUserInfo> faceInfoList = userMapper.getUserFaceInfoByGroupId(groupId);
        System.out.println("从数据库中获取的："+faceInfoList);
        //分成1000组，多线程处理
        List<List<FaceUserInfo>> faceUserInfoPartList = Lists.partition(faceInfoList,1000);
        CompletionService<List<FaceUserInfo>> completionService = new ExecutorCompletionService(executorService);
        for (List<FaceUserInfo> part : faceUserInfoPartList) {
            completionService.submit(new CompareFaceTask(part, targetFaceFeature));
        }
        for (int i = 0; i < faceUserInfoPartList.size(); i++) {
            List<FaceUserInfo> faceUserInfoList = completionService.take().get();
            //检测人脸是否存在，如果不存在，则添加
            if (CollectionUtil.isNotEmpty(faceInfoList)) {
                resultFaceInfoList.addAll(faceUserInfoList);
            }
        }
        //从小到大进行排序
        resultFaceInfoList.sort((h1,h2) -> h2.getSimilarValue().compareTo(h1.getSimilarValue()));
        return resultFaceInfoList;
    }


    private class CompareFaceTask implements Callable<List<FaceUserInfo>> {

        private List<FaceUserInfo> faceUserInfoList;
        private FaceFeature targetFaceFeature;

        public CompareFaceTask(List<FaceUserInfo> faceUserInfoList, FaceFeature targetFaceFeature) {
            this.faceUserInfoList = faceUserInfoList;
            this.targetFaceFeature = targetFaceFeature;
        }

        @Override
        public List<FaceUserInfo> call() throws Exception {
            FaceEngine faceEngine = null;
            //识别到的人脸列表
            List<FaceUserInfo> resultFaceInfoList = Lists.newLinkedList();
            try {
                faceEngine = faceEngineObjectPool.borrowObject();
                for (FaceUserInfo faceUserInfo : faceUserInfoList) {
                    FaceFeature sourceFaceFeature = new FaceFeature();
                    sourceFaceFeature.setFeatureData(faceUserInfo.getFaceFeature());

                    FaceSimilar faceSimilar = new FaceSimilar();
                    faceEngine.compareFaceFeature(targetFaceFeature,sourceFaceFeature,faceSimilar);
                    //获取相似值
                    Integer similarValue = plusHundred(faceSimilar.getScore());
                    //相似值大于配置预期，加入到识别到人脸的列表
                    if (similarValue > passRate) {
                        FaceUserInfo info = new FaceUserInfo();
                        info.setUsername(faceUserInfo.getUsername());
                        info.setFaceId(faceUserInfo.getFaceId());
                        info.setSimilarValue(similarValue);
                        resultFaceInfoList.add(info);
                    }
                }
            } catch (Exception e) {
                log.error("【出现异常】e={}",e);
            } finally {
                if (faceEngine != null) {
                    //关闭引擎
                    faceEngineObjectPool.returnObject(faceEngine);
                }
            }
            return resultFaceInfoList;
        }
    }
}
