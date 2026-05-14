package dev.bsprout.btweaks.client.qol;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class Calculator {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        for (String name : new String[]{"calc", "calculate", "calculator"}) {
            dispatcher.register(ClientCommandManager.literal(name)
                    .then(ClientCommandManager.argument("expression", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                String expr = StringArgumentType.getString(ctx, "expression");
                                try {
                                    double result = evaluate(expr);
                                    ctx.getSource().sendFeedback(Component.literal(expr + " = " + result));
                                } catch (Exception e) {
                                    ctx.getSource().sendError(Component.literal("Invalid expression: " + expr));
                                }
                                return 1;
                            })));
        }
    }

    private static double evaluate(String expr) {
        expr = expr.replaceAll("\\s+", "").toLowerCase();
        return parse(expr, new int[]{0});
    }

    private static double parse(String expr, int[] pos) {
        double result = parseTerm(expr, pos);
        while (pos[0] < expr.length()) {
            char op = expr.charAt(pos[0]);
            if (op != '+' && op != '-') break;
            pos[0]++;
            double term = parseTerm(expr, pos);
            result = op == '+' ? result + term : result - term;
        }
        return result;
    }

    private static double parseTerm(String expr, int[] pos) {
        double result = parsePower(expr, pos);
        while (pos[0] < expr.length()) {
            char op = expr.charAt(pos[0]);
            if (op != '*' && op != '/' && op != 'x' && op != '%') break;
            pos[0]++;
            double factor = parsePower(expr, pos);
            if (op == '/') result = result / factor;
            else if (op == '%') result = result % factor;
            else result = result * factor;
        }
        return result;
    }

    private static double parsePower(String expr, int[] pos) {
        double result = parseFactor(expr, pos);
        while (pos[0] < expr.length() && expr.charAt(pos[0]) == '^') {
            pos[0]++;
            double exp = parseFactor(expr, pos);
            result = Math.pow(result, exp);
        }
        return result;
    }

    private static double parseFactor(String expr, int[] pos) {
        if (expr.charAt(pos[0]) == '(') {
            pos[0]++;
            double result = parse(expr, pos);
            pos[0]++; // closing )
            return result;
        }
        if (expr.charAt(pos[0]) == '-') {
            pos[0]++;
            return -parseFactor(expr, pos);
        }
        int start = pos[0];
        while (pos[0] < expr.length() && (Character.isDigit(expr.charAt(pos[0])) || expr.charAt(pos[0]) == '.'))
            pos[0]++;
        return Double.parseDouble(expr.substring(start, pos[0]));
    }
}