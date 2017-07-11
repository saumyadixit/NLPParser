package intentprocessor;

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
public class NLPParserController {

    //private static final String template = "Parse Tree: %s!";
    private final AtomicLong counter = new AtomicLong();
    
    private String intent;
    private String full_name;
    private String first_name;
    private String last_name;
    private String application_name;

    @RequestMapping("/process")
    public NLPParser process(@RequestParam(value="text", defaultValue="call Saumya Dixit") String text) {
    	parser(text);
        return new NLPParser(counter.incrementAndGet(),
                            text, intent, full_name, first_name , last_name, application_name);
    }
    
    public void parser(String text)
    {
    	first_name=null;
    	full_name=null;
    	last_name=null;
    	intent=null;
    	application_name = null;
    	
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
        
        String verb = check_verbs(text, lp, verbs);
        ArrayList<String> object = check_nouns(text, lp, noun_discards);
        switch(verb)
        {
        case "call":
        	intent="call";
        	first_name ="";
        	for(int i = 0; i<object.size()-1;i++)
        		first_name = first_name + " "+ object.get(i);
        	first_name = first_name.trim();
        	last_name = object.get(object.size()-1);
        	full_name = last_name + ", "+first_name;
        	break;
        case "open":
        	intent="open";
        	application_name="";
        	for(int i = 0; i<object.size();i++)
        		application_name = application_name + " "+ object.get(i);
        	application_name = application_name.trim();
        	break;
        case "find":
        	intent="find";
        	for(int i = 0; i<object.size()-1;i++)
        		first_name = first_name + " "+ object.get(i);
        	first_name = first_name.trim();
        	last_name = object.get(object.size()-1);
        	full_name = last_name + ", "+first_name;
        	break;
        }

    }
    
    public String check_verbs(String text, LexicalizedParser lp, List<String> verbs)
    {
    	String verb="";
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
          {
        	  System.out.println(str);
        	  verb = str;
          }
        }
        
        return verb;
        
    }
    
    
    public ArrayList<String> check_nouns(String text, LexicalizedParser lp, List<String> discards)
    {
    	ArrayList<String> object= new ArrayList<String>();
    	
    	System.out.println("\nPrinting Proper Nouns .. !");
    	String regex_pattern_noun = "(NNP > NP) | (NN > NP) | (@JJ $, @RB & >> @VP)";
    	Tree parse_tree = lp.parse(text);
        System.out.println(text);
        System.out.println(parse_tree.toString());

        TregexPattern VPpattern = TregexPattern.compile(regex_pattern_noun);
        TregexMatcher vpmatcher = VPpattern.matcher(parse_tree);
        while (vpmatcher.findNextMatchingNode()) {
          Tree match = vpmatcher.getMatch();
          String str = match.yield().get(0).value();
          if(!discards.contains(str))
          {
        	  System.out.println(str);
        	  object.add(str);
          }
        }
        return object;
    }
}
