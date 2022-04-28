package com.fx.pan.admin.controller;

import com.fx.pan.common.Msg;
import com.fx.pan.domain.Chunk;
import com.fx.pan.domain.FileBean;
import com.fx.pan.dto.file.UploadFileDTO;
import com.fx.pan.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author leaving
 * @Date 2022/4/25 18:52
 * @Version 1.0
 */


@RestController
@RequestMapping("/admin/file")
public class FileMangeController {

    @Resource
    private FileService fileService;

    // 所有文件列表分页查看
    @GetMapping("/lsit")
    public Msg list(@RequestParam("path") String path, @RequestParam("page") int page,
                    @RequestParam("size") int size,@RequestParam("onlyFile") boolean onlyFile) {

        List<FileBean> fileBeanList= fileService.fileList(path,onlyFile, page, size);
        // Integer total = fileService.fileTotal()
        return Msg.success("成功").put("list",fileBeanList);
    }

    // 按照文件类型查看
    @GetMapping("/list/type")
    public Msg listType(@RequestParam("fileType") String fileType,@RequestParam("page") int page,
                           @RequestParam("size") int size) {


        return Msg.success("");

    }


    @GetMapping("/search")
    public Msg search(@RequestParam("keyword") String keyword,@RequestParam("page") int page,
                         @RequestParam("size") int size) {

        return Msg.success("");
    }

    @GetMapping("/upload")
    public Msg upload(UploadFileDTO uploadFileDto, @ModelAttribute Chunk chunk) {

        return Msg.success("");
    }

    @GetMapping("/download")
    public Msg download(@RequestParam("file") Long[] files) {

        return Msg.success("");
    }

    @PostMapping("/rename")
    public Msg rename(@RequestParam("id") Long id,@RequestParam("name") String name) {

        return Msg.success("");
    }

    @PostMapping("/delete")
    public Msg delete(@RequestParam("id") Long id) {

        return Msg.success("");
    }

    @PostMapping("/move")
    public Msg move(@RequestParam("id") Long id,@RequestParam("pathId") Long pathId) {

        return Msg.success("");
    }

    @PostMapping("/copy")
    public Msg copy(@RequestParam("id") Long id,@RequestParam("pathId") Long pathId) {

        return Msg.success("");
    }

    @PostMapping("/create")
    public Msg create(@RequestParam("id") Long id,@RequestParam("name") String name) {

        return Msg.success("");
    }

    @PostMapping("/create/folder")
    public Msg createFolder(@RequestParam("id") Long id,@RequestParam("name") String name) {

        return Msg.success("");
    }



    @GetMapping("/detail")
    public Msg detail(@RequestParam("id") Long id) {

        return Msg.success("");
    }




}
