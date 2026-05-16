package cl.duocuc.venta.service;

import cl.duocuc.venta.client.ProductoClient;
import cl.duocuc.venta.dto.VentaRequest;
import cl.duocuc.venta.dto.VentaResponse;
import cl.duocuc.venta.model.Venta;
import cl.duocuc.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoClient productoClient;
    private VentaResponse convertirAResponse(Venta venta) {
        log.debug("Convirtiendo entidad Venta a Response - ID: {}", venta.getId());

        VentaResponse response = new VentaResponse();
        response.setId(venta.getId());
        response.setFecha(venta.getFecha());
        response.setCliente(venta.getCliente());
        response.setProductoId(venta.getProductoId());
        response.setCantidad(venta.getCantidad());
        response.setTotal(venta.getTotal());
        return response;
    }

    private Venta convertirAEntity(VentaRequest request, Double total) {
        log.debug("Convirtiendo request a entidad Venta - Cliente: {}", request.getCliente());

        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setCliente(request.getCliente());
        venta.setProductoId(request.getProductoId());
        venta.setCantidad(request.getCantidad());
        venta.setTotal(total);
        return venta;
    }


    public VentaResponse crearVenta(VentaRequest request) {
        log.info("Iniciando proceso de creación de venta para cliente: {}", request.getCliente());
        log.info("Producto ID: {}, Cantidad: {}", request.getProductoId(), request.getCantidad());

        log.debug("Llamando a Producto Service para obtener información del producto");
        var producto = productoClient.obtenerProductoPorId(request.getProductoId());

        if (producto.getStock() < request.getCantidad()) {
            log.warn("Stock insuficiente para producto ID: {}. Stock actual: {}, Cantidad solicitada: {}", request.getProductoId(), producto.getStock(), request.getCantidad());
            throw new RuntimeException("Stock insuficiente para el producto");
        }

        Double total = producto.getPrecio() * request.getCantidad();
        log.info("Total calculado de la venta: ${}", total);

        log.debug("Llamando a Producto Service para reducir stock");
        productoClient.reducirStock(request.getProductoId(), request.getCantidad());

        Venta venta = convertirAEntity(request, total);
        Venta guardada = ventaRepository.save(venta);

        log.info("Venta creada exitosamente - ID: {}, Cliente: {}, Total: ${}", guardada.getId(), guardada.getCliente(), guardada.getTotal());

        return convertirAResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listarVentas() {
        log.info("Listando todas las ventas registradas");

        List<VentaResponse> ventas = ventaRepository.findAll().stream().map(this::convertirAResponse).collect(Collectors.toList());

        log.info("Se encontraron {} ventas", ventas.size());
        return ventas;
    }

    @Transactional(readOnly = true)
    public VentaResponse obtenerVentaPorId(Long id) {
        log.info("Buscando venta por ID: {}", id);

        Venta venta = ventaRepository.findById(id).orElseThrow(() -> {
            log.error("Venta no encontrada con ID: {}", id);
            return new RuntimeException("Venta no encontrada");
        });

        log.info("Venta encontrada - ID: {}, Cliente: {}", venta.getId(), venta.getCliente());
        return convertirAResponse(venta);
    }

    public VentaResponse actualizarVenta(Long id, VentaRequest request) {
        log.info("Iniciando actualización de venta - ID: {}", id);

        Venta ventaExistente = ventaRepository.findById(id).orElseThrow(() -> {
            log.error("Venta no encontrada para actualizar - ID: {}", id);
            return new RuntimeException("Venta no encontrada");
        });

        ventaExistente.setCliente(request.getCliente());
        ventaExistente.setProductoId(request.getProductoId());
        ventaExistente.setCantidad(request.getCantidad());

        Venta actualizada = ventaRepository.save(ventaExistente);

        log.info("Venta actualizada exitosamente - ID: {}", actualizada.getId());
        return convertirAResponse(actualizada);
    }

    public void eliminarVenta(Long id) {
        log.info("Iniciando eliminación de venta - ID: {}", id);

        Venta venta = ventaRepository.findById(id).orElseThrow(() -> {
            log.error("Venta no encontrada para eliminar - ID: {}", id);
            return new RuntimeException("Venta no encontrada");
        });

        ventaRepository.delete(venta);
        log.info("Venta eliminada correctamente - ID: {}", id);
    }
}
