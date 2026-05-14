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

@Service
@RequiredArgsConstructor
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoClient productoClient;

    private VentaResponse convertirAResponse(Venta venta) {
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
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setCliente(request.getCliente());
        venta.setProductoId(request.getProductoId());
        venta.setCantidad(request.getCantidad());
        venta.setTotal(total);
        return venta;
    }

    // Crear venta
    public VentaResponse crearVenta(VentaRequest request) {
        // Obtener producto
        var producto = productoClient.obtenerProductoPorId(request.getProductoId());

        // Verificar stock
        if (producto.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para el producto");
        }

        // Calcular total
        Double total = producto.getPrecio() * request.getCantidad();

        // Reducir stock
        productoClient.reducirStock(request.getProductoId(), request.getCantidad());

        // Guardar venta
        Venta venta = convertirAEntity(request, total);
        Venta guardada = ventaRepository.save(venta);

        return convertirAResponse(guardada);
    }

    // Listar ventas
    @Transactional(readOnly = true)
    public List<VentaResponse> listarVentas() {
        return ventaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // Obtener venta por ID
    @Transactional(readOnly = true)
    public VentaResponse obtenerVentaPorId(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        return convertirAResponse(venta);
    }

    // Actualizar venta
    public VentaResponse actualizarVenta(Long id, VentaRequest request) {
        Venta ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // Para simplificar, no recalcular total ni stock, solo actualizar campos
        ventaExistente.setCliente(request.getCliente());
        ventaExistente.setProductoId(request.getProductoId());
        ventaExistente.setCantidad(request.getCantidad());

        Venta actualizada = ventaRepository.save(ventaExistente);
        return convertirAResponse(actualizada);
    }

    // Eliminar venta
    public void eliminarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        ventaRepository.delete(venta);
    }

}
