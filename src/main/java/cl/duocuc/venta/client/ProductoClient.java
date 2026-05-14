package cl.duocuc.venta.client;

import cl.duocuc.venta.dto.ProductoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "producto-service", url = "http://localhost:8082")
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoResponse obtenerProductoPorId(@PathVariable("id") Long id);

    @PutMapping("/api/productos/{id}/reducir-stock")
    ProductoResponse reducirStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);

}
