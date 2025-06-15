package org.dromara.mpe.demo.bind.autoResultMap;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.bind.Binder;
import org.dromara.mpe.demo.bind.DemoBindApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

@EnableAutoTableTest
@SpringBootTest(classes = DemoBindApplication.class)
public class AutoResultMapTest {

    @Resource
    private ProductRepository productRepository;
    @Resource
    private WoProductRepository woProductRepository;
    @Resource
    private WorkOrderRepository workOrderRepository;

    private Long workOrderId = 1L;

    @BeforeEach
    public void init() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("产品1");
        product.setFileNames(Arrays.asList("文件1-1", "文件1-2"));
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("产品2");
        product2.setFileNames(Arrays.asList("文件2-1", "文件2-2"));

        productRepository.saveBatch(Arrays.asList(product, product2), 2);

        WoProduct woProduct = new WoProduct();
        woProduct.setWorkOrderId(workOrderId);
        woProduct.setProductId(1L);

        WoProduct woProduct2 = new WoProduct();
        woProduct2.setWorkOrderId(2L);
        woProduct2.setProductId(2L);

        woProductRepository.saveBatch(Arrays.asList(woProduct, woProduct2), 2);

        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderId(workOrderId);
        workOrderRepository.save(workOrder);
    }

    @Test
    public void test() {
        WorkOrder workOrder = workOrderRepository.getById(workOrderId);
        Binder.bind(workOrder);
        Binder.bindOn(workOrder.getWoProducts(), WoProduct::getProductName);
        System.out.println(workOrder);
    }
}
