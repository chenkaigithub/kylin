package org.kylin.service.encode.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.W3DCode;
import org.kylin.bean.WelfareCode;
import org.kylin.constant.CodeTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.kylin.service.encode.WyfEncodeService;

import java.util.*;

/**
 * @author huangyawu
 * @date 2017/6/29 下午11:26.
 */

@Service
public class WyfEncodeServiceImpl implements WyfEncodeService{
    private static final Logger LOGGER = LoggerFactory.getLogger(WyfEncodeServiceImpl.class);


    @Override
    public WelfareCode gossipEncoder(List<Set<Integer>> riddles) {

        return null;
    }

    @Override
    public WelfareCode groupSelectEncoder(List<Set<Integer>> riddles) {
        if(CollectionUtils.size(riddles) < 3){
            LOGGER.warn("group-select-exception riddles={}", riddles);
            throw new IllegalArgumentException("参数错误");
        }



        return null;
    }

    @Override
    public WelfareCode directSelectEncoder(List<Set<Integer>> riddles) {

        if(CollectionUtils.size(riddles) < 3){
            LOGGER.warn("group-select-exception riddles={}", riddles);
            throw new IllegalArgumentException("参数错误");
        }

        LOGGER.info("direct-select-encode riddles={} ", riddles);

        WelfareCode welfareCode = predict3DCodes(riddles);
        if(welfareCode != null) {
            Collections.sort(welfareCode.getW3DCodes(), new Comparator<W3DCode>() {
                @Override
                public int compare(W3DCode o1, W3DCode o2) {
                    if(o1.getH() != null && o2.getH() != null && o1.getH() != o2.getH()){
                        return o1.getH().compareTo(o2.getH());
                    }else if (o1.getD() != o2.getD()){
                        return o1.getD().compareTo(o2.getD());
                    }else{
                        return o1.getU().compareTo(o2.getU());
                    }
                }
            });
            welfareCode.distinct().generate();
        }

        LOGGER.info("direct-select-codes={}", JSON.toJSONString(welfareCode));
        return welfareCode;
    }

    @Override
    public WelfareCode group2directSelectEncoder(WelfareCode groupWelfareCode) {
        return null;
    }

    @Override
    public WelfareCode direct2GroupSelectEncoder(WelfareCode directWelfareCode) {
        return null;
    }


    private WelfareCode predict3DCodes(List<Set<Integer>> riddles){
        if(CollectionUtils.size(riddles) < 3){
            return null;
        }

        WelfareCode welfareCode = new WelfareCode();
        welfareCode.setCodeTypeEnum(CodeTypeEnum.DIRECT);

        List<W3DCode> w3DCodes = new ArrayList<>();

        for(int i=0; i<riddles.size(); i++){
            for(Integer e1: riddles.get(i)){
                for(int j=0; j<riddles.size(); j++){
                    if(j == i){
                        continue;
                    }
                    for(Integer e2:riddles.get(j)){
                        for(int k=0; k<riddles.size(); k++){
                            if(j == k || i == k){
                                continue;
                            }
                            for(Integer e3: riddles.get(k)){
                                W3DCode w3DCode = new W3DCode();
                                w3DCode.setH(e1);
                                w3DCode.setD(e2);
                                w3DCode.setU(e3);
                                w3DCode.setFreq(1);
                                w3DCode.setSumTail((e1 + e2 + e3) % 3);
                                w3DCodes.add(w3DCode);
                            }

                        }
                    }
                }
            }
        }
        welfareCode.setW3DCodes(w3DCodes);
        return welfareCode;
    }

}
