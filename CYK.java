import java.io.*;
import java.util.*;

/**
 * The CYK class implements CYK algorithm for parsing
 * context-free grammars and determining if a given string
 * is in a CFG in CNF. 
 */
public class CYK
{

	/**
	 * Main method to execute the CYK algorithm on grammars and
	 * input strings read from the file
	 */
	public static void main(String[] args) 
	{
	    try 
	    {
	        String filePath = "src/input.txt";
	        List<List<String>> grammars = readMultipleGrammarsFromFile(filePath);

	        for (List<String> grammarLines : grammars)
	        {
				// The last line of each grammar block is the input string.
	            String inputString = grammarLines.remove(grammarLines.size() - 1); 
	            Map<String, List<String>> grammar = parseGrammar(grammarLines);

	            System.out.println("Parsed Grammar: " + grammar); 
	            System.out.println("Input String: " + inputString);

				// Run the CYK algorithm and print whether the string is accepted.
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

	/**
	 * Reads multiple grammars from a file. Each grammar is separated
	 * by a blank space. The last line of each grammar specifies the 
	 * last string.
	 * 
	 * @param filePath the path to the input file.
	 * @return a list of grammar, with each one represented by a string
	 * @throws IOException if an error occurs while reading the file.
	 */
	private static List<List<String>> readMultipleGrammarsFromFile(String filePath) throws IOException 
   	{
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		List<List<String>> grammars = new ArrayList<>();
 		List<String> currentGrammar = new ArrayList<>();
        String line;
	
		    while ((line = reader.readLine()) != null) 
		    {
				// Blank lines separate different grammars in the input file.
	            if (line.trim().isEmpty()) 
				{
					// Add the current grammar to the list and reset for the next one.
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

	/**
	 * Parses a grammar from a list of strings into a map representation.
	 * 
	 * @param grammarLines a list of strings representing grammar rules.
	 * @return a map where keys are non-terminals, and values are lists of productions.
	 */
	private static Map<String, List<String>> parseGrammar(List<String> grammarLines) 
	{
		Map<String, List<String>> grammar = new HashMap<>();
	
		for (String rule : grammarLines)
		{
			// Split each grammar rule into the non-terminal and its productions.
	        String[] parts = rule.split("->");
	    	String nonTerminal = parts[0].trim();
	    	String[] productions = parts[1].trim().split("\\|");

			// Initialize the list for the non-terminal if it doesn't exist.
        	grammar.putIfAbsent(nonTerminal, new ArrayList<>());
	
	        for (String production : productions) 
	    	{
				// Add each production to the list for the non-terminal.
	    		grammar.get(nonTerminal).add(production.trim());
        	}
		}
	    return grammar;
	}

	/**
	 * Implements the CYK algorithm to determine if the input string is 
	 * accepted by the given grammar.
	 * 
	 * @param grammar a map representation of the grammar
	 * @param inputString the input string to check.
	 * @return true if the string is accepted by the grammar; false otherwise
	 */
    private static boolean cykAlgorithm(Map<String, List<String>> grammar, String inputString) 
    {
        int n = inputString.length();
		
		// Edge case: Empty input strings cannot be accepted.
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

    /**
     * Initializes the DP table for the CYK algorithm with single character products.
     * 
     * @param grammar the grammar used for initialization.
     * @param inputString the input string to parse.
     * @return a 2D array representing the DP table.
     */
	private static Set<String>[][] initializeDPTable(Map<String, List<String>> grammar, String inputString)
	{
		int n = inputString.length();
		Set<String>[][] dp = new HashSet[n][n];

		// Fill the diagonal of the DP table with non-terminals producing each character.
		for (int i = 0; i < n; i++)
		{
	        dp[i][i] = new HashSet<>();
	    	String symbol = String.valueOf(inputString.charAt(i));
	
	        for (String nonTerminal : grammar.keySet()) 
	    	{
				// Check if the current symbol can be derived by a non-terminal.
	    		if (grammar.get(nonTerminal).contains(symbol)) 
        		{
		            dp[i][i].add(nonTerminal);
	        	}
	    	}
		}
	    return dp;
	}

	/**
	 * Adds non-terminals to the DP table based on the given grammar and indices.
	 * @param grammar the grammar used for the algorithm
	 * @param dp the DP table
	 * @param i the starting index
	 * @param j the ending index
	 * @param k the partition index. 
	 */
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

					// If the left and right non-terminals match the partitions, add the non-terminal.
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

	/**
	 * Prints the DP table for debugging purposes.
	 * 
	 * @param dp the DP table 
	 * @param n the size of the table. 
	 */
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
