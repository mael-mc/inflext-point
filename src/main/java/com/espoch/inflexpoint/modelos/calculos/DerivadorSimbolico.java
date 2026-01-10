package com.espoch.inflexpoint.modelos.calculos;

/**
 * Motor de diferenciación simbólica avanzada.
 * Utiliza un Árbol de Sintaxis Abstracta (AST) para aplicar reglas de
 * derivación
 * como la regla de la cadena, producto, cociente y potencias.
 */
public class DerivadorSimbolico {

    public static String derivar(String expresion) {
        if (expresion == null || expresion.trim().isEmpty())
            return "0";
        try {
            String cleanExpr = normalize(expresion);
            Parser parser = new Parser(cleanExpr);
            Node ast = parser.parse();
            Node derived = ast.diff();
            return derived.simplify().toString();
        } catch (Exception e) {
            return "d/dx[" + expresion + "]";
        }
    }

    private static String normalize(String expr) {
        return expr.toLowerCase().replaceAll("\\s+", "")
                .replace("sen", "sin")
                .replace("raiz", "sqrt");
    }

    interface Node {
        Node diff();

        Node simplify();

        String toString();
    }

    static class ConstantNode implements Node {
        double val;

        ConstantNode(double v) {
            this.val = v;
        }

        public Node diff() {
            return new ConstantNode(0);
        }

        public Node simplify() {
            return this;
        }

        public String toString() {
            if (val == (long) val)
                return String.valueOf((long) val);
            return String.format("%.2f", val);
        }
    }

    static class VarNode implements Node {
        public Node diff() {
            return new ConstantNode(1);
        }

        public Node simplify() {
            return this;
        }

        public String toString() {
            return "x";
        }
    }

    static class AddNode implements Node {
        Node left, right;

        AddNode(Node l, Node r) {
            left = l;
            right = r;
        }

        public Node diff() {
            return new AddNode(left.diff(), right.diff());
        }

        public Node simplify() {
            Node sl = left.simplify();
            Node sr = right.simplify();
            if (sl instanceof ConstantNode && ((ConstantNode) sl).val == 0)
                return sr;
            if (sr instanceof ConstantNode && ((ConstantNode) sr).val == 0)
                return sl;
            if (sl instanceof ConstantNode && sr instanceof ConstantNode)
                return new ConstantNode(((ConstantNode) sl).val + ((ConstantNode) sr).val);
            return new AddNode(sl, sr);
        }

        public String toString() {
            return "(" + left + " + " + right + ")";
        }
    }

    static class SubNode implements Node {
        Node left, right;

        SubNode(Node l, Node r) {
            left = l;
            right = r;
        }

        public Node diff() {
            return new SubNode(left.diff(), right.diff());
        }

        public Node simplify() {
            Node sl = left.simplify();
            Node sr = right.simplify();
            if (sr instanceof ConstantNode && ((ConstantNode) sr).val == 0)
                return sl;
            if (sl instanceof ConstantNode && sr instanceof ConstantNode)
                return new ConstantNode(((ConstantNode) sl).val - ((ConstantNode) sr).val);
            return new SubNode(sl, sr);
        }

        public String toString() {
            return "(" + left + " - " + right + ")";
        }
    }

    static class MulNode implements Node {
        Node left, right;

        MulNode(Node l, Node r) {
            left = l;
            right = r;
        }

        public Node diff() {
            return new AddNode(new MulNode(left.diff(), right), new MulNode(left, right.diff()));
        }

        public Node simplify() {
            Node sl = left.simplify();
            Node sr = right.simplify();
            if (sl instanceof ConstantNode) {
                double v = ((ConstantNode) sl).val;
                if (v == 0)
                    return new ConstantNode(0);
                if (v == 1)
                    return sr;
            }
            if (sr instanceof ConstantNode) {
                double v = ((ConstantNode) sr).val;
                if (v == 0)
                    return new ConstantNode(0);
                if (v == 1)
                    return sl;
            }
            if (sl instanceof ConstantNode && sr instanceof ConstantNode)
                return new ConstantNode(((ConstantNode) sl).val * ((ConstantNode) sr).val);
            return new MulNode(sl, sr);
        }

        public String toString() {
            return left + "*" + right;
        }
    }

    static class DivNode implements Node {
        Node left, right;

        DivNode(Node l, Node r) {
            left = l;
            right = r;
        }

        public Node diff() {
            return new DivNode(
                    new SubNode(new MulNode(left.diff(), right), new MulNode(left, right.diff())),
                    new PowNode(right, new ConstantNode(2)));
        }

        public Node simplify() {
            Node sl = left.simplify();
            Node sr = right.simplify();
            if (sl instanceof ConstantNode && ((ConstantNode) sl).val == 0)
                return new ConstantNode(0);
            if (sr instanceof ConstantNode && ((ConstantNode) sr).val == 1)
                return sl;
            return new DivNode(sl, sr);
        }

        public String toString() {
            return "(" + left + "/" + right + ")";
        }
    }

    static class PowNode implements Node {
        Node base, exp;

        PowNode(Node b, Node e) {
            base = b;
            exp = e;
        }

        public Node diff() {
            Node se = exp.simplify();
            if (se instanceof ConstantNode) {
                double n = ((ConstantNode) se).val;
                return new MulNode(
                        new MulNode(new ConstantNode(n), new PowNode(base, new ConstantNode(n - 1))),
                        base.diff());
            }
            return new ConstantNode(0);
        }

        public Node simplify() {
            Node sb = base.simplify();
            Node se = exp.simplify();
            if (se instanceof ConstantNode) {
                double v = ((ConstantNode) se).val;
                if (v == 0)
                    return new ConstantNode(1);
                if (v == 1)
                    return sb;
            }
            return new PowNode(sb, se);
        }

        public String toString() {
            return "(" + base + "^" + exp + ")";
        }
    }

    static class FuncNode implements Node {
        String name;
        Node arg;

        FuncNode(String n, Node a) {
            name = n;
            arg = a;
        }

        public Node diff() {
            Node innerDiff = arg.diff();
            Node outerDiff;
            switch (name) {
                case "sin":
                    outerDiff = new FuncNode("cos", arg);
                    break;
                case "cos":
                    outerDiff = new MulNode(new ConstantNode(-1), new FuncNode("sin", arg));
                    break;
                case "tan":
                    outerDiff = new PowNode(new FuncNode("sec", arg), new ConstantNode(2));
                    break;
                case "ln":
                    outerDiff = new DivNode(new ConstantNode(1), arg);
                    break;
                case "exp":
                    outerDiff = new FuncNode("exp", arg);
                    break;
                case "sqrt":
                    outerDiff = new DivNode(new ConstantNode(1),
                            new MulNode(new ConstantNode(2), new FuncNode("sqrt", arg)));
                    break;
                default:
                    return new ConstantNode(0);
            }
            return new MulNode(outerDiff, innerDiff);
        }

        public Node simplify() {
            Node sa = arg.simplify();
            return new FuncNode(name, sa);
        }

        public String toString() {
            return name + "(" + arg + ")";
        }
    }

    static class Parser {
        String input;
        int pos = 0;

        Parser(String s) {
            input = s;
        }

        Node parse() {
            return parseAddSub();
        }

        Node parseAddSub() {
            Node node = parseMulDiv();
            while (pos < input.length()) {
                if (input.charAt(pos) == '+') {
                    pos++;
                    node = new AddNode(node, parseMulDiv());
                } else if (input.charAt(pos) == '-') {
                    pos++;
                    node = new SubNode(node, parseMulDiv());
                } else
                    break;
            }
            return node;
        }

        Node parseMulDiv() {
            Node node = parsePow();
            while (pos < input.length()) {
                if (input.charAt(pos) == '*') {
                    pos++;
                    node = new MulNode(node, parsePow());
                } else if (input.charAt(pos) == '/') {
                    pos++;
                    node = new DivNode(node, parsePow());
                } else
                    break;
            }
            return node;
        }

        Node parsePow() {
            Node node = parseFactor();
            if (pos < input.length() && input.charAt(pos) == '^') {
                pos++;
                node = new PowNode(node, parsePow());
            }
            return node;
        }

        Node parseFactor() {
            if (pos >= input.length())
                return new ConstantNode(0);
            char c = input.charAt(pos);
            if (c == '(') {
                pos++;
                Node n = parseAddSub();
                if (pos < input.length() && input.charAt(pos) == ')')
                    pos++;
                return n;
            }
            if (c == 'x') {
                pos++;
                return new VarNode();
            }
            if (Character.isDigit(c)) {
                int start = pos;
                while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.'))
                    pos++;
                return new ConstantNode(Double.parseDouble(input.substring(start, pos)));
            }
            if (Character.isLetter(c)) {
                int start = pos;
                while (pos < input.length() && Character.isLetter(input.charAt(pos)))
                    pos++;
                String name = input.substring(start, pos);
                if (name.equals("e"))
                    return new ConstantNode(Math.E);
                if (name.equals("pi"))
                    return new ConstantNode(Math.PI);
                if (pos < input.length() && input.charAt(pos) == '(') {
                    pos++;
                    Node arg = parseAddSub();
                    if (pos < input.length() && input.charAt(pos) == ')')
                        pos++;
                    return new FuncNode(name, arg);
                }
                return new VarNode(); // Fallback if name is just 'x' or similar
            }
            return new ConstantNode(0);
        }
    }
}
