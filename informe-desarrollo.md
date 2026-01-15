# Informe de Desarrollo del Proyecto: InflexPoint

> **Aviso Importante**: Este documento constituye el INFORME DE DESARROLLO académico y técnico. No debe confundirse con el Manual Técnico, el cual se entrega por separado.

## 1. Introducción

### Contexto del Problema
El cálculo diferencial es una rama fundamental de las matemáticas con aplicaciones críticas en ingeniería, física y economía. El análisis de funciones, específicamente la identificación de derivadas, puntos críticos y puntos de inflexión, permite comprender el comportamiento de fenómenos cambiantes. Sin embargo, el cálculo manual de estas propiedades es propenso a errores humanos y puede resultar tedioso para funciones complejas. En el contexto educativo y profesional, existe la necesidad de herramientas de software que no solo calculen resultados, sino que también visualicen el comportamiento de las funciones de manera interactiva.

### Importancia Académica y Técnica
Desde una perspectiva académica, este proyecto integra conceptos avanzados de **Cálculo de Una Variable** (derivación, criterios de la primera y segunda derivada) con **Ingeniería de Software** (arquitectura de aplicaciones, patrones de diseño). Técnicamente, el desarrollo de "InflexPoint" demuestra la capacidad de construir una aplicación de escritorio robusta en Java, implementando algoritmos matemáticos complejos y una interfaz gráfica interactiva, estandarizada mediante patrones de diseño profesional.

## 2. Objetivos

### Objetivo General
Desarrollar una aplicación de escritorio en Java utilizando el patrón de arquitectura MVC y la metodología ágil Scrum, que permita el análisis automático de funciones matemáticas para determinar derivadas, puntos críticos y de inflexión, proporcionando representaciones gráficas y analíticas precisas.

### Objetivos Específicos
1.  **Matemáticos**:
    *   Implementar un motor de análisis simbólico capaz de calcular la primera y segunda derivada de funciones algebraicas y trascendentes.
    *   Algoritmizar el criterio de la primera derivada para hallar máximos y mínimos locales.
    *   Algoritmizar el criterio de la segunda derivada para identificar puntos de inflexión y concavidad.
    *   Integrar un sistema de evaluación numérica de expresiones matemáticas.

2.  **Desarrollo de Software**:
    *   Diseñar la arquitectura del sistema bajo el patrón **Modelo-Vista-Controlador (MVC)** para garantizar la separación de responsabilidades y mantenibilidad.
    *   Implementar el patrón **Data Access Object (DAO)** para la gestión y persistencia temporal del historial de cálculos.
    *   Aplicar la metodología **Scrum**, definiendo roles, sprints y un backlog priorizado.
    *   Desarrollar una interfaz gráfica (GUI) intuitiva y funcional utilizando JavaFx.

## 3. Metodología de Desarrollo

Se ha seleccionado **Scrum** como marco de trabajo ágil, adaptado para este equipo de desarrollo académico. Esto permitió una entrega incremental de valor y una rápida adaptación a los cambios en los requisitos.

### Roles del Equipo
*   **Product Owner**: Responsable de definir las historias de usuario y priorizar el Product Backlog (Representado por la visión del cliente/docente).
*   **Scrum Master**: Facilitador del proceso, eliminando impedimentos técnicos y asegurando el cumplimiento de la metodología.
*   **Equipo de Desarrollo**: Encargados de la arquitectura, implementación (Backend/Frontend), pruebas y documentación.

### Sprints Ejecutados
El desarrollo se dividió en dos ciclos principales (Sprints), alineados con las entregas académicas:

*   **Sprint 1 (Ciclo Inicial)**:
    *   *Objetivo*: Establecer la arquitectura base y funcionalidades core.
    *   *Entregables*:  Estructura de paquetes, cálculo básico de derivadas, interfaz preliminar.
*   **Sprint 2 (Ciclo Final)**:
    *   *Objetivo*: Refinamiento, visualización avanzada y documentación técnica.
    *   *Entregables*: Gráficas dinámicas, detección de puntos de inflexión, validaciones robustas, manual técnico profesional.

## 4. Desarrollo del Sistema

### Arquitectura de Software
Se implementó estrictamente el patrón **MVC (Modelo-Vista-Controlador)** complementado con una capa **DAO** y servicios auxiliares.

*   **Modelo (`com.espoch.inflexpoint.modelos`)**: Contiene la lógica de negocio, entidades (`Funcion`, `Punto`) y algoritmos matemáticos (`DerivadorSimbolico`, `AnalizadorFuncion`). Es independiente de la interfaz de usuario.
*   **Vista (`resources/fxml`)**: Definida mediante archivos FXML (JavaFX), asegurando que la interfaz sea puramente declarativa y desacoplada de la lógica.
*   **Controlador (`com.espoch.inflexpoint.controladores`)**: Gestiona la interacción entre la vista y el modelo. Captura eventos de usuario y actualiza la vista.
*   **DAO (`com.espoch.inflexpoint.modelos.dao`)**: Interfaz `IFuncion` e implementación en memoria para gestionar el historial de operaciones, preparando el sistema para una futura persistencia en base de datos.
*   **Utilidades (`com.espoch.inflexpoint.util`)**: Clases transversales como renderizadores de fórmulas y validadores.

### Decisiones Técnicas Justificadas
1.  **JavaFX vs Swing**: Se eligió JavaFX por su capacidad de separar el diseño (FXML/CSS) del código, soporte nativo para gráficos modernos y mejor rendimiento.
2.  **Librerías de Terceros**: Se minimizó el uso de librerías externas para el núcleo matemático para demostrar dominio algorítmico, implementando un parser y derivador propio o adaptado.
3.  **Graphviz para Documentación**: La generación de diagramas UML mediante scripts ("Infrastructure as Code") asegura que la documentación siempre refleje la realidad del código.

## 5. Cumplimiento de la Rúbrica

| Criterio (Ciclo) | Evidencia en el Proyecto | Estado |
| :--- | :--- | :--- |
| **Manual Técnico** | Documento `manual-tecnico.md` con estructura profesional. | ✅ Completo |
| **Diagrama Casos de Uso** | `docs/diagramas/casos-uso.png` generado desde código. Muestra actores y flujos. | ✅ Completo |
| **Diagrama de Clases** | `docs/diagramas/clases.png`. Refleja herencia, interfaces DAO y relaciones. | ✅ Completo |
| **Diagrama de Paquetes** | `docs/diagramas/paquetes.png`. Muestra `app`, `controladores`, `modelos`. | ✅ Completo |
| **Diagrama Despliegue** | `docs/diagramas/despliegue.png`. Arquitectura física y entorno Runtime. | ✅ Completo |
| **Implementación Funcional** | Cálculo de derivadas, gráfica y puntos críticos operativos. | ✅ Completo |
| **Buenas Prácticas** | Nomenclatura CamelCase, separación MVC, control de excepciones. | ✅ Completo |
| **Control de Versiones** | Repositorio organizado con `.gitignore` adecuado y commits descriptivos. | ✅ Completo |
| **Calidad UI/UX** | Diseño limpio, validación de entradas, feedback visual al usuario. | ✅ Completo |

## 6. Resultados Obtenidos

Se ha logrado una aplicación funcional capaz de:
1.  Validar sintácticamente expresiones matemáticas ingresadas por el usuario.
2.  Calcular simbólicamente $f'(x)$ y $f''(x)$.
3.  Determinar las coordenadas $(x, y)$ de puntos críticos y clasificarlos (máximo/mínimo/silla).
4.  Identificar puntos de inflexión donde cambia la concavidad.
5.  Graficar la función original en un plano cartesiano interactivo.

**Nivel de Cumplimiento**: 100% de los requerimientos funcionales críticos definidos en el alcance inicial y la rúbrica de evaluación.

## 7. Pruebas Realizadas

### Pruebas de Cálculo
Se verificó la exactitud matemática comparando resultados con herramientas estándar (WolframAlpha):
*   *Polinomios*: $x^3 - 3x^2$ $\rightarrow$ Correcto.
*   *Trigonométricas*: $\sin(x)$ $\rightarrow$ Correcto.
*   *Compuestas*: $\ln(x^2 + 1)$ $\rightarrow$ Correcto.

### Pruebas de UI y UX
*   **Responsividad**: La interfaz se adapta a redimensionamiento básico.
*   **Validación**: El campo de texto rechaza caracteres no matemáticos y maneja errores de sintaxis (paréntesis desbalanceados) mostrando alertas claras.

### Manejo de Errores
Se implementaron bloques `try-catch` para capturar `ExpresionInvalidaException` y `CalculoNumericoException`, evitando el cierre inesperado de la aplicación ("crashes") y guiando al usuario para corregir la entrada.

## 8. Lecciones Aprendidas

1.  **Complejidad del Análisis Simbólico**: Construir un derivador simbólico requiere un manejo profundo de árboles de expresión o reglas de cadena recursivas, más allá de la simple evaluación numérica.
2.  **Importancia del MVC**: Al inicio, separar la lógica de la vista pareció añadir trabajo extra, pero resultó vital para depurar los cálculos sin romper la interfaz gráfica.
3.  **Documentación Continua**: Dejar la documentación para el final es un error; documentar diagramas mediante código (Python/Graphviz) facilita mantenerlos actualizados conforme el código evoluciona.

## 9. Conclusiones y Recomendaciones

### Conclusiones
El proyecto "InflexPoint" cumple satisfactoriamente con los objetivos académicos y técnicos. Integra exitosamente conceptos de cálculo avanzado con una arquitectura de software sólida. La decisión de usar una arquitectura por capas y patrones de diseño garantiza que el software sea mantenible y escalable.

### Recomendaciones
*   **Persistencia Real**: Migrar la implementación DAO actual (en memoria) a una base de datos SQLite o H2 para guardar historial entre sesiones.
*   **Exportación**: Implementar funcionalidad para exportar los análisis a PDF o LaTeX.
*   **Mejoras en el Parser**: Ampliar el soporte del analizador sintáctico para incluir funciones más complejas (hiperbólicas, integrales definidas).

---
*Documento generado automáticamente como parte de la entrega final del proyecto.*
