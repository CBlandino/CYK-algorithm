import java.io.*;
import java.util.*;

public class CYK
{

	public static void main(String[] args) 
	{
	    try 
	    {
	        String filePath = "src/input.txt";
	        List<List<String>> grammars = readMultipleGrammarsFromFile(filePath);

	        for (List<String> grammarLines : grammars)
	        {
	            String inputString = grammarLines.remove(grammarLines.size() - 1); 
	            Map<String, List<String>> grammar = parseGrammar(grammarLines);

	            System.out.println("Parsed Grammar: " + grammar); 
	            System.out.println("Input String: " + inputString);

	            boolean result = cykAlgorithm(grammar, inputString);
	            System.out.println("String \"" + inputString + "\" is " + (result ? "accepted" : "not accepted") + " by the grammar.");
	            System.out.println();
	        }
	    } 
	    
	    catch (IOException e)
	    {
	        System.err.println("Error reading input file: " + e.getMessage());
	    }
	}


    private static List<List<String>> readMultipleGrammarsFromFile(String filePath) throws IOException 
    {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<List<String>> grammars = new ArrayList<>();
        List<String> currentGrammar = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) 
        {
            if (line.trim().isEmpty()) 
            {
                if (!currentGrammar.isEmpty())
                {
                    grammars.add(new ArrayList<>(currentGrammar));
                    currentGrammar.clear();
                }
            }
            
            else 
            {
                currentGrammar.add(line.trim());
            }
        }

        if (!currentGrammar.isEmpty()) 
        {
        	// Add the last grammar if the file doesn't end with a blank line
            grammars.add(currentGrammar);
        }

        reader.close();
        return grammars;
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
        
        // Debugging DP table initialization
        System.out.println("Initial DP Table (Single Characters):");
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

        // Debugging final DP table
        System.out.println("Final DP Table:");
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
                        // System.out.println("Added " + nonTerminal + " to DP[" + i + "][" + j + "]");
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
