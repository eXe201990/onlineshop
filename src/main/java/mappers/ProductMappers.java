package mappers;

import entities.Product;
import org.springframework.stereotype.Component;
import vos.ProductVos;

@Component
public class ProductMappers {

    public Product toEntity(ProductVos productVo) {
        if(productVo == null) {
            return null;
        }
        Product product  = new Product();
        product.setId(productVo.getId());
        product.setPrice(productVo.getPrice());
        product.setCode(productVo.getCode());
        product.setDescription(productVo.getDescription());
        product.setStock(productVo.getStock());
        product.setValid(productVo.isValid());
        product.setCurrencies(productVo.getCurrencies());

        return product;

    }
    public ProductVos toVo(Product product) {
        if( product == null) {
            return null;
        }

        ProductVos productVo = new ProductVos();
        productVo.setId( product.getId());
        productVo.setPrice(product.getPrice());
        productVo.setCode( product.getCode());
        productVo.setDescription( product.getDescription());
        productVo.setStock( product.getStock());
        productVo.setValid( product.isValid());
        productVo.setCurrencies(product.getCurrencies());
        return   productVo;
    }

}

