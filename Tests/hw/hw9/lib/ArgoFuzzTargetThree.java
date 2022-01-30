import argo.jdom.JsonNodeDoesNotMatchJsonNodeSelectorException;
import argo.saj.InvalidSyntaxException;
import argo.saj.JsonListener;
import argo.saj.SajParser;
import argo.staj.InvalidSyntaxRuntimeException;
import argo.staj.JsonStreamException;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;

/***
 * Фаззим SajParser.parse() - нет, это не то же самое что во втором,
 * они отличаются на одну букву. На самом деле в этой библиотеке достаточно много
 * разных методов и классов для всевозможной обработки джсонов.
 */
public class ArgoFuzzTargetThree {
    public static void fuzzerTestOneInput(FuzzedDataProvider data){
        try{
            String input = data.consumeRemainingAsString();
            SajParser SAJ_PARSER = new SajParser();
            SAJ_PARSER.parse(input, new JsonListener() {
                public void startField(String name) {   }
                @Override
                public void endField() {}
                @Override
                public void stringValue(String s) {}
                @Override
                public void numberValue(String s) {}
                @Override
                public void trueValue() {}
                @Override
                public void falseValue() {}
                @Override
                public void nullValue() {}
                @Override
                public void startDocument() {}
                @Override
                public void endDocument() {}
                @Override
                public void startArray() {}
                @Override
                public void endArray() {}
                @Override
                public void startObject() {}
                @Override
                public void endObject() {}
            });
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
