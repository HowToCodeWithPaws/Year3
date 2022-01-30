import argo.jdom.JsonNodeDoesNotMatchJsonNodeSelectorException;
import argo.staj.*;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.util.NoSuchElementException;

/***
 * Фаззится итеративное чтение с помощью StajParser.next()
 */
public class ArgoFuzzTargetTwo {
    public static void fuzzerTestOneInput(FuzzedDataProvider data){
        try{
            String input = data.consumeRemainingAsString();
            StajParser STAJ_PARSER = new StajParser(input);
            for (int i = 0; i < 10; ++i) {
                JsonStreamElement next = STAJ_PARSER.next();
            }
        }catch (JsonNodeDoesNotMatchJsonNodeSelectorException e){
            //e.printStackTrace();//everything ok
        }catch (JsonStreamException e){
            //e.printStackTrace();//everything ok
        } catch (InvalidSyntaxRuntimeException e){
            //e.printStackTrace();//everything ok
        }catch (NoSuchElementException e){
            //e.printStackTrace();//everything ok
        }
    }
}
