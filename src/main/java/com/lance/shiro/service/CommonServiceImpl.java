package com.lance.shiro.service;

import com.lance.shiro.entity.IAttachment;
import com.lance.shiro.mapper.CommonConfigMapper;
import com.lance.shiro.mapper.CommonMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class CommonServiceImpl implements CommonService {
    public Logger log = LogManager.getLogger(getClass());

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private CommonConfigMapper commonConfigMapper;

    @Autowired
    private Environment environment;

    @Override
    public List<String> findCountry() {
        return commonMapper.findCountry();
    }

    @Override
    public List<String> findState(String country) {
       return commonMapper.findState(country);
    }

    @Override
    public List<String> findCity(String country, String state) {
        return commonMapper.findCity(country, state);
    }


    @Override
    public List<IAttachment> uploadFiles(List<MultipartFile> files, IAttachment attachment) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<IAttachment> iAttachments = new ArrayList<>();
            // 查询根目录
            String rootFilePath = environment.getProperty("rootFilePath");
            // 查询访问根地址
            String rootHttp = environment.getProperty("rootHttpPath");
            // 上传文件的物理路径
            String uploadPath = rootFilePath + "attachment/";
            // 返回前端的地址
            String rootHttpPath = rootHttp + "rest/common/attachment/";
            // 判断文件夹是否存在，创建文件夹
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 多附件上传
            for (MultipartFile file : files) {
                IAttachment temp = new IAttachment();
                if (!file.isEmpty()) {
                    // 后缀
                    String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    // 新文件名
                    String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf(".")) + UUID.randomUUID().toString();
                    // 文件名+ 后缀
                    String fileFullName = String.format("%s%s", fileName, suffix);
                    // 文件大小
                    String fileSize = String.valueOf(file.getSize());
                    // 文件类型
                    String contentType = file.getContentType();
                    temp.setFilePath(rootHttpPath + fileFullName);
                    temp.setFileName(fileName);
                    temp.setExtension(suffix);
                    temp.setFileSize(fileSize);
                    temp.setBelongToID(attachment.getBelongToID());
                    temp.setBelongToCategory(attachment.getBelongToCategory());
                    temp.setDescription(attachment.getDescription());
                    temp.setContentType(contentType);
                    temp.setCreateUser(attachment.getCreateUser());
                    temp.setModule(attachment.getModule());
                    temp.setRealPath(uploadPath + fileFullName);
                    temp.setStatus("0");
                    temp.setCreateTime(formatter.format(new Date()));
                    temp.setOriginalFilename(file.getOriginalFilename());
                    commonMapper.addAttachment(temp);
                    File fileInfo = new File(String.format("%s%s", uploadPath, fileFullName));
                    org.apache.commons.io.FileUtils.writeByteArrayToFile(fileInfo, file.getBytes());
                    iAttachments.add(temp);
                }
            }
            return iAttachments;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public String findRootFilePath() {
        // 查询根目录
        String rootFilePath = environment.getProperty("rootFilePath");
        return rootFilePath;
    }

    @Override
    public List<IAttachment> findListAttachment(ArrayList<String> id, String module) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(module)) {
            return commonMapper.findListAttachmentByModule(module);
        } else {
            if (null != id && id.size() > 0) {
                String ids = "'" + StringUtils.join(id, "','") + "'";
                return commonMapper.findListAttachmentByIds(ids);
            } else {
                return commonMapper.findAllAttachment();
            }
        }
    }

    @Override
    public int deleteListAttachment(ArrayList<String> id, String module) {
        if (null != id && id.size() > 0) {
            String ids = "'" + StringUtils.join(id, "','") + "'";
            List<IAttachment> iAttachments = commonMapper.findListAttachmentByIds(ids);
            for (IAttachment iAttachment : iAttachments) {
                commonMapper.deleteAttachment(iAttachment.getId());
                File file = new File(iAttachment.getRealPath());
                if (file.isFile()) {
                    file.delete();
                }
            }
        }

        if (org.apache.commons.lang3.StringUtils.isNotBlank(module)) {
            List<IAttachment> iAttachments = commonMapper.findListAttachmentByModule(module);
            for (IAttachment iAttachment : iAttachments) {
                commonMapper.deleteAttachment(iAttachment.getId());
                File file = new File(iAttachment.getRealPath());
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
        return 0;
    }

    @Override
    public IAttachment updateFiles(MultipartFile file, IAttachment attachment) {
        try {
            // 查询记录
            IAttachment temp = commonMapper.findListAttachmentByIds(String.valueOf(attachment.getId())).get(0);
            // 查询根目录
            String rootFilePath = environment.getProperty("rootFilePath");
            // 查询访问根地址
            String rootHttp = environment.getProperty("rootHttpPath");
            // 上传文件的物理路径
            String uploadPath = rootFilePath + "attachment/";
            // 返回前端的地址
            String rootHttpPath = rootHttp + "rest/common/attachment/";
            // 创建文件夹
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 删除原有文件
            File tempFile = new File(temp.getRealPath());
            if (tempFile.isFile()) {
                tempFile.delete();
            }

            // 创建新文件
            if (null != file && !file.isEmpty()) {
                // 后缀
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                // 新文件名
                String fileName = UUID.randomUUID().toString();
                // 文件名+ 后缀
                String fileFullName = String.format("%s%s", fileName, suffix);
                // 文件大小
                String fileSize = String.valueOf(file.getSize());
                // 文件类型
                String contentType = file.getContentType();
                temp.setFilePath(rootHttpPath + fileFullName);
                temp.setFileName(fileName);
                temp.setExtension(suffix);
                temp.setFileSize(fileSize);
                temp.setOriginalFilename(file.getOriginalFilename());
                if (null != attachment.getDescription())
                    temp.setDescription(attachment.getDescription());
                temp.setContentType(contentType);
                if (null != attachment.getCreateUser())
                    temp.setCreateUser(attachment.getCreateUser());
                temp.setRealPath(uploadPath + fileFullName);
                commonMapper.updateAttachment(temp);
                File fileInfo = new File(String.format("%s%s", uploadPath, fileFullName));
                org.apache.commons.io.FileUtils.writeByteArrayToFile(fileInfo, file.getBytes());
            } else {
                temp.setFilePath("");
                temp.setFileName("");
                temp.setExtension("");
                temp.setFileSize("");
                if (null != attachment.getDescription())
                    temp.setDescription(attachment.getDescription());
                temp.setContentType("");
                if (null != attachment.getCreateUser())
                    temp.setCreateUser(attachment.getCreateUser());
                temp.setRealPath("");
                commonMapper.updateAttachment(temp);
            }
            return temp;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public List<IAttachment> findListAttachmentByBelong(String belongToID, String belongToCategory) {
        return commonMapper.findListAttachmentByBelong(belongToID, belongToCategory);
    }

    @Override
    public int deleteListAttachmentByBelong(String belongToID, String belongToCategory) {
        List<IAttachment> iAttachments = commonMapper.findListAttachmentByBelong(belongToID, belongToCategory);
        for (IAttachment iAttachment : iAttachments) {
            commonMapper.deleteAttachment(iAttachment.getId());
            File file = new File(iAttachment.getRealPath());
            if (file.isFile()) {
                file.delete();
            }
        }
        return 0;
    }
}