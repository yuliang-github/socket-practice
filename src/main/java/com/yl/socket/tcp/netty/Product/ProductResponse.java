package com.yl.socket.tcp.netty.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Alex
 * @since 2019/5/10 13:59
 */
@Data
@Setter
@Getter
@AllArgsConstructor
public class ProductResponse implements Serializable {

    private int code;

    private int orderId;

    private String msg;

    @Override
    public String toString() {
        return "ProductResponse{" +
            "code=" + code +
            ", orderId=" + orderId +
            ", msg='" + msg + '\'' +
            '}';
    }
}
