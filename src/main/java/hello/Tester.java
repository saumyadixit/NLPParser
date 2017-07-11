package hello;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

@RestController
public class Tester {

   
    private final AtomicLong counter = new AtomicLong();
    
    private String intent="Check Console for results";
    private String full_name="";
    private String first_name="";
    private String last_name="";
    private String application_name="";

    @RequestMapping("/test")
    public Greeting greeting(@RequestParam(value="text", defaultValue="World") String text) {
    	
    	parser(text);
        return new Greeting(counter.incrementAndGet(),
                            text, intent, full_name, first_name , last_name, application_name);
    }
    
    public String parser(String text)
    {
    	String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
        String[] options = { "-maxLength", "80", "-retainTmpSubcategories" };
        LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
        List<String> noun_discards = new ArrayList<String>();
        noun_discards.add("call");
        noun_discards.add("find");
        noun_discards.add("open");
        List<String> verbs = new ArrayList<String>();
        verbs.add("call");
        verbs.add("open");
        verbs.add("find");
        
    	
        //Call Sentences Test
    	text = "call Saumya Dixit";
    	//print_verbs(text,lp,verbs);
    	print_nouns(text,lp,noun_discards);
        
        text = "Please call Saumya Dixit";
        print_nouns(text,lp,noun_discards);
        //print_verbs(text,lp,verbs);

        
        text = "Can you please call Saumya Dixit?";
        print_nouns(text,lp,noun_discards);
        //print_verbs(text,lp,verbs);
        
        text = "Place a call to Saumya Dixit";
        print_nouns(text,lp,noun_discards);
        //print_verbs(text,lp,verbs);
        
        text = "Make a call to Saumya Dixit";
        print_nouns(text,lp,noun_discards);
        //print_verbs(text,lp,verbs);
        
        text = "Please make a call to Saumya Dixit";
        print_nouns(text,lp,noun_discards);
        //print_verbs(text,lp,verbs);
       
        text = "Please make a call to saumya";
        print_nouns(text,lp,noun_discards);
        //print_verbs(text,lp,verbs);
        
        
        //Open Sentences Test
        
        
        text = "please open Internet Explorer";
        //print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        text = "open Notepad";
        //print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        text = "can you open Notepad";
        //print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        text = "open firefox";
        //print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        text = "can you please open Firefox?";
        //print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        text = "can you please open Google Chrome for me?";
        //print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        
        text = "open Microsoft Outlook please";
        //print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        
        //Find Sentences Test
        
        
        text = "Please find Saumya Dixit";
        print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        text = "find Saumya Dixit";
        print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        text = "Can you please find Saumya Dixit?";
        print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        text = "Can you please find Saumya Dixit in my contacts?";
        print_verbs(text,lp,verbs);
        print_nouns(text,lp,noun_discards);
        
        return("Testing");
    }
    
    public void print_verbs(String text, LexicalizedParser lp, List<String> verbs)
    {
    	
    	System.out.println("\n\nPrinting Verbs .. !");
    	String regex_pattern_noun = "(@ADJP << @JJ & > @VP) | (@NP < @JJ) | (@VP < @VB) | (@VP < @VBP) | (@PRT << @RP & > @VP) | (@NP < @NN  & >> @VP) | (@NN $, @DT & >> @VP) ";
    	
    	//String regex_pattern_noun = "(@ADJP << @JJ & > @VP)";
    	Tree parse_tree = lp.parse(text);
        System.out.println("Sentence : " +text);
        System.out.println(parse_tree.toString());

        TregexPattern VPpattern = TregexPattern.compile(regex_pattern_noun);
        TregexMatcher vpmatcher = VPpattern.matcher(parse_tree);
        while (vpmatcher.findNextMatchingNode()) {
          Tree match = vpmatcher.getMatch();
          String str = match.yield().get(0).value();
          if(verbs.contains(str))
        	  System.out.println(str);
        }
        
    }
    
    
    public void print_nouns(String text, LexicalizedParser lp, List<String> discards)
    {
    	
    	System.out.println("\nPrinting Proper Nouns .. !");
    	String regex_pattern_noun = "(NNP > NP) | (NN > NP)";
    	Tree parse_tree = lp.parse(text);
        System.out.println(text);
        System.out.println(parse_tree.toString());

        TregexPattern VPpattern = TregexPattern.compile(regex_pattern_noun);
        TregexMatcher vpmatcher = VPpattern.matcher(parse_tree);
        while (vpmatcher.findNextMatchingNode()) {
          Tree match = vpmatcher.getMatch();
          String str = match.yield().get(0).value();
          if(!discards.contains(str))
        	  System.out.println(match.yield());
        }
        
    }
}
