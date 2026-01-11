# DOCUMENTACIÓN TÉCNICA - InflexPoint

## Guía de Desarrollo y Extensión

### 1. Agregar un Nuevo Tipo de Función

#### Paso 1: Actualizar enumeración TipoFuncion
Si no está presente, agregar a `TipoFuncion.java`:
```java
public enum TipoFuncion {
    LINEAL,
    CUADRATICA,
    POLINOMICA,
    EXPONENCIAL,
    LOGARITMICA,
    TRIGONOMETRICA,
    RACIONAL,
    NUEVO_TIPO  // <-- Agregar aquí
}
```

#### Paso 2: Implementar en AnalizadorFuncion
En `calcularPrimeraDerivada()`:
```java
case NUEVO_TIPO:
    return "f'(x) = ...";
```

En `calcularSegundaDerivada()`:
```java
case NUEVO_TIPO:
    return "f''(x) = ...";
```

En `analizar()`:
```java
case NUEVO_TIPO:
    analizarNuevoTipo(funcion, resultado);
    break;
```

Implementar método privado:
```java
private void analizarNuevoTipo(Funcion funcion, ResultadoAnalisis resultado) {
    // Lógica específica
    // Usar evaluarFuncion() para calcular puntos
    // Usar derivar() si es necesario
    // Pobluar resultado con hallazgos
}
```

#### Paso 3: Actualizar ComboBox
En `CalcularControlador.cargarTiposFunciones()`:
```java
comboTipoFuncion.getItems().addAll(
    TipoFuncion.LINEAL,
    // ... otros tipos ...
    TipoFuncion.NUEVO_TIPO  // <-- Agregar
);
```

### 2. Agregar un Nuevo Campo a ResultadoAnalisis

#### Ejemplo: Agregar Asintota Vertical
```java
public class ResultadoAnalisis {
    // ... campos existentes ...
    
    private Double asintovaVertical;  // Nueva
    
    // Constructor actualizado
    public ResultadoAnalisis() {
        // ... inicializaciones ...
        this.asintovaVertical = null;
    }
    
    // Getter y Setter
    public Double getAsintovaVertical() {
        return asintovaVertical;
    }
    
    public void setAsintovaVertical(Double asintovaVertical) {
        this.asintovaVertical = asintovaVertical;
    }
}
```

Luego usar en `AnalizadorFuncion`:
```java
resultado.setAsintovaVertical(valor);
```

Y mostrar en `CalcularControlador.mostrarResultados()`:
```java
if (resultado.getAsintovaVertical() != null) {
    sb.append("Asíntota vertical: ").append(resultado.getAsintovaVertical()).append("\n");
}
```

### 3. Mejorar la Evaluación de Expresiones

Actualmente es limitada. Para producción, usar `ScriptEngine`:

```java
private double evaluarExpresion(String expresion) {
    try {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Object resultado = engine.eval(expresion.replace("^", "**"));
        return ((Number) resultado).doubleValue();
    } catch (Exception e) {
        return 0;
    }
}
```

O usar una librería de parsing:
```java
// Agregar dependencia en pom.xml
// <artifactId>exp4j</artifactId>
```

### 4. Extender el Graficador

#### Agregar líneas de asíntotas
```java
private void dibujarAsintota(GraphicsContext gc, double valor, boolean esVertical,
                             double xMin, double xMax, double yMin, double yMax) {
    gc.setStroke(Color.GRAY);
    gc.setLineWidth(1);
    // Dibujar línea punteada
}
```

#### Agregar zoom interactivo
```java
contenedor.setOnScroll(event -> {
    double factor = event.getDeltaY() > 0 ? 1.2 : 0.8;
    xMin *= factor;
    xMax *= factor;
    yMin *= factor;
    yMax *= factor;
    graficar(contenedor, funcion, resultado);
});
```

#### Exportar gráfico como imagen
```java
private void exportarGrafico(Canvas canvas, String nombreArchivo) {
    WritableImage imagen = canvas.snapshot(null, null);
    ImageIO.write(SwingFXUtils.fromFXImage(imagen, null), 
                  "png", new File(nombreArchivo));
}
```

### 5. Mejorar la Validación

#### Validar dominio de la función
```java
private void validarDominio(Funcion funcion) {
    switch (funcion.getTipoFuncion()) {
        case LOGARITMICA:
            if (!expresion.contains("ln") && !expresion.contains("log")) {
                throw new IllegalArgumentException("Dominio: x > 0");
            }
            break;
        case RACIONAL:
            // Verificar denominador ≠ 0
            break;
    }
}
```

#### Validar sintaxis
```java
private void validarSintaxis(String expresion) {
    // Verificar paréntesis balanceados
    int contador = 0;
    for (char c : expresion.toCharArray()) {
        if (c == '(') contador++;
        if (c == ')') contador--;
        if (contador < 0) throw new IllegalArgumentException("Paréntesis desbalanceados");
    }
    if (contador != 0) throw new IllegalArgumentException("Paréntesis desbalanceados");
}
```

### 6. Testing

#### Crear clase de pruebas
```java
public class AnalizadorFuncionTest {
    @Test
    public void testFuncionLineal() {
        Funcion f = new Funcion(1, "2*x + 3", "x", TipoFuncion.LINEAL, 
                                new Dominio(-10.0, 10.0));
        ResultadoAnalisis resultado = analizador.analizar(f);
        assertEquals("2", resultado.getPrimeraDerivada());
        assertEquals("0", resultado.getSegundaDerivada());
    }
    
    @Test
    public void testFuncionCuadratica() {
        Funcion f = new Funcion(1, "x^2 - 4*x + 3", "x", TipoFuncion.CUADRATICA,
                                new Dominio(-10.0, 10.0));
        ResultadoAnalisis resultado = analizador.analizar(f);
        assertNotNull(resultado.getPuntoCriticoMinimo());
        assertEquals(2.0, resultado.getPuntoCriticoMinimo().getX(), 0.01);
        assertEquals(-1.0, resultado.getPuntoCriticoMinimo().getY(), 0.01);
    }
}
```

### 7. Mejoras Futuras (Roadmap)

**Corto plazo:**
- [ ] Mejorar evaluador de expresiones (usar exp4j o similiar)
- [ ] Soportar más funciones trigonométricas (sec, csc, cot)
- [ ] Cálculo de asíntotas
- [ ] Análisis de límites

### 8. Sistema de Diseño (Persian Green)

La aplicación utiliza un sistema de diseño personalizado definido en `estilos.css`:
- **Variables CSS:** Uso intensivo de tokens de color (`-color-50` a `-color-900`) basados en `#2a9d8f`.
- **Componentes:**
  - `.card`: Contenedores con sombra suave y bordes redondeados.
  - `.button-primary`: Botones con feedback visual en hover.
  - `.results-container`: Áreas de texto con acento visual en el borde izquierdo.

### 9. Componentes Especiales

#### Teclado Virtual (`TecladoVirtual.java`)
Inyecta botones dinámicamente en la interfaz. Utiliza el sistema de estilos para mantener la coherencia visual. Permite la entrada de símbolos matemáticos complejos sin depender del teclado físico.

#### Buscador Inteligente (`VistaPrincipalControlador.java`)
Implementa un motor de decisión basado en palabras clave normalizadas. Utiliza un patrón de acceso estático (`getInstancia()`) para permitir que sub-paneles soliciten navegación al controlador principal.

### 10. Mejores Prácticas Mantenidas

✓ **Separación MVC estricta**
- Modelos no importan JavaFX
- Controladores no hacen cálculos
- Vistas son solo XML (FXML)

✓ **No usar Collections**
- Usar atributos individuales o arreglos simples
- Si se necesita N items, usar Array[N] o campo individual

✓ **Encapsulación completa**
- Todos los atributos private
- Getters/setters públicos
- Constructores explícitos

✓ **Código documentado**
- Javadoc en métodos públicos
- Comentarios internos para lógica compleja
- README actualizado

### 9. Debugging

#### Habilitar logs
```java
System.out.println("Analizando función: " + funcion.getExpresion());
System.out.println("Tipo: " + funcion.getTipoFuncion());
System.out.println("Primera derivada: " + resultado.getPrimeraDerivada());
```

O usar un logger:
```java
import java.util.logging.Logger;
private static final Logger LOG = Logger.getLogger(AnalizadorFuncion.class.getName());
LOG.info("Resultado: " + resultado);
```

#### Breakpoints en IDE
- Pause en `AnalizadorFuncion.analizar()`
- Inspeccionar `Funcion` ingresada
- Verificar cada paso del análisis

### 10. Referencias Matemáticas

**Derivadas comunes:**
```
f(x) = x^n          → f'(x) = n·x^(n-1)
f(x) = e^x          → f'(x) = e^x
f(x) = ln(x)        → f'(x) = 1/x
f(x) = sin(x)       → f'(x) = cos(x)
f(x) = cos(x)       → f'(x) = -sin(x)
f(x) = a^x          → f'(x) = a^x·ln(a)
```

**Puntos críticos:**
- f'(x) = 0 → Punto crítico
- f''(x) > 0 → Mínimo local
- f''(x) < 0 → Máximo local
- f''(x) = 0 → Posible punto de inflexión

**Concavidad:**
- f''(x) > 0 → Cóncava hacia arriba (∪)
- f''(x) < 0 → Cóncava hacia abajo (∩)
- Cambio de signo en f''(x) → Punto de inflexión

---

**Última actualización**: Enero 2026