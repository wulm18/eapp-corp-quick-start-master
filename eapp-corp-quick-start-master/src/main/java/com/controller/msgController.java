package com.controller;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.config.URLConstant;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiChatSendRequest;
import com.dingtalk.api.request.OapiMediaUploadRequest;
import com.dingtalk.api.response.OapiChatSendResponse;
import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.taobao.api.ApiException;
import com.taobao.api.FileItem;
import com.util.AccessTokenUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@EnableScheduling
public class msgController {
	private static final Logger bizLogger = LoggerFactory.getLogger(msgController.class);
	
	@Scheduled(cron = "0 0 10 * * ?")//每天上午十点触发
	public void test() {
		String result = sendMsg(); 
		System.out.println("发送结果："+result);
	}
	
	
	/**
	 * 发送群消息
	 * access_token：调用接口凭证，chatId：dd.chooseChat获取，也可以在调用创建群会话接口的返回结果里面获取，msg：
	 * @throws ApiException 
	 */
	@RequestMapping(value="/msgSender",method=RequestMethod.POST)
	@ResponseBody
	public String sendMsg() {
		//获取accessToken,注意正是代码要有异常流处理、
		String token;
		try {
			
			token = AccessTokenUtil.getToken();
			
			System.out.println("token="+token);
			
			DingTalkClient client = new DefaultDingTalkClient(URLConstant.URL_SENT_MESSAGE);
	        OapiChatSendRequest request = new OapiChatSendRequest();
	        request.setChatid("chat583f7e43ac755600d6e7d1e5566f4eaf");
	        OapiChatSendRequest.Msg msg = new OapiChatSendRequest.Msg();

	        //发送图片消息
	        String mId = getMediaId(token);
	        System.out.println("mediaId:"+mId);
	        msg.setMsgtype("image");
	        OapiChatSendRequest.Image image = new OapiChatSendRequest.Image();
	        msg.setImage(image);;
	        image.setMediaId(mId);
	      	
	        request.setMsg(msg);
	        OapiChatSendResponse response = client.execute(request, token);
			return response.getErrmsg();
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
       		
	}
	/**
	 * 用于上传图片、语音等媒体资源文件以及普通文件（如doc、ppt），接口返回媒体资源标识ID：media_id。
	 * 请注意，media_id是可复用的，同一个media_id可用于消息的多次发送。
	 * @param accessToken,调用接口凭证
	 * @param type,媒体文件类型，分别有图片（image）、语音（voice）、普通文件(file)
	 * @param media,form-data中媒体文件标识，有filename、filelength、content-type等信息
	 *  @return
	 */
	public String getMediaId(String accessToken) {
		try {
			DingTalkClient  client = new DefaultDingTalkClient(URLConstant.URL_SENT_MEDIA);
			OapiMediaUploadRequest request = new OapiMediaUploadRequest();
			request.setType("image");
			request.setMedia(new FileItem("E:\\pictures/report.jpg"));
			OapiMediaUploadResponse response = client.execute(request,accessToken);
			return response.getMediaId();
		}catch (ApiException e) {
			// TODO: handle exception
			e.printStackTrace();
            return null;
		}
	}

}
