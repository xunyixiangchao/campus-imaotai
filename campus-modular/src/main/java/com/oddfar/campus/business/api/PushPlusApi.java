package com.oddfar.campus.business.api;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.oddfar.campus.business.entity.ILog;
import com.oddfar.campus.business.entity.IUser;
import com.oddfar.campus.common.utils.StringUtils;
import com.oddfar.campus.framework.manager.AsyncManager;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author zhiyuan
 */
public class PushPlusApi {


    public static void sendNotice(IUser iUser, ILog operLog) {
        String token = iUser.getPushPlusToken();
        if (StringUtils.isEmpty(token)) {
            return;
        }
        String title, content;
        if (operLog.getStatus() == 0) {
            //预约成功
            title = iUser.getRemark() + "-i茅台执行成功";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
            AsyncManager.me().execute(sendNotice(token, title, content, "txt"));
        } else {
            //预约失败
            title = iUser.getRemark() + "-i茅台执行失败";
            content = iUser.getMobile() + System.lineSeparator() + operLog.getLogContent();
            AsyncManager.me().execute(sendNotice(token, title, content, "txt"));
        }


    }

    /**
     * push推送
     *
     * @param token    token
     * @param title    消息标题
     * @param content  具体消息内容
     * @param template 发送消息模板
     */
    public static TimerTask sendNotice(String token, String title, String content, String template) {
        return new TimerTask() {
            @Override
            public void run() {
                String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";
                url += token;
                Map<String, Object> map = new HashMap<>();
//                map.put("token", token);
//                map.put("title", title);
//                map.put("content", content);
//                if (StringUtils.isEmpty(template)) {
//                    map.put("text", "html");
//                }
                map.put("msgtype","text");

                Map<String, Object> text = new HashMap<>();
                text.put("content",content);
                text.put("mentioned_list", Collections.singletonList("@all"));
                map.put("text",text);
                String req = JSON.toJSONString(map);
//                System.out.println(req);
                String post = HttpUtil.post(url, req);
//                System.out.println(post);
            }
        };
    }

}
