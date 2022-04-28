package com.fx.pan.controller;

import com.fx.pan.service.NoteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author leaving
 * @Date 2022/3/31 8:22
 * @Version 1.0
 */

@RestController
@RequestMapping("/note")
public class NoteController {
    private NoteService noteService;

    // @RequestMapping("/get")
    // public Note getNote(){
    //     return noteService.getNote();
    // }
}
