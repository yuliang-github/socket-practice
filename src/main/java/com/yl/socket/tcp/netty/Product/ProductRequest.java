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
public class ProductRequest implements Serializable {

    private int productId;

    private String userName;

    private String productName;

    @Override
    public String toString() {
        return "ProductRequest{" +
            "productId=" + productId +
            ", userName='" + userName + '\'' +
            ", productName='" + productName + '\'' +
            '}';
    }
}
