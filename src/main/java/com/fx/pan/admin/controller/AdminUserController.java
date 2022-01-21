package com.fx.pan.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author leaving
 * @Date 2022/1/20 16:50
 * @Version 1.0
 */

@RestController
@PreAuthorize("admin")
public class AdminUserController {


}
