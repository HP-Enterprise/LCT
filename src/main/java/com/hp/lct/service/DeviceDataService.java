package com.hp.lct.service;

import com.alibaba.fastjson.JSON;
import com.hp.lct.entity.*;
import com.hp.lct.repository.DeviceRepository;
import com.hp.lct.repository.DeviceStatusDataRepository;
import com.hp.lct.repository.RemoteControlRepository;
import com.hp.lct.utils.DataTool;
import com.hp.lct.utils.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by jackl on 2016/11/15.
 */
@Component
public class DeviceDataService {

    @Autowired
    private RedisTool redisTool;
    @Autowired
    private DataTool dataTool;
    @Autowired
    private RemoteControlRepository remoteControlRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private DeviceStatusDataRepository deviceStatusDataRepository;
    @Value("${upload.path}")
    private  String uploadPath;

    private Logger _logger = LoggerFactory.getLogger(DeviceDataService.class);

    public ObjectResult handleRemoteControl(RemoteControlBody remoteControlBody){
        ObjectResult re=null;
        if(remoteControlBody!=null) {
            String commandJson= JSON.toJSONString(remoteControlBody);
            _logger.info("开始处理控制指令:"+commandJson);
            if (!isDeviceOnline(remoteControlBody.getImei())) {
                //todo 保存到数据库
                RemoteControl remoteControl=new RemoteControl();
                remoteControl.setImei(remoteControlBody.getImei());
                remoteControl.setSequenceId(dataTool.getSequenceId());
                remoteControl.setSendingTime(new Date());
                remoteControl.setCommand(dataTool.getCommandDesc(remoteControlBody.getCommand(),remoteControlBody.getOperate()));
                remoteControl.setParams("");
                remoteControl.setStatus((short)0);
                remoteControl.setRemark("设备不在线,无法继续。");
                remoteControlRepository.save(remoteControl);

                _logger.info("设备"+remoteControlBody.getImei()+"不在线,无法继续。");
                re=new ObjectResult(1,"设备不在线");
            } else {
                sendCommand(remoteControlBody);
                re=new ObjectResult(0,"命令已经下发");
            }
        }else{
            re=new ObjectResult(1,"参数无效");
        }

        return re ;
    }


    public boolean isDeviceOnline(String imei){
        boolean exist=redisTool.existHashString(dataTool.onlineDeviceHash,imei);
        _logger.info("检查设备"+imei+"是否在线:"+exist);
        return exist;
    }

    public void sendCommand(RemoteControlBody remoteControlBody){
        String command=remoteControlBody.getCommand();
        String operate=remoteControlBody.getOperate();
        String imei=remoteControlBody.getImei();
        String dest=remoteControlBody.getDest();
        int time=remoteControlBody.getTime();
        String sequenceId=dataTool.getSequenceId();
        String commandKey=dataTool.outCmdPreStr+imei+":"+command+":"+sequenceId;
        String  commandValue="";
        if(command.equals("106")){
            commandValue=operate;
            if(operate.equals("NAVIGATE")){
                commandValue=operate+","+dest;//108.868095, 34.19328
            }else if(operate.equals("VIDEO")){
                commandValue=operate+","+time;//15
            }
            //todo 保存到数据库
            RemoteControl remoteControl=new RemoteControl();
            remoteControl.setImei(imei);
            remoteControl.setSequenceId(String.valueOf(sequenceId));
            remoteControl.setSendingTime(new Date());
            remoteControl.setCommand(dataTool.getCommandDesc(remoteControlBody.getCommand(),remoteControlBody.getOperate()));
            remoteControl.setParams(commandValue);
            remoteControl.setStatus((short)0);
            remoteControl.setRemark("命令已发出");
            remoteControlRepository.save(remoteControl);

            redisTool.saveSetString(commandKey, commandValue,-1);
        }else if(command.equals("101")||command.equals("103")){
            RemoteControl remoteControl=new RemoteControl();
            remoteControl.setImei(imei);
            remoteControl.setSequenceId(String.valueOf(sequenceId));
            remoteControl.setSendingTime(new Date());
            remoteControl.setCommand(dataTool.getCommandDesc(remoteControlBody.getCommand(),remoteControlBody.getOperate()));
            remoteControl.setParams(commandValue);
            remoteControl.setStatus((short)0);
            remoteControl.setRemark("命令已发出");
            remoteControlRepository.save(remoteControl);
            redisTool.saveSetString(commandKey,commandValue,-1);
        }else{
            _logger.info(command+"命令暂不支持,支持（101,103,106）");
        }


    }

    /**
     * 获取在线的设备
     * @return
     */
    public List<DeviceStatusData> getOnlineDevices(){
        List<DeviceStatusData> devices=new ArrayList<>();
        Set<String> onlineImeis=redisTool.listHashKeys(dataTool.onlineDeviceHash);
        for (String imei:onlineImeis){
            DeviceStatusData device=deviceStatusDataRepository.findTopByImeiOrderByReceiveTimeDesc(imei);
            if(device!=null){
                devices.add(device);
            }
        }
        return devices;
    }

    /**
     * 处理文件上传
     * @param imei
     * @param fileName
     * @param file
     * @param basePath
     * @return
     */
    public boolean handUploadFile(String imei,String fileName,MultipartFile file,String basePath){
        boolean result=false;
        String originalFilename=fileName;//file.getOriginalFilename();
        if(originalFilename.lastIndexOf("\\")>0){
            originalFilename=originalFilename.substring(originalFilename.lastIndexOf("\\"));
        }
        String devicePath=uploadPath+imei;
        File base =new File(uploadPath);
        File deviceDir =new File(devicePath);
        if(!base.exists()){
            base .mkdir();
        }
        if(!deviceDir.exists()){
            deviceDir .mkdir();
        }
        File savefile =new File(devicePath+File.separator+originalFilename);
        try{
            file.transferTo(savefile);
            result=true;
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println(imei+"上传文件"+file.getOriginalFilename()+">"+file.getSize()+"将要保存的路径"+savefile.getAbsolutePath());
        return result;

    }


    /**
     * 列出制定后缀的文件
     * @param imei
     * @param suffix
     * @return
     */
    public List<String> listFile(String imei,String suffix){
        String devicePath=uploadPath+imei;
        List<String> result=new ArrayList<>();
        File deviceDir =new File(devicePath);
        if(deviceDir.exists()){
            String[] files=deviceDir.list();
            for(int i=0;i<files.length;i++){
                String _f=files[i];
                if(_f.endsWith(suffix.toLowerCase())||_f.endsWith(suffix.toUpperCase())){
                    result.add(_f);
                }
            }
        }
        return result;
    }

    /**
     * 下载指定文件
     * @param imei
     * @return
     */
    public File downFile(String imei,String fileName){
        String devicePath=uploadPath+imei;
        List<String> result=new ArrayList<>();
        File deviceDir =new File(devicePath);
        File targetFile=new File(deviceDir+File.separator+fileName);
        if(targetFile.exists()){
           return targetFile;
        }else{
            return null;
        }
    }


}
