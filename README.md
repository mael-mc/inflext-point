# InflexPoint - Aplicación de Análisis de Funciones Matemáticas

## Descripción General

InflexPoint es una aplicación de escritorio desarrollada en **Java 17+ con JavaFX** que permite analizar funciones matemáticas y calcular automáticamente:

- **Puntos críticos** (máximos, mínimos, puntos de inflexión)
- **Intervalos de crecimiento y decrecimiento**
- **Intervalos de concavidad**
- **Derivadas primera y segunda** (análíticamente)
- **Representación gráfica** de la función

## Arquitectura y Diseño

### Restricciones de Diseño (por Requisitos del Proyecto)

- ✓ **NO usar estructuras dinámicas**: Se usan únicamente arreglos simples y atributos individuales
- ✓ **MVC estricto**: Separación clara de responsabilidades
  - **Modelo**: Cálculos matemáticos en `AnalizadorFuncion`
  - **Vista**: Componentes FXML
  - **Controlador**: Lógica de presentación sin cálculos
- ✓ **DAO Pattern**: `IFuncion` e `FuncionImpl` como intermediarios
- ✓ **Encapsulación**: Getters/setters, constructores explícitos
- ✓ **Código limpio**: Métodos cortos, comentarios académicos

### Estructura de Paquetes

```
com.espoch.inflexpoint
├── app/
│   └── App.java                  # Punto de entrada JavaFX
├── controladores/
│   ├── paneles/
│   │   ├── CalcularControlador.java    # Captura entrada, muestra resultados
│   │   ├── InicioControlador.java      # Información introductoria
│   │   └── AyudaControlador.java       # Documentación
│   └── vistaprincipal/
│       └── VistaPrincipalControlador.java # Navegación entre vistas
├── modelos/
│   ├── calculos/
│   │   ├── AnalizadorFuncion.java    # Análisis matemático
│   │   └── ResultadoAnalisis.java    # Contenedor de resultados
│   ├── dao/
│   │   ├── interfaces/
│   │   │   └── IFuncion.java
│   │   └── implementaciones/
│   │       └── FuncionImpl.java       # DAO intermediario
│   ├── entidades/
│   │   ├── Dominio.java              # [inicio, fin]
│   │   ├── Funcion.java              # ID, expresión, tipo, dominio
│   │   ├── Intervalo.java            # [inicio, fin, tipo]
│   │   ├── Punto.java                # (x, y)
│   │   └── PuntoCritico.java         # Punto + tipo
│   └── enumeraciones/
│       ├── TipoFuncion.java          # LINEAL, CUADRATICA, ...
│       ├── TipoIntervalo.java        # CRECIENTE, CONCAVIDAD_POSITIVA, ...
│       └── TipoPuntoCritico.java     # MAXIMO, MINIMO, INFLEXION
├── util/
│   └── GraficadorFuncion.java        # Dibuja con Canvas
└── resources/
    ├── paneles/
    │   ├── calcular-inflex.fxml      # UI de cálculo
    │   ├── inicio-inflex.fxml        # Página de inicio
    │   └── ayuda-inflex.fxml         # Documentación
    └── vistaprincipal/
        └── vista-principal.fxml      # Frame principal
```

## Tipos de Funciones Soportadas

| Tipo | Ejemplo | Primera Derivada | Segunda Derivada |
|------|---------|------------------|------------------|
| LINEAL | f(x) = 2x + 3 | f'(x) = 2 | f''(x) = 0 |
| CUADRATICA | f(x) = x² + 2x - 5 | f'(x) = 2x + 2 | f''(x) = 2 |
| POLINOMICA | f(x) = x³ + x² + 1 | Derivada polinómica | Segunda derivada polinómica |
| EXPONENCIAL | f(x) = 2^x | f'(x) = 2^x · ln(2) | f''(x) = 2^x · (ln(2))² |
| LOGARITMICA | f(x) = ln(x) | f'(x) = 1/x | f''(x) = -1/x² |
| TRIGONOMETRICA | f(x) = sin(x) | f'(x) = cos(x) | f''(x) = -sin(x) |
| RACIONAL | f(x) = p(x)/q(x) | Regla del cociente | Segunda derivada |

## Clases Principales

### AnalizadorFuncion
```java
// Punto de entrada para análisis
ResultadoAnalisis resultado = analizador.analizar(funcion);
```

**Métodos privados por tipo:**
- `analizarLineal()`: Determina crecimiento/decrecimiento
- `analizarCuadratica()`: Calcula vértice (máximo/mínimo)
- `analizarExponencial()`: Siempre creciente o decreciente
- `analizarLogaritmica()`: Creciente, concavidad hacia abajo
- `analizarTrigonometrica()`: Periódica
- `analizarPolinomica()`: Análisis de grado superior
- `analizarRacional()`: Análisis de cocientes

### ResultadoAnalisis
Almacena (no usa listas):
- 1 `PuntoCritico` máximo (o null)
- 1 `PuntoCritico` mínimo (o null)
- 1 `PuntoCritico` de inflexión (o null)
- 1 `Intervalo` de crecimiento (o null)
- 1 `Intervalo` de decrecimiento (o null)
- 1 `Intervalo` de concavidad arriba (o null)
- 1 `Intervalo` de concavidad abajo (o null)
- String de primera derivada
- String de segunda derivada

### CalcularControlador
**Responsabilidades:**
1. Capturar entrada del usuario (tipo, expresión)
2. Validar campos (no vacíos, caracteres permitidos)
3. Crear objeto `Funcion`
4. Llamar a `FuncionImpl.analizar()`
5. Mostrar resultados en `TextArea`
6. Invocar `GraficadorFuncion`

**NO realiza cálculos.**

### GraficadorFuncion
Dibuja en Canvas:
- Rejilla de fondo
- Ejes X e Y
- Curva de la función
- Puntos críticos con colores:
  - 🔴 Rojo: Máximos
  - 🟢 Verde: Mínimos
  - 🟠 Naranja: Puntos de inflexión

## Sintaxis de Entrada

Ejemplos de funciones válidas:
```
Lineal:          2*x + 3
Cuadrática:      x^2 + 2*x - 5
Polinómica:      x^3 + 2*x^2 - x + 1
Exponencial:     2^x
Logarítmica:     ln(x)
Trigonométrica:  sin(x), cos(x), tan(x)
```

Operadores: `+`, `-`, `*`, `/`, `^` (potencia)

## Flujo de la Aplicación

```
1. Usuario abre InflexPoint
   ↓
2. VistaPrincipalControlador carga inicio-inflex.fxml
   ↓
3. Usuario selecciona "Calcular"
   ↓
4. Se carga calcular-inflex.fxml
   ↓
5. Usuario ingresa:
   - Tipo de función (ComboBox)
   - Qué calcular (RadioButtons)
   - Expresión (TextField)
   ↓
6. Usuario hace clic en "CALCULAR"
   ↓
7. CalcularControlador valida campos
   ↓
8. Se crea objeto Funcion
   ↓
9. FuncionImpl.analizar(funcion) llama a AnalizadorFuncion
   ↓
10. AnalizadorFuncion realiza análisis (derivadas, puntos críticos)
    ↓
11. Retorna ResultadoAnalisis
    ↓
12. CalcularControlador muestra resultados
    ↓
13. GraficadorFuncion dibuja la función y puntos críticos
    ↓
14. Usuario visualiza gráfico y resultados
```

## Interpretación de Resultados

### Puntos Críticos
- **MÁXIMO**: Donde f'(x) = 0 y f''(x) < 0 (parábola hacia abajo)
- **MÍNIMO**: Donde f'(x) = 0 y f''(x) > 0 (parábola hacia arriba)
- **INFLEXIÓN**: Donde f''(x) = 0 y cambia la concavidad

### Intervalos
- **CRECIENTE**: Donde f'(x) > 0
- **DECRECIENTE**: Donde f'(x) < 0
- **CONCAVIDAD ARRIBA**: Donde f''(x) > 0 (∪)
- **CONCAVIDAD ABAJO**: Donde f''(x) < 0 (∩)

## Manejo de Errores

La aplicación valida:
- ✓ Campos no vacíos
- ✓ Tipo de función seleccionado
- ✓ Opción de cálculo seleccionada
- ✓ Caracteres permitidos
- ✓ División por cero (en gráficos)
- ✓ Logaritmos de números negativos

## Limitaciones Actuales

1. **Evaluación de expresiones**: Implementación simplificada (solo números directos)
   - Para producción, usar `ScriptEngine` o parser dedicado
2. **Funciones polinómicas**: Análisis parcial
   - Requeriría resolución de ecuaciones de orden superior
3. **Funciones racionales**: Análisis parcial
4. **Precisión numérica**: Limitada por Double
5. **Dominio automático**: Fijo de [-10, 10]

## Compilación

```bash
mvn clean compile
```

## Ejecución

```bash
mvn javafx:run
```

## Requisitos

- Java 17+
- JavaFX 17+
- Maven 3.6+

## Dependencias (pom.xml)

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.1</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>17.0.1</version>
</dependency>
```

## Ejemplos de Uso

### Ejemplo 1: Función Cuadrática
```
Tipo: CUADRATICA
Expresión: x^2 - 4*x + 3
Cálculo: Máximos/Mínimos

Resultado:
- Mínimo en (2, -1)
- Crecimiento: (2, ∞)
- Decrecimiento: (-∞, 2)
- Concavidad: Arriba en toda la recta
```

### Ejemplo 2: Función Lineal
```
Tipo: LINEAL
Expresión: 2*x - 5
Cálculo: Intervalos

Resultado:
- Primera derivada: 2
- Segunda derivada: 0
- La función crece en toda la recta
- Sin máximos, mínimos ni inflexión
```

## Consideraciones Académicas

Este código ha sido desarrollado con enfoque educativo:

1. **Claridad**: Variables y métodos con nombres descriptivos
2. **Encapsulación**: Private/public apropiados
3. **Documentación**: Comentarios javadoc en métodos públicos
4. **Separación de responsabilidades**: MVC, DAO, entidades
5. **Código limpio**: Métodos cortos, sin duplicación
6. **Sin atajos**: Respeto estricto a la arquitectura

## Autor

Aplicación desarrollada como proyecto universitario de Análisis Matemático Avanzado con herramientas computacionales.

---

**Versión**: 1.0  
**Última actualización**: Enero 2026  
**Estado**: Funcional para tipos de funciones básicas