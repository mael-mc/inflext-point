from graphviz import Digraph
import os

def generar_diagrama_clases():
    dot = Digraph(comment='Diagrama de Clases', format='png')
    dot.attr(rankdir='BT')  # Bottom to Top helps with inheritance visualization
    dot.attr('node', shape='record')

    # --- Paquete App ---
    dot.node('App', '{App|+ main(args: String[]): void}')

    # --- Paquete Controladores ---
    dot.node('VistaPrincipalControlador', '{VistaPrincipalControlador| - panelActual: Node\\n - calcularControlador: CalcularControlador | + inicializar(): void}')
    dot.node('CalcularControlador', '{CalcularControlador| - funcionDao: IFuncion\\n - analizador: AnalizadorFuncion | + procesarFuncion(): void}')
    
    # --- Paquete Modelos.Entidades ---
    dot.node('Funcion', '{Funcion| - expresion: String\\n - tipo: TipoFuncion | + getExpresion(): String}')
    dot.node('Punto', '{Punto| - x: double\\n - y: double | + toString(): String}')
    dot.node('PuntoCritico', '{PuntoCritico| - tipo: TipoPuntoCritico | + getTipo(): TipoPuntoCritico}')
    dot.node('Intervalo', '{Intervalo| - inicio: double\\n - fin: double | + getLongitud(): double}')
    
    # Herencia
    dot.edge('PuntoCritico', 'Punto', arrowtail='onormal', dir='back')

    # --- Paquete Modelos.Calculos ---
    dot.node('AnalizadorFuncion', '{AnalizadorFuncion| - derivador: DerivadorSimbolico\\n - evaluador: Evaluador | + analizar(f: Funcion): ResultadoAnalisis}')
    dot.node('DerivadorSimbolico', '{DerivadorSimbolico| | + derivar(exp: String): String}')
    dot.node('Evaluador', '{Evaluador| | + evaluar(exp: String, x: double): double}')
    dot.node('ResultadoAnalisis', '{ResultadoAnalisis| - primeraDerivada: String\\n - segundaDerivada: String\\n - puntosCriticos: List<PuntoCritico> | + getInforme(): String}')
    
    # --- Paquete Modelos.DAO ---
    dot.node('IFuncion', r'{ \<\<interface\>\> \nIFuncion| + guardar(f: Funcion): void\n + listar(): List<Funcion>}')
    dot.node('FuncionImpl', '{FuncionImpl| - historial: List<Funcion> | + guardar(f: Funcion): void}')
    
    dot.edge('FuncionImpl', 'IFuncion', arrowtail='empty', style='dashed', dir='back', label=r'\<\<implements\>\>')

    # --- Paquete Util ---
    dot.node('GraficadorCanvas', '{GraficadorCanvas| - canvas: Canvas | + graficar(f: String): void}')
    dot.node('ValidadorExpresion', '{ValidadorExpresion| | + esValida(exp: String): boolean}')

    # --- Relaciones ---
    # App llama a VistaPrincipal
    dot.edge('App', 'VistaPrincipalControlador', label='inicia')
    
    # Controladores usan modelos
    dot.edge('VistaPrincipalControlador', 'CalcularControlador', label='contiene')
    dot.edge('CalcularControlador', 'AnalizadorFuncion', label='usa')
    dot.edge('CalcularControlador', 'IFuncion', label='usa')
    dot.edge('CalcularControlador', 'GraficadorCanvas', label='usa')
    dot.edge('CalcularControlador', 'ValidadorExpresion', label='usa')

    # Analizador compone lógica
    dot.edge('AnalizadorFuncion', 'DerivadorSimbolico', label='usa')
    dot.edge('AnalizadorFuncion', 'Evaluador', label='usa')
    dot.edge('AnalizadorFuncion', 'ResultadoAnalisis', label='produce')

    output_path = os.path.join('docs', 'diagramas', 'clases')
    try:
        dot.render(output_path)
        print(f"Diagrama generado en: {output_path}.png")
    except Exception as e:
        dot.save(output_path + '.dot')
        print(f"Error al renderizar (falta Graphviz?): {e}. Se guardo el fuente en {output_path}.dot")

if __name__ == '__main__':
    generar_diagrama_clases()
