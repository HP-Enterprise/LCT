package com.hp.lct.controller;

import com.hp.lct.service.DeviceDataService;
import com.hp.lct.entity.ObjectResult;
import com.hp.lct.entity.RemoteControlBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * Created by jackl on 2016/11/15.
 */

@RestController
@RequestMapping("/api")
public class DeviceDataController {

    @Autowired
    private DeviceDataService deviceDataService;

    /**
     * 下发控制指令
     * @param model
     * @param imei
     * @param remoteControlBody
     * @param request
     * @return
     */
    @RequestMapping(value = "/service/model/{model}/device/{imei}/remoteControl",method = RequestMethod.POST)
    public Object remoteControl(@PathVariable("model") String model,@PathVariable("imei") String imei,@RequestBody RemoteControlBody remoteControlBody,HttpServletRequest request){
        RequestContext requestContext = new RequestContext(request);
        Object result=deviceDataService.handleRemoteControl(remoteControlBody);
        return result;
    }

    /**
     * 查询在线设备
     * @param request
     * @return
     */
    @RequestMapping(value = "/service/device/online",method = RequestMethod.GET)
    public ObjectResult getOnlineDevice(HttpServletRequest request){
        RequestContext requestContext = new RequestContext(request);
        ObjectResult objectResult=new ObjectResult(0,deviceDataService.getOnlineDevices());
        return objectResult;
    }


    /**
     * 视频上传
     * @param model
     * @param imei
     * @param filename
     * @param video
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/service/{model}/video/{imei}/{filename:.+}",method = RequestMethod.POST)
    public ObjectResult uploadVideo(@PathVariable("model") String model,@PathVariable("imei") String imei,@PathVariable("filename") String filename,   @RequestParam(value="video")MultipartFile video,HttpServletRequest request,HttpServletResponse response){
        RequestContext requestContext = new RequestContext(request);
        String basePath=request.getServletContext().getRealPath("");
        boolean result=deviceDataService.handUploadFile(imei, filename, video, basePath);
       ObjectResult objectResult=new ObjectResult(0,"上传成功");
       if(!result){
           objectResult=new ObjectResult(1,"上传失败");
       }
       return objectResult;

    }

    /**
     * 视频列表
     * @param model
     * @param imei
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/service/{model}/video/{imei}",method = RequestMethod.GET)
    public ObjectResult listVideo(@PathVariable("model") String model,@PathVariable("imei") String imei,HttpServletRequest request,HttpServletResponse response){
        RequestContext requestContext = new RequestContext(request);
        String basePath=request.getServletContext().getRealPath("");
        List<String> result=deviceDataService.listFile(imei, ".MP4");
        ObjectResult objectResult=new ObjectResult(0,result);
        return objectResult;
    }

    /**
     * 视频下载
     * @param model
     * @param imei
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/service/{model}/video/{imei}/{fileName:.+}",method = RequestMethod.GET)
    public ObjectResult downVideo(@PathVariable("model") String model,@PathVariable("imei") String imei,@PathVariable("fileName") String fileName,HttpServletRequest request,HttpServletResponse response){
        RequestContext requestContext = new RequestContext(request);
        try{
            File file=deviceDataService.downFile(imei,fileName);
            if(file==null){
                ObjectResult objectResult=new ObjectResult(1,"文件不存在");
                return objectResult;
            }else{
                InputStream fis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();
                // 设置response的Header
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes()));
                response.addHeader("Content-Length", "" + file.length());
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                toClient.write(buffer);
                toClient.flush();
                toClient.close();

            }
        }catch (IOException e){
            e.printStackTrace();
        }

      return null;
    }


    /**
     * 照片上传
     * @param model
     * @param imei
     * @param filename
     * @param photo
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/service/{model}/photo/{imei}/{filename:.+}",method = RequestMethod.POST)
    public ObjectResult uploadPhoto(@PathVariable("model") String model,@PathVariable("imei") String imei,@PathVariable("filename") String filename,   @RequestParam(value="video")MultipartFile photo,HttpServletRequest request,HttpServletResponse response){
        RequestContext requestContext = new RequestContext(request);
        String basePath=request.getServletContext().getRealPath("");
        boolean result=deviceDataService.handUploadFile(imei, filename, photo, basePath);
        ObjectResult objectResult=new ObjectResult(0,"上传成功");
        if(!result){
            objectResult=new ObjectResult(1,"上传失败");
        }
        return objectResult;

    }

    /**
     * 图片列表
     * @param model
     * @param imei
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/service/{model}/photo/{imei}",method = RequestMethod.GET)
    public ObjectResult listPhoto(@PathVariable("model") String model,@PathVariable("imei") String imei,HttpServletRequest request,HttpServletResponse response){
        RequestContext requestContext = new RequestContext(request);
        String basePath=request.getServletContext().getRealPath("");
        List<String> result=deviceDataService.listFile(imei, ".JPG");
        ObjectResult objectResult=new ObjectResult(0,result);
        return objectResult;
    }


    /**
     * 图片下载
     * @param model
     * @param imei
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/service/{model}/photo/{imei}/{fileName:.+}",method = RequestMethod.GET)
    public ObjectResult downPhoto(@PathVariable("model") String model,@PathVariable("imei") String imei,@PathVariable("fileName") String fileName,HttpServletRequest request,HttpServletResponse response){
        RequestContext requestContext = new RequestContext(request);
        try{
            File file=deviceDataService.downFile(imei,fileName);
            if(file==null){
                ObjectResult objectResult=new ObjectResult(1,"文件不存在");
                return objectResult;
            }else{
                InputStream fis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();
                // 设置response的Header
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes()));
                response.addHeader("Content-Length", "" + file.length());
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                toClient.write(buffer);
                toClient.flush();
                toClient.close();

            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////
    /**
     * 设备归属关系 绑定解绑
     * @param model
     * @param imei
     * @param remoteControlBody
     * @param request
     * @return
     */
    @RequestMapping(value = "/service/{model}/relation/{imei}",method = RequestMethod.POST)
    public Object relation(@PathVariable("model") String model,@PathVariable("imei") String imei,@RequestBody RemoteControlBody remoteControlBody,HttpServletRequest request){
        RequestContext requestContext = new RequestContext(request);
        Object result=deviceDataService.handleRemoteControl(remoteControlBody);
        return result;
    }

    /**
     * 获取被接人位置信息，并上报服务器
     * @param model
     * @param uid
     * @param remoteControlBody
     * @param request
     * @return
     */
    @RequestMapping(value = "/service/{model}/location/{uid}",method = RequestMethod.POST)
    public Object location(@PathVariable("model") String model,@PathVariable("uid") String uid,@RequestBody RemoteControlBody remoteControlBody,HttpServletRequest request){
        RequestContext requestContext = new RequestContext(request);
        Object result=deviceDataService.handleRemoteControl(remoteControlBody);
        return result;
    }

    /**
     * 监控时段设置
     * @param model
     * @param uid
     * @param remoteControlBody
     * @param request
     * @return
     */
    @RequestMapping(value = "/service/{model}/config/{uid}",method = RequestMethod.POST)
    public Object config(@PathVariable("model") String model,@PathVariable("uid") String uid,@RequestBody RemoteControlBody remoteControlBody,HttpServletRequest request){
        RequestContext requestContext = new RequestContext(request);
        Object result=deviceDataService.handleRemoteControl(remoteControlBody);
        return result;
    }
    //其他辅助功能***
    //获取最新视频
    //获取最新图片
    //设备信息查询
    //查询新版本
    //————————


}
