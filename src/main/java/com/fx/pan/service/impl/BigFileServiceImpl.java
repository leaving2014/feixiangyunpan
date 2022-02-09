package com.fx.pan.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fx.pan.common.Msg;
import com.fx.pan.domain.Chunk;
import com.fx.pan.domain.FileBean;
import com.fx.pan.domain.Storage;
import com.fx.pan.mapper.StorageMapper;
import com.fx.pan.service.BigFileService;
import com.fx.pan.service.FileService;
import com.fx.pan.utils.FileUtil;
import com.fx.pan.utils.Md5Utils;
import com.fx.pan.utils.PathUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author leaving
 * @Date 2021/12/14 9:11
 * @Version 1.0
 */
@Service
public class BigFileServiceImpl implements BigFileService {
    @Autowired
    private FileService fileService;

    // @Autowired
    // private StorageService storageService;

    @Autowired
    private StorageMapper storageMapper;


    @SneakyThrows
    @Override
    public Msg fileUploadPost(Chunk chunk, HttpServletResponse response) {


        /**
         * 每一个上传块都会包含如下分块信息：
         * chunkNumber: 当前块的次序，第一个块是 1，注意不是从 0 开始的。
         * totalChunks: 文件被分成块的总数。
         * chunkSize: 分块大小，根据 totalSize 和这个值你就可以计算出总共的块数。注意最后一块的大小可能会比这个要大。
         * currentChunkSize: 当前块的大小，实际大小。
         * totalSize: 文件总大小。
         * identifier: 这个就是每个文件的唯一标示。
         * filename: 文件名。
         * relativePath: 文件夹上传的时候文件的相对路径属性。
         * 一个分块可以被上传多次，当然这肯定不是标准行为，但是在实际上传过程中是可能发生这种事情的，这种重传也是本库的特性之一。
         *
         * 根据响应码认为成功或失败的：
         * 200 文件上传完成
         * 201 文加快上传成功
         * 500 第一块上传失败，取消整个文件上传
         * 507 服务器出错自动重试该文件块上传
         */
        File file= new File(PathUtils.getFileDir(), chunk.getFilename());
        //第一个块,则新建文件
        if(chunk.getChunkNumber()==1 && !file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                response.setStatus(500);
                return Msg.error(500, "创建文件异常");
            }
        }

        //进行写文件操作
        try(
                //将块文件写入文件中
                InputStream fos=chunk.getFile().getInputStream();
                RandomAccessFile raf =new RandomAccessFile(file,"rw")
        ) {
            int len=-1;
            byte[] buffer=new byte[1024];
            raf.seek((chunk.getChunkNumber()-1)*1024*1024);
            while((len=fos.read(buffer))!=-1){
                raf.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(chunk.getChunkNumber()==1) {
                file.delete();
            }
            response.setStatus(507);
            return Msg.error(507, "写出文件异常");
        }
        if(chunk.getChunkNumber().equals(chunk.getTotalChunks())){
            response.setStatus(200);
            // TODO 向数据库中保存上传信息
            FileBean f = new FileBean(null, chunk.getFilename(),
                    null, "0", FileUtil.getFileExt(chunk.getFilename()),
                    chunk.getTotalSize(), null, Md5Utils.getMd5(chunk.getFile()),
                    null, null, null, "1", "0","0" , 1L);
            //
            // String fileName = chunk.getFilename();
            // f.setFileName(fileName);
            // f.setFileIsdir("0");
            // f.setFileSize(chunk.getTotalSize());
            // f.setFileExt(FileUtils.getFileExt(fileName));
            // f.setFileMd5(Md5Utils.getMd5(chunk.getFile()));
            // f.setFileAudit("1");
            // f.setUserId(1L);
            fileService.insertFileInfo(f);

            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id", 1L);
            Storage storage = storageMapper.selectOne(queryWrapper);
            UpdateWrapper updateWrapper = new UpdateWrapper();
            updateWrapper.set("storage_size_used", chunk.getTotalSize()+storage.getStorageSizeUsed());
            updateWrapper.eq("user_id", 1L);
            storageMapper.update(storage, updateWrapper);

            return Msg.success("上传成功").put("file", JSONObject.toJSON(f));
        }else {
            response.setStatus(201);
            MultipartFile file1 = chunk.getFile();
            String md5 = Md5Utils.md5HashCode32(new ByteArrayInputStream(file1.getBytes()));
            return Msg.success("ok").put("md5", md5);
        }
    }
}
