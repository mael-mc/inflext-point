from graphviz import Digraph
import os

def generar_diagrama_casos_uso():
    dot = Digraph(comment='Diagrama de Casos de Uso', format='png')
    dot.attr(rankdir='LR')

    # Actores
    dot.node('U', 'Usuario\n(Estudiante/Profesor)', shape='box')

    # Sistema
    with dot.subgraph(name='cluster_sistema') as c:
        c.attr(label='InflexPoint - Sistema de Análisis de Funciones')
        c.attr(style='filled', color='lightgrey')
        
        c.node('CU1', 'Ingresar Función Matemática')
        c.node('CU2', 'Calcular Derivadas\n(1ra y 2da)')
        c.node('CU3', 'Identificar Puntos Críticos\n(Máximos/Mínimos)')
        c.node('CU4', 'Identificar Puntos de Inflexión')
        c.node('CU5', 'Graficar Funciones y Puntos')
        c.node('CU6', 'Ver Historial de Calculos')
        c.node('CU7', 'Exportar/Guardar Resultados')

    # Relaciones
    dot.edge('U', 'CU1')
    dot.edge('U', 'CU2')
    dot.edge('U', 'CU3')
    dot.edge('U', 'CU4')
    dot.edge('U', 'CU5')
    dot.edge('U', 'CU6')
    dot.edge('U', 'CU7')
    
    # Dependencias entre casos de uso (Include/Extend)
    dot.edge('CU2', 'CU1', label=r'\<\<include\>\>', style='dashed')
    dot.edge('CU3', 'CU2', label=r'\<\<include\>\>', style='dashed')
    dot.edge('CU4', 'CU2', label=r'\<\<include\>\>', style='dashed')
    dot.edge('CU5', 'CU1', label=r'\<\<include\>\>', style='dashed')

    output_path = os.path.join('docs', 'diagramas', 'casos-uso')
    try:
        dot.render(output_path)
        print(f"Diagrama generado en: {output_path}.png")
    except Exception as e:
        dot.save(output_path + '.dot')
        print(f"Error al renderizar (falta Graphviz?): {e}. Se guardo el fuente en {output_path}.dot")

if __name__ == '__main__':
    generar_diagrama_casos_uso()
