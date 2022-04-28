package com.fx.pan.dto.office;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author leaving
 * @Date 2022/4/5 20:52
 * @Version 1.0
 */

@Data
@AllArgsConstructor
public class ResponseDTO implements Serializable {

    private static final long serialVersionUID = -275582248840137389L;

    private Integer type;

    private String id;

    private String username;

    private String data;


    public static ResponseDTO success(String id, String username, String data) {
        return new ResponseDTO(1, id, username, data);
    }

    public static ResponseDTO update(String id, String username, String data) {
        return new ResponseDTO(2, id, username, data);
    }

    public static ResponseDTO mv(String id, String username, String data) {
        return new ResponseDTO(3, id, username, data);
    }

    public static ResponseDTO bulkUpdate(String id, String username, String data) {
        return new ResponseDTO(4, id, username, data);
    }

}
