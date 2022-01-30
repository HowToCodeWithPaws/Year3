package calc;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import static calc.Calc.*;

public class CalcFuzz {
   public static void fuzzerTestOneInput(FuzzedDataProvider data){
      try{
         String input = data.consumeRemainingAsString();
         calculate(input);
      }catch (CalcException e){
         //e.printStackTrace();//everything ok
      }
   }
}
