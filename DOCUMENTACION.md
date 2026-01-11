# DOCUMENTACIÓN DE PROYECTO - SCRUM METHODOLOGY

## 1. Introducción
**InflexPoint** es una herramienta educativa diseñada para facilitar el análisis de funciones matemáticas. El desarrollo se llevó a cabo siguiendo el marco de trabajo **SCRUM**, permitiendo una entrega formal y un enfoque centrado en la calidad del diseño y la precisión matemática.

## 2. Roles de SCRUM
- **Product Owner:** Facultad de Informática y Electrónica (ESPOCH).
- **Scrum Master:** Juan Moreno.
- **Equipo de Desarrollo:**
  - [Karla Calvopiña](https://github.com/KARLACALVOPINA1020) (Backend & Matemáticas)
  - [Glenda Alvarado](https://github.com/GlendaAlvarado) (UI/UX Design)
  - [Jojanci Gómez](https://github.com/jojancigomez) (Backend & Logic)
  - [Andrea Quiroga](https://github.com/andrea21qp) (QA & Documentation)
  - [Juan Moreno](https://github.com/mael-mc) (Fullstack & Integration)

## 3. Product Backlog
| ID | Historia de Usuario | Prioridad | Estado |
|----|----------------------|-----------|--------|
| HU1 | Como usuario, quiero ingresar funciones matemáticas para analizarlas. | Alta | Terminado |
| HU2 | Como usuario, quiero ver la gráfica de la función ingresada. | Alta | Terminado |
| HU3 | Como usuario, quiero calcular puntos críticos y concavidad. | Alta | Terminado |
| HU4 | Como usuario, quiero una interfaz moderna y fácil de usar. | Media | Terminado |
| HU5 | Como usuario, quiero buscar información específica dentro de la app. | Baja | Terminado |
| HU6 | Como usuario, quiero usar un teclado virtual para símbolos especiales. | Media | Terminado |

## 4. Sprints del Proyecto

### Sprint 1: El Núcleo Matemático
- **Objetivo:** Implementar la lógica de análisis y derivación simbólica.
- **Entregables:** Clase `AnalizadorFuncion` con soporte para funciones lineales, cuadráticas y trigonométricas.
- **Resultado:** Éxito en la validación de derivadas analíticas.

### Sprint 2: Visualización y UI
- **Objetivo:** Crear la interfaz gráfica base y el motor de dibujo.
- **Entregables:** FXMLs bases y clase `GraficadorFuncion`.
- **Resultado:** Representación visual de funciones en un Canvas dinámico.

### Sprint 3: Experiencia de Usuario y Pulido (Sprint Final)
- **Objetivo:** Modernizar la estética y añadir utilidades de navegación.
- **Entregables:** Sistema Persian Green, Teclado Virtual y Buscador Global.
- **Resultado:** Aplicación profesional con alta calificación en usabilidad.

## 5. Ceremonias de SCRUM
- **Sprint Planning:** Definición del alcance de cada módulo antes de codificar.
- **Daily Stand-ups:** Reuniones de sincronización para resolver bloqueos de código (específicamente en la lógica de derivación).
- **Sprint Review:** Demostración de las gráficas y resultados analíticos correctos.
- **Sprint Retrospective:** Identificación de mejoras en la arquitectura MVC y optimización del CSS.

## 6. Artefactos de Calidad
- **Definición de Hecho (DoD):** Código documentado, sin errores de compilación, lógica validada matemáticamente y diseño CSS cohesivo.
- **Backlog Refinement:** Ajuste constante de las palabras clave para el motor de búsqueda global.

---
**© 2026 InflexPoint Team**
"Simplificando el cálculo, un punto a la vez."
