from graphviz import Digraph
import os

def generar_diagrama_despliegue():
    dot = Digraph(comment='Diagrama de Despliegue', format='png')
    dot.attr(rankdir='TB')

    # Nodo Usuario
    with dot.subgraph(name='cluster_pc') as c:
        c.attr(label='PC Usuario (Windows/Linux/macOS)')
        c.node('OS', 'Sistema Operativo', shape='box3d')
        
        with c.subgraph(name='cluster_jre') as j:
            j.attr(label='Java Runtime Environment (JRE 17+)')
            j.attr(style='filled', color='lightgrey')
            
            j.node('App', label=r'\<\<component\>\>\nInflexPoint.jar', shape='component')
            j.node('JavaFX', 'JavaFX SDK', shape='component')
            
            dot.edge('App', 'JavaFX', label=r'\<\<depende\>\>', style='dashed')

    # Perifericos
    dot.node('Mouse', 'Mouse', shape='note')
    dot.node('Teclado', 'Teclado', shape='note')
    dot.node('Monitor', 'Monitor', shape='note')

    # Conexiones
    dot.edge('Mouse', 'OS', label=r'\<\<input\>\>')
    dot.edge('Teclado', 'OS', label=r'\<\<input\>\>')
    dot.edge('OS', 'Monitor', label=r'\<\<output\>\>')

    output_path = os.path.join('docs', 'diagramas', 'despliegue')
    try:
        dot.render(output_path)
        print(f"Diagrama generado en: {output_path}.png")
    except Exception as e:
        dot.save(output_path + '.dot')
        print(f"Error al renderizar (falta Graphviz?): {e}. Se guardo el fuente en {output_path}.dot")

if __name__ == '__main__':
    generar_diagrama_despliegue()
