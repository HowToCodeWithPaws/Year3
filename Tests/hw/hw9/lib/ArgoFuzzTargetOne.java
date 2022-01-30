import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeDoesNotMatchJsonNodeSelectorException;
import argo.saj.InvalidSyntaxException;
import argo.staj.InvalidSyntaxRuntimeException;
import argo.staj.JsonStreamException;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

/***
 * Здесь фаззится метод парсинга JdomParser.parse() из строки.
 */
public class ArgoFuzzTargetOne {
    public static void fuzzerTestOneInput(FuzzedDataProvider data){
        try{
            String input = data.consumeRemainingAsString();
            JdomParser JDOM_PARSER = new JdomParser();
            JsonNode json = JDOM_PARSER.parse(input);
        }catch (JsonNodeDoesNotMatchJsonNodeSelectorException e){
            //e.printStackTrace();//everything ok
        }catch (JsonStreamException e){
            //e.printStackTrace();//everything ok
        }catch (InvalidSyntaxException e){
            //e.printStackTrace();//everything ok
        }catch (InvalidSyntaxRuntimeException e){
            //e.printStackTrace();//everything ok
        }
    }
}
