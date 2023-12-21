package chongwm.demo.toolbox.String;

import java.util.Arrays;
import java.util.Random;

public class Utils
{

	public static String limitWordsClaudeGenerated(String input, int maxWords)
	{//Claude v2 generated
		String[] words = input.split("\\s+");
		if (words.length <= maxWords)
		{
			return input;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < maxWords; i++)
		{
			sb.append(words[i]).append(" ");
		}

		return sb.toString().trim();
	}
	
	
	public static String seededRandomString(Random randomizer, int length)
	{
        String randomString = "";
        char baseChar;
        for (int i = 0; i < length; i++) 
        {
        	int r = randomizer.nextInt(2);
        	if (r>=1)
        		baseChar='A';
        	else
        		baseChar='a';
            randomString += (char) (randomizer.nextInt(26) + baseChar);
        }
        return randomString;
	}

	public static String limitWords(String input, int maxWords)
	{//Titan Text G1 Express generated
		// Check if the input is null or empty
		if (input == null || input.trim().isEmpty())
		{
			return "";
		}
		// Check if the maxWords is negative
		if (maxWords < 0)
		{
			throw new IllegalArgumentException("maxWords must be non-negative");
		}
		// Split the input into words using whitespace as the delimiter
		String[] words = input.trim().split("\\s+");
		// Check if the number of words is less than or equal to the maxWords
		if (words.length <= maxWords)
		{
			return input;
		}
		// Truncate the words array to the maxWords limit
		String[] truncatedWords = Arrays.copyOfRange(words, 0, maxWords);
		// Join the truncated words back into a string with whitespace as the delimiter
		return String.join(" ", truncatedWords);
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub


	        
	        
	        
	        
	        
	        

	}

}
