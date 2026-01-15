from graphviz import Digraph
import os

def generar_diagrama_paquetes():
    dot = Digraph(comment='Diagrama de Paquetes', format='png')
    dot.attr(rankdir='TB')
    dot.attr('node', shape='tab')

    # Root
    with dot.subgraph(name='cluster_com') as c:
        c.attr(label='com.espoch.inflexpoint')
        c.attr(style='filled', color='ivory')

        # App
        c.node('App', 'app')

        # Controladores
        with c.subgraph(name='cluster_controladores') as cc:
            cc.attr(label='controladores', color='lightblue')
            cc.node('Paneles', 'paneles')
            cc.node('Vista', 'vistaprincipal')
            
            cc.edge('Vista', 'Paneles', style='dashed')

        # Modelos
        with c.subgraph(name='cluster_modelos') as cm:
            cm.attr(label='modelos', color='lightgreen')
            cm.node('Calculos', 'calculos')
            cm.node('DAO', 'dao')
            cm.node('Entidades', 'entidades')
            cm.node('Enumeraciones', 'enumeraciones')
            cm.node('Excepciones', 'excepciones')
            
            # Relaciones internas de modelos
            cm.edge('Calculos', 'Entidades', style='dashed')
            cm.edge('Calculos', 'Enumeraciones', style='dashed')
            cm.edge('DAO', 'Entidades', style='dashed')

        # Util
        with c.subgraph(name='cluster_util') as cu:
            cu.attr(label='util', color='lightyellow')
            cu.node('Util', 'util')

        # Relaciones entre paquetes principales
        c.edge('App', 'Vista', label='inicia')
        c.edge('Paneles', 'Calculos', label='usa')
        c.edge('Paneles', 'DAO', label='usa')
        c.edge('Paneles', 'Util', label='usa')
        c.edge('Calculos', 'Excepciones', label='lanza')

    output_path = os.path.join('docs', 'diagramas', 'paquetes')
    try:
        dot.render(output_path)
        print(f"Diagrama generado en: {output_path}.png")
    except Exception as e:
        dot.save(output_path + '.dot')
        print(f"Error al renderizar (falta Graphviz?): {e}. Se guardo el fuente en {output_path}.dot")

if __name__ == '__main__':
    generar_diagrama_paquetes()
