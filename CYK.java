import java.io.*;
import java.util.*;

public class CYK
{

    public static void main(String[] args) 
    {
        try
        {
            // Read input file
            String filePath = "src/input.txt";
            List<String> grammarLines = readGrammarFromFile(filePath);

            // Last line is the input string
            String inputString = grammarLines.remove(grammarLines.size() - 1); 

            // Parse grammar
            Map<String, List<String>> grammar = parseGrammar(grammarLines);
            System.out.println("Parsed Grammar: " + grammar); // Debugging parsed grammar

            // Perform CYK Algorithm
            boolean result = cykAlgorithm(grammar, inputString);

            // Output result
            System.out.println("String \"" + inputString + "\" is " + (result ? "accepted" : "not accepted") + " by the grammar.");

        }
        
        catch (IOException e) 
        {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

    private static List<String> readGrammarFromFile(String filePath) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) 
        {
            lines.add(line.trim());
        }

        reader.close();
        return lines;
    }

    private static Map<String, List<String>> parseGrammar(List<String> grammarLines) 
    {
        Map<String, List<String>> grammar = new HashMap<>();

        for (String rule : grammarLines)
        {
            String[] parts = rule.split("->");
            String nonTerminal = parts[0].trim();
            String[] productions = parts[1].trim().split("\\|");

            grammar.putIfAbsent(nonTerminal, new ArrayList<>());

            for (String production : productions) 
            {
                grammar.get(nonTerminal).add(production.trim());
            }
        }
        return grammar;
    }

    private static boolean cykAlgorithm(Map<String, List<String>> grammar, String inputString) 
    {
        int n = inputString.length();
        if (n == 0) return false; 

        Set<String>[][] dp = initializeDPTable(grammar, inputString);
        System.out.println("Initial DP Table (Single Characters):");
        
        // Debugging DP table initialization
        printDPTable(dp, n);

        for (int length = 2; length <= n; length++)
        {
            for (int i = 0; i <= n - length; i++) 
            {
                int j = i + length - 1;
                dp[i][j] = new HashSet<>();

                for (int k = i; k < j; k++)
                {
                    addNonTerminals(grammar, dp, i, j, k);
                }
            }
        }

        System.out.println("Final DP Table:");
        // Debugging final DP table
        printDPTable(dp, n);

        return dp[0][n - 1].contains("S");
    }

    private static Set<String>[][] initializeDPTable(Map<String, List<String>> grammar, String inputString)
    {
        int n = inputString.length();
        Set<String>[][] dp = new HashSet[n][n];

        for (int i = 0; i < n; i++)
        {
            dp[i][i] = new HashSet<>();
            String symbol = String.valueOf(inputString.charAt(i));

            for (String nonTerminal : grammar.keySet()) 
            {
                if (grammar.get(nonTerminal).contains(symbol)) 
                {
                    dp[i][i].add(nonTerminal);
                }
            }
        }
        return dp;
    }

    private static void addNonTerminals(Map<String, List<String>> grammar, Set<String>[][] dp, int i, int j, int k) 
    {
        for (String nonTerminal : grammar.keySet()) 
        {
            for (String production : grammar.get(nonTerminal)) 
            {
                if (production.length() == 2) 
                {
                    String left = String.valueOf(production.charAt(0));
                    String right = String.valueOf(production.charAt(1));

                    if (dp[i][k].contains(left) && dp[k + 1][j].contains(right))
                    {
                        dp[i][j].add(nonTerminal);
                        
                        // Debugging addition
                        System.out.println("Added " + nonTerminal + " to DP[" + i + "][" + j + "]");
                    }
                }
            }
        }
    }

    private static void printDPTable(Set<String>[][] dp, int n)
    {
        for (int i = 0; i < n; i++) 
        {
            for (int j = 0; j < n; j++)
            {
                if (dp[i][j] != null)
                {
                    System.out.print(dp[i][j] + "\t");
                } 
                else
                {
                    System.out.print("-\t");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
